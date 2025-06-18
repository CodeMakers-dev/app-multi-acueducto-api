package com.codemakers.api.service.impl;

import java.util.Date;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.codemakers.api.service.ITelefonoPersonaService;
import com.codemakers.commons.dtos.ResponseDTO;
import com.codemakers.commons.dtos.TelefonoPersonaDTO;
import com.codemakers.commons.entities.TelefonoPersonaEntity;
import com.codemakers.commons.maps.TelefonoPersonaMapper;
import com.codemakers.commons.repositories.TelefonoPersonaRepository;
import com.codemakers.commons.utils.Constantes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelefonoPersonaServiceImpl implements ITelefonoPersonaService{
	
	private final TelefonoPersonaRepository telefonoPersonaRepository;
	private final TelefonoPersonaMapper telefonoPersonaMapper;
	
	@Override
	 public ResponseEntity<ResponseDTO> save(TelefonoPersonaDTO telefonoPersonaDTO) {
        log.info("Guardar/Actualizar Telefono de Persona");
        try {
            boolean isUpdate = telefonoPersonaDTO.getId() != null && telefonoPersonaRepository.existsById(telefonoPersonaDTO.getId());
            TelefonoPersonaEntity entity;

            if (isUpdate) {
                entity = telefonoPersonaRepository.findById(telefonoPersonaDTO.getId())
                        .orElseThrow(() -> new RuntimeException("TelefonoPersona not found with ID: " + telefonoPersonaDTO.getId())); 

                if (!entity.getNumero().equals(telefonoPersonaDTO.getNumero())) {
                    if (telefonoPersonaRepository.existsByNumero(telefonoPersonaDTO.getNumero())) {
                        log.warn("El número de teléfono {} ya existe en el sistema.", telefonoPersonaDTO.getNumero());
                        ResponseDTO errorResponse = ResponseDTO.builder()
                                .success(false)
                                .message(Constantes.DUPLICATE_PHONE_NUMBER_ERROR) 
                                .code(HttpStatus.CONFLICT.value())
                                .build();
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
                    }
                }

                telefonoPersonaMapper.updateEntityFromDto(telefonoPersonaDTO, entity);
                entity.setFechaModificacion(new Date());
                entity.setUsuarioModificacion(telefonoPersonaDTO.getUsuarioModificacion());
            } else {
                if (telefonoPersonaRepository.existsByNumero(telefonoPersonaDTO.getNumero())) {
                    log.warn("El número de teléfono {} ya existe en el sistema.", telefonoPersonaDTO.getNumero());
                    ResponseDTO errorResponse = ResponseDTO.builder()
                            .success(false)
                            .message(Constantes.DUPLICATE_PHONE_NUMBER_ERROR) 
                            .code(HttpStatus.CONFLICT.value())
                            .build();
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
                }
                entity = telefonoPersonaMapper.dtoToEntity(telefonoPersonaDTO);
                entity.setFechaCreacion(new Date());
                entity.setUsuarioCreacion(telefonoPersonaDTO.getUsuarioCreacion());
                entity.setActivo(true);
            }

            TelefonoPersonaEntity saved = telefonoPersonaRepository.save(entity);
            TelefonoPersonaDTO savedDTO = telefonoPersonaMapper.entityToDto(saved);

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
            log.error("Error guardando el Telefono de Persona", e);
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
	    log.info("Buscar Telefono de Persona por id: {}", id);
	    try {
	        Optional<TelefonoPersonaEntity> telefonoPersona = telefonoPersonaRepository.findById(id);
	        if (telefonoPersona.isPresent()) {
	        	TelefonoPersonaDTO dto = telefonoPersonaMapper.entityToDto(telefonoPersona.get());
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
	        log.error("Error al buscar Telefono de Persona por id: {}", id, e);
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
        log.info("Listar todos Telefono de Persona");
        try {
            var list = telefonoPersonaRepository.findAll();
            var dtoList = telefonoPersonaMapper.listEntityToDtoList(list);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(true)
                    .message(Constantes.CONSULTED_SUCCESSFULLY)
                    .code(HttpStatus.OK.value())
                    .response(dtoList)
                    .build();
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("Error al listar Telefono de Persona", e);
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
        log.info("Inicio método para eliminar Telefono de Persona por id: {}", id);
        try {
            if (!telefonoPersonaRepository.existsById(id)) {
                ResponseDTO responseDTO = ResponseDTO.builder()
                        .success(false)
                        .message(Constantes.RECORD_NOT_FOUND)
                        .code(HttpStatus.NOT_FOUND.value())
                        .build();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
            }
            telefonoPersonaRepository.deleteById(id);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(true)
                    .message(Constantes.DELETED_SUCCESSFULLY)
                    .code(HttpStatus.OK.value())
                    .build();
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("Error al eliminar Telefono de Persona con id: {}", id, e);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(false)
                    .message(Constantes.DELETE_ERROR)
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
    }
}