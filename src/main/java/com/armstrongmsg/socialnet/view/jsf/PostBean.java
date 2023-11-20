package com.armstrongmsg.socialnet.view.jsf;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.armstrongmsg.socialnet.core.ApplicationFacade;
import com.armstrongmsg.socialnet.view.jsf.model.JsfConnector;
import com.armstrongmsg.socialnet.view.jsf.model.Post;
import com.armstrongmsg.socialnet.view.jsf.model.User;

@ManagedBean(name = "postBean", eager = true)
@SessionScoped
public class PostBean {
	private User user;
	private String title;
	private String content;
	private String postVisibility;
	
	private Post post;
	private List<Post> userPosts;
	
	private static ApplicationFacade facade = ApplicationFacade.getInstance();
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public String getPostVisibility() {
		return postVisibility;
	}


	public void setPostVisibility(String postVisibility) {
		this.postVisibility = postVisibility;
	}
	
	public String createPost() {
		facade.createPost(SessionManager.getCurrentSession().getUserToken(), title, content, postVisibility);
		userPosts = new JsfConnector().getViewPosts(facade.getUserPosts(SessionManager.getCurrentSession().getUserToken(), 
				getUser().getUserId()));
		return null;
	}
	
	public Post getPost() {
		return post;
	}
	
	
	public void setPost(Post post) {
		this.post = post;
	}
	
	public List<Post> getUserPosts() { 
		if (userPosts == null) {
			userPosts = new JsfConnector().getViewPosts(facade.getUserPosts(
					SessionManager.getCurrentSession().getUserToken(), 
					getUser().getUserId()));
		}
		
		return userPosts;
	}
	
	public List<Post> getFriendsPosts() {
		User user = getUser();
		String userId = "";
		
		if (user == null) {
			userId = SessionManager.getCurrentSession().getUserToken().getUserId();
		} else {
			userId = user.getUserId();
		}
		
		return new JsfConnector().getViewPosts(facade.getFriendsPosts(SessionManager.getCurrentSession().getUserToken(), 
				userId));
	}
}
