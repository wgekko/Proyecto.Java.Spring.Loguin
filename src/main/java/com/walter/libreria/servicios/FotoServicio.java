package com.walter.libreria.servicios;

import com.walter.libreria.entidades.Foto;
import com.walter.libreria.repositorios.FotoRepositorio;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
public class FotoServicio {

    @Autowired
    private FotoRepositorio fotoRepositorio;

    @Transactional
    public Foto guardar(MultipartFile archivo) throws IOException {
        if (archivo != null && !archivo.isEmpty()) {
            try {
                Foto foto = new Foto();
                foto.setMime(archivo.getContentType());
                foto.setNombre(archivo.getName());      
                foto.setContenido(archivo.getBytes());
                return fotoRepositorio.save(foto);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        return null;
    }
   
    @Transactional
    public Foto actualizar(String idFoto, MultipartFile archivo) throws IOException {
        if (archivo != null) {
            try {
                Foto foto = new Foto();
                if (idFoto != null) {
                    foto = fotoRepositorio.getById(idFoto);
                }              
                foto.setMime(archivo.getContentType());
                foto.setNombre(archivo.getName());              
                foto.setContenido(archivo.getBytes());
                return fotoRepositorio.save(foto);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        return null;
    }

}
