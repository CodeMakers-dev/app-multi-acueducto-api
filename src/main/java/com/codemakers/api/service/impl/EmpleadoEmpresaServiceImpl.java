package com.codemakers.api.service.impl;

import java.util.Date;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codemakers.api.service.IEmpleadoEmpresaService;
import com.codemakers.commons.dtos.EmpleadoEmpresaDTO;
import com.codemakers.commons.dtos.ResponseDTO;
import com.codemakers.commons.entities.EmpleadoEmpresaEntity;
import com.codemakers.commons.entities.EmpresaEntity;
import com.codemakers.commons.entities.PersonaEntity;
import com.codemakers.commons.maps.EmpleadoEmpresaMapper;
import com.codemakers.commons.repositories.EmpleadoEmpresaRepository;
import com.codemakers.commons.repositories.EmpresaRepository;
import com.codemakers.commons.repositories.PersonaRepository;
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
public class EmpleadoEmpresaServiceImpl implements IEmpleadoEmpresaService {

	private final EmpleadoEmpresaRepository empleadoEmpresaRepository;
	private final EmpresaRepository empresaRepository;
	private final PersonaRepository personaRepository;
	private final EmpleadoEmpresaMapper empleadoEmpresaMapper;
	
	@Override
	@Transactional
	public ResponseEntity<ResponseDTO> save(EmpleadoEmpresaDTO empleadoEmpresaDTO) {
	    log.info("Guardar/Actualizar Empleado Empresa");
	    try {
	        boolean isUpdate = empleadoEmpresaDTO.getId() != null && empleadoEmpresaRepository.existsById(empleadoEmpresaDTO.getId());
	        EmpleadoEmpresaEntity entity;

	        if (isUpdate) {
	            entity = empleadoEmpresaRepository.findById(empleadoEmpresaDTO.getId()).orElseThrow();
	            empleadoEmpresaMapper.updateEntityFromDto(empleadoEmpresaDTO, entity);
	            entity.setFechaModificacion(new Date());
	            entity.setUsuarioModificacion(empleadoEmpresaDTO.getUsuarioModificacion());
	        } else {
	            entity = empleadoEmpresaMapper.dtoToEntity(empleadoEmpresaDTO);
	            entity.setFechaCreacion(new Date());
	            entity.setUsuarioCreacion(empleadoEmpresaDTO.getUsuarioCreacion());
	            entity.setActivo(true);
	        }
	        
	        if (empleadoEmpresaDTO.getEmpresa() != null && empleadoEmpresaDTO.getEmpresa().getId() != null) {
	            EmpresaEntity empresa = empresaRepository
	                .findById(empleadoEmpresaDTO.getEmpresa().getId())
	                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));
	            entity.setEmpresa(empresa);
	        }
	        
	        if (empleadoEmpresaDTO.getPersona() != null && empleadoEmpresaDTO.getPersona().getId() != null) {
	            PersonaEntity persona = personaRepository
	                .findById(empleadoEmpresaDTO.getPersona().getId())
	                .orElseThrow(() -> new RuntimeException("Ciudad no encontrado"));
	            entity.setPersona(persona);
	        }

	        EmpleadoEmpresaEntity saved = empleadoEmpresaRepository.save(entity);
	        EmpleadoEmpresaDTO savedDTO = empleadoEmpresaMapper.entityToDto(saved);

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
	        log.error("Error guardando empleado empresa", e);
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
	    log.info("Buscar empleado empresa por id: {}", id);
	    try {
	        Optional<EmpleadoEmpresaEntity> empleadoEmpresa = empleadoEmpresaRepository.findById(id);
	        if (empleadoEmpresa.isPresent()) {
	        	EmpleadoEmpresaDTO dto = empleadoEmpresaMapper.entityToDto(empleadoEmpresa.get());
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
	        log.error("Error al buscar empleado empresa por id: {}", id, e);
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
        log.info("Listar todos los empleado empresa");
        try {
            var list = empleadoEmpresaRepository.findAll();
            var dtoList = empleadoEmpresaMapper.listEntityToDtoList(list);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(true)
                    .message(Constantes.CONSULTED_SUCCESSFULLY)
                    .code(HttpStatus.OK.value())
                    .response(dtoList)
                    .build();
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("Error al listar los empleados empresas", e);
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
        log.info("Inicio método para eliminar empelado empresa por id: {}", id);
        try {
            if (!empleadoEmpresaRepository.existsById(id)) {
                ResponseDTO responseDTO = ResponseDTO.builder()
                        .success(false)
                        .message(Constantes.RECORD_NOT_FOUND)
                        .code(HttpStatus.NOT_FOUND.value())
                        .build();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
            }
            empleadoEmpresaRepository.deleteById(id);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(true)
                    .message(Constantes.DELETED_SUCCESSFULLY)
                    .code(HttpStatus.OK.value())
                    .build();
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("Error al eliminar el empleado empresa con id: {}", id, e);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(false)
                    .message(Constantes.DELETE_ERROR)
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
    }
}
