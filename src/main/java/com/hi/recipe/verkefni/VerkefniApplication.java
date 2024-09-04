package com.hi.recipe.verkefni;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VerkefniApplication {

	public static void main(String[] args) {
		SpringApplication.run(VerkefniApplication.class, args);
		System.out.println("Sturla skrifaði streng");
		System.out.println("Ásdís skrifaði streng nr 2");
		System.out.println("Hilla mús er dúlla");
	}

}
