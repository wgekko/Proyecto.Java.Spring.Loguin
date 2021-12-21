package com.walter.libreria.controllers;

import com.walter.libreria.entidades.Libro;
import com.walter.libreria.entidades.Prestamo;
import com.walter.libreria.entidades.Usuario;
import com.walter.libreria.servicios.LibroServicio;
import com.walter.libreria.servicios.PrestamoServicio;
import com.walter.libreria.servicios.UsuarioServicio;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private PrestamoServicio prestamoServicio;

    @Autowired
    private LibroServicio libroServicio;

    @GetMapping("/dashboard")
    public String homeAdmin(ModelMap model) {        
        List<Usuario> usuariosActivos = usuarioServicio.buscarActivos();
        model.addAttribute("usuariosActivos", usuariosActivos);
        List<Usuario> usuariosInactivos = usuarioServicio.buscarInactivos();
        model.addAttribute("usuariosInactivos", usuariosInactivos);
        return "admin.html";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(ModelMap model, @PathVariable String id) {
        try {
            usuarioServicio.eliminar(id);
            return "redirect:/admin/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "ERROR en la elección de  eliminar el usuario .");
            return "admin.html";
        }
    }

    @GetMapping("/habilitar/{id}")
    public String habilitar(ModelMap model, @PathVariable String id) {
        try {
            usuarioServicio.habilitar(id);
            return "redirect:/admin/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "ERROR en la elección de dar de alta al usuario.");
            return "admin.html";
        }
    }

    @GetMapping("/deshabilitar/{id}")
    public String deshabilitar(ModelMap model, @PathVariable String id) {
        try {
            usuarioServicio.deshabilitar(id);
            return "redirect:/admin/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Error en la elección de dar de baja al usuario.");
            return "admin.html";
        }
    }

    @GetMapping("/cambiar-rol/{id}")
    public String cambiarRol(ModelMap model, @PathVariable String id) {
        try {
            usuarioServicio.cambiarRol(id);
            return "redirect:/admin/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Error en la elección de modificar el rol.");
            return "admin.html";
        }
    }

    @GetMapping("/prestamos/admin-prestamos")
    public String administradorPrestamos(ModelMap model) {
        List<Libro> libros = libroServicio.findAll();
        model.addAttribute("libros", libros);
        List<Usuario> usuarios = usuarioServicio.buscarActivos();
        model.addAttribute("usuarios", usuarios);
        List<Prestamo> prestamosAlta = prestamoServicio.listarDeAlta();
        model.addAttribute("prestamosAlta", prestamosAlta);
        List<Prestamo> prestamosBaja = prestamoServicio.listarDeBaja();
        model.addAttribute("prestamosBaja", prestamosBaja);
        return "admin-prestamo.html";
    }

    @PostMapping("/prestamos/registrar-prestamo")
    public String registrarPrestamo(ModelMap model, @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaPrestamo, @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaDevolucion, String idLibro, String idUsuario) throws ParseException {
        Libro libro;
        Usuario usuario;
        try {        
            libro = libroServicio.getById(idLibro);       
            usuario = usuarioServicio.getById(idUsuario);         
            List<Prestamo> prestamosUsuario = prestamoServicio.listarDeAltaUsuario(idUsuario);
            if (prestamosUsuario.size() >= 4) {
                throw new Exception(" Ha alcanzado el límite de 4 prestamos activos por parte del Usuario. Debe registrar una devolución para solicitar un nuevo préstamo.");
            } else {               
                prestamoServicio.agregarPrestamo(fechaPrestamo, fechaDevolucion, libro.getId(), usuario.getId());              
                model.addAttribute("success", "El préstamo del libro '" + libro.getTitulo().toUpperCase() + "' al usuario '" + usuario.getNombre().toUpperCase() + " " + usuario.getApellido().toUpperCase() + "' fue registrado exitosamente. "
                        + "Quedan " + libro.getEjemplaresRestantes() + " ejemplares disponibles.");
            }
        } catch (Exception e) {
            if (e.getMessage() == null || fechaPrestamo == null || fechaDevolucion == null || idLibro == null) {
                model.addAttribute("error", "ERROR en la elección de registrar Préstamo: faltó completar algún campo.");
            } else {
                model.addAttribute("error", "ERROR en la eleccción de registrar Préstamo: " + e.getMessage());
            }
        }
        List<Prestamo> prestamosAlta = prestamoServicio.listarDeAlta();
        model.addAttribute("prestamosAlta", prestamosAlta);
        List<Prestamo> prestamosBaja = prestamoServicio.listarDeBaja();
        model.addAttribute("prestamosBaja", prestamosBaja);
        List<Libro> libros = libroServicio.findAll();
        model.addAttribute("libros", libros);
        List<Usuario> usuarios = usuarioServicio.buscarActivos();
        model.addAttribute("usuarios", usuarios);
        return "admin-prestamo.html";
    }

    @GetMapping("/prestamos/registar-prestamo-usuario/{id}")
    public String registrarPrestamoUsuario(ModelMap model, @PathVariable String id) {
        Usuario usuario = usuarioServicio.getById(id);
        model.addAttribute("usuarioSelected", usuario);
        List<Libro> libros = libroServicio.findAll();
        model.addAttribute("libros", libros);
        List<Usuario> usuarios = usuarioServicio.buscarActivos();
        model.addAttribute("usuarios", usuarios);
        List<Prestamo> prestamosAlta = prestamoServicio.listarDeAlta();
        model.addAttribute("prestamosAlta", prestamosAlta);
        List<Prestamo> prestamosBaja = prestamoServicio.listarDeBaja();
        model.addAttribute("prestamosBaja", prestamosBaja);
        return "admin-prestamo.html";
    }

    @GetMapping("/prestamos/admin-prestamos-usuario/{id}")
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
        return "admin-prestamo.html";
    }

    @GetMapping("/prestamos/registrar-devolucion/{idPrestamo}")
    public String registrarDevolucion(ModelMap model, @PathVariable String idPrestamo) throws Exception {
        try {
            Prestamo prestamo = prestamoServicio.buscarPorId(idPrestamo);
            Libro libro = prestamo.getLibro();
            Usuario usuario = prestamo.getUsuario();
            prestamoServicio.baja(idPrestamo);
            model.addAttribute("success", "La devolución del libro '" + libro.getTitulo().toUpperCase() + "' del usuario '" + usuario.getNombre().toUpperCase() + " " + usuario.getApellido().toUpperCase() + "' fue registrada exitosamente. "
                    + "Quedan " + libro.getEjemplaresRestantes() + " ejemplares disponibles.");
        } catch (Exception e) {
            if (e.getMessage() == null) {
                model.addAttribute("error", "ERROR en la elección de registrar Devolución. Intente nuevamente.");
            } else {
                model.addAttribute("error", "ERROR en la elección de registrar Devolución: " + e.getMessage());
            }
        }
        List<Prestamo> prestamosAlta = prestamoServicio.listarDeAlta();
        model.addAttribute("prestamosAlta", prestamosAlta);
        List<Prestamo> prestamosBaja = prestamoServicio.listarDeBaja();
        model.addAttribute("prestamosBaja", prestamosBaja);
        List<Libro> libros = libroServicio.findAll();
        model.addAttribute("libros", libros);
        List<Usuario> usuarios = usuarioServicio.buscarActivos();
        model.addAttribute("usuarios", usuarios);
        return "admin-prestamo.html";
    }

    @GetMapping("/prestamos/modificar-prestamo-datos/{idPrestamoModif}")
    public String datosPrestamo(ModelMap model, @PathVariable String idPrestamoModif) {
        Prestamo prestamo = prestamoServicio.buscarPorId(idPrestamoModif);
        model.addAttribute("prestamoModif", prestamo);
        return "modif-prestamo.html";
    }

    @PostMapping("/prestamos/modificar-prestamo")
    public String modificarPrestamo(ModelMap model, @RequestParam String id, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaPrestamo, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaDevolucion) throws ParseException {
        Libro libro = prestamoServicio.buscarPorId(id).getLibro();
        Usuario usuario = prestamoServicio.buscarPorId(id).getUsuario();
        try {    
            prestamoServicio.modificarPrestamo(id, fechaPrestamo, fechaDevolucion);
            model.addAttribute("success", "El préstamo del libro '" + libro.getTitulo().toUpperCase() + "' al usuario '" + usuario.getNombre().toUpperCase() + " " + usuario.getApellido().toUpperCase() + "' fue modificado exitosamente. "
                    + "Quedan " + libro.getEjemplaresRestantes() + " ejemplares disponibles.");
        } catch (Exception e) {
            if (e.getMessage() == null || fechaPrestamo == null || fechaDevolucion == null) {
                model.addAttribute("error", "ERROR en la elección  al modificar Préstamo: faltó completar algún dato.");
            } else {
                model.addAttribute("error", "ERROR en la elección de  modificar Préstamo: " + e.getMessage());
            }
        }
        List<Prestamo> prestamosAlta = prestamoServicio.listarDeAlta();
        model.addAttribute("prestamosAlta", prestamosAlta);
        List<Prestamo> prestamosBaja = prestamoServicio.listarDeBaja();
        model.addAttribute("prestamosBaja", prestamosBaja);
        List<Libro> libros = libroServicio.findAll();
        model.addAttribute("libros", libros);
        List<Usuario> usuarios = usuarioServicio.buscarActivos();
        model.addAttribute("usuarios", usuarios);
        return "admin-prestamo.html";
    }
  
    @GetMapping("/prestamos/eliminar-prestamo/{id}")
    public String eliminarPrestamo(ModelMap model, @PathVariable String id) {
        try {
            Libro libro = prestamoServicio.buscarPorId(id).getLibro();
            String tituloLibro = libro.getTitulo().toUpperCase();
            String nombreUsuario = prestamoServicio.buscarPorId(id).getUsuario().getNombre().toUpperCase() + ' ' + prestamoServicio.buscarPorId(id).getUsuario().getApellido().toUpperCase();
            prestamoServicio.eliminarPrestamo(id);
            model.addAttribute("success", "El préstamo del libro '" + tituloLibro + "' al usuario '" + nombreUsuario + "' fue eliminado exitosamente. "
                    + "Quedan " + libro.getEjemplaresRestantes() + " ejemplares disponibles.");
        } catch (Exception e) {
            if (e.getMessage() == null) {
                model.addAttribute("error", "ERROR en la elección de eliminar Préstamo: faltó completar algún dato.");
            } else {
                model.addAttribute("error", "ERROR en la elección de eliminar Préstamo: " + e.getMessage());
            }
        }
        List<Prestamo> prestamosAlta = prestamoServicio.listarDeAlta();
        model.addAttribute("prestamosAlta", prestamosAlta);
        List<Prestamo> prestamosBaja = prestamoServicio.listarDeBaja();
        model.addAttribute("prestamosBaja", prestamosBaja);
        List<Libro> libros = libroServicio.findAll();
        model.addAttribute("libros", libros);
        List<Usuario> usuarios = usuarioServicio.buscarActivos();
        model.addAttribute("usuarios", usuarios);
        return "admin-prestamo.html";
    }
}
