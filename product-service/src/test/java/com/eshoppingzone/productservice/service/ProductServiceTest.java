package com.eshoppingzone.productservice.service;

import com.eshoppingzone.productservice.client.CategoryClient;
import com.eshoppingzone.productservice.dto.Category;
import com.eshoppingzone.productservice.entity.Product;
import com.eshoppingzone.productservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock private ProductRepository productRepo;
    @Mock private CategoryClient categoryClient;
    @InjectMocks private ProductService productService;

    private final Product sampleProduct = new Product(1L, "Laptop", "Gaming", 999.99, 10, 101L, null);
    private final Category sampleCategory = new Category(101L, "Electronics", "Tech gadgets");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllProducts_shouldReturnProductsWithCategories() {
        when(productRepo.findAll()).thenReturn(Arrays.asList(sampleProduct));
        when(categoryClient.getCategoryById(101L)).thenReturn(sampleCategory);

        List<Product> result = productService.getAllProducts();

        assertEquals(1, result.size());
        assertEquals("Electronics", result.get(0).getCategoryName());
        verify(productRepo).findAll();
    }

    @Test
    void getProductById_shouldReturnProductWithCategory() {
        when(productRepo.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(categoryClient.getCategoryById(101L)).thenReturn(sampleCategory);

        Optional<Product> result = productService.getProductById(1L);

        assertTrue(result.isPresent());
        assertEquals("Electronics", result.get().getCategoryName());
    }

    @Test
    void getProductsByCategory_shouldReturnFilteredProducts() {
        when(productRepo.findByCategoryId(101L)).thenReturn(Arrays.asList(sampleProduct));
        when(categoryClient.getCategoryById(101L)).thenReturn(sampleCategory);

        List<Product> result = productService.getProductsByCategory(101L);

        assertEquals(1, result.size());
        assertEquals("Electronics", result.get(0).getCategoryName());
    }

    @Test
    void addProduct_shouldSetCategoryAndSave() {
        Product newProduct = new Product(null, "Phone", "Smartphone", 499.99, 5, 101L, null);
        when(categoryClient.getCategoryById(101L)).thenReturn(sampleCategory);
        when(productRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Product result = productService.addProduct(newProduct);

        assertEquals("Electronics", result.getCategoryName());
        assertEquals(101L, result.getCategoryId());
        verify(productRepo).save(newProduct);
    }

    @Test
    void addProduct_shouldThrowWhenInvalidCategory() {
        Product newProduct = new Product(null, "Phone", "Smartphone", 499.99, 5, 999L, null);
        when(categoryClient.getCategoryById(999L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> productService.addProduct(newProduct));
    }

    @Test
    void updateProduct_shouldUpdateAllFields() {
        Product existing = new Product(1L, "Old", "Old", 100.0, 1, 101L, null);
        Product updates = new Product(1L, "New", "New", 200.0, 2, 101L, null);

        when(productRepo.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryClient.getCategoryById(101L)).thenReturn(sampleCategory);
        when(productRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Product result = productService.updateProduct(1L, updates);

        assertEquals("New", result.getName());
        assertEquals("Electronics", result.getCategoryName());
        assertEquals(200.0, result.getPrice());
    }

    @Test
    void updateProduct_shouldReturnNullWhenNotFound() {
        when(productRepo.findById(1L)).thenReturn(Optional.empty());

        Product result = productService.updateProduct(1L, new Product());

        assertNull(result);
    }

    @Test
    void deleteProduct_shouldReturnTrueWhenDeleted() {
        when(productRepo.findById(1L)).thenReturn(Optional.of(sampleProduct));

        boolean result = productService.deleteProduct(1L);

        assertTrue(result);
        verify(productRepo).deleteById(1L);
    }

    @Test
    void deleteProduct_shouldReturnFalseWhenNotFound() {
        when(productRepo.findById(1L)).thenReturn(Optional.empty());

        boolean result = productService.deleteProduct(1L);

        assertFalse(result);
        verify(productRepo, never()).deleteById(any());
    }
}