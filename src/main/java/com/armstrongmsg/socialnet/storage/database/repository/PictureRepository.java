package com.armstrongmsg.socialnet.storage.database.repository;

import com.armstrongmsg.socialnet.model.Picture;

public interface PictureRepository {
	Picture getPictureById(String id);
	void savePicture(Picture picture);
	void deletePicture(Picture picture);
}
