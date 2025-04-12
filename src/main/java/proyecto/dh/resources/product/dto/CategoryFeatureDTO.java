package proyecto.dh.resources.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryFeatureDTO {
    private Long id;
    private String name;
    private String description;
    private String icon;
    private List<Long> categoryIds;
}
