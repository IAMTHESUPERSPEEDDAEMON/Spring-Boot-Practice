package org.example.springbootpractice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderDto {
    private List<Long> productIds; // При создании заказа передаём только ID продуктов
}