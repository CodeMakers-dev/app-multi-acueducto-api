package com.codemakers.api.service.impl;

import java.util.Date;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.codemakers.api.service.IPersonaService;
import com.codemakers.commons.dtos.PersonaDTO;
import com.codemakers.commons.dtos.ResponseDTO;
import com.codemakers.commons.entities.DireccionEntity;
import com.codemakers.commons.entities.PersonaEntity;
import com.codemakers.commons.entities.TipoDocumentoEntity;
import com.codemakers.commons.maps.PersonaMapper;
import com.codemakers.commons.repositories.DireccionRepository;
import com.codemakers.commons.repositories.PersonaRepository;
import com.codemakers.commons.repositories.TipoDocumentoRepository;
import com.codemakers.commons.utils.Constantes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author nicope
 * @version 1.0
 * 
 *          Clase que implementa la interfaz de la lógica de negocio.
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonaServiceImpl implements IPersonaService {

	private final PersonaRepository personaRepository;
	private final DireccionRepository direccionRepository;
	private final TipoDocumentoRepository tipoDocumentoRepository;
    private final PersonaMapper personaMapper;

    @Override
    public ResponseEntity<ResponseDTO> save(PersonaDTO personaDTO) {
        log.info("Guardar/Actualizar persona");
        try {
            boolean isUpdate = personaDTO.getId() != null && personaRepository.existsById(personaDTO.getId());
            PersonaEntity entity;

            if (isUpdate) {
                entity = personaRepository.findById(personaDTO.getId()).orElseThrow();
                personaMapper.updateEntityFromDto(personaDTO, entity);
                entity.setFechaModificacion(new Date());
                entity.setUsuarioModificacion(personaDTO.getUsuarioModificacion());
            } else {
                entity = personaMapper.dtoToEntity(personaDTO);
                entity.setFechaCreacion(new Date());
                entity.setUsuarioCreacion(personaDTO.getUsuarioCreacion());
                entity.setActivo(true);
            }

            if (personaDTO.getDireccion() != null && personaDTO.getDireccion().getId() != null) {
                DireccionEntity direccion = direccionRepository.findById(personaDTO.getDireccion().getId())
                    .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));
                entity.setDireccion(direccion);
            }
            if (personaDTO.getTipoDocumento() != null && personaDTO.getTipoDocumento().getId() != null) {
                TipoDocumentoEntity tipoDocumento = tipoDocumentoRepository.findById(personaDTO.getTipoDocumento().getId())
                    .orElseThrow(() -> new RuntimeException(Constantes.TD_NOT_FOUND));
                entity.setTipoDocumento(tipoDocumento);
            }

            PersonaEntity saved = personaRepository.save(entity);
            PersonaDTO savedDTO = personaMapper.entityToDto(saved);

            String message = isUpdate ? Constantes.UPDATED_SUCCESSFULLY : Constantes.SAVED_SUCCESSFULLY;
            int statusCode = isUpdate ? HttpStatus.OK.value() : HttpStatus.CREATED.value();

            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(true)
                    .message(message)
                    .code(statusCode)
                    .response(savedDTO)
                    .build();

            return ResponseEntity.status(statusCode).body(responseDTO);

        } catch (Exception e) {
            log.error("Error guardando persona", e);
            ResponseDTO errorResponse = ResponseDTO.builder()
                    .success(false)
                    .message(Constantes.SAVE_ERROR)
                    .code(HttpStatus.BAD_REQUEST.value())
                    .build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @Override
	public ResponseEntity<ResponseDTO> findById(Integer id) {
	    log.info("Buscar persona por id: {}", id);
	    try {
	        Optional<PersonaEntity> persona = personaRepository.findById(id);
	        if (persona.isPresent()) {
	            PersonaDTO dto = personaMapper.entityToDto(persona.get());
	            ResponseDTO responseDTO = ResponseDTO.builder()
	                    .success(true)
	                    .message(Constantes.CONSULTED_SUCCESSFULLY)
	                    .code(HttpStatus.OK.value())
	                    .response(dto)
	                    .build();
	            return ResponseEntity.ok(responseDTO);
	        } else {
	            ResponseDTO responseDTO = ResponseDTO.builder()
	                    .success(false)
	                    .message(Constantes.CONSULTING_ERROR)
	                    .code(HttpStatus.NOT_FOUND.value())
	                    .build();
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
	        }
	    } catch (Exception e) {
	        log.error("Error al buscar la persona por id: {}", id, e);
	        ResponseDTO responseDTO = ResponseDTO.builder()
	                .success(false)
	                .message(Constantes.ERROR_QUERY_RECORD_BY_ID)
	                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
	                .build();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
	    }
	}

    @Override
    public ResponseEntity<ResponseDTO> findAll() {
        log.info("Listar todas las personas");
        try {
            var list = personaRepository.findAll();
            var dtoList = personaMapper.listEntityToDtoList(list);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(true)
                    .message(Constantes.CONSULTED_SUCCESSFULLY)
                    .code(HttpStatus.OK.value())
                    .response(dtoList)
                    .build();
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("Error al listar las personas", e);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(false)
                    .message(Constantes.CONSULTING_ERROR)
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .response(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
    }

    @Override
    public ResponseEntity<ResponseDTO> deleteById(Integer id) {
        log.info("Inicio método para eliminar persona por id: {}", id);
        try {
            if (!personaRepository.existsById(id)) {
                ResponseDTO responseDTO = ResponseDTO.builder()
                        .success(false)
                        .message(Constantes.RECORD_NOT_FOUND)
                        .code(HttpStatus.NOT_FOUND.value())
                        .build();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
            }
            personaRepository.deleteById(id);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(true)
                    .message(Constantes.DELETED_SUCCESSFULLY)
                    .code(HttpStatus.OK.value())
                    .build();
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("Error al eliminar la persona con id: {}", id, e);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(false)
                    .message(Constantes.DELETE_ERROR)
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
    }
}
