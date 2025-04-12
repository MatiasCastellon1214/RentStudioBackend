package proyecto.dh.resources.reservation.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.transaction.Transactional;
import proyecto.dh.common.enums.Role;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.product.entity.Product;
import proyecto.dh.resources.product.repository.ProductRepository;
import proyecto.dh.resources.reservation.dto.ReservationDTO;
import proyecto.dh.resources.reservation.dto.ReservationSaveDTO;
import proyecto.dh.resources.reservation.entity.Reservation;
import proyecto.dh.resources.reservation.repository.ReservationRepository;
import proyecto.dh.resources.users.entity.User;
import proyecto.dh.resources.users.repository.UserRepository;

@Service
@Validated
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public ReservationService(ReservationRepository reservationRepository, ProductRepository productRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.reservationRepository = reservationRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    // ============================================================
    // Business Methods
    // ============================================================

    /**
     * Creates a new reservation based on the provided reservation data.
     *
     * @param reservationSaveDTO The DTO containing the reservation data.
     * @param currentUser The details of the current user.
     * @return The created reservation as a DTO.
     * @throws NotFoundException If the user is not found.
     * @throws BadRequestException If the request is invalid.
     */
    @Transactional
    public ReservationDTO create(@Valid ReservationSaveDTO reservationSaveDTO, UserDetails currentUser) throws NotFoundException, BadRequestException {
        User user = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        Product product = productRepository.findById(reservationSaveDTO.getProductId())
            .orElseThrow(() -> new NotFoundException("Producto no encontrado"));

        LocalDate startDate = reservationSaveDTO.getStartDate();
        LocalDate endDate = reservationSaveDTO.getEndDate();

        // Validar que la fecha de inicio no sea una fecha pasada
        if (startDate.isBefore(LocalDate.now())) {
            throw new BadRequestException("La fecha de inicio no puede ser anterior a la fecha actual");
        }

        // Validar que la fecha de finalización no sea anterior a la fecha de inicio
        if (endDate.isBefore(startDate)) {
            throw new BadRequestException("La fecha de finalización no puede ser anterior a la fecha de inicio");
        }

        Reservation reservation = convertToEntity(reservationSaveDTO);
        reservation.setUser(user);
        reservation.setProduct(product);
        reservation.setCreationDateTime(LocalDateTime.now());
        reservation.setCancelled(false);

        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        reservation.setAmount(product.getPrice() * daysBetween);

        syncReservationWithProduct(reservation, reservationSaveDTO.getProductId());

        Reservation savedReservation = reservationRepository.save(reservation);

        return convertToDTO(savedReservation);
    }

    /**
     * Retrieves the reservation history for the specified user.
     *
     * @param currentUser the current user details
     * @return a list of ReservationDTO objects representing the user's reservation history
     * @throws NotFoundException if the user is not found
     */
    @Transactional
    public List<ReservationDTO> getUserReservationHistory(UserDetails currentUser) throws NotFoundException {
        User user = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        List<Reservation> reservations = reservationRepository.findByUserId(user.getId());
        return reservations.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
        * Retrieves all reservations.
        *
        * @param currentUser the current user details
        * @return a list of ReservationDTO objects representing the reservations
        * @throws NotFoundException   if the reservations are not found
        * @throws BadRequestException if the current user does not have admin privileges
        */
    @Transactional
    public List<ReservationDTO> getAllReservations(UserDetails currentUser) throws NotFoundException, BadRequestException {
        if (!hasAdminPrivileges(currentUser)) {
            throw new BadRequestException("No tienes permiso para acceder a todas las reservas");
        }

        List<Reservation> reservations = reservationRepository.findAll();
        return reservations.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
        * Retrieves a reservation by its ID.
        *
        * @param reservationId The ID of the reservation to retrieve.
        * @param currentUser   The details of the current user.
        * @return The ReservationDTO object representing the retrieved reservation.
        * @throws NotFoundException   If the reservation with the given ID is not found.
        * @throws BadRequestException If the request is invalid.
        */
    @Transactional
    public ReservationDTO getReservationById(Long reservationId, UserDetails currentUser) throws NotFoundException, BadRequestException {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Reserva no encontrada"));

        checkReservationPermissions(reservation, currentUser);

        return convertToDTO(reservation);
    }

    /**
        * Cancels a reservation.
        *
        * @param reservationId The ID of the reservation to cancel.
        * @param currentUser   The details of the current user.
        * @throws NotFoundException   If the reservation is not found.
        * @throws BadRequestException If the request is invalid.
        */
    @Transactional
    public void cancelReservation(Long reservationId, UserDetails currentUser) throws NotFoundException, BadRequestException {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Reserva no encontrada"));

        checkReservationPermissions(reservation, currentUser);

        reservation.setCancelled(true);
        reservationRepository.save(reservation);
    }

    // ============================================================
    // Auxiliar Methods
    // ============================================================

    public void checkReservationPermissions(Reservation reservation, UserDetails currentUser) throws NotFoundException, BadRequestException {
        if (!hasAdminPrivileges(currentUser) && !reservation.getUser().getEmail().equals(currentUser.getUsername())) {
            throw new BadRequestException("No tienes permiso para cancelar esta reserva");
        }
    }

    public boolean hasAdminPrivileges(UserDetails currentUser) throws NotFoundException {
        User user = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        return user.getRole().equals(Role.ROLE_ADMIN);
    }

    private void syncReservationWithProduct(Reservation reservation, Long productId) throws NotFoundException, BadRequestException {
        Product product = validateProductAvailability(productId);

        reservation.setProduct(product);
        product.getReservations().add(reservation);
        product.setStock(product.getStock() - 1);
    }

    private Product validateProductAvailability(Long productId) throws NotFoundException, BadRequestException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("El producto no existe"));

        if (product.getStock() <= 0) {
            throw new BadRequestException("El producto " + product.getName() + " no tiene stock disponible");
        }

        return product;
    }

    public Reservation convertToEntity(ReservationSaveDTO reservationSaveDTO){
        return modelMapper.map(reservationSaveDTO, Reservation.class);
    }

    private ReservationDTO convertToDTO(Reservation reservation) {
        ReservationDTO reservationDTO = modelMapper.map(reservation, ReservationDTO.class);
        reservationDTO.setProductId(reservation.getProduct().getId());
        reservationDTO.setUserId(reservation.getUser().getId());
        return reservationDTO;
    }
}
