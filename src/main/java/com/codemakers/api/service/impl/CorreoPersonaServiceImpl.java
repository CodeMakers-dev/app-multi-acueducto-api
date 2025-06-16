package com.codemakers.api.service.impl;

import java.util.Date;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.codemakers.api.service.ICorreoPersonaService;
import com.codemakers.commons.dtos.CorreoPersonaDTO;
import com.codemakers.commons.dtos.ResponseDTO;
import com.codemakers.commons.entities.CorreoPersonaEntity;
import com.codemakers.commons.maps.CorreoPersonaMapper;
import com.codemakers.commons.repositories.CorreoPersonaRepository;
import com.codemakers.commons.utils.Constantes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CorreoPersonaServiceImpl implements ICorreoPersonaService{
	
	private final CorreoPersonaRepository correoPersonaRepository;
	private final CorreoPersonaMapper correoPersonaMapper;
	
	@Override
	public ResponseEntity<ResponseDTO> save(CorreoPersonaDTO correoPersonaDTO) {
	    log.info("Guardar/Actualizar Correo Persona");
	    try {
	        boolean isUpdate = correoPersonaDTO.getId() != null && correoPersonaRepository.existsById(correoPersonaDTO.getId());

	        Optional<CorreoPersonaEntity> existingCorreo = correoPersonaRepository.findByCorreoIgnoreCase(correoPersonaDTO.getCorreo());

	        if (existingCorreo.isPresent()) {
	            if (!isUpdate || !existingCorreo.get().getId().equals(correoPersonaDTO.getId())) {
	                ResponseDTO errorResponse = ResponseDTO.builder()
	                        .success(false)
	                        .message("El correo '" + correoPersonaDTO.getCorreo() + "' ya se encuentra registrado.")
	                        .code(HttpStatus.BAD_REQUEST.value())
	                        .build();
	                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	            }
	        }
	        CorreoPersonaEntity entity;
	        if (isUpdate) {
	            entity = correoPersonaRepository.findById(correoPersonaDTO.getId()).orElseThrow();
	            correoPersonaMapper.updateEntityFromDto(correoPersonaDTO, entity);
	            entity.setFechaModificacion(new Date());
	            entity.setUsuarioModificacion(correoPersonaDTO.getUsuarioModificacion());
	        } else {
	            entity = correoPersonaMapper.dtoToEntity(correoPersonaDTO);
	            entity.setFechaCreacion(new Date());
	            entity.setUsuarioCreacion(correoPersonaDTO.getUsuarioCreacion());
	            entity.setActivo(true);
	        }

	        CorreoPersonaEntity saved = correoPersonaRepository.save(entity);
	        CorreoPersonaDTO savedDTO = correoPersonaMapper.entityToDto(saved);

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
	        log.error("Error guardando el Correo Persona", e);
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
	    log.info("Buscar Correo Persona por id: {}", id);
	    try {
	        Optional<CorreoPersonaEntity> correoPersona = correoPersonaRepository.findById(id);
	        if (correoPersona.isPresent()) {
	        	CorreoPersonaDTO dto = correoPersonaMapper.entityToDto(correoPersona.get());
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
	        log.error("Error al buscar Correo Persona por id: {}", id, e);
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
        log.info("Listar todos los Correo Persona");
        try {
            var list = correoPersonaRepository.findAll();
            var dtoList = correoPersonaMapper.listEntityToDtoList(list);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(true)
                    .message(Constantes.CONSULTED_SUCCESSFULLY)
                    .code(HttpStatus.OK.value())
                    .response(dtoList)
                    .build();
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("Error al listar los Correo Persona", e);
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
        log.info("Inicio método para eliminar Correo Persona por id: {}", id);
        try {
            if (!correoPersonaRepository.existsById(id)) {
                ResponseDTO responseDTO = ResponseDTO.builder()
                        .success(false)
                        .message(Constantes.RECORD_NOT_FOUND)
                        .code(HttpStatus.NOT_FOUND.value())
                        .build();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
            }
            correoPersonaRepository.deleteById(id);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(true)
                    .message(Constantes.DELETED_SUCCESSFULLY)
                    .code(HttpStatus.OK.value())
                    .build();
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("Error al eliminar el Correo Persona con id: {}", id, e);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(false)
                    .message(Constantes.DELETE_ERROR)
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
    }
}