package proyecto.dh.common.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDTO<T> {
    @Schema(description = "Marca de tiempo de la respuesta", example = "2024-05-23T23:32:22.951Z")
    private Date timestamp;
    @Schema(description = "Mensaje de la respuesta", example = "Archivos adjuntos eliminados correctamente")
    private String message;
    @Schema(description = "Estado de la respuesta", example = "200 OK")
    private String status;
    @Schema(description = "Datos adicionales de la respuesta")
    private T data;
}
