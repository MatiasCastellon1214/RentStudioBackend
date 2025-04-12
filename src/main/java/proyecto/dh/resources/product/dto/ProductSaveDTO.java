package proyecto.dh.resources.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.*;
import proyecto.dh.common.enums.RentType;
import proyecto.dh.resources.favorite.dto.ProductFavoriteSaveDTO;
import proyecto.dh.resources.reservation.dto.ReservationSaveDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductSaveDTO {

    @NotBlank(message = "Name is mandatory")
    @Size(max = 100, message = "Name cannot be longer than 100 characters")
    private String name;

    @NotBlank(message = "Description is mandatory")
    @Size(max = 500, message = "Description cannot be longer than 500 characters")
    private String description;

    @NotNull(message = "Stock is mandatory")
    @Min(value = 1, message = "Stock must be at least 1")
    private Integer stock;

    @NotNull(message = "Price is mandatory")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private Double price;

    @NotNull(message = "Rent Type is mandatory")
    private RentType rentType;

    @NotNull(message = "Category ID is mandatory")
    private Long categoryId;

    private List<Long> featureIds;

    private List<Long> attachments;
}
