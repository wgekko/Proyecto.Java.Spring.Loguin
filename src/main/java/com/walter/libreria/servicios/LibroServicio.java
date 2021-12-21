package com.walter.libreria.servicios;

import com.walter.libreria.entidades.Autor;
import com.walter.libreria.entidades.Editorial;
import com.walter.libreria.entidades.Foto;
import com.walter.libreria.entidades.Libro;
import com.walter.libreria.entidades.Prestamo;
import com.walter.libreria.repositorios.AutorRepositorio;
import com.walter.libreria.repositorios.EditorialRepositorio;
import com.walter.libreria.repositorios.LibroRepositorio;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LibroServicio {

    @Autowired
    private LibroRepositorio libroRepositorio;

    @Autowired
    private AutorRepositorio autorRepositorio;

    @Autowired
    private AutorServicio autorServicio;

    @Autowired
    private EditorialRepositorio editorialRepositorio;

    @Autowired
    private EditorialServicio editorialServicio;

    @Autowired
    private PrestamoServicio prestamoServicio;

    @Autowired
    private FotoServicio fotoServicio;


    @Transactional
    public void agregarLibro(MultipartFile archivo, Long isbn, String titulo, Integer anio, String descripcion, Integer ejemplares, Autor autor, Editorial editorial) throws Exception {
        try {
            Libro libro = new Libro();        
            validar(isbn, titulo, anio, descripcion, ejemplares);       
            libro.setAlta(true);
            libro.setIsbn(isbn);
            libro.setTitulo(titulo);
            libro.setAnio(anio);
            libro.setDescripcion(descripcion);
            libro.setEjemplares(ejemplares);
            libro.setEjemplaresPrestados(0);
            libro.setEjemplaresRestantes(ejemplares);
            libro.setAutor(autor);         
            if (!libro.getAutor().isAlta()) {
                autorServicio.alta(libro.getAutor().getId());
            }
            libro.setEditorial(editorial);           
            if (!libro.getEditorial().isAlta()) {
                editorialServicio.alta(libro.getEditorial().getId());
            }
            Foto foto = fotoServicio.guardar(archivo);
            libro.setFoto(foto);            
            libroRepositorio.save(libro);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
//            throw new Exception("Error al intentar guardar el Libro.");
        }
    }

    @Transactional
    public void modificarLibro(String id, MultipartFile archivo, Long isbn, String titulo, Integer anio, String descripcion, Integer ejemplares, Autor autor, Editorial editorial) throws Exception {
        try {         
            validar(isbn, titulo, anio, descripcion, ejemplares);         
            Optional<Libro> respuesta = libroRepositorio.findById(id);
            if (respuesta.isPresent()) {  
                Libro libro = respuesta.get();                
                libro.setIsbn(isbn);
                libro.setTitulo(titulo);
                libro.setAnio(anio);
                libro.setDescripcion(descripcion);             
                List<Prestamo> prestados = prestamoServicio.buscarPorLibro(id);
                libro.setEjemplaresPrestados(prestados.size());
                if (libro.getEjemplaresPrestados() > ejemplares) {
                    throw new Exception("Existen más préstamos vigentes que la cantidad de ejemplares que se indicó. Revise por favor los datos ingresados.");
                } else {
                    libro.setEjemplares(ejemplares);
                }
                libro.setEjemplaresRestantes(ejemplares - libro.getEjemplaresPrestados());            
                libro.setAutor(autor);
                libro.setEditorial(editorial);
                if (!archivo.isEmpty()) {
                    String idFoto = null;
                    if (libro.getFoto() != null) {
                        idFoto = libro.getFoto().getId();
                    }
                    Foto foto = fotoServicio.actualizar(idFoto, archivo);
                    libro.setFoto(foto);
                }               
                libroRepositorio.save(libro);
            } else { 
                throw new Exception("No existe el Libro con el id indicado.");
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
//            throw new Exception("Error al intentar modificar el Libro.");
        }
    }
 
    @Transactional
    public void eliminarLibro(String id) throws Exception {
        try {       
            Optional<Libro> respuesta = libroRepositorio.findById(id);
            if (respuesta.isPresent()) { 
                Libro libro = respuesta.get();             
                List<Prestamo> prestamosLibro = prestamoServicio.buscarPorLibro(id);
                for (Prestamo prestamo : prestamosLibro) {
                    prestamoServicio.eliminarPrestamo(prestamo.getId());
                }                
                libroRepositorio.delete(libro);
            } else { 
                throw new Exception("No existe el Libro con el id indicado.");
            }
        } catch (Exception e) {
            throw new Exception("ERROR al intentar eliminar el Libro.");
        }
    }
  
    @Transactional
    public void baja(String id) throws Exception {
        try {          
            Libro libro = libroRepositorio.getById(id);
            if (libro != null) {                 
                List<Prestamo> prestamosLibro = prestamoServicio.buscarPorLibro(id);
                for (Prestamo prestamo : prestamosLibro) {
                    if (prestamo.isAlta()) {
                        prestamoServicio.baja(prestamo.getId());
                    }
                }
                libro.setAlta(false);
                libroRepositorio.save(libro);
            } else { 
                throw new Exception("No existe el Libro con el id indicado.");
            }
        } catch (Exception e) {
            throw new Exception("ERROR al intentar dar de baja el Libro.");
        }
    }
  
    @Transactional
    public void alta(String id) throws Exception {
        try {           
            Optional<Libro> respuesta = libroRepositorio.findById(id);
            if (respuesta.isPresent()) { 
                Libro libro = respuesta.get();
                libro.setAlta(true);
                libroRepositorio.save(libro);             
                if (!libro.getAutor().isAlta()) {
                    autorServicio.alta(libro.getAutor().getId());
                }
                if (!libro.getEditorial().isAlta()) {
                    editorialServicio.alta(libro.getEditorial().getId());
                }

            } else { 
                throw new Exception("No existe el Libro con el id indicado.");
            }
        } catch (Exception e) {
            throw new Exception("ERROR al intentar dar de alta el Libro.");
        }
    }
  
    @Transactional
    public void prestamoLibro(Libro libro) throws Exception {
        if (libro.getEjemplaresRestantes() >= 1) {
            libro.setEjemplaresPrestados(libro.getEjemplaresPrestados() + 1);
            libro.setEjemplaresRestantes(libro.getEjemplares() - libro.getEjemplaresPrestados());
        } else {
            throw new Exception("No hay suficientes ejemplares disponibles para realizar el préstamo.");
        }
    }
 
    @Transactional
    public void devolucionLibro(Libro libro) throws Exception {
        if (libro.getEjemplaresPrestados() >= 1) {
            libro.setEjemplaresPrestados(libro.getEjemplaresPrestados() - 1);
            libro.setEjemplaresRestantes(libro.getEjemplares() - libro.getEjemplaresPrestados());
        } else {
            throw new Exception("No hay préstamos registrados para este Libro.");
        }
    }

    public void validar(Long isbn, String titulo, Integer anio, String descripcion, Integer ejemplares) throws Exception {

        if (isbn <= 0 || isbn == null) {
            throw new Exception("ISBN no válido.");
        }
        if (titulo == null || titulo.isEmpty()) {
            throw new Exception("Título no válido.");
        }
        if (anio <= 0 || anio == null) {
            throw new Exception("Año no válido.");
        }
        if (descripcion == null || descripcion.isEmpty()) {
            throw new Exception("La descripción es obligatoria.");
        }
        if (descripcion.length() > 255) {
            throw new Exception("La descripción no puede tener más de 200 caracteres.");
        }
        if (ejemplares < 0 || ejemplares == null) {
            throw new Exception("Cantidad de ejemplares no válidas.");
        }
    }

   
    public Libro getById(String id) {
        return libroRepositorio.getById(id);
    }
   
    public Libro buscarPorIsbn(Long isbn) {
        return libroRepositorio.buscarPorIsbn(isbn);
    }
   
    public List<Libro> buscarPorAutor(String idAutor) {
        return libroRepositorio.buscarPorAutor(idAutor);
    }
   
    public List<Libro> buscarPorEditorial(String id) {
        return libroRepositorio.buscarPorEditorial(id);
    }
    
    public List<Libro> findAll() {
        return libroRepositorio.findAll();
    }
  
    public List<Libro> listarDeBaja() {
        return libroRepositorio.listarDeBaja();
    }
}
