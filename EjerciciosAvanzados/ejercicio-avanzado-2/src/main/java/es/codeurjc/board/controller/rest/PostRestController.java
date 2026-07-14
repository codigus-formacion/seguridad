package es.codeurjc.board.controller.rest;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.board.domain.Post;
import es.codeurjc.board.service.ImageService;
import es.codeurjc.board.service.PostService;

@RestController
@RequestMapping("/api/posts")
public class PostRestController {

	@Autowired
	private PostService postService;

	@Autowired
	private ImageService imageService;

	@GetMapping("/")
	public Collection<Post> getPosts() {

		return postService.getPosts();
	}

	@GetMapping("/{id}")
	public Post getPost(@PathVariable long id) {

		return postService.getPost(id);
	}

	@PostMapping("/")
	public ResponseEntity<Post> createPost(@RequestBody Post post) {

		Post createdPost = postService.createPost(post);

		URI location = fromCurrentRequest().path("/{id}").buildAndExpand(createdPost.getId()).toUri();

		return ResponseEntity.created(location).body(createdPost);
	}

	@PutMapping("/{id}")
	public Post replacePost(@PathVariable long id, @RequestBody Post updatedPost) {

		return postService.replacePost(id, updatedPost);
	}

	@DeleteMapping("/{id}")
	public Post deletePost(@PathVariable long id) {

		return postService.deletePost(id);
	}

	@PostMapping("/{id}/image")
	public ResponseEntity<?> createPostImage(@PathVariable long id, @RequestParam MultipartFile imageFile)
			throws IOException {

		if (imageFile.isEmpty()) {
			return new ResponseEntity<>("Image file cannot be empty", HttpStatus.BAD_REQUEST);
		}

		String imagePath = imageService.createImage(imageFile.getInputStream(), imageFile.getOriginalFilename());
		postService.setImageToPost(id, imagePath);

		URI location = fromCurrentContextPath()
				.path("/image/" + imagePath)
				.build()
				.toUri();

		return ResponseEntity.created(location).body(imagePath);
	}

	@DeleteMapping("/{postId}/image")
	public ResponseEntity<?> deletePostImage(@PathVariable long postId)
			throws IOException {

		var post = postService.getPost(postId);
		
		if (post.getImagePath() != null) {
			String imagePath = post.getImagePath();
			postService.removeImageFromPost(postId);
			imageService.deleteImage(imagePath);
			return ResponseEntity.ok(imagePath);
		}

		return ResponseEntity.notFound().build();
	}
}