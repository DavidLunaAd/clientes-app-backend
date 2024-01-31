package com.apirest.bolsadeideas.services;

import java.util.List;

import com.apirest.bolsadeideas.models.Cliente;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IClienteService {
	
	public List<Cliente> findAll();

	public Page<Cliente> findAll(Pageable pageable);

	public Cliente save(Cliente cliente);
	
	public void delete(Long id);
	
	public Cliente findById(Long id);

	public boolean existsById(Long id);

}
