package com.sena.springecommerce.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.sena.springecommerce.repository.IOrdenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sena.springecommerce.model.Orden;
import com.sena.springecommerce.model.Usuario;

@Service
public class OrdenServiceImplement implements IOrdenService {

	@Autowired
	private IOrdenRepository ordenRepository;

	@Override
	public Orden save(Orden orden) {
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
		return ordenRepository.FindByUsuario(usuario);
	}

	@Override
	public Optional<Orden> findById(Integer id) {
		// TODO Auto-generated method stub
		return ordenRepository.findById(id);
	}

	@Override
	public String generarNumeroOrden() {
		// TODO Auto-generated method stub
		int numero = 0;
		String numeroConcatenado = "";
		List<Orden> ordenes = findAll();
		List<Integer> numeros = new ArrayList<>();
		// funciones de java 8
		// variable anonima
		ordenes.stream().forEach(o -> numeros.add(Integer.parseInt(o.getNumero())));
		// validacion
		if (ordenes.isEmpty()) {
			numero = 1;

		} else {
			numero = numeros.stream().max(Integer::compare).get();
			numero++;
		}
		// numero de ordenes
		if (numero < 10) {
			numeroConcatenado = "1000000000000" + String.valueOf(numero);
		} else if (numero < 100) {
			numeroConcatenado = "100000000000" + String.valueOf(numero);
		} else if (numero < 1000) {
			numeroConcatenado = "10000000000" + String.valueOf(numero);
		}

		return numeroConcatenado;

	}

}
