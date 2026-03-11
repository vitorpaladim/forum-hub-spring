package com.forumhub.config;

import com.forumhub.entity.Topic;
import com.forumhub.entity.User;
import com.forumhub.repository.TopicRepository;
import com.forumhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create admin user
        User admin = User.builder()
                .username("admin")
                .email("admin@forumhub.com")
                .password(passwordEncoder.encode("admin123"))
                .role(User.Role.ADMIN)
                .build();
        userRepository.save(admin);

        // Create regular user
        User user = User.builder()
                .username("joao_silva")
                .email("joao@forumhub.com")
                .password(passwordEncoder.encode("senha123"))
                .role(User.Role.USER)
                .build();
        userRepository.save(user);

        // Seed some topics
        topicRepository.save(Topic.builder()
                .title("Como usar Spring Security com JWT?")
                .message("Estou tentando implementar autenticação JWT no Spring Boot 3 mas estou tendo problemas com a configuração do SecurityFilterChain. Alguém pode ajudar?")
                .courseName("Spring Boot 3")
                .author(user)
                .status(Topic.Status.OPEN)
                .build());

        topicRepository.save(Topic.builder()
                .title("Diferença entre @RestController e @Controller")
                .message("Qual é a principal diferença entre as anotações @RestController e @Controller no Spring? Quando devo usar cada uma?")
                .courseName("Spring Framework")
                .author(admin)
                .status(Topic.Status.ANSWERED)
                .build());

        topicRepository.save(Topic.builder()
                .title("Como fazer paginação com Spring Data JPA?")
                .message("Preciso implementar paginação na minha API REST. Como usar o Pageable do Spring Data JPA?")
                .courseName("Spring Boot 3")
                .author(user)
                .status(Topic.Status.OPEN)
                .build());

        System.out.println("\n===========================================");
        System.out.println(" Fórum Hub API started successfully!");
        System.out.println("===========================================");
        System.out.println(" Swagger UI: http://localhost:8080/swagger-ui.html");
        System.out.println(" H2 Console: http://localhost:8080/h2-console");
        System.out.println("-------------------------------------------");
        System.out.println(" Demo users:");
        System.out.println("   Admin: admin@forumhub.com / admin123");
        System.out.println("   User:  joao@forumhub.com  / senha123");
        System.out.println("===========================================\n");
    }
}
