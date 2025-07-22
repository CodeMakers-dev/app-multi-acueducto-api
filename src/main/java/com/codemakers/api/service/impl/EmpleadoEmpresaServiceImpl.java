package com.codemakers.api.service.impl;

import java.util.Date;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codemakers.api.service.IEmpleadoEmpresaService;
import com.codemakers.commons.dtos.DireccionDTO;
import com.codemakers.commons.dtos.EmpleadoEmpresaDTO;
import com.codemakers.commons.dtos.PersonaDTO;
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
	private final PersonaServiceImpl personaServiceImpl;
	private final DireccionServiceImpl direccionServiceImpl;
	

	@Override
	@Transactional
	public ResponseEntity<ResponseDTO> save(EmpleadoEmpresaDTO empleadoEmpresaDTO) {
	    log.info("Guardar/Actualizar Empleado Empresa");

	    try {
	        boolean isUpdate = isUpdate(empleadoEmpresaDTO);
	        EmpleadoEmpresaEntity entity = buildEmpleadoEmpresaEntity(empleadoEmpresaDTO, isUpdate);

	        ResponseEntity<ResponseDTO> direccionResponse = guardarDireccionSiExiste(empleadoEmpresaDTO.getPersona());
	        if (direccionResponse != null) return direccionResponse;

	        ResponseEntity<ResponseDTO> personaResponse = guardarPersonaSiExiste(empleadoEmpresaDTO.getPersona(), entity);
	        if (personaResponse != null) return personaResponse;

	        setEmpresaSiExiste(empleadoEmpresaDTO, entity);

	        EmpleadoEmpresaEntity saved = empleadoEmpresaRepository.save(entity);
	        EmpleadoEmpresaDTO savedDTO = empleadoEmpresaMapper.entityToDto(saved);

	        String message = isUpdate ? Constantes.UPDATED_SUCCESSFULLY : Constantes.SAVED_SUCCESSFULLY;
	        int statusCode = isUpdate ? HttpStatus.OK.value() : HttpStatus.CREATED.value();

	        return ResponseEntity.status(statusCode).body(
	                ResponseDTO.builder().success(true).message(message).code(statusCode).response(savedDTO).build());

	    } catch (Exception e) {
	        log.error("Error guardando empleado empresa", e);
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
	                ResponseDTO.builder().success(false).message(Constantes.SAVE_ERROR)
	                        .code(HttpStatus.BAD_REQUEST.value()).build());
	    }
	}
	private boolean isUpdate(EmpleadoEmpresaDTO dto) {
	    return dto.getId() != null && empleadoEmpresaRepository.existsById(dto.getId());
	}

	private EmpleadoEmpresaEntity buildEmpleadoEmpresaEntity(EmpleadoEmpresaDTO dto, boolean isUpdate) {
	    EmpleadoEmpresaEntity entity;
	    if (isUpdate) {
	        entity = empleadoEmpresaRepository.findById(dto.getId()).orElseThrow();
	        empleadoEmpresaMapper.updateEntityFromDto(dto, entity);
	        entity.setFechaModificacion(new Date());
	        entity.setUsuarioModificacion(dto.getUsuarioModificacion());
	    } else {
	        entity = empleadoEmpresaMapper.dtoToEntity(dto);
	        entity.setFechaCreacion(new Date());
	        entity.setUsuarioCreacion(dto.getUsuarioCreacion());
	        entity.setActivo(true);
	    }
	    return entity;
	}

	private ResponseEntity<ResponseDTO> guardarDireccionSiExiste(PersonaDTO personaDTO) {
	    if (personaDTO != null && personaDTO.getDireccion() != null) {
	        ResponseEntity<ResponseDTO> response = direccionServiceImpl.save(personaDTO.getDireccion());
	        if (!Boolean.TRUE.equals(response.getBody().getSuccess())) {
	            return response;
	        }
	        DireccionDTO direccionGuardada = (DireccionDTO) response.getBody().getResponse();
	        personaDTO.setDireccion(direccionGuardada);
	    }
	    return null;
	}

	private ResponseEntity<ResponseDTO> guardarPersonaSiExiste(PersonaDTO personaDTO, EmpleadoEmpresaEntity entity) {
	    if (personaDTO != null) {
	        ResponseEntity<ResponseDTO> response = personaServiceImpl.save(personaDTO);
	        if (!Boolean.TRUE.equals(response.getBody().getSuccess())) {
	            return response;
	        }
	        PersonaDTO personaGuardada = (PersonaDTO) response.getBody().getResponse();
	        PersonaEntity personaEntity = personaRepository.findById(personaGuardada.getId())
	                .orElseThrow(() -> new RuntimeException("Persona no encontrada después de guardar"));
	        entity.setPersona(personaEntity);
	    }
	    return null;
	}

	private void setEmpresaSiExiste(EmpleadoEmpresaDTO dto, EmpleadoEmpresaEntity entity) {
	    if (dto.getEmpresa() != null && dto.getEmpresa().getId() != null) {
	        EmpresaEntity empresa = empresaRepository.findById(dto.getEmpresa().getId())
	                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));
	        entity.setEmpresa(empresa);
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
				ResponseDTO responseDTO = ResponseDTO.builder().success(true).message(Constantes.CONSULTED_SUCCESSFULLY)
						.code(HttpStatus.OK.value()).response(dto).build();
				return ResponseEntity.ok(responseDTO);
			} else {
				ResponseDTO responseDTO = ResponseDTO.builder().success(false).message(Constantes.CONSULTING_ERROR)
						.code(HttpStatus.NOT_FOUND.value()).build();
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
			}
		} catch (Exception e) {
			log.error("Error al buscar empleado empresa por id: {}", id, e);
			ResponseDTO responseDTO = ResponseDTO.builder().success(false).message(Constantes.ERROR_QUERY_RECORD_BY_ID)
					.code(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<ResponseDTO> findAll() {
		log.info("Listar todos los empleado empresa");
		try {
			var list = empleadoEmpresaRepository.findAll();
			var dtoList = empleadoEmpresaMapper.listEntityToResumenDtoList(list);
			ResponseDTO responseDTO = ResponseDTO.builder().success(true).message(Constantes.CONSULTED_SUCCESSFULLY)
					.code(HttpStatus.OK.value()).response(dtoList).build();
			return ResponseEntity.ok(responseDTO);
		} catch (Exception e) {
			log.error("Error al listar los empleados empresas", e);
			ResponseDTO responseDTO = ResponseDTO.builder().success(false).message(Constantes.CONSULTING_ERROR)
					.code(HttpStatus.INTERNAL_SERVER_ERROR.value()).response(null).build();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
		}
	}

	@Override
	@Transactional
	public ResponseEntity<ResponseDTO> deleteById(Integer id) {
		log.info("Inicio método para eliminar empelado empresa por id: {}", id);
		try {
			if (!empleadoEmpresaRepository.existsById(id)) {
				ResponseDTO responseDTO = ResponseDTO.builder().success(false).message(Constantes.RECORD_NOT_FOUND)
						.code(HttpStatus.NOT_FOUND.value()).build();
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
			}
			empleadoEmpresaRepository.deleteById(id);
			ResponseDTO responseDTO = ResponseDTO.builder().success(true).message(Constantes.DELETED_SUCCESSFULLY)
					.code(HttpStatus.OK.value()).build();
			return ResponseEntity.ok(responseDTO);
		} catch (Exception e) {
			log.error("Error al eliminar el empleado empresa con id: {}", id, e);
			ResponseDTO responseDTO = ResponseDTO.builder().success(false).message(Constantes.DELETE_ERROR)
					.code(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
		}
	}
	
	

}
