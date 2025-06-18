package com.codemakers.api.service.impl;

import java.util.Date;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service; 

import com.codemakers.api.service.IRutaEmpleadoService;
import com.codemakers.commons.dtos.ResponseDTO;
import com.codemakers.commons.dtos.RutaEmpleadoDTO;
import com.codemakers.commons.entities.RutaEmpleadoEntity;
import com.codemakers.commons.maps.RutaEmpleadoMapper;
import com.codemakers.commons.repositories.RutaEmpleadoRepository;
import com.codemakers.commons.utils.Constantes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RutaEmpleadoServiceImpl implements IRutaEmpleadoService{
	
	private final RutaEmpleadoRepository rutaEmpleadoRepository;
	private final RutaEmpleadoMapper rutaEmpleadoMapper;
	
	@Override
    public ResponseEntity<ResponseDTO> save(RutaEmpleadoDTO rutaEmpleadoDTO) {
        log.info("Creando Ruta Empleado");
        try {
        	RutaEmpleadoEntity entity = rutaEmpleadoMapper.dtoToEntity(rutaEmpleadoDTO);
            entity.setFechaCreacion(new Date());
            entity.setUsuarioCreacion(rutaEmpleadoDTO.getUsuarioCreacion());
            entity.setActivo(true);

            RutaEmpleadoEntity saved = rutaEmpleadoRepository.save(entity);
            RutaEmpleadoDTO savedDTO = rutaEmpleadoMapper.entityToDto(saved);

            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(true)
                    .message(Constantes.SAVED_SUCCESSFULLY)
                    .code(HttpStatus.CREATED.value())
                    .response(savedDTO)
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (Exception e) {
            log.error("Error creando la Ruta Empleado", e);
            ResponseDTO errorResponse = ResponseDTO.builder()
                    .success(false)
                    .message(Constantes.SAVE_ERROR)
                    .code(HttpStatus.BAD_REQUEST.value())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    
    @Override
    public ResponseEntity<ResponseDTO> update(RutaEmpleadoDTO rutaEmpleadoDTO) {
        log.info("Actualizando Ruta Empleado");
        try {
            if (rutaEmpleadoDTO.getId() == null || !rutaEmpleadoRepository.existsById(rutaEmpleadoDTO.getId())) {
                throw new IllegalArgumentException("La Ruta Empleado no existe.");
            }

            RutaEmpleadoEntity entity = rutaEmpleadoRepository.findById(rutaEmpleadoDTO.getId()).orElseThrow();
            rutaEmpleadoMapper.updateEntityFromDto(rutaEmpleadoDTO, entity); 
            entity.setFechaModificacion(new Date());
            entity.setUsuarioModificacion(rutaEmpleadoDTO.getUsuarioModificacion());

            RutaEmpleadoEntity updated = rutaEmpleadoRepository.save(entity);
            RutaEmpleadoDTO updatedDTO = rutaEmpleadoMapper.entityToDto(updated);

            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(true)
                    .message(Constantes.UPDATED_SUCCESSFULLY)
                    .code(HttpStatus.OK.value())
                    .response(updatedDTO)
                    .build();

            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } catch (Exception e) {
            log.error("Error actualizando la Ruta Empleado", e);
            ResponseDTO errorResponse = ResponseDTO.builder()
                    .success(false)
                    .message(Constantes.UPDATE_ERROR)
                    .code(HttpStatus.BAD_REQUEST.value())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

	@Override
	public ResponseEntity<ResponseDTO> findById(Integer id) {
	    log.info("Buscar Ruta Empleado por id: {}", id);
	    try {
	        Optional<RutaEmpleadoEntity> rutaEmpleado = rutaEmpleadoRepository.findById(id);
	        if (rutaEmpleado.isPresent()) {
	        	RutaEmpleadoDTO dto = rutaEmpleadoMapper.entityToDto(rutaEmpleado.get());
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
	        log.error("Error al buscar  Ruta Empleado por id: {}", id, e);
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
        log.info("Listar todos las Ruta Empleado");
        try {
            var list = rutaEmpleadoRepository.findAll();
            var dtoList = rutaEmpleadoMapper.listEntityToDtoList(list);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(true)
                    .message(Constantes.CONSULTED_SUCCESSFULLY)
                    .code(HttpStatus.OK.value())
                    .response(dtoList)
                    .build();
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("Error al listar las Ruta Empleado", e);
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
        log.info("Inicio método para eliminar Ruta Empleado por id: {}", id);
        try {
            if (!rutaEmpleadoRepository.existsById(id)) {
                ResponseDTO responseDTO = ResponseDTO.builder()
                        .success(false)
                        .message(Constantes.RECORD_NOT_FOUND)
                        .code(HttpStatus.NOT_FOUND.value())
                        .build();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
            }
            rutaEmpleadoRepository.deleteById(id);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(true)
                    .message(Constantes.DELETED_SUCCESSFULLY)
                    .code(HttpStatus.OK.value())
                    .build();
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("Error al eliminar Ruta Empleado con id: {}", id, e);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(false)
                    .message(Constantes.DELETE_ERROR)
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
    }
}
