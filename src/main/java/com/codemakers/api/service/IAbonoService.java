package com.codemakers.api.service;

import org.springframework.http.ResponseEntity;

import com.codemakers.commons.dtos.AbonoDTO;
import com.codemakers.commons.dtos.ResponseDTO;

public interface IAbonoService {
	
	ResponseEntity<ResponseDTO> save(AbonoDTO abonoDTO);
	
	ResponseEntity<ResponseDTO> update(AbonoDTO abonoDTO);
	
    ResponseEntity<ResponseDTO> findById(Integer id);
    
    ResponseEntity<ResponseDTO> findAll();
    
    ResponseEntity<ResponseDTO> deleteById(Integer id);
}
