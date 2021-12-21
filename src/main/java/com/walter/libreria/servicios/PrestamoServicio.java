package com.walter.libreria.servicios;

import com.walter.libreria.entidades.Libro;
import com.walter.libreria.entidades.Prestamo;
import com.walter.libreria.repositorios.PrestamoRepositorio;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PrestamoServicio {

    @Autowired
    private PrestamoRepositorio prestamoRepositorio;

    @Autowired
    private LibroServicio libroServicio;

    @Autowired
    private UsuarioServicio usuarioServicio;
  
    @Transactional
    public void agregarPrestamo(Date fechaPrestamo, Date fechaDevolucion, String idLibro, String idUsuario) throws Exception {
        Prestamo prestamo = new Prestamo();
        try {       
            validar(fechaPrestamo, fechaDevolucion, idLibro, idUsuario);           
            prestamo.setFechaPrestamo(fechaPrestamo);
            prestamo.setFechaDevolucion(fechaDevolucion);
            prestamo.setUsuario(usuarioServicio.getById(idUsuario));
            prestamo.setAlta(true);          
            try {
                Libro libro = libroServicio.getById(idLibro);
                validarEjemplaresLibroPrestamo(libro);
                prestamo.setLibro(libro);
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }            
            prestamoRepositorio.save(prestamo);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
  
    @Transactional
    public void modificarPrestamo(String id, Date fechaPrestamo, Date fechaDevolucion) throws Exception {
        try {        
            validarFechas(fechaPrestamo, fechaDevolucion);           
            Prestamo prestamo = prestamoRepositorio.getById(id);
            if (prestamo != null) {             
                prestamo.setFechaPrestamo(fechaPrestamo);
                prestamo.setFechaDevolucion(fechaDevolucion);           
                prestamoRepositorio.save(prestamo);
            } else {
                throw new Exception("No existe el prestamo vinculado a ese ID.");
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
   
    @Transactional
    public void eliminarPrestamo(String id) throws Exception {
        try {         
            Prestamo prestamo = prestamoRepositorio.getById(id);
            if (prestamo.isAlta()) {
                Libro libro = prestamo.getLibro();
                libroServicio.devolucionLibro(libro);             
                prestamoRepositorio.delete(prestamo);
            } else {              
                prestamoRepositorio.delete(prestamo);
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
 
    @Transactional
    public void baja(String id) throws Exception {
        try {           
            Prestamo prestamo = prestamoRepositorio.getById(id);
            if (prestamo != null) {
                Libro libro = prestamo.getLibro();
                libroServicio.devolucionLibro(libro);
                prestamo.setFechaDevolucion(new Date());
                prestamo.setAlta(false);            
                prestamoRepositorio.save(prestamo);
            } else {
                throw new Exception("No existe el prestamo vinculado a ese ID.");
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Transactional
    public void alta(String id) throws Exception {
        try {            
            Prestamo prestamo = prestamoRepositorio.getById(id);
            if (prestamo != null) {
                prestamo.setAlta(true);             
                prestamoRepositorio.save(prestamo);
            } else {
                throw new Exception("No existe el prestamo vinculado a ese ID.");
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
  
    public void validar(Date fechaPrestamo, Date fechaDevolucion, String idLibro, String idUsuario) throws Exception {
        if (fechaPrestamo == null) {
            throw new Exception("Fecha de Préstamo no válida.");
        }
        if (fechaDevolucion == null) {
            throw new Exception("Fecha de Devolución no válida.");
        }
        if (fechaPrestamo.after(fechaDevolucion)) {
            throw new Exception("La fecha de retiro del Libro ingresada es posterior a la de devolución.");
        }
        if (idLibro == null || libroServicio.getById(idLibro) == null) {
            throw new Exception("Id de Libro no válido.");
        }
        if (idUsuario == null || usuarioServicio.getById(idUsuario) == null) {
            throw new Exception("Id de Usuario no válido.");
        }
    }
  
    public void validarFechas(Date fechaPrestamo, Date fechaDevolucion) throws Exception {
        if (fechaPrestamo == null) {
            throw new Exception("Fecha de Préstamo no válida.");
        }
        if (fechaDevolucion == null) {
            throw new Exception("Fecha de Devolución no válida.");
        }
        if (fechaPrestamo.after(fechaDevolucion)) {
            throw new Exception("La fecha de retiro del Libro ingresada es posterior a la de devolución.");
        }
    }
  
    public void validarEjemplaresLibroPrestamo(Libro libro) throws Exception {
        try {
            libroServicio.prestamoLibro(libro);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public Prestamo buscarPorId(String id) {
        return prestamoRepositorio.getById(id);
    }
   
    public List<Prestamo> buscarPorLibro(String idLibro) {
        return prestamoRepositorio.buscarPorLibro(idLibro);
    }

    public List<Prestamo> buscarPorUsuario(String idUsuario) {
        return prestamoRepositorio.buscarPorUsuario(idUsuario);
    }
    
    public List<Prestamo> listarTodos() {
        return prestamoRepositorio.findAll();
    }
  
    public List<Prestamo> listarDeAlta() {
        return prestamoRepositorio.buscarPrestamosAlta();
    }

    public List<Prestamo> listarDeBaja() {
        return prestamoRepositorio.buscarPrestamosBaja();
    }
  
    public List<Prestamo> listarDeAltaUsuario(String idUsuario) {
        return prestamoRepositorio.buscarPrestamosAltaUsuario(idUsuario);
    }
  
    public List<Prestamo> listarDeBajaUsuario(String idUsuario) {
        return prestamoRepositorio.buscarPrestamosBajaUsuario(idUsuario);
    }

}
