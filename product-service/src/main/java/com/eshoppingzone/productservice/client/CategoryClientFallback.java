package com.eshoppingzone.productservice.client;

import com.eshoppingzone.productservice.dto.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryClientFallback implements CategoryClient {

    @Override
    public Category getCategoryById(Long id) {
        return new Category(id, "Unknown", "Category service is unavailable.");
    }
}
