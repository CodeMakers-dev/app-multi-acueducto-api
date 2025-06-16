package com.codemakers.api.service;

import org.springframework.http.ResponseEntity;

import com.codemakers.commons.dtos.CorreoEmpresaDTO;
import com.codemakers.commons.dtos.ResponseDTO;

public interface ICorreoEmpresaService {
    
	ResponseEntity<ResponseDTO> save(CorreoEmpresaDTO correoEmpresaDTO);
	
    ResponseEntity<ResponseDTO> findById(Integer id);
    
    ResponseEntity<ResponseDTO> findAll();
    
    ResponseEntity<ResponseDTO> deleteById(Integer id);
}
