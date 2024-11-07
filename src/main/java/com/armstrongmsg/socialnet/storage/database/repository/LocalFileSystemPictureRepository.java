package com.armstrongmsg.socialnet.storage.database.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.armstrongmsg.socialnet.exceptions.FatalErrorException;
import com.armstrongmsg.socialnet.model.Picture;
import com.armstrongmsg.socialnet.util.ApplicationPaths;

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
		pictureRepositoryLocalPath = ApplicationPaths.getApplicationBasePath() + File.separator + "pics";
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
			return new Picture(id, data, "pics/" + id/* + pictureRepositoryLocalPath + File.separator + id */);
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
		String pictureLocalPath = pictureRepositoryLocalPath + File.separator + picture.getId();
		File localPathFile = new File(pictureLocalPath);
		FileOutputStream out = null;

		try {
			if (!localPathFile.exists()) {
				localPathFile.createNewFile();
				out = new FileOutputStream(localPathFile);
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

	// TODO test
	@Override
	public void deletePicture(Picture picture) {
		File localPath = new File(pictureRepositoryLocalPath + File.separator + picture.getId());
		
		if (localPath.exists()) {
			localPath.delete();
		}
	}
}
