package proyecto.dh.resources.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import proyecto.dh.exceptions.handler.NotFoundException;
import proyecto.dh.resources.product.dto.CategoryFeatureDTO;
import proyecto.dh.resources.product.service.FeatureService;

import java.util.List;

/**
 * Controlador para gestionar características de categorías.
 * Contiene endpoints para recuperar todas las características y una característica por su ID.
 */
@RestController
@RequestMapping("/public/categories/features")
@Tag(name = "Public Features Controller", description = "Controlador para obtener datos públicos de características de categorías")
public class FeatureController {

    @Autowired
    private FeatureService featureService;

    /**
     * Recupera todas las características de las categorías en el sistema.
     *
     * @return una lista de todas las características con estado HTTP 200 (OK)
     */
    @Operation(summary = "Obtener todas las características de categorías", description = "Esta operación recupera todas las características de las categorías en el sistema.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Características recuperadas con éxito", content = @Content(schema = @Schema(implementation = CategoryFeatureDTO.class))), @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    @PermitAll
    @GetMapping
    public ResponseEntity<List<CategoryFeatureDTO>> findAll() {
        List<CategoryFeatureDTO> features = featureService.findAll();
        return ResponseEntity.ok(features);
    }

    /**
     * Recupera una característica por su ID.
     *
     * @param featureId el ID de la característica a recuperar
     * @return la característica recuperada con estado HTTP 200 (OK)
     * @throws NotFoundException si la característica no se encuentra
     */
    @Operation(summary = "Obtener característica por ID", description = "Esta operación recupera una característica por su ID.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Características recuperada con éxito", content = @Content(schema = @Schema(implementation = CategoryFeatureDTO.class))), @ApiResponse(responseCode = "404", description = "Características no encontrada"), @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    @PermitAll
    @GetMapping("/{featureId}")
    public ResponseEntity<CategoryFeatureDTO> findById(@PathVariable Long featureId) throws NotFoundException {
        CategoryFeatureDTO feature = featureService.findById(featureId);
        return ResponseEntity.ok(feature);
    }
}
