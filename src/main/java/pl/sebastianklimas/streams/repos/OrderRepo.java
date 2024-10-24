package pl.sebastianklimas.streams.repos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.sebastianklimas.streams.models.Order;

import java.util.List;

@Repository
public interface OrderRepo extends CrudRepository<Order, Long> {

	List<Order> findAll();
}
