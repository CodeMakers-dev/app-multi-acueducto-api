package com.codemakers.api.service;

import org.springframework.http.ResponseEntity;

import com.codemakers.commons.dtos.ParametrosSistemaDTO;
import com.codemakers.commons.dtos.ResponseDTO;

public interface IParametrosSistemaService {
	   
	    ResponseEntity<ResponseDTO> save(ParametrosSistemaDTO parametrosSistemaDTO);
		
	    ResponseEntity<ResponseDTO> findById(Integer id);
	    
	    ResponseEntity<ResponseDTO> findAll();
	    
	    ResponseEntity<ResponseDTO> deleteById(Integer id);
}
