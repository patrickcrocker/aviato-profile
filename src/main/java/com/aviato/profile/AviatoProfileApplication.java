package com.aviato.profile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

@SpringBootApplication
@Controller
public class AviatoProfileApplication {

	@RequestMapping("/docs")
	public View docs() {
		return new RedirectView("/docs/index.html");
	}

	public static void main(String[] args) {
		SpringApplication.run(AviatoProfileApplication.class, args);
	}
}
