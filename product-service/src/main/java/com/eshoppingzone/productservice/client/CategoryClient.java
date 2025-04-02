package com.eshoppingzone.productservice.client;

import com.eshoppingzone.productservice.dto.Category;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Feign Client for calling Category Service
@FeignClient(name = "category-service", fallback = CategoryClientFallback.class)
public interface CategoryClient {

    @GetMapping("/categories/{id}")
    Category getCategoryById(@PathVariable Long id);
}
