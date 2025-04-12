package proyecto.dh.resources.reservation.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReservationDTO {

    private Long id;

    private Long userId;

    private Long productId;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDateTime creationDateTime;

    private Double amount;

    private boolean cancelled;

    private JsonNode payment;
}
