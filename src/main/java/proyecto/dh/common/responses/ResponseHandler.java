package proyecto.dh.common.responses;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;

public class ResponseHandler {
    public static <T> ResponseEntity<ResponseDTO<T>> generateResponse(String message, HttpStatus status, T data) {
        ResponseDTO<T> response = new ResponseDTO<>(new Date(), message, status.toString(), data);
        return new ResponseEntity<>(response, status);
    }
}
