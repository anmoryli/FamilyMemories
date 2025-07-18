package com.anmory.familymemories.service;

/**
 * @author Anmory
 * @description TODO
 * @date 2025-07-18 上午1:00
 */

import com.anmory.familymemories.mapper.PhotosMapper;
import com.anmory.familymemories.model.Photos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhotosService {
    @Autowired
    private PhotosMapper photosMapper;

    public int uploadPhoto(int familyId, int uploaderId, String filePath, String title,
                           String description, java.util.Date takeTime, String takeAt,
                           String cameraParameters, String tag) {
        return photosMapper.insert(familyId, uploaderId, filePath, title,
                description, takeTime, takeAt, cameraParameters, tag);
    }

    public int updatePhoto(int photoId, String title, String description,
                           java.util.Date takeTime, String takeAt,
                           String cameraParameters, String tag) {
        return photosMapper.update(photoId, title, description,
                takeTime, takeAt, cameraParameters, tag);
    }

    public int getPhotoIdByFilePath(String filePath) {
        return photosMapper.getPhotoIdByFilePath(filePath);
    }

    public int deletePhoto(int photoId) {
        return photosMapper.deleteById(photoId);
    }

    public Photos getPhoto(int photoId) {
        return photosMapper.selectById(photoId);
    }

    public List<Photos> getPhotosByTag(String tag) {
        return photosMapper.selectByTag(tag);
    }

    public List<Photos> selectByFamilyId(int familyId) {
        return photosMapper.selectByFamilyId(familyId);
    }

    /**
     * 根据ID列表查询照片
     */
    public List<Photos> getPhotosByIds(List<Integer> photoIds) {
        return photosMapper.selectByIds(photoIds);
    }

    int addTagToPhoto(int photoId, String tag) {
        return photosMapper.addTagToPhoto(photoId, tag);
    }
}
