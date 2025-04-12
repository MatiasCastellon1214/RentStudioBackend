package proyecto.dh.resources.product.controller.secure;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import proyecto.dh.common.responses.ResponseDTO;
import proyecto.dh.common.responses.ResponseHandler;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.product.dto.ProductDTO;
import proyecto.dh.resources.product.dto.ProductSaveDTO;
import proyecto.dh.resources.product.dto.ProductUpdateDTO;
import proyecto.dh.resources.product.service.ProductService;

/**
 * Controlador para gestionar productos.
 * Contiene endpoints para crear, actualizar, eliminar y recuperar productos.
 */
@RestController
@RequestMapping("/products")
@Tag(name = "[Admin] Products Controller", description = "Controlador para gestionar productos como administrador")
public class ProductSecuredController {

    @Autowired
    private ProductService productService;

    /**
     * Crea un nuevo producto en el sistema.
     *
     * @param productSaveDTO el DTO que contiene los datos del producto a crear
     * @return el producto creado con un estado HTTP 201 (Created)
     * @throws NotFoundException   si no se encuentra la categoría del producto
     * @throws BadRequestException si los datos de entrada son inválidos
     */
    @Operation(summary = "Crear un nuevo producto", description = "Esta operación crea un nuevo producto en el sistema.")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Producto creado con éxito", content = @Content(schema = @Schema(implementation = ProductDTO.class))), @ApiResponse(responseCode = "400", description = "Entrada inválida"), @ApiResponse(responseCode = "404", description = "Categoría no encontrada"), @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    @Secured({"ROLE_ADMIN", "ROLE_EDITOR"})
    @PostMapping()
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductSaveDTO productSaveDTO) throws NotFoundException, BadRequestException {
        ProductDTO createdProduct = productService.save(productSaveDTO);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    /**
     * Actualiza un producto existente en el sistema.
     *
     * @param id               el ID del producto a actualizar
     * @param productUpdateDTO el DTO que contiene los nuevos datos del producto
     * @return el producto actualizado con un estado HTTP 200 (OK)
     * @throws NotFoundException   si el producto o la categoría no se encuentran
     * @throws BadRequestException si los datos de entrada son inválidos
     */
    @Operation(summary = "Actualizar un producto existente", description = "Esta operación actualiza un producto existente en el sistema.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Producto actualizado con éxito", content = @Content(schema = @Schema(implementation = ProductDTO.class))), @ApiResponse(responseCode = "400", description = "Entrada inválida"), @ApiResponse(responseCode = "404", description = "Producto o categoría no encontrada"), @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    @Secured({"ROLE_ADMIN", "ROLE_EDITOR"})
    @PutMapping("{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductUpdateDTO productUpdateDTO) throws NotFoundException, BadRequestException {
        ProductDTO updatedProduct = productService.updateProduct(id, productUpdateDTO);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    /**
     * Elimina un producto del sistema.
     *
     * @param id el ID del producto a eliminar
     * @return una respuesta con estado HTTP 200 (OK) si el producto fue eliminado correctamente
     * @throws NotFoundException si el producto no se encuentra
     */
    @Operation(summary = "Eliminar un producto", description = "Esta operación elimina un producto del sistema.")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Producto eliminado con éxito"), @ApiResponse(responseCode = "404", description = "Producto no encontrado"), @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    @Secured({"ROLE_ADMIN", "ROLE_EDITOR"})
    @DeleteMapping("{id}")
    public ResponseEntity<ResponseDTO<Void>> deleteProduct(@PathVariable Long id) throws NotFoundException {
        productService.delete(id);
        return ResponseHandler.generateResponse("Producto eliminado correctamente", HttpStatus.OK, null);
    }
}
