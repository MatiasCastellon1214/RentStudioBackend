package proyecto.dh.resources.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategorySaveDTO {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 30, message = "El nombre no puede tener más de 30 caracteres")
    private String name;
    @Size(max = 300, message = "La descripción no puede tener más de 300 caracteres")
    private String description;
    @NotBlank(message = "El slug es obligatorio")
    private String slug;
    private Long attachmentId;
    private List<CategoryFeatureSaveDTO> features;
    private List<CategoryPolicySaveDTO> policies;
}