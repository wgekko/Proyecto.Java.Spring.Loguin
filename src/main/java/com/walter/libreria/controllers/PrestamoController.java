package com.walter.libreria.controllers;

import com.walter.libreria.entidades.Autor;
import com.walter.libreria.entidades.Libro;
import com.walter.libreria.entidades.Prestamo;
import com.walter.libreria.entidades.Usuario;
import com.walter.libreria.servicios.AutorServicio;
import com.walter.libreria.servicios.LibroServicio;
import com.walter.libreria.servicios.PrestamoServicio;
import com.walter.libreria.servicios.UsuarioServicio;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/prestamos")
public class PrestamoController {

    @Autowired
    private PrestamoServicio prestamoServicio;

    @Autowired
    private LibroServicio libroServicio;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private AutorServicio autorServicio;

    @PostMapping("/registrar-prestamo")
    public String registrarPrestamo(ModelMap model, @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaPrestamo, @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaDevolucion, String idLibro, String idUsuario) throws ParseException, Exception {
        Libro libro;
        Usuario usuario;
        try {
            // Seteo del Libro:
            libro = libroServicio.getById(idLibro);
            // Seteo del Usuario:
            usuario = usuarioServicio.getById(idUsuario);
            // Validación: un usuario no puede exceder los 4 préstamos activos.
            List<Prestamo> prestamosUsuario = prestamoServicio.listarDeAltaUsuario(idUsuario);
            if (prestamosUsuario.size() >= 4) {
                throw new Exception(" Ha alcanzado el límite de 4 prestamos activos. Por favor debe registrar una devolución para solicitar un nuevo préstamo.");
            } else {
                // Registro del Préstamo:
                prestamoServicio.agregarPrestamo(fechaPrestamo, fechaDevolucion, libro.getId(), usuario.getId());
                // Mensaje de éxito:
                model.addAttribute("success", "El préstamo del libro '" + libro.getTitulo().toUpperCase() + "' fue registrado en forma exitosa. "
                        + "Quedan " + libro.getEjemplaresRestantes() + " ejemplares disponibles.");
            }
        } catch (Exception e) {
            if (e.getMessage() == null || fechaDevolucion == null || idLibro == null) {
                model.addAttribute("error", "ERROR en la elección de registrar Préstamo: faltó completar algún dato.");
            } else {
                model.addAttribute("error", "ERROR en la elección de registrar el préstamo : " + e.getMessage());
            }
        }
        List<Autor> autores = autorServicio.findAll();
        model.addAttribute("autores", autores);
        List<Libro> libros = libroServicio.findAll();
        model.addAttribute("libros", libros);
        return "inicio.html";
    }

    @GetMapping("/admin-prestamos-usuario/{id}")
    public String verPrestamosUsuario(ModelMap model, @PathVariable String id) {
        Usuario usuario = usuarioServicio.getById(id);
        model.addAttribute("usuarioPrestamo", usuario);
        List<Libro> libros = libroServicio.findAll();
        model.addAttribute("libros", libros);
        List<Usuario> usuarios = usuarioServicio.buscarActivos();
        model.addAttribute("usuarios", usuarios);
        List<Prestamo> prestamosAlta = prestamoServicio.listarDeAltaUsuario(id);
        model.addAttribute("prestamosAlta", prestamosAlta);
        List<Prestamo> prestamosBaja = prestamoServicio.listarDeBajaUsuario(id);
        model.addAttribute("prestamosBaja", prestamosBaja);
        return "prestamos-cliente.html";
    }
}
