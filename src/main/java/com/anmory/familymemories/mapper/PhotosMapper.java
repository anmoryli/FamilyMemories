package com.anmory.familymemories.mapper;

import com.anmory.familymemories.model.Photos;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PhotosMapper {

    /**
     * 上传照片
     * @param familyId 所属家庭ID
     * @param uploaderId 上传者ID
     * @param filePath 文件路径
     * @param title 照片标题
     * @param description 照片描述
     * @param takeTime 拍摄时间
     * @param takeAt 拍摄地点
     * @param cameraParameters 相机参数
     * @param tag 照片标签
     * @return 插入的照片ID（自增主键）
     */
    @Insert("INSERT INTO photos (" +
            "family_id, uploader_id, file_path, title, description, " +
            "take_time, take_at, camera_parameters, tag, uploaded_at, updated_at" +
            ") VALUES (" +
            "#{familyId}, #{uploaderId}, #{filePath}, #{title}, #{description}, " +
            "#{takeTime}, #{takeAt}, #{cameraParameters}, #{tag}, NOW(), NOW()" +
            ")")
    int insert(
            @Param("familyId") int familyId,
            @Param("uploaderId") int uploaderId,
            @Param("filePath") String filePath,
            @Param("title") String title,
            @Param("description") String description,
            @Param("takeTime") java.util.Date takeTime,
            @Param("takeAt") String takeAt,
            @Param("cameraParameters") String cameraParameters,
            @Param("tag") String tag
    );

    /**
     * 更新照片信息
     * @param photoId 照片ID
     * @param title 照片标题
     * @param description 照片描述
     * @param takeTime 拍摄时间
     * @param takeAt 拍摄地点
     * @param cameraParameters 相机参数
     * @param tag 照片标签
     * @return 更新记录数（成功返回1）
     */
    @Update("UPDATE photos SET " +
            "title = #{title}, " +
            "description = #{description}, " +
            "take_time = #{takeTime}, " +
            "take_at = #{takeAt}, " +
            "camera_parameters = #{cameraParameters}, " +
            "tag = #{tag}, " +
            "updated_at = NOW() " +
            "WHERE photo_id = #{photoId}")
    int update(
            @Param("photoId") int photoId,
            @Param("title") String title,
            @Param("description") String description,
            @Param("takeTime") java.util.Date takeTime,
            @Param("takeAt") String takeAt,
            @Param("cameraParameters") String cameraParameters,
            @Param("tag") String tag
    );

    /**
     * 根据照片ID删除照片
     * @param photoId 照片ID
     * @return 删除记录数（成功返回1）
     */
    @Delete("DELETE FROM photos WHERE photo_id = #{photoId}")
    int deleteById(@Param("photoId") int photoId);

    /**
     * 根据照片ID查询照片
     * @param photoId 照片ID
     * @return 照片实体（不存在返回null）
     */
    @Select("SELECT * FROM photos WHERE photo_id = #{photoId}")
    Photos selectById(@Param("photoId") int photoId);

    /**
     * 根据标签查询照片列表
     * @param tag 照片标签
     * @return 照片列表
     */
    @Select("SELECT * FROM photos WHERE tag = #{tag}")
    List<Photos> selectByTag(@Param("tag") String tag);

    /**
     * 根据家庭ID查询照片列表
     * @param familyId 家庭ID
     * @return 照片列表
     */
    @Select("SELECT * FROM photos WHERE family_id = #{familyId}")
    List<Photos> selectByFamilyId(@Param("familyId") int familyId);

    /**
     * 根据ID列表查询照片
     * @param photoIds 照片ID列表
     * @return 照片列表
     */
    @Select("<script>" +
            "SELECT * FROM photos " +
            "WHERE photo_id IN " +
            "<foreach item='id' collection='photoIds' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<Photos> selectByIds(@Param("photoIds") List<Integer> photoIds);

    /**
     * 为照片添加标签（覆盖原标签）
     * @param photoId 照片ID
     * @param tag 新标签
     * @return 更新记录数（成功返回1）
     */
    @Update("UPDATE photos SET " +
            "tag = #{tag}, " +
            "updated_at = NOW() " +
            "WHERE photo_id = #{photoId}")
    int addTagToPhoto(
            @Param("photoId") int photoId,
            @Param("tag") String tag
    );

    @Select("SELECT photo_id FROM photos WHERE file_path = #{filePath}")
    int getPhotoIdByFilePath(String filePath);
}