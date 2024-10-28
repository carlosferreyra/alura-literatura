package com.alura.literatura.principal;

import com.alura.literatura.model.Autor;
import com.alura.literatura.model.DatosLibro;
import com.alura.literatura.model.Lenguaje;
import com.alura.literatura.model.Libro;
import com.alura.literatura.repository.AutorRepository;
import com.alura.literatura.repository.LenguajeRepository;
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
    private LenguajeRepository repositorioLenguaje;
    private List<Libro> libros;

    public Principal(LibroRepository repositoryLibro, AutorRepository repositoryAutor) {
        this.repositorioLibro = repositoryLibro;
        this.repositorioAutor = repositoryAutor;
    }

    public void muestraElMenu() throws Exception {
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
                    buscarRegistrado();
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
    private Optional<Libro> buscarLibroRegistrado(String titulo) {
        return repositorioLibro.findByTituloContainingIgnoreCase(teclado.nextLine());
    }
    private void registrarLibro() throws Exception {
        System.out.println("Ingrese el nombre del libro que desea registrar: ");
        var nombreLibro = teclado.nextLine();
        Optional<Libro> libro = buscarLibroRegistrado(nombreLibro);
        if (libro.isPresent()) {
            System.out.println("El libro ya existe en la base de datos\n");
            Libro unLibro = libro.get();
            System.out.println("-----LIBRO-----");
            System.out.println(unLibro);
            System.out.println("-----LIBRO-----");
        }
        else {
            // agregar nuevo libro
            DatosLibro datosLibro = agregarDatosLibro(nombreLibro);
            Libro instanciaLibro = new Libro(datosLibro);
            repositorioLibro.save(instanciaLibro);
            System.out.println("-----LIBRO-----");
            System.out.println(instanciaLibro);
            System.out.println("-----LIBRO-----");

        }
    }
    private void buscarRegistrado(){
        System.out.println("Ingrese el nombre del libro que desea buscar en la bbdd: ");
        var nombreLibro = teclado.nextLine();
        Optional<Libro> libro = buscarLibroRegistrado(nombreLibro);
        if (libro.isPresent()) {
            System.out.println("-----LIBRO-----");
            libro.stream()
                    .map(l ->"Titulo: "+ l.getTitulo() +"\n" +
                            "Autor: " + l.getAutores().stream().map(a -> a.getNombre().split(",").toString()) +"\n" +
                            "Idioma: " + l.getLenguajes().stream().map(Lenguaje::getPrefijo) +"\n" +
                            "Nro Descargas: " + l.getDescargas().toString() +"\n"
                            );
            System.out.println("---------------");
        }else {
            System.out.println("No existe el libro en la bbdd");
            }
        }

    private void listarAutoresRegistrados() {
        List<Autor> autores = repositorioAutor.findAll();
        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados");
        }else {
            autores.stream()
                    .sorted(Comparator.comparing(Autor::getNombre))
                    .forEach(System.out::println);
        }
    }
    private void listarAutoresVivos() {
        List<Autor> autores = repositorioAutor.findAll();
        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados");
        }
    }
    private void listarLibrosPorIdioma() {
        List<Lenguaje> lenguas = repositorioLenguaje.findAll();
        if (lenguas.isEmpty()) {
            System.out.println("No hay libros registrados");
            }
        else {

            System.out.println("elija el lenguaje para buscar libros: ");
            for (int i = 0; i < lenguas.size(); i++) {
                System.out.println(i+1") " + lenguas.get(i));
            }

            String filtro = teclado.nextLine()
            List<Libro> libros = repositorioLibro.findByLenguaje();
            if (libros.isEmpty()) {
                System.out.println("No hay libros registrados");
            } else {
                libros.stream()
                        .sorted(Comparator.comparing(Libro::getTitulo))
                        .forEach(System.out::println);
                }
            }
        }
}
