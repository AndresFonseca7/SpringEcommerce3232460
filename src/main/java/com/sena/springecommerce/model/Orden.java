package com.sena.springecommerce.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "ordenes")
public class Orden {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // anotacion JPA
	private Integer id;
	private String numero;
	private Date fechacreacion;
	private Double total;
	
	
	@ManyToOne(fetch = FetchType.EAGER)
	private Usuario usuario;
	
	
	@JsonManagedReference
	@OneToMany(
			mappedBy = "orden", 
		    fetch = FetchType.EAGER,
		    cascade = CascadeType.ALL, 
		    orphanRemoval = true
			)
	private List<DetalleOrden> ordenes;
	public Orden() {
		
	}

	
	@PrePersist
	protected void onCreate() {
		this.fechacreacion = new Date();
	
	}
		
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public Date getFechacreacion() {
		return fechacreacion;
	}

	public void setFechacreacion(Date fechacreacion) {
		this.fechacreacion = fechacreacion;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<DetalleOrden> getOrdenes() {
		return ordenes;
	}

	public void setOrdenes(List<DetalleOrden> ordenes) {
		this.ordenes = ordenes;
	}

	@Override
	public String toString() {
		return "Orden [id=" + id + ", numero=" + numero + ", fechacreacion=" + fechacreacion + ", total=" + total + "]";
	}
	
	
	
}
