package com.codemakers.api.service;

import org.springframework.http.ResponseEntity;

import com.codemakers.commons.dtos.ResponseDTO;
import com.codemakers.commons.dtos.RutaEmpleadoDTO;

public interface IRutaEmpleadoService {
    
	ResponseEntity<ResponseDTO> save(RutaEmpleadoDTO rutaEmpleadoDTO);
	
	ResponseEntity<ResponseDTO> update(RutaEmpleadoDTO rutaEmpleadoDTO); 
	
    ResponseEntity<ResponseDTO> findById(Integer id);
    
    ResponseEntity<ResponseDTO> findAll();
    
    ResponseEntity<ResponseDTO> deleteById(Integer id);
}
