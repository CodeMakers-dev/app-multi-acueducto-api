package com.codemakers.api.service;

import org.springframework.http.ResponseEntity;

import com.codemakers.commons.dtos.CorreoGeneralDTO;
import com.codemakers.commons.dtos.ResponseDTO;

public interface ICorreoGeneralService {

	ResponseEntity<ResponseDTO> save(CorreoGeneralDTO correoGeneralDTO);
	
    ResponseEntity<ResponseDTO> findById(Integer id);
    
    ResponseEntity<ResponseDTO> findAll();
    
    ResponseEntity<ResponseDTO> deleteById(Integer id);
}
