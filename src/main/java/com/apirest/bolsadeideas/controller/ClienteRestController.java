package com.apirest.bolsadeideas.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.apirest.bolsadeideas.models.Cliente;
import com.apirest.bolsadeideas.services.IClienteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = { "http://localhost:4200" })
@RestController
@RequestMapping("/api")
public class ClienteRestController {

	@Autowired
	private IClienteService clienteService;

	@GetMapping("/clientes")
	public List<Cliente> index() {
		return clienteService.findAll();

	}
	
	@GetMapping("/clientes/page/{page}")
	public Page<Cliente> index(@PathVariable Integer page) {
		Pageable pageable = PageRequest.of(page, 5);
		return clienteService.findAll(pageable);

	}

	@GetMapping("clientes/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {

		Cliente cliente = null;
		Map<String, Object> response = new HashMap<>();

		try {
			cliente = clienteService.findById(id);

		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta");
			response.put("error", e.getMessage().concat(": ".concat(e.getMostSpecificCause().getMessage())));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (cliente == null) {
			response.put("mensaje",
					"El cliente con ID:".concat(id.toString().concat(" no existe en la base de datos!")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);

		}
		return new ResponseEntity<Cliente>(cliente, HttpStatus.OK);
	}

	@PostMapping("clientes")
	public ResponseEntity<?> create(@Valid @RequestBody Cliente cliente, BindingResult result) {

		Cliente clienteNew = null;
		Map<String, Object> response = new HashMap<>();

		if(result.hasErrors()) {
			
			List<String> errors= result.getFieldErrors()
					.stream()
					.map(err -> "El campo '" + err.getField()+"' "+ err.getDefaultMessage())
					.collect(Collectors.toList());
					
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);

		}
		
		try {
			clienteNew = clienteService.save(cliente);

		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el insert en la base de datos");
			response.put("error", e.getMessage().concat(": ".concat(e.getMostSpecificCause().getMessage())));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);

		}

		response.put("mensaje", "El cliente ha sido creado!");
		response.put("cliente", clienteNew);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@DeleteMapping("/clientes/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();
		
		if (!clienteService.existsById(id)) {
	        response.put("mensaje", "El cliente con ID " + id + " no existe.");
	        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
	    }
		
		try {
			clienteService.delete(id);
	        System.out.println("Cliente "+ id +" eliminado correctamente");

		}  catch (DataAccessException e) {
	        System.out.println("Error al eliminar cliente");
			response.put("mensaje", "Error al eliminar en la base de datos");
			response.put("error", e.getMessage().concat(": ".concat(e.getMostSpecificCause().getMessage())));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "El cliente ha sido eliminado!");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
		
	}

	@PutMapping("/clientes/{id}")
	public ResponseEntity<?> update(@Valid @RequestBody Cliente cliente, BindingResult result, @PathVariable Long id) {
		
		Cliente clienteActual = clienteService.findById(id);
		Cliente clienteUpdated = null;
		Map<String, Object> response = new HashMap<>();
		
		if(result.hasErrors()) {
			
			List<String> errors= result.getFieldErrors()
					.stream()
					.map(err -> "El campo '" + err.getField()+"' "+ err.getDefaultMessage())
					.collect(Collectors.toList());
					
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);

		}

		if (clienteActual == null) {
			response.put("mensaje", "No se puede editar el cliente con ID:"
					.concat(id.toString().concat(" no existe en la base de datos!")));
			log.info(response.toString());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {

			clienteActual.setApellido(cliente.getApellido());
			clienteActual.setNombre(cliente.getNombre());
			clienteActual.setEmail(cliente.getEmail());

			clienteUpdated = clienteService.save(clienteActual);

		} catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar en la base de datos");
			response.put("error", e.getMessage().concat(": ".concat(e.getMostSpecificCause().getMessage())));
			log.info(response.toString());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);

		}

		response.put("mensaje", "El cliente ha sido actualizado!");
		response.put("cliente", clienteUpdated);
		log.info(response.toString());
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	/* private String compareObjects(Object originalObj, Object updatedObj) {
        if (!originalObj.getClass().equals(updatedObj.getClass())) {
            return "Los objetos no son del mismo tipo";
        }
        StringBuilder differences = new StringBuilder();
        differences.append("Campo cambiado; Campo antiguo; Campo nuevo\n");

        // Obtenemos la clase del objeto
        Class<?> clazz = originalObj.getClass();

        // Obtenemos todos los campos declarados en la clase y sus superclases
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            // Hacemos que el campo sea accesible, ya que algunos pueden ser privados
            field.setAccessible(true);

            try {
                // Obtenemos el valor del campo en los dos objetos
                Object originalValue = field.get(originalObj);
                Object updatedValue = field.get(updatedObj);

                // Comparamos los valores de los campos
                if (originalValue == null && updatedValue != null) {
                    differences.append(field.getName())
                            .append(";") // Separador de columna
                            .append("null") // Valor nulo para el campo antiguo
                            .append(";") // Separador de columna
                            .append(updatedValue)
                            .append("\n"); // Nueva línea
                } else if (originalValue != null && !originalValue.equals(updatedValue)) {
                    differences.append(field.getName())
                            .append(";") // Separador de columna
                            .append(originalValue)
                            .append(";") // Separador de columna
                            .append(updatedValue)
                            .append("\n"); // Nueva línea
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace(); // Manejo adecuado del error según tus requerimientos
            }
        } */
}
