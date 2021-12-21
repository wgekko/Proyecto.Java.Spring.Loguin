package com.walter.libreria.controllers;

import com.walter.libreria.entidades.Autor;
import com.walter.libreria.entidades.Libro;
import com.walter.libreria.servicios.AutorServicio;
import com.walter.libreria.servicios.LibroServicio;
import com.walter.libreria.servicios.UsuarioServicio;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/")
public class PortalController {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private LibroServicio libroServicio;

    @Autowired
    private AutorServicio autorServicio;
 
    @GetMapping("/")
    public String index(@RequestParam(required = false) String error, @RequestParam(required = false) String logout, ModelMap model) {
        if (error != null) {
            model.put("error", "ERROR, el Usuario o clave son incorrectos....");
        }
        if (logout != null) {
            model.put("logout", "Ha cerrado sesión correctamente.");
        }
        return "index.html";
    }
  
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USUARIO')")
    @GetMapping("/inicio")
    public String inicio(ModelMap model) {
        List<Autor> autores = autorServicio.findAll();
        model.addAttribute("autores", autores);
        model.addAttribute("autorSelected", null);
        List<Libro> libros = libroServicio.findAll();
        model.addAttribute("libros", libros);
        return "inicio.html";
    }
  
    @PostMapping("/registrar")
    public String registrar(ModelMap model, MultipartFile archivo, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String dni, @RequestParam String telefono, @RequestParam String mail, @RequestParam String clave, @RequestParam String clave2) {
        try {
            usuarioServicio.registrar(archivo, nombre, apellido, dni, telefono, mail, clave, clave2);
        } catch (Exception e) {
                       model.put("errorReg", "ERROR en la elección de intentar registrarse : " + e.getMessage() + "Por Favor, vuelva a intentarlo...");
       
            model.put("nombre", nombre);
            model.put("apellido", apellido);
            model.put("dni", dni);
            model.put("telefono", telefono);
            model.put("mail", mail);
            model.put("archivo", archivo);
            return "index.html";
        }
        model.put("success", "¡Se ha registrado en forma exitosa!!! Puede iniciar sesión sin problemas ...");
        return "index.html";
    }
  
    @GetMapping("/autor")
    public String autores(ModelMap model, String idAutor) {
        model.addAttribute("autorSelected", autorServicio.getById(idAutor));
        List<Autor> autores = autorServicio.findAll();
        model.addAttribute("autores", autores);
        List<Libro> libros = libroServicio.buscarPorAutor(idAutor);
        model.addAttribute("libros", libros);
        return "inicio.html";
    }
}
