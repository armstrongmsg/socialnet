package com.armstrongmsg.socialnet.storage.media;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.armstrongmsg.socialnet.constants.Messages;
import com.armstrongmsg.socialnet.exceptions.FatalErrorException;
import com.armstrongmsg.socialnet.exceptions.InternalErrorException;
import com.armstrongmsg.socialnet.exceptions.MediaNotFoundException;
import com.armstrongmsg.socialnet.util.ApplicationPaths;

public class LocalFileSystemMediaRepository implements MediaRepository {
	public static final String DEFAULT_MEDIA_LOCAL_PATH = "pics/";
	private String pictureRepositoryLocalPath;

	public LocalFileSystemMediaRepository(String pictureRepositoryLocalPath) {
		this.pictureRepositoryLocalPath = pictureRepositoryLocalPath + File.separator + DEFAULT_MEDIA_LOCAL_PATH;
		File path = new File(this.pictureRepositoryLocalPath);

		if (!path.exists()) {
			path.mkdirs();
		}
	}

	public LocalFileSystemMediaRepository() throws FatalErrorException {
		this(ApplicationPaths.getApplicationBasePath());
	}

	@Override
	public void createMedia(String requester, String id, Map<String, String> metadata, byte[] data) throws InternalErrorException {
		String pictureLocalPath = pictureRepositoryLocalPath + File.separator + id;
		File localPathFile = new File(pictureLocalPath);
		FileOutputStream out = null;

		try {
			if (!localPathFile.exists()) {
				localPathFile.createNewFile();
				out = new FileOutputStream(localPathFile);
				out.write(data);
			}
		} catch (IOException e) {
			throw new InternalErrorException(
					String.format(Messages.Exception.ERROR_WHILE_CREATING_MEDIA_LOCALLY, id, e.getMessage()));
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				throw new InternalErrorException(
						String.format(Messages.Exception.ERROR_WHILE_CREATING_MEDIA_LOCALLY, id, e.getMessage()));
			}
		}
	}

	@Override
	public String getMediaUri(String requester, String id) throws MediaNotFoundException {
		File localPath = new File(pictureRepositoryLocalPath + File.separator + id);

		if (!localPath.exists()) {
			throw new MediaNotFoundException(
					String.format(Messages.Exception.MEDIA_NOT_FOUND, id));
		}

		return DEFAULT_MEDIA_LOCAL_PATH + id;
	}

	@Override
	public void deleteMedia(String requester, String id) throws MediaNotFoundException {
		File localPath = new File(pictureRepositoryLocalPath + File.separator + id);

		if (localPath.exists()) {
			localPath.delete();
		} else {
			throw new MediaNotFoundException(
					String.format(Messages.Exception.MEDIA_NOT_FOUND, id));
		}
	}

	@Override
	public void updateMedia(String requester, String id, Map<String, String> metadata, byte[] data) 
			throws InternalErrorException, MediaNotFoundException {
		String pictureLocalPath = pictureRepositoryLocalPath + File.separator + id;
		File localPathFile = new File(pictureLocalPath);
		FileOutputStream out = null;

		try {
			if (!localPathFile.exists()) {
				throw new MediaNotFoundException(
						String.format(Messages.Exception.MEDIA_NOT_FOUND, id));
			}
			out = new FileOutputStream(localPathFile);
			out.write(data);
		} catch (IOException e) {
			throw new InternalErrorException(
					String.format(Messages.Exception.ERROR_WHILE_ACCESSING_MEDIA_LOCALLY, 
							id, e.getMessage()));
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				throw new InternalErrorException(
						String.format(Messages.Exception.ERROR_WHILE_ACCESSING_MEDIA_LOCALLY, 
								id, e.getMessage()));
			}
		}
	}

	@Override
	public List<String> searchMedia(String requester, String metadataKey, String metadataValue) {
		// TODO implement
		return null;
	}
}
