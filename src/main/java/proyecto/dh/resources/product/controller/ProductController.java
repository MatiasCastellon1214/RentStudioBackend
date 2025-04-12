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
import org.springframework.web.bind.annotation.*;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.product.dto.AvailabilityDTO;
import proyecto.dh.resources.product.dto.ProductDTO;
import proyecto.dh.resources.product.service.ProductService;

import java.util.List;

/**
 * Controlador para gestionar productos.
 * Contiene endpoints para crear, actualizar, eliminar y recuperar productos.
 */
@RestController
@RequestMapping("/public/products")
@Tag(name = "Public Products Controller", description = "Controlador para obtener datos públicos de productos")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * Recupera todos los productos en el sistema.
     *
     * @return una lista de todos los productos con estado HTTP 200 (OK)
     */
    @Operation(summary = "Obtener todos los productos", description = "Esta operación recupera todos los productos en el sistema.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Productos recuperados con éxito", content = @Content(schema = @Schema(implementation = ProductDTO.class))), @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    @PermitAll
    @GetMapping()
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.findAll();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    /**
     * Recupera un producto por su ID.
     *
     * @param id el ID del producto a recuperar
     * @return el producto recuperado con estado HTTP 200 (OK)
     * @throws NotFoundException si el producto no se encuentra
     */
    @Operation(summary = "Obtener producto por ID", description = "Esta operación recupera un producto por su ID.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Producto recuperado con éxito", content = @Content(schema = @Schema(implementation = ProductDTO.class))), @ApiResponse(responseCode = "404", description = "Producto no encontrado"), @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) throws NotFoundException {
        ProductDTO product = productService.findById(id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @GetMapping("/search")
    public List<ProductDTO> search(@RequestParam String searchText, @RequestParam(required = false) Long categoryId) throws NotFoundException {
        return productService.searchProducts(searchText, categoryId);
    }

    @GetMapping("/search/suggestions")
    public ResponseEntity<List<String>> getSuggestions(@RequestParam String query) {
        List<String> suggestions = productService.getSuggestions(query);
        return ResponseEntity.ok(suggestions);
    }

    @GetMapping("/{productId}/availability")
    public ResponseEntity<AvailabilityDTO> getProductAvailability(@PathVariable Long productId) throws NotFoundException {
        AvailabilityDTO availability = productService.getProductAvailability(productId);
        return ResponseEntity.ok(availability);
    }
}
