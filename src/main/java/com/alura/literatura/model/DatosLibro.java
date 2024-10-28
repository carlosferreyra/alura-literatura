package com.alura.literatura.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosLibro(
    @JsonAlias("id") Long id,
    @JsonAlias("title") String titulo,
    @JsonAlias("authors") List<Autor> autores,
    @JsonAlias("languages") List<String> lenguajes,
    @JsonAlias("download_count") Integer descargas
) {
}
