package com.armstrongmsg.socialnet.storage.database.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.armstrongmsg.socialnet.constants.ConfigurationProperties;
import com.armstrongmsg.socialnet.constants.Messages;
import com.armstrongmsg.socialnet.exceptions.FatalErrorException;
import com.armstrongmsg.socialnet.model.Picture;
import com.armstrongmsg.socialnet.util.ApplicationPaths;
import com.armstrongmsg.socialnet.util.PropertiesUtil;

public class LocalFileSystemPictureRepository implements PictureRepository {
	private String pictureRepositoryLocalPath;
	private String pictureCacheDirectory;
	
	public LocalFileSystemPictureRepository(String pictureRepositoryLocalPath, String pictureCacheDirectory) {
		this.pictureRepositoryLocalPath = pictureRepositoryLocalPath;
		
		File path = new File(pictureRepositoryLocalPath);
		
		if (!path.exists()) {
			path.mkdirs();
		}
		
		this.pictureCacheDirectory = pictureCacheDirectory;
		File cachePath = new File(pictureCacheDirectory);
		
		if (!cachePath.exists()) {
			cachePath.mkdir();
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
		
		pictureCacheDirectory = ApplicationPaths.getApplicationImageCachePath();
		File cachePath = new File(pictureCacheDirectory);
		
		if (!cachePath.exists()) {
			cachePath.mkdir();
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
			String pictureLocalPath = pictureCacheDirectory + File.separator + id;
			createCachedImageCopies(id, data, pictureLocalPath);
			return new Picture(id, data, pictureLocalPath);
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

	private void createCachedImageCopies(String id, byte[] data, String pictureLocalPath) throws IOException {
		File cachedImageFile = new File(pictureLocalPath);

		if (!cachedImageFile.exists()) {
			FileOutputStream out = null;
			try {
				cachedImageFile.createNewFile();
				out = new FileOutputStream(cachedImageFile);
				out.write(data);
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

	@Override
	public void savePicture(Picture picture) {
		String pictureLocalPath = pictureRepositoryLocalPath + File.separator + picture.getId();
		File localPathFile = new File(pictureLocalPath);
		FileOutputStream out = null;

		try {
			if (!localPathFile.exists()) {
				localPathFile.createNewFile();
				out = new FileOutputStream(localPathFile);
				out.write(picture.getData());
				String cachedImageLocalPath = this.pictureCacheDirectory + File.separator + picture.getId();
				createCachedImageCopies(picture.getId(), picture.getData(), cachedImageLocalPath);
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

	@Override
	public void deletePicture(Picture picture) {
		File localPath = new File(pictureRepositoryLocalPath + File.separator + picture.getId());
		
		if (localPath.exists()) {
			localPath.delete();
		}
		
		String cachedImageLocalPath = pictureCacheDirectory + File.separator + picture.getId();
		File cachedImageLocalPathFile = new File(cachedImageLocalPath);
		
		if (cachedImageLocalPathFile.exists()) {
			cachedImageLocalPathFile.delete();
		}
	}
}
