package com.armstrongmsg.socialnet.storage.database;

import java.util.List;

import com.armstrongmsg.socialnet.constants.ConfigurationProperties;
import com.armstrongmsg.socialnet.constants.SystemConstants;
import com.armstrongmsg.socialnet.exceptions.FatalErrorException;
import com.armstrongmsg.socialnet.exceptions.UserNotFoundException;
import com.armstrongmsg.socialnet.model.Follow;
import com.armstrongmsg.socialnet.model.Friendship;
import com.armstrongmsg.socialnet.model.FriendshipRequest;
import com.armstrongmsg.socialnet.model.Picture;
import com.armstrongmsg.socialnet.model.Post;
import com.armstrongmsg.socialnet.model.User;
import com.armstrongmsg.socialnet.storage.database.repository.FollowRepository;
import com.armstrongmsg.socialnet.storage.database.repository.FriendshipRepository;
import com.armstrongmsg.socialnet.storage.database.repository.FriendshipRequestRepository;
import com.armstrongmsg.socialnet.storage.database.repository.LocalFileSystemPictureRepository;
import com.armstrongmsg.socialnet.storage.database.repository.MediaServicePictureRepository;
import com.armstrongmsg.socialnet.storage.database.repository.PictureRepository;
import com.armstrongmsg.socialnet.storage.database.repository.UserRepository;
import com.armstrongmsg.socialnet.util.PropertiesUtil;

// TODO test
public class PictureLoadingAwareDatabaseManager extends DefaultDatabaseManager {
	private PictureRepository pictureRepository;
	
	public PictureLoadingAwareDatabaseManager(UserRepository userRepository, FriendshipRepository friendshipRepository,
			FollowRepository followRepository, FriendshipRequestRepository friendshipRequestsRepository,
			PictureRepository pictureRepository) {
		super(userRepository, friendshipRepository, followRepository, friendshipRequestsRepository);
		this.pictureRepository = pictureRepository;
	}
	
	public PictureLoadingAwareDatabaseManager() throws FatalErrorException {
		super();
		
		String remoteMediaStorageProperty = "false";
		
		try {
			remoteMediaStorageProperty = PropertiesUtil.getInstance().
					getProperty(ConfigurationProperties.REMOTE_MEDIA_STORAGE);
		} catch (FatalErrorException e) {
			// TODO log
		}
		
		if (remoteMediaStorageProperty.equals("true")) {
			String mediaServiceUrl = PropertiesUtil.getInstance().getProperty(ConfigurationProperties.REMOTE_MEDIA_STORAGE_URL);
			String mediaServicePublicUrl = PropertiesUtil.getInstance().getProperty(ConfigurationProperties.REMOTE_MEDIA_STORAGE_PUBLIC_URL);
			String mediaServicePort = PropertiesUtil.getInstance().getProperty(ConfigurationProperties.REMOTE_MEDIA_STORAGE_PORT);
			this.pictureRepository = new MediaServicePictureRepository(mediaServiceUrl, mediaServicePublicUrl, mediaServicePort);
		} else {
			this.pictureRepository = new LocalFileSystemPictureRepository();
		}
	}
	
	@Override
	public User getUserById(String id) throws UserNotFoundException {
		User user = super.getUserById(id);
		loadUserProfilePicture(user);
		loadUserPostsPictures(user);
		return user;
	}

	@Override
	public User getUserByUsername(String username) throws UserNotFoundException {
		User user = super.getUserByUsername(username);
		loadUserProfilePicture(user);
		loadUserPostsPictures(user);
		return user;
	}

	@Override
	public void saveUser(User user) {
		super.saveUser(user);
		saveUserProfilePicture(user);
		saveUserPostsPictures(user);
	}

	@Override
	public void updateUser(User user) throws UserNotFoundException {
		User userInStorage = super.getUserById(user.getUserId()); 
		super.updateUser(user);
		saveUserProfilePicture(user);
		removePicturesFromRemovedPosts(user, userInStorage);
		saveUserPostsPictures(user);
	}
	
	@Override
	public List<User> getAllUsers() {
		List<User> users = super.getAllUsers();
		
		for (User user : users) {
			loadUserProfilePicture(user);
			loadUserPostsPictures(user);
		}
		
		return users;
	}

