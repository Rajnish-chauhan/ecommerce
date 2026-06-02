package com.project.ecommerce.repo;

import com.project.ecommerce.model.Orders;
import com.project.ecommerce.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface OrderRepository extends MongoRepository<Orders, String> {
    List<Orders> findByUser(User user);
}