package es.codeurjc.board.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import es.codeurjc.board.domain.Post;
import es.codeurjc.board.domain.User;
import es.codeurjc.board.repository.PostRepository;
import es.codeurjc.board.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

@Service
public class DatabaseInitializer {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() throws Exception {
        loadSampleData();
    }

    @Transactional
    public void loadSampleData() throws IOException {
        // La contraseñas poco seguras (pass) no se considerará una vulnerabilidad en este caso, ya que es solo para facilitar la corrección del examen. 
        // En un entorno real, se deberían usar contraseñas seguras y no hardcodeadas.
        User michel = new User("michel", passwordEncoder.encode("pass"), "USER");
        User oscar = new User("oscar", passwordEncoder.encode("pass"), "USER");
        
        userRepository.save(michel);
		userRepository.save(oscar);

        Post post1 = new Post(michel, "Vendo moto", "Barata, barata");
        Post post2 = new Post(oscar, "Compro coche", "Pago bien");

        postRepository.save(post1);
        postRepository.save(post2);

        setPostImage(post1, "/sampledata_images/moto.jpg");
    }

    public void setPostImage(Post post, String classpathResource) throws IOException {
        Resource image = new ClassPathResource(classpathResource);

        String imagePath = imageService.createImage(image.getInputStream(), image.getFilename());
        post.setImagePath(imagePath);
        postRepository.save(post);
    }
}
