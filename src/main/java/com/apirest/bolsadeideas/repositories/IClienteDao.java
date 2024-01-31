package com.apirest.bolsadeideas.repositories;

import com.apirest.bolsadeideas.models.Cliente;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IClienteDao extends JpaRepository<Cliente, Long>{

}
