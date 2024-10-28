package com.alura.literatura.principal;

import com.alura.literatura.model.DatosAutor;
import com.alura.literatura.model.DatosLibro;
import com.alura.literatura.model.Libro;
import com.alura.literatura.repository.AutorRepository;
import com.alura.literatura.repository.LibroRepository;
import com.alura.literatura.service.ConsumoAPI;
import com.alura.literatura.service.ConvierteDatos;

import java.util.*;

public class Principal {
    // aca va el codigo principal de menu y ejecucion
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://gutendex.com/book";
    private ConvierteDatos conversor = new ConvierteDatos();
    private List<DatosLibro> datosLibro = new ArrayList<>();
    private LibroRepository repositorioLibro;
    private AutorRepository repositorioAutor;
    private List<Libro> libros;

    public Principal(LibroRepository repositoryLibro, AutorRepository repositoryAutor) {
        this.repositorioLibro = repositoryLibro;
        this.repositorioAutor = repositoryAutor;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Buscar libro por titulo
                    2 - Buscar libros registrados
                    3 - listar autores registrados
                    4 - listar autores vivos en un determinado año
                    5 - listar libros por idioma
                                  
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    registrarLibro();
                    break;
                case 2:
                    buscarLibroRegistrado();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:
                    listarAutoresVivos();
                case 5:
                    listarLibrosPorIdioma();
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }

    }

    private DatosLibro agregarDatosLibro(String titulo) {
        var json = consumoApi.obtenerDatos(URL_BASE+"s" + "?search="+titulo.replace(" ", "+"));
        return conversor.obtenerDatos(json, DatosLibro.class);
    }
    private void registrarLibro() throws Exception {
        System.out.println("Ingrese el nombre del libro que desea buscar: ");
        var nombreLibro = teclado.nextLine();
        Optional<Libro> libro = repositorioLibro.findByTituloContainingIgnoreCase(nombreLibro);
        if (libro.isPresent()) {
            System.out.println("El libro ya existe en la base de datos\n");
            Libro unLibro = libro.get();
            System.out.println(unLibro);
        }
        else {
            // agregar nuevo libro
            DatosLibro nuevo_libro = agregarDatosLibro(nombreLibro);

        }
    }

    private void mostrarLibrosRegistrados() {
        libros = repositorioLibro.findAll();
        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados");
        }else {
            libros.stream()
                    .sorted(Comparator.comparing(Libro::getTitulo))
                    .forEach(System.out::println);
        }
    }
}
