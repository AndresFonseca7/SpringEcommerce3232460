package com.sena.springecommerce.service;

import java.util.List;
import java.util.Optional;

import com.sena.springecommerce.model.Usuario;

public interface IUsuarioService{

public Usuario save(Usuario usuario);

public Optional<Usuario> get(Integer id);

public void update(Usuario usuario);

public void delete(Integer id);

Optional<Usuario> findById(Integer id);

Optional<Usuario> findByEmail(String email);

List<Usuario> FindAll();
	
}
