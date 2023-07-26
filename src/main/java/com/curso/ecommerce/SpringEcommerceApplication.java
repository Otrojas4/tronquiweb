package com.curso.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;


@SpringBootApplication
public class SpringEcommerceApplication {	
	public static void main(String[] args) {
		SpringApplication.run(SpringEcommerceApplication.class, args);
	}
	
	
    // Configura las credenciales de Cloudinary
    private static final String CLOUD_NAME = "dy9iknefl";
    private static final String API_KEY = "296294878843716";
    private static final String API_SECRET = "ZVIcZH0IbLngKEZho0_MczzkNcA";



    // Bean para configurar Cloudinary
    @Bean
    public Cloudinary cloudinaryConfig() {
        return new Cloudinary(ObjectUtils.asMap(
            "cloud_name", CLOUD_NAME,
            "api_key", API_KEY,
            "api_secret", API_SECRET));
    }

}
