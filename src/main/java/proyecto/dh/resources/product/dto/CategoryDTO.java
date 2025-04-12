package proyecto.dh.resources.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import proyecto.dh.resources.attachment.dto.AttachmentDTO;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryDTO {
    @Schema(hidden = true)
    private Long id;
    private String name;
    private String description;
    private String slug;
    private AttachmentDTO attachment;
    private List<CategoryFeatureDTO> features;
    private List<CategoryPolicyDTO> policies;
}
