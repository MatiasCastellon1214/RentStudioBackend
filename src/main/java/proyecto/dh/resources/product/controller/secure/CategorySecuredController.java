package proyecto.dh.resources.product.controller.secure;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.product.dto.CategoryDTO;
import proyecto.dh.resources.product.dto.CategorySaveDTO;
import proyecto.dh.resources.product.service.CategoryService;

@RestController
@RequestMapping("/categories")
@Tag(name = "[Admin] Categories Controller", description = "Controlador para gestionar categorías como administrador")
public class CategorySecuredController {

    @Autowired
    private CategoryService categoryService;

    /**
     * Crea una nueva categoría en el sistema.
     *
     * @param categorySaveDTO el DTO que contiene los datos de la categoría a crear
     * @return la categoría creada con un estado HTTP 201 (Created)
     * @throws BadRequestException si los datos de entrada son inválidos
     * @throws NotFoundException   si no se encuentra algún recurso necesario
     */
    @Operation(summary = "Crear una nueva categoría", description = "Esta operación crea una nueva categoría en el sistema.")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Categoría creada con éxito", content = @Content(schema = @Schema(implementation = CategoryDTO.class))), @ApiResponse(responseCode = "400", description = "Entrada inválida"), @ApiResponse(responseCode = "404", description = "Recurso no encontrado"), @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    @Secured({"ROLE_ADMIN", "ROLE_EDITOR"})
    @PostMapping
    public ResponseEntity<CategoryDTO> create(@RequestBody CategorySaveDTO categorySaveDTO) throws BadRequestException, NotFoundException {
        CategoryDTO savedCategory = categoryService.save(categorySaveDTO);
        return ResponseEntity.ok(savedCategory);
    }

    /**
     * Actualiza una categoría existente en el sistema.
     *
     * @param id              el ID de la categoría a actualizar
     * @param categorySaveDTO el DTO que contiene los nuevos datos de la categoría
     * @return la categoría actualizada con un estado HTTP 200 (OK)
     * @throws NotFoundException   si la categoría no se encuentra
     * @throws BadRequestException si los datos de entrada son inválidos
     */
    @Operation(summary = "Actualizar una categoría existente", description = "Esta operación actualiza una categoría existente en el sistema.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Categoría actualizada con éxito", content = @Content(schema = @Schema(implementation = CategoryDTO.class))), @ApiResponse(responseCode = "400", description = "Entrada inválida"), @ApiResponse(responseCode = "404", description = "Categoría no encontrada"), @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    @Secured({"ROLE_ADMIN", "ROLE_EDITOR"})
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> update(@PathVariable Long id, @RequestBody CategorySaveDTO categorySaveDTO) throws NotFoundException, BadRequestException {
        CategoryDTO updatedCategory = categoryService.updateCategory(id, categorySaveDTO);
        return ResponseEntity.ok(updatedCategory);
    }

    /**
     * Elimina una categoría del sistema.
     *
     * @param id el ID de la categoría a eliminar
     * @return una respuesta con estado HTTP 204 (No Content) si la categoría fue eliminada correctamente
     * @throws NotFoundException si la categoría no se encuentra
     */
    @Operation(summary = "Eliminar una categoría", description = "Esta operación elimina una categoría del sistema.")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Categoría eliminada con éxito"), @ApiResponse(responseCode = "404", description = "Categoría no encontrada"), @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    @Secured({"ROLE_ADMIN", "ROLE_EDITOR"})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws BadRequestException, NotFoundException {
        categoryService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}