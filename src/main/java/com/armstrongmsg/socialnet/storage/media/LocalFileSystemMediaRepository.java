package com.armstrongmsg.socialnet.storage.media;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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
		this(ApplicationPaths.getApplicationBasePath() + File.separator + DEFAULT_MEDIA_LOCAL_PATH);
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
			// TODO add message
			throw new InternalErrorException();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				// TODO add message
				throw new InternalErrorException();
			}
		}
	}

	@Override
	public String getMediaUri(String requester, String id) throws MediaNotFoundException {
		File localPath = new File(pictureRepositoryLocalPath + File.separator + id);

		if (!localPath.exists()) {
			// TODO add message
			throw new MediaNotFoundException();
		}

		return DEFAULT_MEDIA_LOCAL_PATH + id;
	}

	@Override
	public void deleteMedia(String requester, String id) throws MediaNotFoundException {
		File localPath = new File(pictureRepositoryLocalPath + File.separator + id);

		if (localPath.exists()) {
			localPath.delete();
		} else {
			// TODO add message
			throw new MediaNotFoundException();
		}
	}

	@Override
	public void updateMedia(String requester, String id, Map<String, String> metadata, byte[] data) throws InternalErrorException, MediaNotFoundException {
		String pictureLocalPath = pictureRepositoryLocalPath + File.separator + id;
		File localPathFile = new File(pictureLocalPath);
		FileOutputStream out = null;

		try {
			if (!localPathFile.exists()) {
				// TODO add message
				throw new MediaNotFoundException();
			}
			out = new FileOutputStream(localPathFile);
			out.write(data);
		} catch (IOException e) {
			// TODO add message
			throw new InternalErrorException();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				// TODO add message
				throw new InternalErrorException();
			}
		}
	}

	@Override
	public List<String> searchMedia(String requester, String metadataKey, String metadataValue) {
		// TODO Auto-generated method stub
		return null;
	}
}
