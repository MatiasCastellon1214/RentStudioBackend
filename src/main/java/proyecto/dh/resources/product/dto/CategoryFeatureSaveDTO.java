package proyecto.dh.resources.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryFeatureSaveDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 30, message = "El nombre no puede tener más de 30 caracteres")
    private String name;
    @Size(max = 300, message = "La descripción no puede tener más de 300 caracteres")
    private String description;
    @NotBlank(message = "El icono es obligatorio")
    private String icon;
}
