package com.ecommerce.Service;

import com.ecommerce.Dto.OrderRequest;
import com.ecommerce.Entity.Inventory;
import com.ecommerce.Entity.Order;
import com.ecommerce.Entity.Product;
import com.ecommerce.Exception.InsufficientInventoryException;
import com.ecommerce.Repository.InventoryRepository;
import com.ecommerce.Repository.OrderRepository;
import com.ecommerce.Repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class OrderService {

    private final InventoryRepository inventoryRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(InventoryRepository inventoryRepository, OrderRepository orderRepository, ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Order placeOrder(OrderRequest request) {
        Long productId = request.getProductId();
        Long qty = request.getQuantity();

        // Load product price
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // Pessimistic lock on inventory row to prevent oversell
        Inventory inventory = inventoryRepository.findByProductIdForUpdate(productId)
                .orElseThrow(() -> new InsufficientInventoryException("Inventory not configured for product"));

        if (inventory.getQuantity() < qty) {
            throw new InsufficientInventoryException("Insufficient inventory");
        }

        // Deduct inventory
        inventory.setQuantity(inventory.getQuantity() - qty);
        inventoryRepository.save(inventory);

        // Create order
        Order order = new Order();
        order.setProductId(productId);
        order.setQuantity(qty);
        BigDecimal total = product.getPrice().multiply(BigDecimal.valueOf(qty));
        order.setTotalPrice(total);
        order.setStatus("COMPLETED");

        return orderRepository.save(order);
    }
}
