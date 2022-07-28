package com.springboot.restblog;

import com.springboot.restblog.model.entity.RoleEntity;
import com.springboot.restblog.model.entity.UserEntity;
import com.springboot.restblog.model.entity.UserProfileEntity;
import com.springboot.restblog.repository.RoleRepository;
import com.springboot.restblog.repository.UserProfileRepository;
import com.springboot.restblog.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class RestBlogApplication implements CommandLineRunner {

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	public static void main(String[] args) {
		SpringApplication.run(RestBlogApplication.class, args);
	}

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserProfileRepository profileRepository;

	@Autowired
	private UserRepository userRepository;

	@Override
	public void run(String... args) throws Exception {

//		RoleEntity adminRole = new RoleEntity();
//		adminRole.setName("ROLE_ADMIN");
//		roleRepository.save(adminRole);
//
//		RoleEntity userRole = new RoleEntity();
//		userRole.setName("ROLE_USER");
//		roleRepository.save(userRole);

//		Set<RoleEntity> roleEntities = new HashSet<>();
//		roleEntities.add(adminRole);
//
//		UserEntity admin = new UserEntity();
//		admin.setRoles(roleEntities);
//		admin.setEmail("prnvc5802@gmail.com");
//		admin.setDateRegistered(new Date());
//		admin.setPassword("$2a$10$atTewjB9/.3EE/f8GZv6h.H/AaKLHuNNOsA6IH/.bfaL/cmuvSB7a");
//		admin.setUsername("admin5802");
//
//		UserProfileEntity userProfile = new UserProfileEntity();
//		userProfile.setUser(admin);
//		userProfile.setFirstName("Cuong");
//		userProfile.setLastName("Nguyen");
//		profileRepository.save(userProfile);
//
//		userRepository.save(admin);





	}
}
