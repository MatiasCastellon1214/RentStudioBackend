package proyecto.dh.resources.favorite.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductFavoriteDTO {

    private Long id;

    private Long userId;

    private List<Long> productIds;

    private LocalDateTime creationDateTime;

}
