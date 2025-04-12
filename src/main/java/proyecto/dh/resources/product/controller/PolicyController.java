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
import proyecto.dh.resources.product.dto.CategoryPolicyDTO;
import proyecto.dh.resources.product.service.PolicyService;

import java.util.List;

/**
 * Controlador para gestionar políticas de categorías.
 * Contiene endpoints para recuperar todas las políticas y una política por su ID.
 */
@RestController
@RequestMapping("/public/categories/policies")
@Tag(name = "Public Policies Controller", description = "Controlador para obtener datos públicos de políticas de categorías")
public class PolicyController {

    @Autowired
    private PolicyService policyService;

    /**
     * Recupera todas las políticas de las categorías en el sistema.
     *
     * @return una lista de todas las políticas con estado HTTP 200 (OK)
     */
    @Operation(summary = "Obtener todas las políticas de categorías", description = "Esta operación recupera todas las políticas de las categorías en el sistema.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Políticas recuperadas con éxito", content = @Content(schema = @Schema(implementation = CategoryPolicyDTO.class))), @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    @PermitAll
    @GetMapping
    public ResponseEntity<List<CategoryPolicyDTO>> findAll() {
        List<CategoryPolicyDTO> policies = policyService.findAll();
        return ResponseEntity.ok(policies);
    }

    /**
     * Recupera una política por su ID.
     *
     * @param policyId el ID de la política a recuperar
     * @return la política recuperada con estado HTTP 200 (OK)
     * @throws NotFoundException si la política no se encuentra
     */
    @Operation(summary = "Obtener política por ID", description = "Esta operación recupera una política por su ID.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Política recuperada con éxito", content = @Content(schema = @Schema(implementation = CategoryPolicyDTO.class))), @ApiResponse(responseCode = "404", description = "Política no encontrada"), @ApiResponse(responseCode = "500", description = "Error interno del servidor")})
    @PermitAll
    @GetMapping("/{policyId}")
    public ResponseEntity<CategoryPolicyDTO> findById(@PathVariable Long policyId) throws NotFoundException {
        CategoryPolicyDTO policy = policyService.findById(policyId);
        return ResponseEntity.ok(policy);
    }
}
