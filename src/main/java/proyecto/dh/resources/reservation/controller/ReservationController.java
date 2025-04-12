package proyecto.dh.resources.reservation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.reservation.dto.ReservationDTO;
import proyecto.dh.resources.reservation.dto.ReservationSaveDTO;
import proyecto.dh.resources.reservation.service.ReservationService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;

    @GetMapping("/my")
    public ResponseEntity<List<ReservationDTO>> getUserReservationHistory(@AuthenticationPrincipal UserDetails userDetails) throws NotFoundException {
        List<ReservationDTO> reservations = reservationService.getUserReservationHistory(userDetails);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/admin")
    public ResponseEntity<List<ReservationDTO>> getAllReservations(@AuthenticationPrincipal UserDetails userDetails) throws NotFoundException, BadRequestException {
        List<ReservationDTO> reservations = reservationService.getAllReservations(userDetails);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationDTO> findById(@PathVariable Long reservationId, @AuthenticationPrincipal UserDetails userDetails) throws NotFoundException, BadRequestException {
        ReservationDTO reservation = reservationService.getReservationById(reservationId, userDetails);
        return ResponseEntity.ok(reservation);
    }

    @PostMapping
    public ResponseEntity<ReservationDTO> createReservation(@Valid @RequestBody ReservationSaveDTO reservationSaveDTO, @AuthenticationPrincipal UserDetails userDetails) throws NotFoundException, BadRequestException {
        ReservationDTO createdReservation = reservationService.create(reservationSaveDTO, userDetails);
        return new ResponseEntity<>(createdReservation, HttpStatus.OK);
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<String> cancelReservation(@PathVariable Long reservationId, @AuthenticationPrincipal UserDetails userDetails) throws NotFoundException, BadRequestException {
        reservationService.cancelReservation(reservationId, userDetails);
        return ResponseEntity.ok().body("Reserva cancelada exitosamente");
    }
}
