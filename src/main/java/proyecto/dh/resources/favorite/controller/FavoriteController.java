package proyecto.dh.resources.favorite.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.favorite.dto.ProductFavoriteDTO;
import proyecto.dh.resources.favorite.dto.ProductFavoriteSaveDTO;
import proyecto.dh.resources.favorite.service.FavoriteService;
import proyecto.dh.resources.users.repository.UserRepository;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("public/products/favorites")
public class FavoriteController {
    @Autowired
    private FavoriteService favoriteService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<ProductFavoriteDTO>> findAll() {
        List<ProductFavoriteDTO> favorites = favoriteService.findAll();
        return ResponseEntity.ok(favorites);
    }

    @GetMapping("/{favoriteId}")
    public ResponseEntity<ProductFavoriteDTO> findById(@Valid @PathVariable Long favoriteId) throws NotFoundException {
        ProductFavoriteDTO favorite = favoriteService.findById(favoriteId);
        return ResponseEntity.ok(favorite);
    }

    @PostMapping
    public ResponseEntity<ProductFavoriteDTO> createFavorite(@RequestBody ProductFavoriteSaveDTO favoriteSaveDTO) throws NotFoundException {
        ProductFavoriteDTO createdFavorite = favoriteService.save(favoriteSaveDTO);
        return  new ResponseEntity<>(createdFavorite, HttpStatus.CREATED);
    }

    @DeleteMapping("/{favoriteId}")
    public ResponseEntity<Void> delete(@PathVariable Long favoriteId) throws NotFoundException {
        favoriteService.deleteById(favoriteId);
        return ResponseEntity.ok().build();
    }

}
