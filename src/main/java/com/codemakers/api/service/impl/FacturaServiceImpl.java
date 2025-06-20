package com.codemakers.api.service.impl;

import java.util.Date;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.codemakers.api.service.IFacturaService;
import com.codemakers.commons.dtos.FacturaDTO;
import com.codemakers.commons.dtos.ResponseDTO;
import com.codemakers.commons.entities.FacturaEntity;
import com.codemakers.commons.maps.EmpresaClienteContadorMapper;
import com.codemakers.commons.maps.EstadoMapper;
import com.codemakers.commons.maps.FacturaMapper;
import com.codemakers.commons.maps.LecturaMapper;
import com.codemakers.commons.maps.TarifaMapper;
import com.codemakers.commons.maps.TipoPagoMapper;
import com.codemakers.commons.repositories.FacturaRepository;
import com.codemakers.commons.utils.Constantes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FacturaServiceImpl implements IFacturaService{
	
	private final FacturaRepository facturaRepository;
	private final FacturaMapper facturaMapper;
	private final EmpresaClienteContadorMapper empresaMapper;
	private final TarifaMapper tarifaMapper;
	private final LecturaMapper lecturaMapper;
	private final TipoPagoMapper tipoPagoMapper;
	private final EstadoMapper estadoMapper;
	
	@Override
    public ResponseEntity<ResponseDTO> save(FacturaDTO facturaDTO) {
        log.info("Creando Factura");
        try {
            FacturaEntity entity = facturaMapper.dtoToEntity(facturaDTO);
            entity.setFechaCreacion(new Date());
            entity.setUsuarioCreacion(facturaDTO.getUsuarioCreacion());
            entity.setActivo(true);

            FacturaEntity saved = facturaRepository.save(entity);
            FacturaDTO savedDTO = facturaMapper.entityToDto(saved);

            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(true)
                    .message(Constantes.SAVED_SUCCESSFULLY)
                    .code(HttpStatus.CREATED.value())
                    .response(savedDTO)
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (Exception e) {
            log.error("Error creando la factura", e);
            ResponseDTO errorResponse = ResponseDTO.builder()
                    .success(false)
                    .message(Constantes.SAVE_ERROR)
                    .code(HttpStatus.BAD_REQUEST.value())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    
	@Override
	public ResponseEntity<ResponseDTO> update(FacturaDTO facturaDTO) {
	    log.info("Actualizando factura con ID: {}", facturaDTO.getId());

	    try {
	        Optional<FacturaEntity> optionalFactura = facturaRepository.findById(facturaDTO.getId());

	        if (optionalFactura.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
	                ResponseDTO.builder()
	                    .success(false)
	                    .message(Constantes.FAC_NOT_FOUND)
	                    .code(HttpStatus.NOT_FOUND.value())
	                    .build()
	            );
	        }

	        FacturaEntity entity = optionalFactura.get();
	        facturaMapper.updateEntityFromDto(facturaDTO, entity);

	        if (facturaDTO.getEmpresaClienteContador() != null)
	            entity.setEmpresaClienteContador(empresaMapper.dtoToEntity(facturaDTO.getEmpresaClienteContador()));

	        if (facturaDTO.getTarifa() != null)
	            entity.setTarifa(tarifaMapper.dtoToEntity(facturaDTO.getTarifa()));

	        if (facturaDTO.getLectura() != null)
	            entity.setLectura(lecturaMapper.dtoToEntity(facturaDTO.getLectura()));

	        if (facturaDTO.getTipoPago() != null)
	            entity.setTipoPago(tipoPagoMapper.dtoToEntity(facturaDTO.getTipoPago()));

	        if (facturaDTO.getEstado() != null)
	            entity.setEstado(estadoMapper.dtoToEntity(facturaDTO.getEstado()));

	        entity.setUsuarioModificacion(facturaDTO.getUsuarioModificacion());
	        entity.setFechaModificacion(new Date());

	        FacturaEntity updated = facturaRepository.save(entity);
	        FacturaDTO updatedDTO = facturaMapper.entityToDto(updated);

	        return ResponseEntity.ok(
	            ResponseDTO.builder()
	                .success(true)
	                .message(Constantes.FAC_UPD_SUCCESS)
	                .code(HttpStatus.OK.value())
	                .response(updatedDTO)
	                .build()
	        );

	    } catch (Exception e) {
	        log.error("Error actualizando la factura", e);
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
	            ResponseDTO.builder()
	                .success(false)
	                .message(Constantes.UPD_NOT_FOUND)
	                .code(HttpStatus.BAD_REQUEST.value())
	                .build()
	        );
	    }
	}


	@Override
	public ResponseEntity<ResponseDTO> findById(Integer id) {
	    log.info("Buscar factura por id: {}", id);
	    try {
	        Optional<FacturaEntity> factura = facturaRepository.findById(id);
	        if (factura.isPresent()) {
	        	FacturaDTO dto = facturaMapper.entityToDto(factura.get());
	            ResponseDTO responseDTO = ResponseDTO.builder()
	                    .success(true)
	                    .message(Constantes.CONSULTED_SUCCESSFULLY)
	                    .code(HttpStatus.OK.value())
	                    .response(dto)
	                    .build();
	            return ResponseEntity.ok(responseDTO);
	        } else {
	            ResponseDTO responseDTO = ResponseDTO.builder()
	                    .success(false)
	                    .message(Constantes.CONSULTING_ERROR)
	                    .code(HttpStatus.NOT_FOUND.value())
	                    .build();
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
	        }
	    } catch (Exception e) {
	        log.error("Error al buscar  factura por id: {}", id, e);
	        ResponseDTO responseDTO = ResponseDTO.builder()
	                .success(false)
	                .message(Constantes.ERROR_QUERY_RECORD_BY_ID)
	                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
	                .build();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
	    }
	}

    @Override
    public ResponseEntity<ResponseDTO> findAll() {
        log.info("Listar todos las facturas");
        try {
            var list = facturaRepository.findAll();
            var dtoList = facturaMapper.listEntityToDtoList(list);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(true)
                    .message(Constantes.CONSULTED_SUCCESSFULLY)
                    .code(HttpStatus.OK.value())
                    .response(dtoList)
                    .build();
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("Error al listar las facturas", e);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(false)
                    .message(Constantes.CONSULTING_ERROR)
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .response(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
    }

    @Override
    public ResponseEntity<ResponseDTO> deleteById(Integer id) {
        log.info("Inicio método para eliminar factura por id: {}", id);
        try {
            if (!facturaRepository.existsById(id)) {
                ResponseDTO responseDTO = ResponseDTO.builder()
                        .success(false)
                        .message(Constantes.RECORD_NOT_FOUND)
                        .code(HttpStatus.NOT_FOUND.value())
                        .build();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
            }
            facturaRepository.deleteById(id);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(true)
                    .message(Constantes.DELETED_SUCCESSFULLY)
                    .code(HttpStatus.OK.value())
                    .build();
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("Error al eliminar factura con id: {}", id, e);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .success(false)
                    .message(Constantes.DELETE_ERROR)
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
    }
}