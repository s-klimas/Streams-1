package pl.sebastianklimas.streams.repos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.sebastianklimas.streams.models.Customer;


import java.util.List;

@Repository
public interface CustomerRepo extends CrudRepository<Customer, Long> {

	List<Customer> findAll();
}
