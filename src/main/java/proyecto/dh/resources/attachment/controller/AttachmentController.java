package proyecto.dh.resources.attachment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import proyecto.dh.common.responses.ResponseDTO;
import proyecto.dh.common.responses.ResponseHandler;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.resources.attachment.dto.AttachmentDTO;
import proyecto.dh.resources.attachment.dto.DeleteAttachmentDTO;
import proyecto.dh.resources.attachment.entity.Attachment;
import proyecto.dh.resources.attachment.service.AttachmentService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("attachments")
@Tag(name = "[Admin] Attachments Controller", description = "Controlador para la gestión de archivos")
public class AttachmentController {

    @Autowired
    private AttachmentService attachmentService;


    @Operation(summary = "Subir archivos adjuntos", description = "Esta operación sube archivos adjuntos y devuelve sus IDs.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Archivos subidos correctamente"),
            @ApiResponse(responseCode = "400", description = "Error al subir archivos"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(value = "/upload", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<Attachment>> uploadAttachments(
            @Parameter(description = "Los archivos a subir", required = true, content = @Content(mediaType = "multipart/form-data", schema = @Schema(type = "array", format = "binary"))) @RequestPart("files") List<MultipartFile> files) throws IOException, BadRequestException {
        return ResponseEntity.ok(attachmentService.uploadAttachments(files));
    }

    @Operation(summary = "Eliminar un archivo adjunto por ID", description = "Esta operación elimina un archivo adjunto del S3 basado en su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Archivo adjunto eliminado correctamente"),
            @ApiResponse(responseCode = "400", description = "ID del archivo adjunto no válido"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<String>> deleteAttachment(@PathVariable Long id) throws BadRequestException {
        attachmentService.deleteAttachment(id);
        return ResponseHandler.generateResponse("Archivo adjunto eliminado correctamente", HttpStatus.ACCEPTED, null);
    }


    @Operation(summary = "Eliminar múltiples archivos adjuntos por ID", description = "Esta operación elimina varios archivos adjuntos del S3 basados en sus IDs.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Archivos adjuntos eliminados correctamente"),
            @ApiResponse(responseCode = "400", description = "IDs de archivos adjuntos no válidos"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping
    public ResponseEntity<ResponseDTO<String>> deleteAttachments(@RequestParam List<Long> ids) throws BadRequestException {
        attachmentService.deleteAttachments(ids);
        return ResponseHandler.generateResponse("Archivos adjuntos eliminados correctamente", HttpStatus.ACCEPTED, null);
    }



    @Operation(summary = "Obtener detalles de un archivo adjunto por ID", description = "Esta operación devuelve los detalles de un archivo adjunto basado en su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Archivo adjunto encontrado"),
            @ApiResponse(responseCode = "404", description = "Archivo adjunto no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Attachment> getAttachmentById(@PathVariable Long id) throws BadRequestException {
        Attachment attachment = attachmentService.findById(id);
        return ResponseEntity.ok(attachment);
    }

    @GetMapping()
    public List<AttachmentDTO> getAll()  {
        return attachmentService.findAll();
    }
}
