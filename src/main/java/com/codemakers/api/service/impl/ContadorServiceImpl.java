package com.codemakers.api.service.impl;

import java.util.Date;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.codemakers.api.service.IContadorService;
import com.codemakers.commons.dtos.ContadorDTO;
import com.codemakers.commons.dtos.ResponseDTO;
import com.codemakers.commons.entities.ContadorEntity;
import com.codemakers.commons.entities.DireccionEntity;
import com.codemakers.commons.entities.PersonaEntity;
import com.codemakers.commons.entities.TipoContadorEntity;
import com.codemakers.commons.maps.ContadorMapper;
import com.codemakers.commons.repositories.ContadorRepository;
import com.codemakers.commons.repositories.DireccionRepository;
import com.codemakers.commons.repositories.PersonaRepository;
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
public class ContadorServiceImpl implements IContadorService {

	private final ContadorRepository contadorRepository;
	private final PersonaRepository personaRepository;
	private final TipoContadorRepository tipoContadorRepository;
	private final DireccionRepository direccionRepository;
	private final ContadorMapper contadorMapper;
	
	@Override
	public ResponseEntity<ResponseDTO> save(ContadorDTO contadorDTO) {
	    log.info("Guardar/Actualizar contador");
	    try {
	        boolean isUpdate = contadorDTO.getId() != null && contadorRepository.existsById(contadorDTO.getId());
	        ContadorEntity entity;
	        log.info("exite id contador:{} ",contadorDTO.getId());
	        if (isUpdate) {
	            entity = contadorRepository.findById(contadorDTO.getId()).orElseThrow();
	            contadorMapper.updateEntityFromDto(contadorDTO, entity);
	            entity.setFechaModificacion(new Date());
	            entity.setUsuarioModificacion(contadorDTO.getUsuarioModificacion());
	        } else {
	            entity = contadorMapper.dtoToEntity(contadorDTO);
	            entity.setFechaCreacion(new Date());
	            entity.setUsuarioCreacion(contadorDTO.getUsuarioCreacion());
	            entity.setActivo(true);
	        }
	        
	        if (contadorDTO.getCliente() != null && contadorDTO.getCliente().getId() != null) {
	            PersonaEntity cliente = personaRepository
	                .findById(contadorDTO.getCliente().getId())
	                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
	            entity.setCliente(cliente);
	        }
	        
	        if (contadorDTO.getTipoContador() != null && contadorDTO.getTipoContador().getId() != null) {
	            TipoContadorEntity tipoContador = tipoContadorRepository
	                .findById(contadorDTO.getTipoContador().getId())
	                .orElseThrow(() -> new RuntimeException("Tipo de contador no encontrado"));
	            entity.setTipoContador(tipoContador);
	        }

	        if (contadorDTO.getDescripcion() != null && contadorDTO.getDescripcion().getId() != null) {
	            DireccionEntity direccion = direccionRepository
	                .findById(contadorDTO.getDescripcion().getId())
	                .orElseThrow(() -> new RuntimeException("Direccion no encontrada"));
	            entity.setDescripcion(direccion);
	        }

	        ContadorEntity saved = contadorRepository.save(entity);
	        ContadorDTO savedDTO = contadorMapper.entityToDto(saved);

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
	        log.error("Error guardando contador", e);
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
	    log.info("Buscar contador por id: {}", id);
	    try {
	        Optional<ContadorEntity> contador = contadorRepository.findById(id);
	        if (contador.isPresent()) {
	        	ContadorDTO dto = contadorMapper.entityToDto(contador.get());
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
	        log.error("Error al buscar contador por id: {}", id, e);
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
        log.info("Listar todos los contadores");
        try {
            var list = contadorRepository.findAll();
            var dtoList = contadorMapper.listEntityToDtoList(list);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(true)
                    .message(Constantes.CONSULTED_SUCCESSFULLY)
                    .code(HttpStatus.OK.value())
                    .response(dtoList)
                    .build();
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("Error al listar los contadores", e);
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
        log.info("Inicio método para eliminar contador por id: {}", id);
        try {
            if (!contadorRepository.existsById(id)) {
                ResponseDTO responseDTO = ResponseDTO.builder()
                        .success(false)
                        .message(Constantes.RECORD_NOT_FOUND)
                        .code(HttpStatus.NOT_FOUND.value())
                        .build();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
            }
            contadorRepository.deleteById(id);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(true)
                    .message(Constantes.DELETED_SUCCESSFULLY)
                    .code(HttpStatus.OK.value())
                    .build();
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("Error al eliminar el contador con id: {}", id, e);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(false)
                    .message(Constantes.DELETE_ERROR)
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
    }
}
