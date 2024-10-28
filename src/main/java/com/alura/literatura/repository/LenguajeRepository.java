package com.alura.literatura.repository;

import com.alura.literatura.model.Lenguaje;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LenguajeRepository extends JpaRepository<Lenguaje, String > {
    List<Lenguaje> findByPrefijo(String prefijo);
}
