package com.codemakers.api.service.impl;

import java.util.Date;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codemakers.api.service.ITelefonoEmpresaService;
import com.codemakers.commons.dtos.ResponseDTO;
import com.codemakers.commons.dtos.TelefonoEmpresaDTO;
import com.codemakers.commons.entities.TelefonoEmpresaEntity;
import com.codemakers.commons.maps.TelefonoEmpresaMapper;
import com.codemakers.commons.repositories.TelefonoEmpresaRepository;
import com.codemakers.commons.utils.Constantes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelefonoEmpresaServiceImpl implements ITelefonoEmpresaService{
	
	private final TelefonoEmpresaRepository telefonoEmpresaRepository;
	private final TelefonoEmpresaMapper telefonoEmpresaMapper;
	
	@Override
	@Transactional
	public ResponseEntity<ResponseDTO> save(TelefonoEmpresaDTO telefonoEmpresaDTO) {
	    log.info("Guardar/Actualizar Telefono de Empresa");
	    try {
	        boolean isUpdate = telefonoEmpresaDTO.getId() != null && telefonoEmpresaRepository.existsById(telefonoEmpresaDTO.getId());
	        TelefonoEmpresaEntity entity;

	        if (isUpdate) {
	            entity = telefonoEmpresaRepository.findById(telefonoEmpresaDTO.getId())
	                    .orElseThrow(() -> new RuntimeException("TelefonoEmpresa not found with ID: " + telefonoEmpresaDTO.getId()));

	            if (!entity.getNumero().equals(telefonoEmpresaDTO.getNumero()) &&
	                telefonoEmpresaRepository.existsByNumero(telefonoEmpresaDTO.getNumero())) {
	                
	                log.warn("El número de teléfono {} ya existe en el sistema.", telefonoEmpresaDTO.getNumero());
	                ResponseDTO errorResponse = ResponseDTO.builder()
	                        .success(false)
	                        .message(Constantes.DUPLICATE_PHONE_NUMBER_ERROR)
	                        .code(HttpStatus.CONFLICT.value())
	                        .build();
	                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
	            }

	            telefonoEmpresaMapper.updateEntityFromDto(telefonoEmpresaDTO, entity);
	            entity.setFechaModificacion(new Date());
	            entity.setUsuarioModificacion(telefonoEmpresaDTO.getUsuarioModificacion());
	        } else {
	            if (telefonoEmpresaRepository.existsByNumero(telefonoEmpresaDTO.getNumero())) {
	                log.warn("El número de teléfono {} ya existe en el sistema.", telefonoEmpresaDTO.getNumero());
	                ResponseDTO errorResponse = ResponseDTO.builder()
	                        .success(false)
	                        .message(Constantes.DUPLICATE_PHONE_NUMBER_ERROR)
	                        .code(HttpStatus.CONFLICT.value())
	                        .build();
	                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
	            }

	            entity = telefonoEmpresaMapper.dtoToEntity(telefonoEmpresaDTO);
	            entity.setFechaCreacion(new Date());
	            entity.setUsuarioCreacion(telefonoEmpresaDTO.getUsuarioCreacion());
	            entity.setActivo(true);
	        }

	        TelefonoEmpresaEntity saved = telefonoEmpresaRepository.save(entity);
	        TelefonoEmpresaDTO savedDTO = telefonoEmpresaMapper.entityToDto(saved);

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
	        log.error("Error guardando el Telefono de Empresa", e);
	        ResponseDTO errorResponse = ResponseDTO.builder()
	                .success(false)
	                .message(Constantes.SAVE_ERROR)
	                .code(HttpStatus.BAD_REQUEST.value())
	                .build();

	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	    }
	}


	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<ResponseDTO> findById(Integer id) {
	    log.info("Buscar Telefono de Empresa por id: {}", id);
	    try {
	        Optional<TelefonoEmpresaEntity> telefonoEmpresa = telefonoEmpresaRepository.findById(id);
	        if (telefonoEmpresa.isPresent()) {
	        	TelefonoEmpresaDTO dto = telefonoEmpresaMapper.entityToDto(telefonoEmpresa.get());
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
	        log.error("Error al buscar Telefono de Empresa por id: {}", id, e);
	        ResponseDTO responseDTO = ResponseDTO.builder()
	                .success(false)
	                .message(Constantes.ERROR_QUERY_RECORD_BY_ID)
	                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
	                .build();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
	    }
	}

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ResponseDTO> findAll() {
        log.info("Listar todos Telefono de Empresa");
        try {
            var list = telefonoEmpresaRepository.findAll();
            var dtoList = telefonoEmpresaMapper.listEntityToDtoList(list);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(true)
                    .message(Constantes.CONSULTED_SUCCESSFULLY)
                    .code(HttpStatus.OK.value())
                    .response(dtoList)
                    .build();
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("Error al listar Telefono de Empresa", e);
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
    @Transactional
    public ResponseEntity<ResponseDTO> deleteById(Integer id) {
        log.info("Inicio método para eliminar Telefono de Empresa por id: {}", id);
        try {
            if (!telefonoEmpresaRepository.existsById(id)) {
                ResponseDTO responseDTO = ResponseDTO.builder()
                        .success(false)
                        .message(Constantes.RECORD_NOT_FOUND)
                        .code(HttpStatus.NOT_FOUND.value())
                        .build();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
            }
            telefonoEmpresaRepository.deleteById(id);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(true)
                    .message(Constantes.DELETED_SUCCESSFULLY)
                    .code(HttpStatus.OK.value())
                    .build();
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("Error al eliminar Telefono de Empresa con id: {}", id, e);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(false)
                    .message(Constantes.DELETE_ERROR)
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
    }
}
