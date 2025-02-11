package apartado3;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class PeliculaController {

  private final PeliculaRepository repository;

  PeliculaController(PeliculaRepository repository) {
    this.repository = repository;
  }


  // Aggregate root
  // tag::get-aggregate-root[]
  
  @GetMapping("/peliculas")
  public CollectionModel<EntityModel<Pelicula>> cogerTodasPeliculas() {
      List<EntityModel<Pelicula>> peliculas = repository.findAll().stream()
              .map(pelicula -> EntityModel.of(pelicula,
                      linkTo(methodOn(PeliculaController.class).cogerPelicula(pelicula.getId())).withSelfRel(),
                      linkTo(methodOn(PeliculaController.class).cogerTodasPeliculas()).withRel("peliculas")))
              .toList();

      return CollectionModel.of(peliculas,
              linkTo(methodOn(PeliculaController.class).cogerTodasPeliculas()).withSelfRel());
  }
  // end::get-aggregate-root[]

  @PostMapping("/peliculas")
  public ResponseEntity<EntityModel<Pelicula>> nuevaPelicula(@RequestBody Pelicula newPelicula) {
      Pelicula peliculaCreada = repository.save(newPelicula);

      // Crear modelo con enlaces HATEOAS
      EntityModel<Pelicula> entityPelicula = EntityModel.of(peliculaCreada,
              linkTo(methodOn(PeliculaController.class).cogerPelicula(peliculaCreada.getId())).withSelfRel(),
              linkTo(methodOn(PeliculaController.class).cogerTodasPeliculas()).withRel("peliculas"));

      return ResponseEntity
              .created(linkTo(methodOn(PeliculaController.class).cogerPelicula(peliculaCreada.getId())).toUri())
              .body(entityPelicula);
  }


  // Single item
  
  @GetMapping("/peliculas/{id}")
  public EntityModel<Pelicula> cogerPelicula(@PathVariable Long id) {
      Pelicula pelicula = repository.findById(id)
              .orElseThrow(() -> new PeliculaNotFoundException(id));

      return EntityModel.of(pelicula,
              linkTo(methodOn(PeliculaController.class).cogerPelicula(id)).withSelfRel(),
              linkTo(methodOn(PeliculaController.class).cogerTodasPeliculas()).withRel("peliculas"));
  }


  @PutMapping("/peliculas/{id}")
  ResponseEntity<Pelicula> reemplazarPelicula(@RequestBody Pelicula newEmployee, @PathVariable Long id) {
    
    return repository.findById(id)
      .map(employee -> {
        employee.setNombre(newEmployee.getNombre());
        employee.setDirector(newEmployee.getDirector());
        employee.setClasificacion(newEmployee.getClasificacion());
        return ResponseEntity.ok(repository.save(employee));
      })
      .orElseThrow(() -> new PeliculaNotFoundException(id));
  }
  


  @DeleteMapping("/peliculas/{id}")
  void borrarPelicula(@PathVariable Long id) {
    repository.deleteById(id);
  }
}