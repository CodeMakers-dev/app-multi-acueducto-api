package com.codemakers.api.service.impl;

import java.util.Date;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.codemakers.api.service.ITipoContadorService;
import com.codemakers.commons.dtos.ResponseDTO;
import com.codemakers.commons.dtos.TipoContadorDTO;
import com.codemakers.commons.entities.TipoContadorEntity;
import com.codemakers.commons.maps.TipoContadorMapper;
import com.codemakers.commons.repositories.TipoContadorRepository;
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
public class TipoContadorServiceImpl implements ITipoContadorService {

	private final TipoContadorRepository tipoContadorRepository;
	private final TipoContadorMapper tipoContadorMapper;
	
	@Override
	public ResponseEntity<ResponseDTO> save(TipoContadorDTO tipoContadorDTO) {
	    log.info("Guardar/Actualizar Tipo de Documento");
	    try {
	        boolean isUpdate = tipoContadorDTO.getId() != null && tipoContadorRepository.existsById(tipoContadorDTO.getId());
	        TipoContadorEntity entity;

	        if (isUpdate) {
	            entity = tipoContadorRepository.findById(tipoContadorDTO.getId()).orElseThrow();
	            tipoContadorMapper.updateEntityFromDto(tipoContadorDTO, entity);
	            entity.setFechaModificacion(new Date());
	            entity.setUsuarioModificacion(tipoContadorDTO.getUsuarioModificacion());
	        } else {
	            entity = tipoContadorMapper.dtoToEntity(tipoContadorDTO);
	            entity.setFechaCreacion(new Date());
	            entity.setUsuarioCreacion(tipoContadorDTO.getUsuarioCreacion());
	            entity.setActivo(true);
	        }

	        TipoContadorEntity saved = tipoContadorRepository.save(entity);
	        TipoContadorDTO savedDTO = tipoContadorMapper.entityToDto(saved);

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
	        log.error("Error guardando el tipo de contador", e);
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
	    log.info("Buscar tipo de contador por id: {}", id);
	    try {
	        Optional<TipoContadorEntity> ciudad = tipoContadorRepository.findById(id);
	        if (ciudad.isPresent()) {
	        	TipoContadorDTO dto = tipoContadorMapper.entityToDto(ciudad.get());
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
	        log.error("Error al buscar tipo contador por id: {}", id, e);
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
        log.info("Listar todos los tipos de contadores");
        try {
            var list = tipoContadorRepository.findAll();
            var dtoList = tipoContadorMapper.listEntityToDtoList(list);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(true)
                    .message(Constantes.CONSULTED_SUCCESSFULLY)
                    .code(HttpStatus.OK.value())
                    .response(dtoList)
                    .build();
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("Error al listar los tipos de contadores", e);
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
        log.info("Inicio método para eliminar tipo de contador por id: {}", id);
        try {
            if (!tipoContadorRepository.existsById(id)) {
                ResponseDTO responseDTO = ResponseDTO.builder()
                        .success(false)
                        .message(Constantes.RECORD_NOT_FOUND)
                        .code(HttpStatus.NOT_FOUND.value())
                        .build();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
            }
            tipoContadorRepository.deleteById(id);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(true)
                    .message(Constantes.DELETED_SUCCESSFULLY)
                    .code(HttpStatus.OK.value())
                    .build();
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("Error al eliminar el tipo de contador con id: {}", id, e);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(false)
                    .message(Constantes.DELETE_ERROR)
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
    }
}
