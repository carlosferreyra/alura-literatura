package com.alura.literatura.repository;

import com.alura.literatura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    Optional<Libro> findByTituloContainingIgnoreCase(String nombreLibro);
    @Query("SELECT l FROM Libro l WHERE :lenguaje MEMBER OF l.lenguajes")
    List<Libro> findLibrosByLenguaje(@Param("lenguaje") String lenguaje);
    @Query("SELECT DISTINCT l.lenguajes FROM Libro l")
    List<String> findAllLenguajes();
}
