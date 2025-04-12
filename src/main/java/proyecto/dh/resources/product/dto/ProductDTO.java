package proyecto.dh.resources.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import proyecto.dh.common.enums.RentType;
import proyecto.dh.resources.attachment.dto.AttachmentDTO;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDTO {
    @Schema(hidden = true)
    private Long id;
    private String name;
    private String description;
    private int stock;
    private double price;
    private RentType rentType;
    private CategoryDTO category;
    private List<AttachmentDTO> attachments;
    private List<CategoryFeatureDTO> features;
}
