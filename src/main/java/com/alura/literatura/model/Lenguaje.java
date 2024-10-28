package com.alura.literatura.model;

import jakarta.persistence.*;

@Entity
@Table(name = "lenguajes")
public class Lenguaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String prefijo;
    @ManyToOne
    private Libro libro;

    public Lenguaje(String prefijo, Libro libro) {
        this.prefijo = prefijo;
        this.libro = libro;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }
}
