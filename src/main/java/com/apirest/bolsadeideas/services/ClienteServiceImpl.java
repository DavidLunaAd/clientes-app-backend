package com.apirest.bolsadeideas.services;

import java.util.List;

import com.apirest.bolsadeideas.models.Cliente;
import com.apirest.bolsadeideas.repositories.IClienteDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteServiceImpl implements IClienteService {

	@Autowired
	private IClienteDao clienteDao;
	
	@Override
	@Transactional(readOnly= true)
	public List<Cliente> findAll() {
		return (List<Cliente>) clienteDao.findAll();
	}

	@Override
	@Transactional
	public Page<Cliente> findAll(Pageable pageable) {
		return clienteDao.findAll(pageable);
	}
	
	@Override
	@Transactional
	public Cliente save(Cliente cliente) {
		return clienteDao.save(cliente);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		clienteDao.deleteById(id);
		
	}

	@Override
	@Transactional(readOnly= true)
	public Cliente findById(Long id) {
		return clienteDao.findById(id).orElse(null);
	}
	@Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return clienteDao.findById(id).isPresent();    
    }


}
