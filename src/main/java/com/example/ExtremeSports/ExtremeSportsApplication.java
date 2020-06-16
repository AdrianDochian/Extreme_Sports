package com.example.ExtremeSports;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExtremeSportsApplication {

	public static void main(String[] args)
	{
		Service.getInstance().getData();
		SpringApplication.run(ExtremeSportsApplication.class, args);
	}

}
