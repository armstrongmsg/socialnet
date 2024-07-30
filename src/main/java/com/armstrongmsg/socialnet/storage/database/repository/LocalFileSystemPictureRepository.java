package com.armstrongmsg.socialnet.storage.database.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.armstrongmsg.socialnet.constants.ConfigurationProperties;
import com.armstrongmsg.socialnet.constants.Messages;
import com.armstrongmsg.socialnet.exceptions.FatalErrorException;
import com.armstrongmsg.socialnet.model.Picture;
import com.armstrongmsg.socialnet.util.PropertiesUtil;

public class LocalFileSystemPictureRepository implements PictureRepository {
	private String pictureRepositoryLocalPath;
	
	public LocalFileSystemPictureRepository(String pictureRepositoryLocalPath) {
		this.pictureRepositoryLocalPath = pictureRepositoryLocalPath;
		
		File path = new File(pictureRepositoryLocalPath);
		
		if (!path.exists()) {
			path.mkdirs();
		}
	}
			
	public LocalFileSystemPictureRepository() throws FatalErrorException {
		String repositoryLocalPathProperty =
				PropertiesUtil.getInstance().getProperty(ConfigurationProperties.PICTURE_REPOSITORY_LOCAL_PATH);
		
		if (repositoryLocalPathProperty == null || repositoryLocalPathProperty.isEmpty()) {
			throw new FatalErrorException(String.format(Messages.Exception.CANNOT_LOAD_LOCAL_PATH_PROPERTY,
					ConfigurationProperties.PICTURE_REPOSITORY_LOCAL_PATH));
		}
		
		pictureRepositoryLocalPath = repositoryLocalPathProperty;
		File path = new File(pictureRepositoryLocalPath);
		
		if (!path.exists()) {
			path.mkdirs();
		}
	}
	
	@Override
	public Picture getPictureById(String id) {
		File localPath = new File(pictureRepositoryLocalPath + File.separator + id);
		
		if (!localPath.exists()) {
			return null;
		}
		
		FileInputStream pictureInputStream = null;
		try {
			pictureInputStream = new FileInputStream(localPath);
			Long sizeAsLong = localPath.length();
			int size = sizeAsLong.intValue();
			byte[] data = new byte[size];
			pictureInputStream.read(data);
			return new Picture(id, data);
		} catch (IOException e) {
			// maybe should throw RollbackException
			return null;
		} finally {
			if (pictureInputStream != null) {
				try {
					pictureInputStream.close();
				} catch (IOException e) {
					// maybe should throw RollbackException
				}
			}
		}
	}

	@Override
	public void savePicture(Picture picture) {
		File localPath = new File(pictureRepositoryLocalPath + File.separator + picture.getId());
		FileOutputStream out = null;

		try {
			if (!localPath.exists()) {
				localPath.createNewFile();
				out = new FileOutputStream(localPath);
				out.write(picture.getData());
			}
		} catch (IOException e) {
			// maybe should throw RollbackException
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				// maybe should throw RollbackException
			}
		}
	}
}
