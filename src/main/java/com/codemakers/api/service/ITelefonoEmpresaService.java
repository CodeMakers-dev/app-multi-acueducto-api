package com.codemakers.api.service;

import org.springframework.http.ResponseEntity;

import com.codemakers.commons.dtos.ResponseDTO;
import com.codemakers.commons.dtos.TelefonoEmpresaDTO;

public interface ITelefonoEmpresaService {
    
	ResponseEntity<ResponseDTO> save(TelefonoEmpresaDTO telefonoEmpresaDTO);
	
    ResponseEntity<ResponseDTO> findById(Integer id);
    
    ResponseEntity<ResponseDTO> findAll();
    
    ResponseEntity<ResponseDTO> deleteById(Integer id);
}
