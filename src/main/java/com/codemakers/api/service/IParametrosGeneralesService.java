package com.codemakers.api.service;

import org.springframework.http.ResponseEntity;

import com.codemakers.commons.dtos.ParametrosGeneralesDTO;
import com.codemakers.commons.dtos.ResponseDTO;

public interface IParametrosGeneralesService {
	
	ResponseEntity<ResponseDTO> save(ParametrosGeneralesDTO parametrosGeneralesDTO);
	
    ResponseEntity<ResponseDTO> findById(Integer id);
    
    ResponseEntity<ResponseDTO> findAll();
    
    ResponseEntity<ResponseDTO> deleteById(Integer id);
}
