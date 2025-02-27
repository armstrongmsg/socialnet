package com.armstrongmsg.socialnet.view.jsf.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import org.primefaces.model.file.UploadedFile;

import com.armstrongmsg.socialnet.core.ApplicationFacade;
import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.exceptions.InternalErrorException;
import com.armstrongmsg.socialnet.exceptions.InvalidParameterException;
import com.armstrongmsg.socialnet.exceptions.PostNotFoundException;
import com.armstrongmsg.socialnet.exceptions.UnauthorizedOperationException;
import com.armstrongmsg.socialnet.exceptions.UserNotFoundException;
import com.armstrongmsg.socialnet.model.PostVisibility;
import com.armstrongmsg.socialnet.model.UserView;
import com.armstrongmsg.socialnet.view.jsf.model.JsfConnector;
import com.armstrongmsg.socialnet.view.jsf.model.Post;

@ManagedBean(name = "postBean", eager = true)
@RequestScoped
public class PostBean {
	private String title;
	private String content;
	private String postVisibility;
	private boolean privatePost;
	private List<PostVisibility> visibilities = Arrays.asList(PostVisibility.values());
	private Post post;
	private List<Post> selfPosts;
	private List<Post> friendsPosts;
	private List<Post> feedPosts;
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

	public UploadedFile getPostPic() {
		return postPic;
	}

	public void setPostPic(UploadedFile postPic) {
		this.postPic = postPic;
	}
	
	public List<PostVisibility> getVisibilities() {
		return visibilities;
	}

	public void setVisibilities(List<PostVisibility> visibilities) {
		this.visibilities = visibilities;
	}
	
	public Post getPost() {
		return post;
	}
	
	public void setPost(Post post) {
		this.post = post;
	}
	
	public String createPost() throws UserNotFoundException {
		String loggedUserToken = contextBean.getCurrentSession().getUserToken();
		try {
			byte[] picData = null;
			
			if (this.postPic != null) {
				picData = this.postPic.getContent();
			}
			
			PostVisibility visibility = PostVisibility.PUBLIC;
			
			if (privatePost) {
				visibility = PostVisibility.PRIVATE;
			}
			
			ArrayList<byte[]> postMediaData = new ArrayList<byte[]>();
			postMediaData.add(picData);
			facade.createPost(loggedUserToken, title, content, visibility, postMediaData);
		} catch (AuthenticationException e) {
			this.exceptionHandler.handle(e);
		} catch (InternalErrorException e) {
			this.exceptionHandler.handle(e);
		} catch (InvalidParameterException e) {
			this.exceptionHandler.handle(e);
		}
		
		return null;
	}
	
	public List<Post> getSelfPosts() {
		try {
			String loggedUserToken = contextBean.getCurrentSession().getUserToken();
			if (selfPosts == null) {
				selfPosts = new JsfConnector(facade, loggedUserToken).getViewPosts(
						facade.getSelfPosts(loggedUserToken));
			}
		} catch (AuthenticationException e) {
			this.exceptionHandler.handle(e);
		} catch (InternalErrorException e) {
			this.exceptionHandler.handle(e);
		}
		
		return selfPosts;
	}
	
	public List<Post> getFeedPosts() {
		if (feedPosts == null) {
			try {
				String loggedUserToken = contextBean.getCurrentSession().getUserToken();
				feedPosts = new JsfConnector(facade, loggedUserToken).getViewPosts(
						facade.getFeedPosts(loggedUserToken));
			} catch (AuthenticationException e) {
				this.exceptionHandler.handle(e);
			} catch (InternalErrorException e) {
				this.exceptionHandler.handle(e);
			}
		}
		
		return feedPosts;
	}
	
	public List<Post> getFriendsPosts() {
		try {
			if (friendsPosts == null) {
				String loggedUserToken = contextBean.getCurrentSession().getUserToken();
				friendsPosts = new JsfConnector(facade, loggedUserToken).getViewPosts(
						facade.getFriendsPosts(loggedUserToken));
			}
		} catch (AuthenticationException e) {
			this.exceptionHandler.handle(e);
		} catch (InternalErrorException e) {
			this.exceptionHandler.handle(e);
		}
		
		return friendsPosts;
	}
	
	public List<Post> getPosts() {
		try {
			if (userPosts == null) {
				String username = contextBean.getCurrentSession().getCurrentViewUser().getUsername();
				String loggedUserToken = contextBean.getCurrentSession().getUserToken();
				
				if (username.isEmpty()) {
					UserView userSummary = facade.getSelf(loggedUserToken);
					username = userSummary.getUsername();
				}
				
				userPosts = new JsfConnector(facade, loggedUserToken).getViewPosts(
						facade.getUserPosts(loggedUserToken, username));
			}
		} catch (UnauthorizedOperationException e) {
			this.exceptionHandler.handle(e);
		} catch (AuthenticationException e) {
			this.exceptionHandler.handle(e);
		} catch (InternalErrorException e) {
			this.exceptionHandler.handle(e);
		} catch (UserNotFoundException e) {
			this.exceptionHandler.handle(e);
		}
		
		return userPosts;
	}
	
	public void deletePost() {
		try {
			String loggedUserToken = contextBean.getCurrentSession().getUserToken();
			facade.deletePost(loggedUserToken, post.getId());
			userPosts = new JsfConnector(facade, loggedUserToken).getViewPosts(
					facade.getSelfPosts(loggedUserToken));
		} catch (AuthenticationException e) {
			this.exceptionHandler.handle(e);
		} catch (UnauthorizedOperationException e) {
			this.exceptionHandler.handle(e);
		} catch (InternalErrorException e) {
			this.exceptionHandler.handle(e);
		} catch (PostNotFoundException e) {
			this.exceptionHandler.handle(e);
		}
	}
}
