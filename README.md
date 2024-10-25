# Streams-1
Java Stream API learning project.

The exercises are based on a data model — customer, order and product. Refer to the entity relationship diagram below, customers can place multiple orders and so it is a one-to-many relationship while the relationship between products and orders is many-to-many.

![Data_Model](https://github.com/user-attachments/assets/a68120af-0fe7-45c2-8d3a-6129adeb9418)


## Exercises
Some exercises have 2 solutions.

1 - Obtain a list of products belongs to category “Books” with price > 100
```java
    private List<Product> exercise1() {
        List<Product> result = productRepo.findAll()
                .stream()
                .filter(p -> p.getCategory().equalsIgnoreCase("books"))
                .filter(p -> p.getPrice() > 100)
                .toList();
        return result;
    }
```
2 - Obtain a list of order with products belong to category “Baby”
```java
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
```
3 - Obtain a list of product with category = “Toys” and then apply 10% discount
```java
    private List<Product> exercise3WithMap() {
        List<Product> result = productRepo.findAll().stream()
                .filter(product -> product.getCategory().equalsIgnoreCase("toys"))
                .map(product -> product.withPrice(0.9 * product.getPrice()))
                .toList();
        return result;
    }
```
4 - Obtain a list of products ordered by customer of tier 2 between 01-Feb-2021 and 01-Apr-2021
```java
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
```
5 - Get the cheapest products of “Books” category
```java
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
```
6 - Get the 3 most recent placed order
```java
    private List<Order> exercise6() {
        List<Order> result = orderRepo.findAll().stream()
                .sorted(Comparator.comparing(Order::getOrderDate).reversed())
                .limit(3)
                .toList();
        return result;
    }
```
7 - Get a list of orders which were ordered on 15-Mar-2021, log the order records to the console and then return its product list
```java
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
```
8 - Calculate total lump sum of all orders placed in Feb 2021
```java
    private double exercise8() {
        double result = orderRepo.findAll().stream()
                .filter(order -> order.getOrderDate().isAfter(LocalDate.of(2021, 1, 31)))
                .filter(order -> order.getOrderDate().isBefore(LocalDate.of(2021, 3, 1)))
                .flatMap(order -> order.getProducts().stream())
                .mapToDouble(Product::getPrice)
                .sum();
        return result;
    }
```
9 - Calculate order average payment placed on 14-Mar-2021
```java
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
```
10 - Obtain a collection of statistic figures (i.e. sum, average, max, min, count) for all products of category “Books”
```java
    private DoubleSummaryStatistics exercise10() {
        DoubleSummaryStatistics result = productRepo.findAll().stream()
                .filter(product -> product.getCategory().equalsIgnoreCase("books"))
                .mapToDouble(Product::getPrice)
                .summaryStatistics();
        return result;
    }
```
11 - Obtain a data map with order id and order’s product count
```java
    private Map<Long, Integer> exercise11() {
        Map<Long, Integer> result = orderRepo.findAll().stream()
                .collect(Collectors.toMap(
                        Order::getId,
                        order -> order.getProducts().size()
                ));
        return result;
    }
```
12 - Produce a data map with order records grouped by customer
```java
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
```
13 - Produce a data map with order record and product total sum
```java
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
```
14 - Obtain a data map with list of product name by category
```java
    private Map<String, List<Product>> exercise14() {
        Map<String, List<Product>> result = productRepo.findAll().stream()
                .collect(Collectors.groupingBy(
                        Product::getCategory,
                        Collectors.toList()
                ));
        return result;
    }
```
15 - Get the most expensive product by category
```java
    private Map<String, Optional<Product>> exercise15() {
        Map<String, Optional<Product>> result = productRepo.findAll().stream()
                .collect(Collectors.groupingBy(
                        Product::getCategory,
                        Collectors.maxBy(Comparator.comparing(Product::getPrice))
                ));
        return result;
    }
```