	@Override
	public void removeUserById(String userId) throws UserNotFoundException {
		User user = super.getUserById(userId);
		super.removeUserById(userId);
		removeUserPicture(user);
		removePostsPictures(user);
	}

	@Override
	public List<Friendship> getFriendshipsByUserId(String userId) {
		List<Friendship> friendships = super.getFriendshipsByUserId(userId);
		
		for (Friendship friendship : friendships) {
			loadUserProfilePicture(friendship.getFriend1());
			loadUserPostsPictures(friendship.getFriend1());
			loadUserProfilePicture(friendship.getFriend2());
			loadUserPostsPictures(friendship.getFriend2());
		}
		
		return friendships;
	}

	@Override
	public void saveFriendship(Friendship friendship) {
		super.saveFriendship(friendship);
	}
	
	@Override
	public void removeFriendship(Friendship friendship) {
		super.removeFriendship(friendship);
	}

	@Override
	public List<Follow> getFollowsByUserId(String userId) {
		List<Follow> follows =  super.getFollowsByUserId(userId);
		
		for (Follow follow : follows) {
			loadUserProfilePicture(follow.getFollowed());
			loadUserPostsPictures(follow.getFollowed());
			loadUserProfilePicture(follow.getFollower());
			loadUserPostsPictures(follow.getFollower());
		}
		
		return follows;
	}

	@Override
	public void saveFollow(Follow follow) {
		super.saveFollow(follow);
	}

	@Override
	public void removeFollow(Follow follow) {
		super.removeFollow(follow);
	}

	@Override
	public void saveFriendshipRequest(FriendshipRequest friendshipRequest) {
		super.saveFriendshipRequest(friendshipRequest);
	}

	@Override
	public List<FriendshipRequest> getSentFriendshipRequestsById(String userId) {
		return super.getSentFriendshipRequestsById(userId);
	}

	@Override
	public List<FriendshipRequest> getReceivedFriendshipRequestsById(String userId) {	
		return super.getReceivedFriendshipRequestsById(userId);
	}

	@Override
	public FriendshipRequest getReceivedFriendshipRequestById(String userId, String username) {		
		return super.getReceivedFriendshipRequestById(userId, username);
	}

	@Override
	public void removeFriendshipRequestById(FriendshipRequest friendshipRequest) {
		super.removeFriendshipRequestById(friendshipRequest);
	}

	private void loadUserProfilePicture(User user) {
		if (!user.getProfile().getProfilePicId().equals(SystemConstants.DEFAULT_PROFILE_PIC_ID)) {
			Picture profilePic = this.pictureRepository.getPictureById(
					user.getProfile().getProfilePicId());
			user.getProfile().setProfilePic(profilePic);
		}
	}

	private void loadPostPicture(Post post) {
		if (post.getPictureId() != null) {
			Picture postPicture = 
					this.pictureRepository.getPictureById(post.getPictureId());
			post.setPicture(postPicture);
		}
	}

	private void loadUserPostsPictures(User user) {
		for (Post post : user.getProfile().getPosts()) {
			loadPostPicture(post);
		}
	}
	
	private void saveUserPostsPictures(User user) {
		for (Post post : user.getProfile().getPosts()) {
			if (post.getPicture() != null) {
				this.pictureRepository.savePicture(post.getPicture());
			}
		}
	}

	private void saveUserProfilePicture(User user) {
		if (!user.getProfile().getProfilePic().getId().equals(SystemConstants.DEFAULT_PROFILE_PIC_ID)) {
			this.pictureRepository.savePicture(user.getProfile().getProfilePic());
		}
	}
	
	private void removePicturesFromRemovedPosts(User user, User userInStorage) {
		for (Post postInStorage : userInStorage.getProfile().getPosts()) {
			if (!user.getProfile().getPosts().contains(postInStorage) && 
					postInStorage.getPicture() != null) {
				this.pictureRepository.deletePicture(postInStorage.getPicture());
			}
		}
	}
	
	private void removePostsPictures(User user) {
		for (Post post : user.getProfile().getPosts()) {
			if (post.getPicture() != null) {
				this.pictureRepository.deletePicture(post.getPicture());
			}
		}
	}

	private void removeUserPicture(User user) {
		if (!user.getProfile().getProfilePicId().equals(SystemConstants.DEFAULT_PROFILE_PIC_ID)) {
			this.pictureRepository.deletePicture(user.getProfile().getProfilePic());
		}
	}
}
