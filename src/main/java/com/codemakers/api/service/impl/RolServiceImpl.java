package com.codemakers.api.service.impl;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.codemakers.api.service.IRolService;
import com.codemakers.commons.dtos.ResponseDTO;
import com.codemakers.commons.dtos.RolDTO;
import com.codemakers.commons.entities.RolEntity;
import com.codemakers.commons.maps.RolMapper;
import com.codemakers.commons.repositories.RolRepository;
import com.codemakers.commons.utils.Constantes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RolServiceImpl implements IRolService {

	private final RolRepository rolRepository;
	
	@Override
	public ResponseEntity<ResponseDTO> save(RolDTO rolDTO) {
	    log.info("Inicio guardar rol");
	    try {
	        RolEntity rolEntity = RolMapper.INSTANCE.dtoToEntity(rolDTO);
	        rolEntity.setFechaCreacion(new Date());
	        rolEntity.setActivo(true);

	        RolEntity savedEntity = rolRepository.save(rolEntity);

	        RolDTO savedDTO = RolMapper.INSTANCE.entityToDto(savedEntity);

	        log.info("Fin guardar rol");

	        ResponseDTO responseDTO = ResponseDTO.builder()
	                .success(true)
	                .message(Constantes.SAVED_SUCCESSFULLY)
	                .code(HttpStatus.CREATED.value())
	                .response(savedDTO)
	                .build();

	        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

	    } catch (Exception e) {
	        log.error("Error al guardar rol", e);
	        ResponseDTO errorResponse = ResponseDTO.builder()
	                .success(false)
	                .message(Constantes.SAVE_ERROR)
	                .code(HttpStatus.BAD_REQUEST.value())
	                .build();

	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	    }
	}
}
