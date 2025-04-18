package it.aulab.aulabchronicles;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;

@EnableAsync(proxyTargetClass = true)
@EnableTransactionManagement
@SpringBootApplication
public class AulabchroniclesApplication {

	public static void main(String[] args) {
		SpringApplication.run(AulabchroniclesApplication.class, args);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public ModelMapper instanceModelMapper() {
		ModelMapper mapper = new ModelMapper();
		return mapper;
	}

	@Bean
	public SpringSecurityDialect springSecurityDialect() {
		return new SpringSecurityDialect();
	}
}
