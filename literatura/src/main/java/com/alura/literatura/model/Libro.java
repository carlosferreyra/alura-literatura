package com.alura.literatura.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



/*ARREGLAR TODA ESTA TABLA DE LA BBDD*/
@Entity
@Table(name = "libros")
public class Libro {
    @Id
    private Long Id;
    @Column(unique = true)
    private String titulo;
    @OneToMany(mappedBy = "libro",cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private List<Autor> autores;
    @ElementCollection
    private List<String> lenguajes=new ArrayList<String>();
    private Integer descargas;

    public Libro(){}

    public Libro(DatosLibro datosLibro){
        this.Id = datosLibro.id();
        this.titulo = datosLibro.titulo();
        this.autores = datosLibro.autores();
        this.lenguajes = new ArrayList<>(datosLibro.lenguajes());
        this.descargas = datosLibro.descargas();
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public List<Autor> getAutores() {
        return autores;
    }

    public void setAutores(List<Autor> autores) {
        this.autores = autores;
    }

    public List<String> getLenguajes() {
        return lenguajes;
    }

    public void setLenguajes(List<String> lenguajes) {
        this.lenguajes = lenguajes;
    }

    public Integer getDescargas() {
        return descargas;
    }

    public void setDescargas(Integer descargas) {
        this.descargas = descargas;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }
}
