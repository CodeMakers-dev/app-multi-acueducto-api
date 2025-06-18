package com.codemakers.api.service;

import org.springframework.http.ResponseEntity;

import com.codemakers.commons.dtos.EmpresaDTO;
import com.codemakers.commons.dtos.ResponseDTO;

public interface IEmpresaService {
	
	ResponseEntity<ResponseDTO> save(EmpresaDTO empresaDTO);
	
	ResponseEntity<ResponseDTO> update(EmpresaDTO empresaDTO); 
	
    ResponseEntity<ResponseDTO> findById(Integer id);
    
    ResponseEntity<ResponseDTO> findAll();
    
    ResponseEntity<ResponseDTO> deleteById(Integer id);
}
