package pl.sebastianklimas.streams.repos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.sebastianklimas.streams.models.Product;

import java.util.List;

@Repository
public interface ProductRepo extends CrudRepository<Product, Long> {

	List<Product> findAll();
}
