package proyecto.dh.resources.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import proyecto.dh.common.enums.RentType;

import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductUpdateDTO {
    @Size(max = 100, message = "Name cannot be longer than 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot be longer than 500 characters")
    private String description;

    @Min(value = 1, message = "Stock must be at least 1")
    private Integer stock;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private Double price;

    private RentType rentType;

    private Long categoryId;

    private List<Long> featureIds;

    private List<Long> attachmentsIds;
}