package com.walter.libreria.entidades;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Prestamo {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    private boolean alta;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fechaPrestamo;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fechaDevolucion;

    @OneToOne
    private Libro libro;

    @OneToOne
    private Usuario usuario;

  
    public String getId() {
        return id;
    }

   
    public void setId(String id) {
        this.id = id;
    }

   
    public boolean isAlta() {
        return alta;
    }

    
    public void setAlta(boolean alta) {
        this.alta = alta;
    }

   
    public Date getFechaPrestamo() {
        return fechaPrestamo;
    }

 
    public void setFechaPrestamo(Date fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }


    public Date getFechaDevolucion() {
        return fechaDevolucion;
    }

   
    public void setFechaDevolucion(Date fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

   
    public Libro getLibro() {
        return libro;
    }

 
    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    
    public Usuario getUsuario() {
        return usuario;
    }

   
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

}