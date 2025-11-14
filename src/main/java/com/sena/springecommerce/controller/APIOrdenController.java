package com.sena.springecommerce.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sena.springecommerce.model.DetalleOrden;
import com.sena.springecommerce.model.Orden;
import com.sena.springecommerce.model.Producto;
import com.sena.springecommerce.model.Usuario;
import com.sena.springecommerce.service.IDetalleOrdenService;
import com.sena.springecommerce.service.IOrdenService;
import com.sena.springecommerce.service.IProductoService;
import com.sena.springecommerce.service.IUsuarioService;

@RestController
@RequestMapping("/apiordenes")
public class APIOrdenController {
	
	@Autowired
	private IDetalleOrdenService detalleService;
	
	@Autowired
	private IOrdenService ordenService;
	
	@Autowired 
	private IUsuarioService userService;
	
	@Autowired 
	private IProductoService productService;
	
	private List<DetalleOrden> listaTemporal = new ArrayList<>();
	
	private Usuario userTemporal = null;
	
	// Endpoint GET para obtener todas las ordenes
	@GetMapping("/list")
	public List<Orden> getAllOrdenes() {
		return ordenService.findAll();
	}

	// Endpoint GET para obtener orden por ID
	@GetMapping("/orden/{id}")
	public ResponseEntity<Orden> getOrdenId(@PathVariable Integer id) {
		Optional<Orden> orden = ordenService.findById(id);
		return orden.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
		
	}
	
	//Endpoind para selecionar el usuario q realizara la orden
	@PostMapping("/userOrden/id/{idUser}")
	public ResponseEntity<?> usuarioOrden(@PathVariable Integer idUser){
		
		Optional<Usuario> usuarioOpt = userService.findById(idUser);
		if (usuarioOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
		}
		
		userTemporal = usuarioOpt.get();
		
		return ResponseEntity.ok("Usuario selecionado = id " + userTemporal.getId() + ": " + userTemporal.getNombre());
	}
	
	@PostMapping("/userOrden/email/{email}")
	public ResponseEntity<?> usuarioOrdenEmail(@PathVariable String email){
		
		Optional<Usuario> usuarioOpt = userService.findByEmail(email);
		if (usuarioOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
		}
		
		userTemporal = usuarioOpt.get();
		
		return ResponseEntity.ok("Usuario selecionado = email " + userTemporal.getEmail() + ": " + userTemporal.getNombre());
	}
	
	
	// POST para agregar producto a la lista temporal
	@PostMapping("/agregar")
	public ResponseEntity<?> agregarProducto(@RequestParam(required = false) Integer id,@RequestParam(required = false) String nombre , @RequestParam Integer cantidad) {
		
		if (userTemporal == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Debes seleccionar un usuario antes de agregar la orden");
		}
		
		Optional<Producto> productoOpt = Optional.empty();
		
		//Buscador po id del producto
		if (id != null) {  
			productoOpt = productService.get(id);
		}
		
		//Buscador por el Nombre del producto
		if (!productoOpt.isPresent() && nombre != null) {
			productoOpt = productService.findAll().stream().filter(p -> p.getNombre().equalsIgnoreCase(nombre)).findFirst();
		}
		
		if (!productoOpt.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");
		}
		
		Producto producto = productoOpt.get();
		
		if (producto.getCantidad() < cantidad.intValue()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Stock insuficiente para: " + producto.getNombre());
			
		}
		 //Crear el DetalleOrden temporal
		DetalleOrden detalle = new DetalleOrden();
		detalle.setProducto(producto);
		detalle.setNombre(producto.getNombre());
		detalle.setCantidad(cantidad);
		detalle.setPrecio(producto.getPrecio());
		detalle.setTotal(producto.getPrecio() * cantidad);
		
		listaTemporal.add(detalle);
		return ResponseEntity.ok(listaTemporal);
		
	}
		
	//POST confirma la compra
	@PostMapping("/confirmar")
	public ResponseEntity<?> confirmarOrden(){
		
		//Valida que haya un usuario selecionado
		if (userTemporal == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Debe selecccionar un usuario antes de confirmar");
		}
		
		//Valida que haya una lista temporal
		if (listaTemporal.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No hay productos agregados");
		}
		 //Crear la Orden
		Orden orden = new Orden();
		orden.setNumero(ordenService.generarNumeroOrden());
		orden.setFechacreacion(new Date());
		
		double total = listaTemporal.stream().mapToDouble(DetalleOrden::getTotal).sum();
		
		orden.setTotal(total);
		orden.setUsuario(userTemporal);
		
		Orden nuevaOrden = ordenService.save(orden);
		
		//Procesar cada uno de los detalles
		for (DetalleOrden d : listaTemporal) {
			
			Optional<Producto> pOpt = productService.get(d.getProducto().getId());
			if (pOpt.isPresent()) {
				Producto p = pOpt.get();
				
				//Actualiza la cantidad despues de confirmar el pedido
				int stockActual = p.getCantidad();
				int cantidadPedida = d.getCantidad();
				
				if (stockActual < cantidadPedida) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Stock insuficente para: " + p.getNombre());
				}
				
				p.setCantidad(stockActual - cantidadPedida); 
				productService.update(p);
			}
			
			//Guarda el DetalleOrden en la db despues de confirmar
			d.setOrden(nuevaOrden);
			detalleService.save(d);
		}
		
		//Limpia los datos para volver hacer otra Orden
		listaTemporal.clear();
		userTemporal = null;
		return ResponseEntity.ok("Orden creada correctamente con ID: " + nuevaOrden.getId());
		
	}
		//Endpoint DELETE para eliminar una orden por su ID.
		
		@DeleteMapping("/delete/{id}")
		public ResponseEntity<String> deleteOrden(@PathVariable Integer id) {
		
			// 1. Verificar si la orden existe
			Optional<Orden> ordenOpt = ordenService.findById(id);
			
			if (!ordenOpt.isPresent()) {
				// Si la orden no se encuentra, devuelve 404 Not Found
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Orden con ID " + id + " no encontrada.");
			}
			
			// 2. Si la orden existe, proceder a eliminarla.
			// NOTA: Asegúrate de que tu base de datos maneje la eliminación en cascada (CASCADE) 
			// para los DetalleOrden asociados, o elimina manualmente los detalles primero.
			
			try {
				// Asegúrate de que el servicio maneje la eliminación de DetalleOrden
				ordenService.delete(id);
				// Devuelve 200 OK
				return ResponseEntity.ok("Orden con ID " + id + " eliminada correctamente.");
			} catch (Exception e) {
				// Si hay un error de base de datos u otra excepción
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al intentar eliminar la orden: " + e.getMessage());
			}
		} 
		
	}
			
			
