package proyecto.dh.resources.favorite.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductFavoriteSaveDTO {

    private Long userId;
    private List<Long> productIds;
    private LocalDateTime creationDateTime;

}
