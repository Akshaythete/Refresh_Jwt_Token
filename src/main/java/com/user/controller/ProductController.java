package com.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user.Entity.RefreshToken;
import com.user.Entity.User;
import com.user.dto.AuthRequest;
import com.user.dto.JwtResponse;
import com.user.dto.Product;
import com.user.dto.RefreshTokenRequest;
import com.user.service.JwtService;
import com.user.service.ProductService;
import com.user.service.RefreshTokenService;
@RestController
@RequestMapping("/products")
public class ProductController {
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private RefreshTokenService refreshTokenService;
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	 
	
	@PostMapping("/signUp")
	    public String addNewUser(@RequestBody User user) {
	        return productService.addUser(user);
	    }

	    @GetMapping("/all")
	    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
	    public List<Product> getAllTheProducts() {
	        return productService.getProducts();
	    }

	    @GetMapping("/{id}")
	    @PreAuthorize("hasAuthority('ROLE_USER')")
	    public Product getProductById(@PathVariable int id) {
	        return productService.getProduct(id);
	    }


	    @PostMapping("/login")
	    public JwtResponse authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
	        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
	        if (authentication.isAuthenticated()) {
	            RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequest.getUsername());
	            return JwtResponse.builder()
	                    .token(jwtService.generateToken(authRequest.getUsername()))
	                    .refreshToken(refreshToken.getToken()).build();
	        } else {
	            throw new UsernameNotFoundException("invalid user request !");
	        }
	    }

	    @PostMapping("/refreshToken")
	    public JwtResponse refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
	        return refreshTokenService.findByToken(refreshTokenRequest.getToken())
	                .map(refreshTokenService::verifyExpiration)
	                .map(RefreshToken::getUser)
	                .map(user -> {
	                    String token = jwtService.generateToken(user.getName());
	                    return JwtResponse.builder()
	                            .token(token)
	                            .refreshToken(refreshTokenRequest.getToken())
	                            .build();
	                }).orElseThrow(() -> new RuntimeException(
	                        "Refresh token is not in database!"));
	    }

}
