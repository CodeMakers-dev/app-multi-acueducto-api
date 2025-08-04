package com.codemakers.api.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codemakers.api.service.impl.EmpresaClienteContadorServiceImpl;
import com.codemakers.commons.dtos.EmpresaClienteContadorDTO;
import com.codemakers.commons.dtos.ResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/EmpresaClienteContador")
@Tag(name = "EmpresaClienteContador - Controller", description = "Controller encargado de gestionar las operaciones de la Empresa Cliente Contador")
@CrossOrigin(origins = "*", methods = { RequestMethod.DELETE, RequestMethod.GET, RequestMethod.POST,
		RequestMethod.PUT })
@RequiredArgsConstructor
public class EmpresaClienteContadorController {

    private final EmpresaClienteContadorServiceImpl empresaClienteContadorServiceImpl;

    @Operation(summary = "Guardar cliente empresa contador")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Se ha guardado satisfactoriamente", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
                        @ApiResponse(responseCode = "200", description = "Se ha actualizado satisfactoriamente", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
                        @ApiResponse(responseCode = "400", description = "La petición no puede ser entendida por el servidor debido a errores de sintaxis", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
                        @ApiResponse(responseCode = "404", description = "El recurso solicitado no puede ser encontrado", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
                        @ApiResponse(responseCode = "500", description = "Se presentó una condición inesperada que impidió completar la petición", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
        })
        @PostMapping("/save")
        public ResponseEntity<Map<String, Object>> saveClient(@RequestBody Map<String, Object> jsonParams) {
                try {
                        Map<String, Object> resultado = empresaClienteContadorServiceImpl.saveClient(jsonParams);

                        if (resultado.containsKey("error")) {
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
                        }

                        return ResponseEntity.ok(resultado);
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(Map.of("error", "Error interno del servidor"));
                }
        }
    
    @Operation(summary = "Eliminar cliente empresa contador por ID de persona")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente eliminado correctamente", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
        @ApiResponse(responseCode = "404", description = "La persona no fue encontrada", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
        @ApiResponse(responseCode = "400", description = "Error en los datos enviados", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
        @ApiResponse(responseCode = "500", description = "Error inesperado en el servidor", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) })
    })
    @DeleteMapping("/delete/{idPersona}")
    public ResponseEntity<Map<String, Object>> deleteClient(@PathVariable Integer idPersona) {
        try {
            Map<String, Object> resultado = empresaClienteContadorServiceImpl.deleteClient(idPersona);

            Object statusCode = resultado.get("statusCode");
            if (statusCode instanceof Integer status) {
                if (status == 200) {
                    return ResponseEntity.ok(resultado);
                } else if (status == 404) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
                }
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", "Respuesta inesperada del servicio"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }

	
	@Operation(summary = "Guardar  Empresa Cliente Contador")
	@ApiResponses(value = {
	        @ApiResponse(responseCode = "201", description = "Se ha guardado satisfactoriamente", content = {
	                @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
	        @ApiResponse(responseCode = "200", description = "Se ha actualizado satisfactoriamente", content = {
	                @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
	        @ApiResponse(responseCode = "400", description = "La petición no puede ser entendida por el servidor debido a errores de sintaxis", content = {
	                @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
	        @ApiResponse(responseCode = "404", description = "El recurso solicitado no puede ser encontrado", content = {
	                @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
	        @ApiResponse(responseCode = "500", description = "Se presentó una condición inesperada que impidió completar la petición", content = {
	                @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
	})
    @PostMapping
    public ResponseEntity<ResponseDTO> save(@RequestBody EmpresaClienteContadorDTO empresaClienteContadorDTO) {
        return empresaClienteContadorServiceImpl.save(empresaClienteContadorDTO);
    }
     @Operation(summary = "Actualizar estado activo/inactivo de persona y sus relaciones")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Estado actualizado correctamente", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
                        @ApiResponse(responseCode = "404", description = "La persona no fue encontrada", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
                        @ApiResponse(responseCode = "400", description = "Error de solicitud", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
        })
        @PostMapping("/estado")
        public ResponseEntity<Map<String, Object>> actualizarEstado(
                        @RequestBody Map<String, Object> jsonParams) {
                try {
                        Map<String, Object> resultado = empresaClienteContadorServiceImpl.actualizarEstado(jsonParams);

                        if (resultado.containsKey("statusCode")) {
                                int status = (int) resultado.get("statusCode");

                                return switch (status) {
                                        case 404 -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
                                        case 500 ->
                                                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resultado);
                                        default -> ResponseEntity.ok(resultado);
                                };
                        }

                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(Map.of("error", "Respuesta inesperada del servidor"));

                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(Map.of("error", "Error interno del servidor"));
                }
        }
	
	@Operation(summary = "Buscar Empresa Cliente Contador por id de la empresa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Se ha guardado satisfactoriamente", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "La petición no puede ser entendida por el servidor debido a errores de sintaxis", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "El recurso solicitado no puede ser encontrado", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
            @ApiResponse(responseCode = "500", description = "Se presentó una condición inesperada que impidió completar la petición", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
    })
	@GetMapping("/empresa/{idEmpresa}")
	public ResponseEntity<ResponseDTO> findByEmpresaId(@PathVariable Integer idEmpresa) {
	    return empresaClienteContadorServiceImpl.findByEmpresaId(idEmpresa);
	}

    @Operation(summary = "Buscar Empresa Cliente Contador por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Se ha guardado satisfactoriamente", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "La petición no puede ser entendida por el servidor debido a errores de sintaxis", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "El recurso solicitado no puede ser encontrado", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
            @ApiResponse(responseCode = "500", description = "Se presentó una condición inesperada que impidió completar la petición", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO> getById(@PathVariable Integer id) {
        return empresaClienteContadorServiceImpl.findById(id);
    }

    @Operation(summary = "Listar todas las  Empresa Cliente Contador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Se ha guardado satisfactoriamente", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "La petición no puede ser entendida por el servidor debido a errores de sintaxis", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "El recurso solicitado no puede ser encontrado", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
            @ApiResponse(responseCode = "500", description = "Se presentó una condición inesperada que impidió completar la petición", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
    })
    @GetMapping("/all")
    public ResponseEntity<ResponseDTO> getAll() {
        return empresaClienteContadorServiceImpl.findAll();
    }

    @Operation(summary = "Eliminar Empresa Cliente Contador por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Se ha guardado satisfactoriamente", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "La petición no puede ser entendida por el servidor debido a errores de sintaxis", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "El recurso solicitado no puede ser encontrado", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
            @ApiResponse(responseCode = "500", description = "Se presentó una condición inesperada que impidió completar la petición", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> deleteById(@PathVariable Integer id) {
        return empresaClienteContadorServiceImpl.deleteById(id);
    }
    
    @Operation(summary = "actualizar Empresa")
	@ApiResponses(value = {
	        @ApiResponse(responseCode = "201", description = "Se ha guardado satisfactoriamente", content = {
	                @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
	        @ApiResponse(responseCode = "200", description = "Se ha actualizado satisfactoriamente", content = {
	                @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
	        @ApiResponse(responseCode = "400", description = "La petición no puede ser entendida por el servidor debido a errores de sintaxis", content = {
	                @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
	        @ApiResponse(responseCode = "404", description = "El recurso solicitado no puede ser encontrado", content = {
	                @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
	        @ApiResponse(responseCode = "500", description = "Se presentó una condición inesperada que impidió completar la petición", content = {
	                @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)) }),
	})
    @PutMapping
    public ResponseEntity<ResponseDTO> update(@RequestBody EmpresaClienteContadorDTO empresaClienteContadorDTO) {
        return empresaClienteContadorServiceImpl.update(empresaClienteContadorDTO);
    }
}
