package com.codemakers.api.service;

import org.springframework.http.ResponseEntity;

import com.codemakers.commons.dtos.ResponseDTO;
import com.codemakers.commons.dtos.RolDTO;

public interface IRolService {

	ResponseEntity<ResponseDTO> save(RolDTO rolDTO);
}
