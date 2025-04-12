package proyecto.dh.resources.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.product.dto.CategoryDTO;
import proyecto.dh.resources.product.service.CategoryService;

import java.util.List;

/**
 * Controlador para gestionar categorías.
 * Contiene endpoints para recuperar todas las categorías y una categoría por su ID.
 */
@RestController
@RequestMapping("public/categories")
@Tag(name = "Public Categories Controller", description = "Controlador para obtener datos públicos de categorías")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * Recupera todas las categorías en el sistema.
     *
     * @return una lista de todas las categorías con estado HTTP 200 (OK)
     */
    @Operation(summary = "Obtener todas las categorías", description = "Esta operación recupera todas las categorías en el sistema.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Categorías recuperadas con éxito", content = @Content(schema = @Schema(implementation = CategoryDTO.class))), @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    @PermitAll
    @GetMapping()
    public ResponseEntity<List<CategoryDTO>> findAll() {
        List<CategoryDTO> categories = categoryService.findAll();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    /**
     * Recupera una categoría por su ID.
     *
     * @param id el ID de la categoría a recuperar
     * @return la categoría recuperada con estado HTTP 200 (OK)
     * @throws NotFoundException si la categoría no se encuentra
     */
    @Operation(summary = "Obtener categoría por ID", description = "Esta operación recupera una categoría por su ID.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Categoría recuperada con éxito", content = @Content(schema = @Schema(implementation = CategoryDTO.class))), @ApiResponse(responseCode = "404", description = "Categoría no encontrada"), @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    @PermitAll
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> findById(@PathVariable Long id) throws NotFoundException {
        CategoryDTO category = categoryService.findById(id);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }
}