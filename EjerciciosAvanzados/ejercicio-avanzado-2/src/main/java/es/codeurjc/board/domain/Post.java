package es.codeurjc.board.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Post {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@ManyToOne
	@JsonIgnore
	private User user;
	
	private String title;
	private String text;

	private String imagePath;

	public Post() {
	}

	public Post(User user, String title, String text) {
		super();
		this.user = user;
		this.title = title;
		this.text = text;
	}

	public Post(Long id, User user, String title, String text) {
		super();
		this.id = id;
		this.user = user;
		this.title = title;
		this.text = text;
	}

	public Long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	// Convenience method for templates
	public String getUsername() {
		return user != null ? user.getName() : null;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	@Override
	public String toString() {
		return "Post [id=" + id + ", user=" + (user != null ? user.getName() : "null") + ", title=" + title + ", text=" + text + "]";
	}
}
