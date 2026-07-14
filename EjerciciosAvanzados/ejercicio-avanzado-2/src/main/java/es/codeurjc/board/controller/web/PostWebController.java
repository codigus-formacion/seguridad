package es.codeurjc.board.controller.web;

import java.io.IOException;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import es.codeurjc.board.domain.Post;
import es.codeurjc.board.domain.User;
import es.codeurjc.board.service.ImageService;
import es.codeurjc.board.service.PostService;
import es.codeurjc.board.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class PostWebController {

	@Autowired
	private PostService postService;

	@Autowired
	private ImageService imageService;

	@Autowired
	private UserService userService;

    @ModelAttribute
	public void addAttributes(Model model, HttpServletRequest request) {

		Principal principal = request.getUserPrincipal();

		if (principal != null) {

			model.addAttribute("loggedUser", true);
			model.addAttribute("username", principal.getName());
		} else {
			model.addAttribute("loggedUser", false);
		}
	}

	@GetMapping("/")
	public String getPosts(Model model) {
		model.addAttribute("posts", postService.getPosts());
		return "posts";
	}

	@GetMapping("/posts/{id}")
	public String getPost(@PathVariable long id, Model model) {
		model.addAttribute("post", postService.getPost(id));
		return "post";
	}

	@GetMapping("/posts/new")
	public String showCreatePostForm() {
		return "createPost";
	}

	@PostMapping("/posts/new")
	public String createPost(@RequestParam String title, 
	                        @RequestParam String text) {
		Post post = new Post();
		post.setTitle(title);
		post.setText(text);
		Post createdPost = postService.createPost(post);
		return "redirect:/posts/" + createdPost.getId();
	}

	@GetMapping("/posts/{id}/edit")
	public String showEditPostForm(@PathVariable long id, Model model) {
		model.addAttribute("post", postService.getPost(id));
		return "editPost";
	}

	@PostMapping("/posts/{id}/edit")
	public String replacePost(@PathVariable long id, 
	                         @RequestParam String title, 
	                         @RequestParam String text) {
		checkPostOwnership(id);
		Post post = new Post();
		post.setTitle(title);
		post.setText(text);
		postService.replacePost(id, post);
		return "redirect:/posts/" + id;
	}

	@GetMapping("/posts/{id}/delete")
	public String deletePost(@PathVariable long id) {
		checkPostOwnership(id);
		postService.deletePost(id);
		return "redirect:/";
	}

	@GetMapping("/posts/{id}/image")
	public ResponseEntity<Resource> showPostImage(@PathVariable long id, Model model) throws IOException {
		Resource imageFile = imageService.getImageFile(postService.getPost(id).getImagePath());

		MediaType mediaType = MediaTypeFactory
                .getMediaType(imageFile)
                .orElse(MediaType.IMAGE_JPEG);

        return ResponseEntity
                .ok()
                .contentType(mediaType)
                .body(imageFile);
	}

	@PostMapping("/posts/{id}/image")
	public String createPostImage(@PathVariable long id, 
	                             @RequestParam MultipartFile imageFile,
	                             Model model) throws IOException {
		if (imageFile.isEmpty()) {
			model.addAttribute("error", "Image file cannot be empty");
			model.addAttribute("post", postService.getPost(id));
			return "post";
		}
		checkPostOwnership(id);
		Post post = postService.getPost(id);
		
		// Delete old image if exists
		if (post.getImagePath() != null) {
			imageService.deleteImage(post.getImagePath());
		}

		String imagePath = imageService.createImage(imageFile.getInputStream(), imageFile.getOriginalFilename());
		postService.setImageToPost(id, imagePath);

		return "redirect:/posts/" + id;
	}

	@PostMapping("/posts/{postId}/image/delete")
	public String deletePostImage(@PathVariable long postId) throws IOException {
		checkPostOwnership(postId);
		Post post = postService.getPost(postId);
		
		if (post.getImagePath() != null) {
			imageService.deleteImage(post.getImagePath());
			postService.removeImageFromPost(postId);
		}

		return "redirect:/posts/" + postId;
	}

	private void checkPostOwnership(long postId) {
		User loggedUser = userService.getLoggedUser();
		Post post = postService.getPost(postId);
		if (!post.getUser().equals(loggedUser)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't own this post");
		}
	}


}
