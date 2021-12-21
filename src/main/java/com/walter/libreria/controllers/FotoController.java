package com.walter.libreria.controllers;

import com.walter.libreria.entidades.Libro;
import com.walter.libreria.entidades.Usuario;
import com.walter.libreria.servicios.LibroServicio;
import com.walter.libreria.servicios.UsuarioServicio;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/foto")
public class FotoController {

    @Autowired
    private LibroServicio libroServicio;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @GetMapping("/libro/{id}")
    public ResponseEntity<byte[]> fotoLibro(@PathVariable String id) throws Exception {
        try {        
            Libro libro = libroServicio.getById(id);
            if (libro.getFoto() == null) {
                throw new Exception("OOPPPSSS!!, El Libro no tiene una foto de portada..");
            }
            byte[] foto = libro.getFoto().getContenido();        
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);         
            return new ResponseEntity<>(foto, headers, HttpStatus.OK);
        } catch (Exception ex) {
            Logger.getLogger(FotoController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
  
    @GetMapping("/usuario/{id}")
    public ResponseEntity<byte[]> fotoUsuario(@PathVariable String id) throws Exception {
        try {           
            Usuario usuario = usuarioServicio.getById(id);
            if (usuario.getFoto() == null) {
                throw new Exception("OOPPSSS!!!, El Usuario no tiene una foto de perfil...");
            }
            byte[] foto = usuario.getFoto().getContenido();         
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);         
            return new ResponseEntity<>(foto, headers, HttpStatus.OK);
        } catch (Exception ex) {
            Logger.getLogger(FotoController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
