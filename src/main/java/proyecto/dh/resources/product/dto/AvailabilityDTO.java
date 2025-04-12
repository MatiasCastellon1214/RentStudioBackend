package proyecto.dh.resources.product.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AvailabilityDTO {
    private Long productId;
    private List<DateRange> occupiedDates;

    @Getter
    @Setter
    public static class DateRange {
        private LocalDate startDate;
        private LocalDate endDate;
    }
}
