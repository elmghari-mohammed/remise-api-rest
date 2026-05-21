package org.example.tpremise;

import org.example.tpremise.service.FacturationService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class TpRemiseApplication {
	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(TpRemiseApplication.class, args);
		FacturationService facturationService = ctx.getBean(FacturationService.class);
		System.out.println(facturationService.calculerFacture(10000));
	}
}