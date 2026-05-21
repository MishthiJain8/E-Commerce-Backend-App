package com.ecommerce.Service;

import com.ecommerce.Entity.Product;
import com.ecommerce.Repository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Cacheable(cacheNames = "products")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Cacheable(cacheNames = "product", key = "#id")
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Transactional
    @CachePut(cacheNames = "product", key = "#result.id")
    @CacheEvict(cacheNames = "products", allEntries = true)
    public Product createOrUpdate(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    @CacheEvict(cacheNames = {"product","products"}, key = "#id", allEntries = true)
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
