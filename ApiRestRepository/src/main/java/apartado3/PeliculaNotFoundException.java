package apartado3;

class PeliculaNotFoundException extends RuntimeException {

	PeliculaNotFoundException(Long id) {
    super("No se ha podido encontrar la pelicula " + id);
  }
	
}
