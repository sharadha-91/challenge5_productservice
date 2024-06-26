package com.projects.product_ms.services;

import com.projects.product_ms.exceptions.ProductNotFoundException;
import com.projects.product_ms.models.Category;
import com.projects.product_ms.models.Product;
import com.projects.product_ms.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@Service("productService")
public class ProductServiceImpl implements ProductService {

    private final ProductRepository  productRepository;
    private final CategoryService categoryService;
    private final WebClient webClient;
    private final String BASE_URL = "http://localhost:8091/orders";

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, CategoryService categoryService, WebClient webClient) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.webClient = webClient;
    }

    @Override
    public Product getProductById(long productId) throws ProductNotFoundException {
        return getProduct(productId);
    }

    @Override
    public List<Product> getAllProducts() {
        return this.productRepository.findAll();
    }

    @Override
    public Product createProduct(String title, String description, double price, String image, String categoryName, int availableQuantity) {
        Product product = new Product();
   
        product.setTitle(title);
        System.out.println(" create product service : "+ title);
        product.setDescription(description);
        System.out.println(" create product service : "+ description);
        product.setPrice(price);
        System.out.println(" create product service : "+ price);
        product.setImage(image);
        System.out.println(" create product service : "+ image);
        product.setAvailableQuantity(availableQuantity);
        System.out.println(" create product service : "+ availableQuantity);
        Category category = this.categoryService.createCategory(categoryName);
        product.setCategory(category);
        System.out.println(" create product service : "+ category);
        return this.productRepository.save(product);
    }

    @Override
    public Product updatePrice(long productId, double updatedPrice) throws ProductNotFoundException {
        Product product = getProduct(productId);
        System.out.println(" update product service : "+ product);
        product.setPrice(updatedPrice);
        System.out.println(" update product service : "+ product + "updatedPrice" +updatedPrice);
        return this.productRepository.save(product);
    }

    @Override
    public Product updateImage(long productId, String updatedImageUrl) throws ProductNotFoundException {
        Product product = getProduct(productId);
        product.setImage(updatedImageUrl);
        return this.productRepository.save(product);
    }

    @Override
    public Product updateAvailableQuantity(long productId, int updatedQuantity) throws ProductNotFoundException {
        Product product = getProduct(productId);
        System.out.println(" product service >>>  " + productId +" updatedQuantity " +updatedQuantity);
        System.out.println(" product service >>>  " + product);
        product.setAvailableQuantity(updatedQuantity);
        System.out.println(" product service >>>  " +product);
        return this.productRepository.save(product);
    }

    @Override
    public void deleteProduct(long productId) throws ProductNotFoundException {
        Product product = getProduct(productId);
        this.productRepository.delete(product);
    }

    private Product getProduct(long productId) throws ProductNotFoundException {
        Optional<Product> productOptional = this.productRepository.findById(productId);
        if(productOptional.isEmpty()) throw new ProductNotFoundException("Invalid Product ID");
        return productOptional.get();
    }

    @Override
    public List<Product> getProductsById(List<Long> productIds) {
        return this.productRepository.findByIdIn(productIds);
    }

    @Override
    public List<Product> getTrendingProducts() {
        List<Long> trendingProductIds = getTrendingProductIds();
        return this.productRepository.findByIdIn(trendingProductIds);
    }

    private List<Long> getTrendingProductIds() {
        return this.webClient
                .get()
                .uri(BASE_URL + "/trending")
                .retrieve()
                .bodyToFlux(Long.class)
                .collectList()
                .block();
    }

}
