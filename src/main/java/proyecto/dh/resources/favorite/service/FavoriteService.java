package proyecto.dh.resources.favorite.service;

import org.springframework.validation.annotation.Validated;
import proyecto.dh.exceptions.handler.NotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import proyecto.dh.resources.favorite.dto.ProductFavoriteDTO;
import proyecto.dh.resources.favorite.dto.ProductFavoriteSaveDTO;
import proyecto.dh.resources.favorite.entity.ProductFavorite;
import proyecto.dh.resources.favorite.repository.ProductFavoriteRepository;
import proyecto.dh.resources.product.entity.Product;
import proyecto.dh.resources.product.repository.ProductRepository;
import proyecto.dh.resources.users.entity.User;
import proyecto.dh.resources.users.repository.UserRepository;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Validated
public class FavoriteService {

    private final ProductFavoriteRepository favoriteRepository;

    private final ProductRepository productRepository;

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    public FavoriteService(ProductFavoriteRepository favoriteRepository, ProductRepository productRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.favoriteRepository = favoriteRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public ProductFavoriteDTO save(@Valid ProductFavoriteSaveDTO favoriteSaveDTO) throws NotFoundException {
        User user = userRepository.findById(favoriteSaveDTO.getUserId())
                .orElseThrow(()-> new NotFoundException("Usuario no encontrado"));

        for (Long productId: favoriteSaveDTO.getProductIds()) {
            Optional<ProductFavorite> existingFavorite = favoriteRepository.findByUserIdAndProductId(user.getId(), productId);
            if(existingFavorite.isPresent()) {
                throw new IllegalArgumentException(("El ususario ya tiene un favorito para el producto con el id: " + productId));
            }
        }

        ProductFavorite productFavorite = convertToEntity(favoriteSaveDTO);
        productFavorite.setUser(user);

        syncFavoriteWithProducts(productFavorite, favoriteSaveDTO.getProductIds());


        ProductFavorite savedFavorite = favoriteRepository.save(productFavorite);
        return convertToDTO(savedFavorite);
    }

    public List<ProductFavoriteDTO> findAll(){
        return favoriteRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) throws NotFoundException {
        ProductFavorite favorite = findByInEntity(id)
                .orElseThrow(() -> new NotFoundException("Favorito con id " + id + " no encontrado"));

        for (Product product: favorite.getProduct()){
            product.getFavorites().remove(favorite);
        }

        favoriteRepository.deleteById(id);
    }

    public ProductFavoriteDTO findById(Long id) throws NotFoundException {
        ProductFavorite favorite = findByInEntity(id)
                .orElseThrow(()-> new NotFoundException("Favorito con id " + id + " no encontrado"));

        return convertToDTO(favorite);
    }
    public ProductFavorite convertToEntity(ProductFavoriteSaveDTO favoriteSaveDTO){
        return modelMapper.map(favoriteSaveDTO, ProductFavorite.class);
    }

    private void syncFavoriteWithProducts(ProductFavorite favorite, List<Long> productIds) throws NotFoundException {
        if (productIds != null) {
            for (Long productId : productIds) {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new NotFoundException("El producto no existe"));

                // Se añade para manejar los valores nulos
               if (favorite.getProduct() == null) {
                    favorite.setProduct(new HashSet<>());
                }

               if (product.getFavorites() == null) {
                   product.setFavorites(new HashSet<>());
               }

                favorite.getProduct().add(product);
                product.getFavorites().add(favorite);
            }
        }
    }

    private ProductFavoriteDTO convertToDTO(ProductFavorite favorite) {
        ProductFavoriteDTO favoriteDTO = modelMapper.map(favorite, ProductFavoriteDTO.class);
        favoriteDTO.setProductIds(
                favorite.getProduct().stream()
                        .map(Product::getId)
                        .collect(Collectors.toList())
        );
        favoriteDTO.setUserId(favorite.getUser().getId());
        return favoriteDTO;
    }

    private Optional<ProductFavorite> findByInEntity(Long id){
        return favoriteRepository.findById(id);
    }


}
