package com.sena.springecommerce.service;

import java.util.List;
import java.util.Optional;
import com.sena.springecommerce.repository.IOrdenRepository;
import com.sena.springecommerce.repository.IUsuarioRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sena.springecommerce.model.Orden;
import com.sena.springecommerce.model.Usuario;

@Service
public class OrdenServiceImplement implements IOrdenService {

	@Autowired
	private IOrdenRepository ordenRepository;
	
	@Autowired
	private IUsuarioRepository usuarioRepository;

	@Override
	@jakarta.transaction.Transactional
	public Orden save(Orden orden) {
		
	Usuario usuarioRequest = orden.getUsuario();
		
	if (usuarioRequest == null || usuarioRequest.getId() == null || usuarioRequest.getId() == 0) {
        throw new RuntimeException("Error: La orden debe estar asociada a un Usuario con ID válido.");
    }
	
	Optional<Usuario> optionalUsuario = usuarioRepository.findById(usuarioRequest.getId());

	if (!optionalUsuario.isPresent()) {
        // Lanza error si el ID no existe en la DB
        throw new RuntimeException("Error: Usuario con ID " + usuarioRequest.getId() + " no encontrado en la base de datos.");
    }
	
	orden.setUsuario(optionalUsuario.get());
    
	if (orden.getNumero() == null || orden.getNumero().trim().isEmpty()) {
        String nuevoNumero = generarNumeroOrden();
        orden.setNumero(nuevoNumero);
    }
		// TODO Auto-generated method stub
	return ordenRepository.save(orden);
	}
	@Override
	public List<Orden> findAll() {
		// TODO Auto-generated method stub
		return ordenRepository.findAll();
	}

	@Override
	public List<Orden> findByUsuario(Usuario usuario) {
		// TODO Auto-generated method stub
		return ordenRepository.findByUsuario(usuario);
	}

	@Override
	public Optional<Orden> findById(Integer id) {
		// TODO Auto-generated method stub
		return ordenRepository.findById(id);
	}

	@Override
	public String generarNumeroOrden() {
		// Define el número base de la secuencia (1 menos que el inicio)
		final long NUMERO_BASE = 10000000000000L; 
	    
	    Optional<Long> maxNumeroOpt = ordenRepository.findMaxNumeroOrden();
	    
	    long siguienteNumero;
	    
	 
	    if (maxNumeroOpt.isPresent() && maxNumeroOpt.get() != null) {
	  
	        siguienteNumero = maxNumeroOpt.get() + 1;
	    } else {
	    
	        siguienteNumero = NUMERO_BASE + 1; // Resultado: 10000000000001
	    }
	    
	  
	    return String.valueOf(siguienteNumero);
	}
		

	@Override
	@Transactional
	public void delete(Integer id) {
		
	ordenRepository.deleteById(id);
		// TODO Auto-generated method stub

	}

}