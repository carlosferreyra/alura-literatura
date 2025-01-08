package com.alura.literatura.principal;

import com.alura.literatura.dto.DatosAPI;
import com.alura.literatura.dto.DatosAutor;
import com.alura.literatura.dto.DatosLibro;
import com.alura.literatura.model.Autor;
import com.alura.literatura.model.Idioma;
import com.alura.literatura.model.Libro;
import com.alura.literatura.repository.AutorRepository;
import com.alura.literatura.repository.LibroRepository;
import com.alura.literatura.service.ConsumoAPI;
import com.alura.literatura.service.ConvierteDatos;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://gutendex.com/books/?search=";
    private ConvierteDatos conversor = new ConvierteDatos();
    private LibroRepository libroRepository;
    private AutorRepository autorRepository;

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
        this.libroRepository = libroRepository;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Buscar libro por titulo 
                    2 - Listar libros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos en un determinado año
                    5 - Listar libros por idioma
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    registrarLibroDesdeAPI();
                    break;
                case 2:
                    ListarLibrosRegistrados();
                    break;
                case 3:
                    ListarAutoresRegistrados();
                    break;
                case 4:
                    ListarAutoresVivos();
                    break;
                case 5:
                    ListarLibrosPorIdioma();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }

    }

    private Optional<DatosLibro> getDatosLibro() {
        System.out.println("Escribe el nombre del libro que deseas buscar");
        var nombreLibro = teclado.nextLine();
        var url = URL_BASE + nombreLibro.replace(" ", "+");
        System.out.println(url);
        var json = consumoApi.obtenerDatos(url);
        DatosAPI respuestaAPI = conversor.obtenerDatos(json, DatosAPI.class);
        List<DatosLibro> libros = respuestaAPI.libros();
        return libros.stream().findFirst();
    }

    private void registrarLibroDesdeAPI() {
        Optional<DatosLibro> datosLibro = getDatosLibro();
        if (datosLibro.isEmpty()) {
            System.out.println("No se encontraron libros con el nombre ingresado, intente de nuevo");
        } else {
            DatosAutor datosAutor = datosLibro.get().autores().get(0);
            Autor autor = new Autor(datosAutor.nombre(), datosAutor.anioNacimiento(), datosAutor.anioMuerte());

            Optional<Libro> libroExistente = libroRepository.findById(datosLibro.get().id());
            if (libroExistente.isPresent()) {
                System.out.println("El libro ya existe en la base de datos: " + libroExistente.get());
                return;
            }

            List<Idioma> idiomas = datosLibro.get().lenguajes().stream()
                    .map(String::toUpperCase)
                    .filter(lang -> lang.equals("ES") || lang.equals("EN") || lang.equals("FR") || lang.equals("PT"))
                    .map(Idioma::valueOf)
                    .toList();
            Libro nuevoLibro = new Libro(datosLibro.get().id(), datosLibro.get().titulo(), autor, idiomas, datosLibro.get().descargas());
            autorRepository.save(autor);
            System.out.println("Autor registrado exitosamente: " + autor);
            libroRepository.save(nuevoLibro);
            System.out.println("Libro registrado exitosamente: " + nuevoLibro);
        }
    }

    private void ListarLibrosRegistrados() {
        List<Libro> libros = libroRepository.findAll();
        System.out.println("=".repeat(50));
        System.out.printf("| %-5s | %-30s | %-10s |\n", "ID", "Título", "Descargas");
        System.out.println("=".repeat(50));
        libros.stream()
                .sorted(Comparator.comparing(Libro::getTitulo))
                .forEach(libro -> System.out.printf("| %-5d | %-30s | %-10d |\n",
                        libro.getId(),
                        libro.getTitulo(),
                        libro.getDescargas()));
        System.out.println("=".repeat(50));
    }

    private void ListarAutoresRegistrados() {
        List<Autor> autores = autorRepository.findAll();
        autores.stream()
                .sorted(Comparator.comparing(Autor::getNombre))
                .forEach(autor -> System.out.printf("Nombre: %s | Año de Nacimiento: %d | Año de Muerte: %s%n",
                        autor.getNombre(),
                        autor.getAnioNacimiento(),
                        autor.getAnioMuerte() != null ? autor.getAnioMuerte() : "N/A"));
    }

    private void ListarAutoresVivos() {
        System.out.println("Ingrese el año en el que desea buscar autores vivos:");
        int anio = teclado.nextInt();
        teclado.nextLine();

        List<Autor> autoresVivos = autorRepository.findAll().stream()
                .filter(autor -> autor.getAnioNacimiento() <= anio &&
                        (autor.getAnioMuerte() == null || autor.getAnioMuerte() > anio))
                .sorted(Comparator.comparing(Autor::getNombre))
                .toList();

        if (autoresVivos.isEmpty()) {
            System.out.println("No se encontraron autores vivos en el año especificado.");
        } else {
            autoresVivos.forEach(autor -> System.out.printf("Nombre: %s | Año de Nacimiento: %d | Año de Muerte: %s%n",
                    autor.getNombre(),
                    autor.getAnioNacimiento(),
                    autor.getAnioMuerte() != null ? autor.getAnioMuerte() : "N/A"));
        }
    }

    private void ListarLibrosPorIdioma() {
        System.out.println("Seleccione el idioma por el cual desea filtrar los libros:");
        System.out.println("1 - Español (ES)");
        System.out.println("2 - Inglés (EN)");
        System.out.println("3 - Francés (FR)");
        System.out.println("4 - Portugués (PT)");
        System.out.println("Ingrese un número entre 1 y 4:");

        int opcion = teclado.nextInt();
        teclado.nextLine();

        String idioma;
        switch (opcion) {
            case 1 -> idioma = "ES";
            case 2 -> idioma = "EN";
            case 3 -> idioma = "FR";
            case 4 -> idioma = "PT";
            default -> {
                System.out.println("Opción inválida. Debe ingresar un número entre 1 y 4. Intente de nuevo.");
                return;
            }
        }

        List<Libro> librosPorIdioma = libroRepository.findAll().stream()
                .filter(libro -> libro.getIdiomas().contains(Idioma.valueOf(idioma)))
                .sorted(Comparator.comparing(Libro::getTitulo))
                .toList();

        if (librosPorIdioma.isEmpty()) {
            System.out.println("No se encontraron libros en el idioma especificado.");
        } else {
            librosPorIdioma.forEach(libro -> System.out.printf("ID: %d | Título: %s | Autor: %s | Idiomas: %s | Descargas: %d%n",
                    libro.getId(), libro.getTitulo(), libro.getAutor().getNombre(), libro.getIdiomas(), libro.getDescargas()));
        }
    }
}
