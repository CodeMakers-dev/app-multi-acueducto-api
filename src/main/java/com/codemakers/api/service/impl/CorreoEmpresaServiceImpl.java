package com.codemakers.api.service.impl;

import java.util.Date;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.codemakers.api.service.ICorreoEmpresaService;
import com.codemakers.commons.dtos.CorreoEmpresaDTO;
import com.codemakers.commons.dtos.ResponseDTO;
import com.codemakers.commons.entities.CorreoEmpresaEntity;
import com.codemakers.commons.maps.CorreoEmpresaMapper;
import com.codemakers.commons.repositories.CorreoEmpresaRepository;
import com.codemakers.commons.utils.Constantes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CorreoEmpresaServiceImpl implements ICorreoEmpresaService{
	
	private final CorreoEmpresaRepository correoEmpresaRepository;
	private final CorreoEmpresaMapper correoEmpresaMapper;
	
	@Override
	public ResponseEntity<ResponseDTO> save(CorreoEmpresaDTO correoEmpresaDTO) {
	    log.info("Guardar/Actualizar Correo Empresa");
	    try {
	        boolean isUpdate = correoEmpresaDTO.getId() != null && correoEmpresaRepository.existsById(correoEmpresaDTO.getId());

	        Optional<CorreoEmpresaEntity> existingCorreo = correoEmpresaRepository.findByCorreoIgnoreCase(correoEmpresaDTO.getCorreo());

	        if (existingCorreo.isPresent()) {
	            if (!isUpdate || !existingCorreo.get().getId().equals(correoEmpresaDTO.getId())) {
	                ResponseDTO errorResponse = ResponseDTO.builder()
	                        .success(false)
	                        .message("El correo '" + correoEmpresaDTO.getCorreo() + "' ya se encuentra registrado.")
	                        .code(HttpStatus.BAD_REQUEST.value())
	                        .build();
	                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	            }
	        }
	        CorreoEmpresaEntity entity;
	        if (isUpdate) {
	            entity = correoEmpresaRepository.findById(correoEmpresaDTO.getId()).orElseThrow();
	            correoEmpresaMapper.updateEntityFromDto(correoEmpresaDTO, entity);
	            entity.setFechaModificacion(new Date());
	            entity.setUsuarioModificacion(correoEmpresaDTO.getUsuarioModificacion());
	        } else {
	            entity = correoEmpresaMapper.dtoToEntity(correoEmpresaDTO);
	            entity.setFechaCreacion(new Date());
	            entity.setUsuarioCreacion(correoEmpresaDTO.getUsuarioCreacion());
	            entity.setActivo(true);
	        }

	        CorreoEmpresaEntity saved = correoEmpresaRepository.save(entity);
	        CorreoEmpresaDTO savedDTO = correoEmpresaMapper.entityToDto(saved);

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
	        log.error("Error guardando el Correo Empresa", e);
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
	    log.info("Buscar Correo Empresa por id: {}", id);
	    try {
	        Optional<CorreoEmpresaEntity> correoEmpresa = correoEmpresaRepository.findById(id);
	        if (correoEmpresa.isPresent()) {
	        	CorreoEmpresaDTO dto = correoEmpresaMapper.entityToDto(correoEmpresa.get());
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
	        log.error("Error al buscar Correo Empresa por id: {}", id, e);
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
        log.info("Listar todos los Correo Empresa");
        try {
            var list = correoEmpresaRepository.findAll();
            var dtoList = correoEmpresaMapper.listEntityToDtoList(list);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(true)
                    .message(Constantes.CONSULTED_SUCCESSFULLY)
                    .code(HttpStatus.OK.value())
                    .response(dtoList)
                    .build();
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("Error al listar los Correo Empresa", e);
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
        log.info("Inicio método para eliminar Correo Empresa por id: {}", id);
        try {
            if (!correoEmpresaRepository.existsById(id)) {
                ResponseDTO responseDTO = ResponseDTO.builder()
                        .success(false)
                        .message(Constantes.RECORD_NOT_FOUND)
                        .code(HttpStatus.NOT_FOUND.value())
                        .build();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
            }
            correoEmpresaRepository.deleteById(id);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(true)
                    .message(Constantes.DELETED_SUCCESSFULLY)
                    .code(HttpStatus.OK.value())
                    .build();
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("Error al eliminar el Correo Empresa con id: {}", id, e);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(false)
                    .message(Constantes.DELETE_ERROR)
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
    }
}