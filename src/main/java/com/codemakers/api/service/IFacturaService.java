package com.codemakers.api.service;

import org.springframework.http.ResponseEntity;

import com.codemakers.commons.dtos.FacturaDTO;
import com.codemakers.commons.dtos.ResponseDTO;

public interface IFacturaService {
    
	ResponseEntity<ResponseDTO> save(FacturaDTO facturaDTO);
	
	ResponseEntity<ResponseDTO> update(FacturaDTO facturaDTO); 
	
    ResponseEntity<ResponseDTO> findById(Integer id);
    
    ResponseEntity<ResponseDTO> findByEnterpriseId(Integer idEmpresa);
    
    ResponseEntity<ResponseDTO> findAll();
    
    ResponseEntity<ResponseDTO> deleteById(Integer id);

}
