package es.codeurjc.board.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.board.domain.Post;
import es.codeurjc.board.repository.PostRepository;

@Service
public class PostService {

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private UserService userService;

	public Collection<Post> getPosts() {

		return postRepository.findAll();
	}

	public Post getPost(long id) {

		return postRepository.findById(id).orElseThrow();
	}

	public Post createPost(Post newPost) {

		newPost.setUser(userService.getLoggedUser());

		postRepository.save(newPost);

		return newPost;
	}

	public Post replacePost(long id, Post updatedPost) {
		Post post = postRepository.findById(id).orElseThrow();

		updatedPost.setId(id);
		updatedPost.setUser(userService.getLoggedUser());
		updatedPost.setImagePath(post.getImagePath());

		postRepository.save(updatedPost);

		return updatedPost;
	}

	public Post deletePost(long id) {

		Post post = postRepository.findById(id).orElseThrow();

		postRepository.deleteById(id);

		return post;
	}

	public Post setImageToPost(long id, String imagePath) {
		Post post = postRepository.findById(id).orElseThrow();

		post.setImagePath(imagePath);
		postRepository.save(post);

		return post;
	}

	public Post removeImageFromPost(long postId) {
		Post post = postRepository.findById(postId).orElseThrow();

		post.setImagePath(null);
		postRepository.save(post);

		return post;
	}

}
