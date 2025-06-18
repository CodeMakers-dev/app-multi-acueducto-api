package com.codemakers.api.service.impl;

import java.util.Date;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.codemakers.api.service.IDeudaClienteService;
import com.codemakers.commons.dtos.DeudaClienteDTO;
import com.codemakers.commons.dtos.ResponseDTO;
import com.codemakers.commons.entities.DeudaClienteEntity;
import com.codemakers.commons.maps.DeudaClienteMapper;
import com.codemakers.commons.repositories.DeudaClienteRepository;
import com.codemakers.commons.utils.Constantes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeudaClienteServiceImpl implements IDeudaClienteService{
	
	private final DeudaClienteRepository deudaClienteRepository;
	private final DeudaClienteMapper deudaClienteMapper;
	
	@Override
	public ResponseEntity<ResponseDTO> save(DeudaClienteDTO deudaClienteDTO) {
	    log.info("Guardar/Actualizar Deuda de Cliente");
	    try {
	        boolean isUpdate = deudaClienteDTO.getId() != null && deudaClienteRepository.existsById(deudaClienteDTO.getId());
	        DeudaClienteEntity entity;

	        if (isUpdate) {
	            entity = deudaClienteRepository.findById(deudaClienteDTO.getId()).orElseThrow();
	            deudaClienteMapper.updateEntityFromDto(deudaClienteDTO, entity);
	            entity.setFechaModificacion(new Date());
	            entity.setUsuarioModificacion(deudaClienteDTO.getUsuarioModificacion());
	        } else {
	            entity = deudaClienteMapper.dtoToEntity(deudaClienteDTO);
	            entity.setFechaCreacion(new Date());
	            entity.setUsuarioCreacion(deudaClienteDTO.getUsuarioCreacion());
	            entity.setActivo(true);
	        }

	        DeudaClienteEntity saved = deudaClienteRepository.save(entity);
	        DeudaClienteDTO savedDTO = deudaClienteMapper.entityToDto(saved);

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
	        log.error("Error guardando Deuda de Cliente", e);
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
	    log.info("Buscar Deuda de Cliente por id: {}", id);
	    try {
	        Optional<DeudaClienteEntity> deudaCliente = deudaClienteRepository.findById(id);
	        if (deudaCliente.isPresent()) {
	        	DeudaClienteDTO dto = deudaClienteMapper.entityToDto(deudaCliente.get());
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
	        log.error("Error al buscar Deuda de Cliente por id: {}", id, e);
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
        log.info("Listar todos las Deudas de Cliente");
        try {
            var list = deudaClienteRepository.findAll();
            var dtoList = deudaClienteMapper.listEntityToDtoList(list);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(true)
                    .message(Constantes.CONSULTED_SUCCESSFULLY)
                    .code(HttpStatus.OK.value())
                    .response(dtoList)
                    .build();
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("Error al listar las Deudas de Cliente", e);
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
        log.info("Inicio método para eliminar Deuda de Cliente por id: {}", id);
        try {
            if (!deudaClienteRepository.existsById(id)) {
                ResponseDTO responseDTO = ResponseDTO.builder()
                        .success(false)
                        .message(Constantes.RECORD_NOT_FOUND)
                        .code(HttpStatus.NOT_FOUND.value())
                        .build();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
            }
            deudaClienteRepository.deleteById(id);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(true)
                    .message(Constantes.DELETED_SUCCESSFULLY)
                    .code(HttpStatus.OK.value())
                    .build();
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("Error al eliminar Deuda de Cliente con id: {}", id, e);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(false)
                    .message(Constantes.DELETE_ERROR)
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
    }
}
