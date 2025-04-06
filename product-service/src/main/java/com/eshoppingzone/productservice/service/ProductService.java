package com.eshoppingzone.productservice.service;

import com.eshoppingzone.productservice.client.CategoryClient;
import com.eshoppingzone.productservice.dto.Category;
import com.eshoppingzone.productservice.entity.Product;
import com.eshoppingzone.productservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryClient categoryClient;

    public List<Product> getAllProducts() {
        List<Product> products = productRepository.findAll();
        products.forEach(this::attachCategoryName);
        return products;
    }

    public Optional<Product> getProductById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        product.ifPresent(this::attachCategoryName);
        return product;
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        List<Product> products = productRepository.findByCategoryId(categoryId);
        products.forEach(this::attachCategoryName);
        return products;
    }

    public Product addProduct(Product product) {
        try {
            Category category = categoryClient.getCategoryById(product.getCategoryId());
            if (category != null) {
                product.setCategoryId(category.getId());
                product.setCategoryName(category.getName());
            }
        } catch (Exception e) {
            throw new RuntimeException("Category service is unavailable. Please try again later.");
        }
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product productDetails) {
        return productRepository.findById(id).map(product -> {
            try {
                Category category = categoryClient.getCategoryById(productDetails.getCategoryId());
                if (category == null) {
                    throw new RuntimeException("Invalid category ID");
                }
                product.setCategoryId(category.getId());
                product.setCategoryName(category.getName());
            } catch (Exception e) {
                throw new RuntimeException("Category service is unavailable. Please try again later.");
            }

            product.setName(productDetails.getName());
            product.setDescription(productDetails.getDescription());
            product.setPrice(productDetails.getPrice());
            product.setQuantity(productDetails.getQuantity());

            return productRepository.save(product);
        }).orElse(null);
    }

    private void attachCategoryName(Product product) {
        try {
            Category category = categoryClient.getCategoryById(product.getCategoryId());
            if (category != null) {
                product.setCategoryName(category.getName());
            }
        } catch (Exception ex) {
            System.out.println("Failed to fetch category: " + ex.getMessage());
            product.setCategoryName("Unavailable");
        }
    }

    public boolean deleteProduct(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }
}