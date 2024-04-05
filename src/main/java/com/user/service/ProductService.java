package com.user.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.user.Entity.User;
import com.user.Repository.UserRepo;
import com.user.dto.Product;

import jakarta.annotation.PostConstruct;
@Service
public class ProductService {

	 List<Product> productList = null;

	    @Autowired
	    private UserRepo userRepo;

	    @Autowired
	    private PasswordEncoder passwordEncoder;

	    @PostConstruct
	    public void loadProductsFromDB() {
	        productList = IntStream.rangeClosed(1, 100)
	                .mapToObj(i -> Product.builder()
	                        .productId(i)
	                        .name("product " + i)
	                        .qty(new Random().nextInt(10))
	                        .price(new Random().nextInt(5000)).build()
	                ).collect(Collectors.toList());
	    }


	    public List<Product> getProducts() {
	        return productList;
	    }

	    public Product getProduct(int id) {
	        return productList.stream()
	                .filter(product -> product.getProductId() == id)
	                .findAny()
	                .orElseThrow(() -> new RuntimeException("product " + id + " not found"));
	    }


	    public String addUser(User user) 
	    {
	        user.setPassword(passwordEncoder.encode(user.getPassword()));
	        userRepo.save(user);
	        return "user added to system ";
	    }
}
