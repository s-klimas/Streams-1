package pl.sebastianklimas.streams;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.sebastianklimas.streams.models.Customer;
import pl.sebastianklimas.streams.models.Order;
import pl.sebastianklimas.streams.models.Product;
import pl.sebastianklimas.streams.repos.CustomerRepo;
import pl.sebastianklimas.streams.repos.OrderRepo;
import pl.sebastianklimas.streams.repos.ProductRepo;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class Exercises implements CommandLineRunner {
    private CustomerRepo customerRepo;
    private OrderRepo orderRepo;
    private ProductRepo productRepo;

    public Exercises(CustomerRepo customerRepo, OrderRepo orderRepo, ProductRepo productRepo) {
        this.customerRepo = customerRepo;
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
    }

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        log.info("Excercise 1:");
        exercise1().forEach(product -> log.info(product.toString()));
        log.info("Excercise 2 (from order):");
        exercise2FromOrder().forEach(order -> log.info(order.toString()));
        log.info("Excercise 2 (from product):");
        exercise2FromProduct().forEach(order -> log.info(order.toString()));
        log.info("Excercise 3:");
        exercise3WithMap().forEach(product -> log.info(product.toString()));
        log.info("Excercise 4:");
        exercise4().forEach(order -> log.info(order.toString()));
        log.info("Excercise 5:");
        log.info(exercise5UsingMin().toString());
        log.info(exercise5UsingSorted().toString());
        log.info("Excercise 6:");
        exercise6().forEach(order -> log.info(order.toString()));
        log.info("Excercise 7:");
        exercise7().forEach(product -> log.info(product.toString()));
        log.info("Excercise 8:");
        log.info(String.valueOf(exercise8()));
        log.info("Excercise 9:");
        log.info(String.valueOf(exercise9()));
        log.info("Excercise 10:");
        log.info(exercise10().toString());
        log.info("Excercise 11:");
        log.info(exercise11().toString());
        log.info("Excercise 12:");
        log.info(exercise12Simpler().toString());
        log.info("Excercise 13:");
        log.info(exercise13().toString());
        log.info("Excercise 14:");
        log.info(exercise14().toString());
        log.info("Excercise 15:");
        log.info(exercise15().toString());
    }

    /* Obtain a list of products belongs to category “Books” with price > 100 */
    private List<Product> exercise1() {
        List<Product> result = productRepo.findAll()
                .stream()
                .filter(p -> p.getCategory().equalsIgnoreCase("books"))
                .filter(p -> p.getPrice() > 100)
                .toList();
        return result;
    }
    /* Obtain a list of order with products belong to category “Baby” */
    private List<Order> exercise2FromProduct() {
        List<Order> result = productRepo.findAll().stream()
                .filter(product -> product.getCategory().equalsIgnoreCase("baby"))
                .map(product -> product.getOrders().stream().toList())
                .flatMap(List::stream)
                .distinct()
                .sorted(Comparator.comparingLong(Order::getId))
                .toList();
        return result;
    }
    private List<Order> exercise2FromOrder() {
        List<Order> result = orderRepo.findAll().stream()
                .filter(order -> order.getProducts().stream()
                        .anyMatch(product -> product.getCategory().equalsIgnoreCase("baby")))
                .toList();
        return result;
    }
    /* Obtain a list of product with category = “Toys” and then apply 10% discount */
    private void exercise3WithForEach() {
        productRepo.findAll().stream()
                .filter(product -> product.getCategory().equalsIgnoreCase("toys"))
                .forEach(product -> product.setPrice(0.9 * product.getPrice()));
    }
    private List<Product> exercise3WithMap() {
        List<Product> result = productRepo.findAll().stream()
                .filter(product -> product.getCategory().equalsIgnoreCase("toys"))
                .map(product -> product.withPrice(0.9 * product.getPrice()))
                .toList();
        return result;
    }
    /* Obtain a list of products ordered by customer of tier 2 between 01-Feb-2021 and 01-Apr-2021 */
    private List<Product> exercise4() {
        List<Product> result = orderRepo.findAll().stream()
                .filter(order -> order.getCustomer().getTier() == 2)
                .filter(order -> order.getOrderDate().isAfter(LocalDate.of(2021, 2, 1)))
                .filter(order -> order.getOrderDate().isBefore(LocalDate.of(2021, 4, 1)))
                .flatMap(order -> order.getProducts().stream())
                .distinct()
                .sorted(Comparator.comparingLong(Product::getId))
                .toList();
        return result;
    }
    /* Get the cheapest products of “Books” category */
    private Product exercise5UsingMin() {
        Product result = productRepo.findAll().stream()
                .filter(product -> product.getCategory().equalsIgnoreCase("books"))
                .min(Comparator.comparingDouble(Product::getPrice))
                .orElseThrow();
        return result;
    }
    private Product exercise5UsingSorted() {
        Product result = productRepo.findAll().stream()
                .filter(product -> product.getCategory().equalsIgnoreCase("books"))
                .sorted(Comparator.comparingDouble(Product::getPrice))
                .findFirst()
                .orElseThrow();
        return result;
    }
    /* Get the 3 most recent placed order */
    private List<Order> exercise6() {
        List<Order> result = orderRepo.findAll().stream()
                .sorted(Comparator.comparing(Order::getOrderDate).reversed())
                .limit(3)
                .toList();
        return result;
    }
    /* Get a list of orders which were ordered on 15-Mar-2021, log the order records to the console and then return its product list */
    private List<Product> exercise7() {
        List<Product> result = orderRepo.findAll().stream()
                .filter(order -> order.getOrderDate().isEqual(LocalDate.of(2021, 3, 15)))
                .peek(order -> log.info(order.toString()))
                .flatMap(order -> order.getProducts().stream())
                .distinct()
                .sorted(Comparator.comparing(Product::getId))
                .toList();
        return result;
    }
    /* Calculate total lump sum of all orders placed in Feb 2021 */
    private double exercise8() {
        double result = orderRepo.findAll().stream()
                .filter(order -> order.getOrderDate().isAfter(LocalDate.of(2021, 1, 31)))
                .filter(order -> order.getOrderDate().isBefore(LocalDate.of(2021, 3, 1)))
                .flatMap(order -> order.getProducts().stream())
                .mapToDouble(Product::getPrice)
                .sum();
        return result;
    }
    /* Calculate order average payment placed on 14-Mar-2021 */
    private double exercise9() {
        double result = orderRepo.findAll().stream()
                .filter(order -> order.getOrderDate().isEqual(LocalDate.of(2021, 3, 13)))
                .mapToDouble(order -> order.getProducts().stream()
                        .mapToDouble(Product::getPrice)
                        .sum())
                .average()
                .orElse(0.0);
        return result;
    }
    private double exercise9WrongCalculatingAveragePriceOfSoldProducts() {
        double result = orderRepo.findAll()
        	        .stream()
        	        .filter(o -> o.getOrderDate().isEqual(LocalDate.of(2021, 3, 15)))
        	        .flatMap(o -> o.getProducts().stream())
        	        .mapToDouble(p -> p.getPrice())
        	        .average().getAsDouble();
        return result;
    }
    /* Obtain a collection of statistic figures (i.e. sum, average, max, min, count) for all products of category “Books” */
    private DoubleSummaryStatistics exercise10() {
        DoubleSummaryStatistics result = productRepo.findAll().stream()
                .filter(product -> product.getCategory().equalsIgnoreCase("books"))
                .mapToDouble(Product::getPrice)
                .summaryStatistics();
        return result;
    }
    /* Obtain a data map with order id and order’s product count */
    private Map<Long, Integer> exercise11() {
        Map<Long, Integer> result = orderRepo.findAll().stream()
                .collect(Collectors.toMap(
                        Order::getId,
                        order -> order.getProducts().size()
                ));
        return result;
    }
    /* Produce a data map with order records grouped by customer */
    private Map<Long, List<Order>> exercise12TwoStreams() {
        Map<Long, List<Order>> result = customerRepo.findAll().stream()
                .collect(Collectors.toMap(
                        Customer::getId,
                        customer -> orderRepo.findAll().stream()
                                .filter(order -> order.getCustomer().equals(customer))
                                .toList()
                ));
        return result;
    }
    private Map<Customer, List<Order>> exercise12Simpler() {
        Map<Customer, List<Order>> result = orderRepo.findAll().stream()
                .collect(Collectors.groupingBy(Order::getCustomer));
        return result;
    }
    /* Produce a data map with order record and product total sum */
    private Map<Order, Double> exercise13() {
        Map<Order, Double> result = orderRepo.findAll().stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        order -> order.getProducts().stream()
                                .mapToDouble(Product::getPrice)
                                .sum()
                ));
        return result;
    }
    /* Obtain a data map with list of product name by category */
    private Map<String, List<Product>> exercise14() {
        Map<String, List<Product>> result = productRepo.findAll().stream()
                .collect(Collectors.groupingBy(
                        Product::getCategory,
                        Collectors.toList()
                ));
        return result;
    }
    /* Get the most expensive product by category */
    private Map<String, Optional<Product>> exercise15() {
        Map<String, Optional<Product>> result = productRepo.findAll().stream()
                .collect(Collectors.groupingBy(
                        Product::getCategory,
                        Collectors.maxBy(Comparator.comparing(Product::getPrice))
                ));
        return result;
    }
}
