package com.alura.literatura.repository;

import com.alura.literatura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    //query aqui
    @Query("SELECT a FROM Autor a WHERE a.muerte IS NULL OR a.muerte > :anio")
    List<Autor> findAutoresVivos(@Param("anio") Integer anio);
}
