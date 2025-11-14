package com.sena.springecommerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sena.springecommerce.model.Orden;
import com.sena.springecommerce.model.Usuario;

@Repository
public interface IOrdenRepository extends JpaRepository<Orden, Integer> { // <-- INICIO DE LA INTERFAZ
	
	// JPQL para encontrar el número de orden más alto (SOLUCIÓN para generarNumeroOrden)
	@Query(value = "SELECT MAX(CAST(o.numero AS UNSIGNED)) FROM ordenes o", nativeQuery = true)
Optional<Long> findMaxNumeroOrden();
    
    // Método que ya tenías
	List<Orden> findByUsuario(Usuario usuario);
	
}