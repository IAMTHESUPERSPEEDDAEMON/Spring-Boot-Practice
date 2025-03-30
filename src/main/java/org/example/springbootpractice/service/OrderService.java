package org.example.springbootpractice.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.example.springbootpractice.dto.CreateOrderDto;
import org.example.springbootpractice.dto.OrderDto;
import org.example.springbootpractice.dto.ProductDto;
import org.example.springbootpractice.model.Order;
import org.example.springbootpractice.model.Product;
import org.example.springbootpractice.repository.OrderRepository;
import org.example.springbootpractice.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    private OrderDto mapToDto(Order order) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setTotalCost(order.getTotalCost());
        orderDto.setCreatedAt(order.getCreatedAt());

        List<ProductDto> productDtos = order.getProducts().stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .collect(Collectors.toList());

        orderDto.setProducts(productDtos);
        return orderDto;
    }

    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() ->new EntityNotFoundException("Order is not found with id: " + id));
        return mapToDto(order);
    }

    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderDto createOrder(CreateOrderDto createOrderDto) {
        // get products by ID
        List<Product> products = createOrderDto.getProductIds().stream()
                .map(productId -> productRepository.findById(productId)
                        .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId)))
                .collect(Collectors.toList());


        // create new order
        Order order = new Order();
        order.setProducts(products);
        order.setTotalCost();

        Order savedOrder = orderRepository.save(order);
        return mapToDto(savedOrder);
    }


    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new EntityNotFoundException("Order not found with id: " + id);
        }
        orderRepository.deleteById(id);
    }

    @Transactional
    public OrderDto updateOrder(Long id, CreateOrderDto createOrderDto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));

        List<Product> products = createOrderDto.getProductIds().stream()
                .map(productId -> productRepository.findById(productId)
                        .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId)))
                .collect(Collectors.toList());

        order.setProducts(products);
        order.setTotalCost();

        Order updatedOrder = orderRepository.save(order);

        return mapToDto(updatedOrder);
    }


}
