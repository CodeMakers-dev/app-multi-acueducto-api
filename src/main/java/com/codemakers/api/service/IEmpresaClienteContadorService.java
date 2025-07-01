package com.codemakers.api.service;

import org.springframework.http.ResponseEntity;

import com.codemakers.commons.dtos.EmpresaClienteContadorDTO;
import com.codemakers.commons.dtos.ResponseDTO;

public interface IEmpresaClienteContadorService {
    
	ResponseEntity<ResponseDTO> save(EmpresaClienteContadorDTO empresaClienteContadorDTO);
	
	ResponseEntity<ResponseDTO> update(EmpresaClienteContadorDTO empresaClienteContadorDTO);
	
	ResponseEntity<ResponseDTO> findByEmpresaId(Integer idEmpresa);
	
    ResponseEntity<ResponseDTO> findById(Integer id);
    
    ResponseEntity<ResponseDTO> findAll();
    
    ResponseEntity<ResponseDTO> deleteById(Integer id);
}
