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

		RoleEntity adminRole = new RoleEntity();
		RoleEntity userRole = new RoleEntity();
		UserEntity admin = new UserEntity();
		UserProfileEntity userProfile = new UserProfileEntity();

		if (!roleRepository.findByName("ROLE_ADMIN").isPresent()) {
			defaulConfigAdmin(adminRole, userRole, admin, userProfile);
		} else {
			System.out.println("Database is exists old data");
			System.out.println("Continute...");
		}
	}

	private void defaulConfigAdmin(RoleEntity adminRole, RoleEntity userRole,
								   UserEntity admin, UserProfileEntity userProfile) {

		adminRole.setName("ROLE_ADMIN");
		roleRepository.save(adminRole);


		userRole.setName("ROLE_USER");
		roleRepository.save(userRole);

		Set<RoleEntity> roleEntities = new HashSet<>();
		roleEntities.add(adminRole);


		admin.setUsername("cuongadminnvc");
		admin.setEmail("prnvc5802@gmail.com");
		admin.setPassword("$2a$10$M4kpgo15C6SC/5eKPx40Juv2OyDq9Ys8za7ZESC9B00L8crY2mOhq");
		admin.setDateRegistered(new Date());


		userProfile.setUser(admin);
		userProfile.setFirstName("Cuong");
		userProfile.setLastName("Admin");
		userProfile.setAvatarPhoto("uploaded-images/user-avatars/default/default-avt.png");
		userProfile.setCoverPhoto("uploaded-images/user-covers/default/default-background.jpg");
		profileRepository.save(userProfile);

		admin.setRoles(roleEntities);
		userRepository.save(admin);
	}
}
