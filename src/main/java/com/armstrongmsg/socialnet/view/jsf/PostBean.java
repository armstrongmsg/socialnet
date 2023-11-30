package com.armstrongmsg.socialnet.view.jsf;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.armstrongmsg.socialnet.core.ApplicationFacade;
import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.exceptions.UnauthorizedOperationException;
import com.armstrongmsg.socialnet.model.PostVisibility;
import com.armstrongmsg.socialnet.model.authentication.UserToken;
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
		UserToken token = SessionManager.getCurrentSession().getUserToken();
		try {
			facade.createPost(token, title, content, PostVisibility.valueOf(postVisibility));
		} catch (AuthenticationException e) {
			// FIXME treat exception
		}
		
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
			try {
				userPosts = new JsfConnector().getViewPosts(facade.getUserPosts(
						SessionManager.getCurrentSession().getUserToken(), 
						getUser().getUserId()));
			} catch (UnauthorizedOperationException | AuthenticationException e) {
				// FIXME treat exception
			}
		}
		
		return userPosts;
	}
	
	public List<Post> getSelfPosts() {
		try {
			return new JsfConnector().getViewPosts(facade.getSelfPosts(
					SessionManager.getCurrentSession().getUserToken()));
		} catch (UnauthorizedOperationException | AuthenticationException e) {
			// FIXME treat exception
		}
		
		return null;
	}
	
	public List<Post> getFriendsPosts() {
		try {
			return new JsfConnector().getViewPosts(facade.getFriendsPosts(SessionManager.getCurrentSession().getUserToken()));
		} catch (UnauthorizedOperationException | AuthenticationException e) {
			// FIXME treat exception
		}
		
		return null;
	}
}
