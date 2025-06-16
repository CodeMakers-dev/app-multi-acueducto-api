package com.codemakers.api.service;

import org.springframework.http.ResponseEntity;

import com.codemakers.commons.dtos.CorreoPersonaDTO;
import com.codemakers.commons.dtos.ResponseDTO;

public interface ICorreoPersonaService {

	ResponseEntity<ResponseDTO> save(CorreoPersonaDTO correoPersonaDTO);
	
    ResponseEntity<ResponseDTO> findById(Integer id);
    
    ResponseEntity<ResponseDTO> findAll();
    
    ResponseEntity<ResponseDTO> deleteById(Integer id);
}
