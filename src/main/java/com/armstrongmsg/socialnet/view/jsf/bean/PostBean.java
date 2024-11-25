package com.armstrongmsg.socialnet.view.jsf.bean;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import org.primefaces.model.file.UploadedFile;

import com.armstrongmsg.socialnet.core.ApplicationFacade;
import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.exceptions.UnauthorizedOperationException;
import com.armstrongmsg.socialnet.exceptions.UserNotFoundException;
import com.armstrongmsg.socialnet.model.PostVisibility;
import com.armstrongmsg.socialnet.model.UserSummary;
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
	private boolean privatePost;
	private List<PostVisibility> visibilities = Arrays.asList(PostVisibility.values());
	private Post post;
	private List<Post> userPostsAdmin;
	private List<Post> selfPosts;
	private List<Post> friendsPosts;
	private List<Post> feedPosts;
	private String username;
	private List<Post> userPosts;
	private UploadedFile postPic;
	private ApplicationFacade facade;
	private JsfExceptionHandler exceptionHandler;
	
	@ManagedProperty(value="#{contextBean}")
	private ContextBean contextBean;
	
	@ManagedProperty(value="#{applicationBean}")
	private ApplicationBean applicationBean;
	
	@PostConstruct
	public void initialize() {
		facade = getApplicationBean().getFacade();
		exceptionHandler = new JsfExceptionHandler();
	}
	
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

	public boolean isPrivatePost() {
		return privatePost;
	}

	public void setPrivatePost(boolean privatePost) {
		this.privatePost = privatePost;
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
	
	public ContextBean getContextBean() {
		return contextBean;
	}

	public void setContextBean(ContextBean contextBean) {
		this.contextBean = contextBean;
	}

	public ApplicationBean getApplicationBean() {
		return applicationBean;
	}

	public void setApplicationBean(ApplicationBean applicationBean) {
		this.applicationBean = applicationBean;
	}
	
	public String createPost() throws UserNotFoundException {
		String token = contextBean.getCurrentSession().getUserToken();
		try {
			byte[] picData = null;
			
			if (this.postPic != null) {
				picData = this.postPic.getContent();
			}
			
			PostVisibility visibility = PostVisibility.PUBLIC;
			
			if (privatePost) {
				visibility = PostVisibility.PRIVATE;
			}
			
			facade.createPost(token, title, content, visibility, picData);
		} catch (AuthenticationException e) {
			this.exceptionHandler.handle(e);
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
						contextBean.getCurrentSession().getUserToken(), 
						getUser().getUserId()));
			} catch (UnauthorizedOperationException | AuthenticationException | UserNotFoundException e) {
				this.exceptionHandler.handle(e);
			}
		}
		
		return userPostsAdmin;
	}
	
	public List<Post> getSelfPosts() {
		try {
			if (selfPosts == null) {
				selfPosts = new JsfConnector().getViewPosts(facade.getSelfPosts(
						contextBean.getCurrentSession().getUserToken()));
			}
		} catch (UnauthorizedOperationException | AuthenticationException e) {
			this.exceptionHandler.handle(e);
		}
		
		return selfPosts;
	}
	
	public List<Post> getFeedPosts() {
		if (feedPosts == null) {
			try {
				feedPosts = new JsfConnector().getViewPosts(
						facade.getFeedPosts(contextBean.getCurrentSession().getUserToken()));
			} catch (UnauthorizedOperationException e) {
				this.exceptionHandler.handle(e);
			} catch (AuthenticationException e) {
				this.exceptionHandler.handle(e);
			} catch (UserNotFoundException e) {
				this.exceptionHandler.handle(e);
			}
		}
		
		return feedPosts;
	}
	
	public List<Post> getFriendsPosts() {
		try {
			if (friendsPosts == null) {
				friendsPosts = new JsfConnector().getViewPosts(
						facade.getFriendsPosts(contextBean.getCurrentSession().getUserToken()));
			}
		} catch (UnauthorizedOperationException | AuthenticationException | UserNotFoundException e) {
			this.exceptionHandler.handle(e);
		}
		
		return friendsPosts;
	}
	
	public List<Post> getPosts() {
		try {
			if (userPosts == null) {
				String username = contextBean.getCurrentSession().getCurrentViewUser().getUsername();
				
				if (username.isEmpty()) {
					UserSummary userSummary = facade.getSelf(
							contextBean.getCurrentSession().getUserToken());
					username = userSummary.getUsername();
				}
				
				userPosts = new JsfConnector().getViewPosts(
						facade.getUserPosts(contextBean.getCurrentSession().getUserToken(), 
								username));
			}
		} catch (UnauthorizedOperationException | AuthenticationException | UserNotFoundException e) {
			this.exceptionHandler.handle(e);
		}
		
		return userPosts;
	}
	
	public void deletePost() throws UserNotFoundException {
		try {
			facade.deletePost(contextBean.getCurrentSession().getUserToken(), post.getId());
			userPosts = new JsfConnector().getViewPosts(facade.getSelfPosts(
					contextBean.getCurrentSession().getUserToken()));
		} catch (AuthenticationException e) {
			this.exceptionHandler.handle(e);
		} catch (UnauthorizedOperationException e) {
			this.exceptionHandler.handle(e);
		}
	}

	public List<PostVisibility> getVisibilities() {
		return visibilities;
	}

	public void setVisibilities(List<PostVisibility> visibilities) {
		this.visibilities = visibilities;
	}
}
