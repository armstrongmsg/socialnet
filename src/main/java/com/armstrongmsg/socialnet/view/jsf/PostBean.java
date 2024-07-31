package com.armstrongmsg.socialnet.view.jsf;

import java.util.Arrays;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.primefaces.model.file.UploadedFile;

import com.armstrongmsg.socialnet.core.ApplicationFacade;
import com.armstrongmsg.socialnet.core.authentication.UserToken;
import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.exceptions.UnauthorizedOperationException;
import com.armstrongmsg.socialnet.exceptions.UserNotFoundException;
import com.armstrongmsg.socialnet.model.PostVisibility;
import com.armstrongmsg.socialnet.view.jsf.model.JsfConnector;
import com.armstrongmsg.socialnet.view.jsf.model.Post;
import com.armstrongmsg.socialnet.view.jsf.model.User;

@ManagedBean(name = "postBean", eager = true)
@RequestScoped
public class PostBean {
	private User user;
	private String title;
	private String content;
	private String postVisibility;
	
	private List<PostVisibility> visibilities = Arrays.asList(PostVisibility.values());
	private Post post;
	private List<Post> userPostsAdmin;
	
	private List<Post> selfPosts;
	private List<Post> friendsPosts;
	private List<Post> feedPosts;
	
	private String username;
	private List<Post> userPosts;
	
	private UploadedFile postPic;
	
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public UploadedFile getPostPic() {
		return postPic;
	}

	public void setPostPic(UploadedFile postPic) {
		this.postPic = postPic;
	}
	
	public String createPost() {
		UserToken token = SessionManager.getCurrentSession().getUserToken();
		try {
			facade.createPost(token, title, content, PostVisibility.valueOf(postVisibility), this.postPic.getContent());
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
		if (userPostsAdmin == null) {
			try {
				userPostsAdmin = new JsfConnector().getViewPosts(facade.getUserPostsAdmin(
						SessionManager.getCurrentSession().getUserToken(), 
						getUser().getUserId()));
			} catch (UnauthorizedOperationException | AuthenticationException | UserNotFoundException e) {
				// FIXME treat exception
			}
		}
		
		return userPostsAdmin;
	}
	
	public List<Post> getSelfPosts() {
		try {
			if (selfPosts == null) {
				selfPosts = new JsfConnector().getViewPosts(facade.getSelfPosts(
						SessionManager.getCurrentSession().getUserToken()));
			}
		} catch (UnauthorizedOperationException | AuthenticationException e) {
			// FIXME treat exception
		}
		
		return selfPosts;
	}
	
	public List<Post> getFeedPosts() {
		if (feedPosts == null) {
			try {
				feedPosts = new JsfConnector().getViewPosts(
						facade.getFeedPosts(SessionManager.getCurrentSession().getUserToken()));
			} catch (UnauthorizedOperationException e) {
				// FIXME treat exception
			} catch (AuthenticationException e) {
				// FIXME treat exception
			} catch (UserNotFoundException e) {
				// FIXME treat exception
			}
		}
		
		return feedPosts;
	}
	
	public List<Post> getFriendsPosts() {
		try {
			if (friendsPosts == null) {
				friendsPosts = new JsfConnector().getViewPosts(
						facade.getFriendsPosts(SessionManager.getCurrentSession().getUserToken()));
			}
		} catch (UnauthorizedOperationException | AuthenticationException | UserNotFoundException e) {
			// FIXME treat exception
		}
		
		return friendsPosts;
	}
	
	public List<Post> getPosts() {
		try {
			if (userPosts == null) {
				String username = SessionManager.getCurrentSession().getCurrentViewUser().getUsername();
				
				if (username.isEmpty()) {
					username = SessionManager.getCurrentSession().getUserToken().getUsername();
				}
				
				userPosts = new JsfConnector().getViewPosts(
						facade.getUserPosts(SessionManager.getCurrentSession().getUserToken(), username));
			}
		} catch (UnauthorizedOperationException | AuthenticationException | UserNotFoundException e) {
			// FIXME treat exception
		}
		
		return userPosts;
	}
	
	public void deletePost() {
		try {
			facade.deletePost(SessionManager.getCurrentSession().getUserToken(), post.getId());
			userPosts = new JsfConnector().getViewPosts(facade.getSelfPosts(
					SessionManager.getCurrentSession().getUserToken()));
		} catch (AuthenticationException e) {
			// FIXME treat exception
		} catch (UnauthorizedOperationException e) {
			// FIXME treat exception
		}
	}

	public List<PostVisibility> getVisibilities() {
		return visibilities;
	}

	public void setVisibilities(List<PostVisibility> visibilities) {
		this.visibilities = visibilities;
	}
}
