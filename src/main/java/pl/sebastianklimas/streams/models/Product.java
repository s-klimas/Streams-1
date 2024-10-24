package pl.sebastianklimas.streams.models;

import lombok.*;

import jakarta.persistence.*;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String category;
	
	@With
	private Double price;
	
	@ManyToMany(mappedBy = "products")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Set<Order> orders;

	@Override
	public String toString() {
		return "Product " + id +", Name: " + name + ", Category: " + category + ", Price: " + price;
	}
}
