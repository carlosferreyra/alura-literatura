package com.alura.literatura.principal;

import com.alura.literatura.model.*;
import com.alura.literatura.repository.AutorRepository;
import com.alura.literatura.repository.LibroRepository;
import com.alura.literatura.service.ConsumoAPI;
import com.alura.literatura.service.ConvierteDatos;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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

    private Optional<DatosLibro> agregarDatosLibro(String titulo) {
        String url = URL_BASE +"s/?search="+ titulo.toLowerCase().replace(" ","%20");
        System.out.println(url);
        String json = consumoApi.obtenerDatos(url);
        List<DatosLibro> busqueda = conversor.obtenerDatos(json, DatosAPI.class).libros();
        System.out.println(busqueda);
        if (busqueda.isEmpty()) {

            return Optional.empty();
        }else {
            System.out.println(busqueda);
            return busqueda.stream().findFirst();
        }
    }
    private Optional<Libro> buscarLibroRegistrado(String titulo) {
        return repositorioLibro.findByTituloContainingIgnoreCase(titulo);
    }
    private void registrarLibro() throws Exception {
        System.out.println("Ingrese el nombre del libro que desea registrar: ");
        var nombreLibro = teclado.nextLine();

        Optional<Libro> libro = buscarLibroRegistrado(nombreLibro);
        if (libro.isPresent()) {
            System.out.println("El libro ya existe en la base de datos: \n");
            Libro unLibro = libro.get();

            System.out.println(unLibro.toString());

        }
        else {
            // agregar nuevo libro
            System.out.println("El libro no existe en la base de datos: Agregando.. \n");
            Optional<DatosLibro> datosLibro = agregarDatosLibro(nombreLibro);
            if (datosLibro.isPresent()) {
                System.out.println("Se ha encontrado un libro con dicho nombre: \n");
                DatosLibro libroEncontrado = datosLibro.get();
                List<DatosAutor> datosAutores = libroEncontrado.autores();
                List<Autor> autores = new ArrayList<>();
                for (DatosAutor datosAutor : datosAutores) {
                    Autor autor = new Autor(datosAutor);
                    autores.add(autor);
                }
                Libro nuevo_libro = new Libro(libroEncontrado);
                nuevo_libro.setAutores(autores);
                repositorioLibro.save(nuevo_libro);

                System.out.println("Nuevo libro agregado: \n");
                System.out.println(nuevo_libro.toString());

            }else {
                System.out.println("El libro no encontrado en la API, intente nuevamente con otro nombre: \n");
            }
        }
    }
    private void buscarRegistrado(){
        System.out.println("Ingrese el nombre del libro que desea buscar en la bbdd: ");
        var nombreLibro = teclado.nextLine();
        Optional<Libro> libro = buscarLibroRegistrado(nombreLibro);
        if (libro.isPresent()) {
            System.out.println(libro.get().toString());
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
        System.out.println("Ingrese el año a buscar: \n");
        Integer anio = teclado.nextInt();
        List<Autor> autores = repositorioAutor.findAutoresVivos(anio);
        if (autores.isEmpty()) {
            System.out.println("No hay autores vivos");
        }else {
            System.out.println("Se han encontrado: ");
            autores.stream()
                    .sorted(Comparator.comparing(Autor::getNombre))
                    .forEach(System.out::println);
        }
    }

    private void listarLibrosPorIdioma() {
        System.out.println("Ingrese el prefijo de lenguaje a buscar: \n ");
        List<String> lenguajes = repositorioLibro.findAllLenguajes();
        int busqueda = -1;
        while (busqueda < 1 || busqueda > lenguajes.size()){
            for (int i = 0; i < lenguajes.size(); i++) {
                System.out.println(i+1+") " + lenguajes.get(i));
            }
            busqueda = teclado.nextInt();
        }
        List<Libro> libros = repositorioLibro.findLibrosByLenguaje(lenguajes.get(busqueda-1));
        libros.stream()
                .sorted(Comparator.comparing(Libro::getTitulo))
                .forEach(System.out::println);

    }
}
