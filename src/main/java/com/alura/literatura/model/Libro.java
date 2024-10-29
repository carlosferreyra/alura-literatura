package com.alura.literatura.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/*ARREGLAR TODA ESTA TABLA DE LA BBDD*/
@Entity
@Table(name = "libros")
public class Libro {
    @Id
    private Long Id;
    @Column(unique = true)
    private String titulo;
    @ManyToMany(mappedBy = "autores",cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Autor> autores;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> lenguajes;
    private Integer descargas;

    public Libro(){
        this.lenguajes = new ArrayList<>();
    }

    public Libro(DatosLibro datosLibro){
        this.Id = datosLibro.id();
        this.titulo = datosLibro.titulo();
        this.lenguajes = new ArrayList<>(datosLibro.lenguajes());
        this.descargas = datosLibro.descargas();
    }
    @Override
    public String toString() {
        return "-----Libro [id=" + Id + "]-----\n " +
                "Titulo: '" + titulo + "',\n" +
                "Autor/es: " + autores.stream().map(Autor::getNombre).collect(Collectors.joining("- ")) + "\n"+
                "Lenguaje/s: " + String.join(", ", lenguajes) + "\n"+
                "Descargas: " + descargas + "\n"+
                "---------------";
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
