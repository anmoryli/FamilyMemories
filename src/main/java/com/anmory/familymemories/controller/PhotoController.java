package com.anmory.familymemories.controller;

import com.anmory.familymemories.model.Photos;
import com.anmory.familymemories.model.UserInfo;
import com.anmory.familymemories.service.PhotoFamilyMembersService;
import com.anmory.familymemories.service.PhotosService;
import com.anmory.familymemories.service.ToolService;
import com.anmory.familymemories.service.UserInfoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/photo")
public class PhotoController {
    @Autowired
    private PhotosService photosService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    PhotoFamilyMembersService photoFamilyMembersService;
    @Autowired
    ToolService toolService;

    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    public boolean uploadPhoto(@RequestParam("file") MultipartFile file,
                               HttpServletRequest request,
                               @RequestParam(required = false) Integer userIdn,
                               @RequestParam String title,
                               @RequestParam String description,
                               @RequestParam(required = false) String takeTime,
                               @RequestParam String takeAt,
                               @RequestParam(required = false) String cameraParameters,
                               @RequestParam String tag,
                               @RequestParam(value = "relatedMembers[]", required = false) List<Integer> relatedMembers) {
        log.info("接收到的参数: title={}, takeTime={}, relatedMembers={}", title, takeTime, relatedMembers);

        // 调用工具把String类型的takeTime转换为Date类型
        Date takeTimeDate = toolService.stringToDate(takeTime);
        if (takeTimeDate == null && takeTime != null && !takeTime.isEmpty()) {
            log.error("日期解析失败：{}", takeTime);
            return false;
        }

        // 校验文件是否为空
        if (file.isEmpty()) {
            log.error("文件上传失败：文件为空");
            return false;
        }

        // 处理文件存储
        String fileName = file.getOriginalFilename();
        String filePath = "/usr/local/nginx/files/family-photos/" + fileName;
        File dir = new File("/usr/local/nginx/files/family-photos/");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(file.getBytes());
            log.info("文件上传成功：{}", filePath);
        } catch (IOException e) {
            log.error("文件写入失败：{}", e.getMessage());
            return false;
        }

        // 确定当前用户ID
        int currentUserId = resolveUserId(request, userIdn);
        if (currentUserId <= 0) {
            log.error("照片上传失败：无法确定有效用户ID");
            return false;
        }

        // 获取用户所属家庭ID
        int familyId = userInfoService.getFamilyIdByUserId(currentUserId);
        if (familyId <= 0) {
            log.error("照片上传失败：用户ID={}未加入任何家庭", currentUserId);
            return false;
        }

        // 保存照片信息
        int result = photosService.uploadPhoto(
                familyId,
                currentUserId,
                filePath,
                title,
                description,
                takeTimeDate,
                takeAt,
                cameraParameters,
                tag
        );

        int photoId = photosService.getPhotoIdByFilePath(filePath);

        // 处理 relatedMembers（允许为空）
        if (relatedMembers != null) {
            for (Integer memberId : relatedMembers) {
                if (memberId != null && memberId > 0) {
                    photoFamilyMembersService.addPhotoFamilyMember(photoId, memberId);
                } else {
                    log.warn("无效的家庭成员ID：{}", memberId);
                }
            }
        }

        if (result > 0) {
            log.info("照片信息保存成功：用户ID={}, 家庭ID={}", currentUserId, familyId);
            return true;
        } else {
            log.error("照片信息保存失败：用户ID={}, 家庭ID={}", currentUserId, familyId);
            return false;
        }
    }

    @PostMapping("/delete")
    public boolean deletePhoto(@RequestParam int photoId) {
        if (photoId <= 0) {
            log.error("删除照片失败：无效的照片ID={}", photoId);
            return false;
        }

        int result = photosService.deletePhoto(photoId);
        if (result > 0) {
            log.info("照片删除成功：ID={}", photoId);
            return true;
        } else {
            log.error("照片删除失败：ID={}（可能不存在）", photoId);
            return false;
        }
    }

    @PostMapping("/update")
    public boolean updatePhoto(@RequestParam int photoId,
                               @RequestParam String title,
                               @RequestParam String description,
                               @RequestParam(required = false) String takeTime,
                               @RequestParam String takeAt,
                               @RequestParam(required = false) String cameraParameters,
                               @RequestParam String tag) {
        Date takeTimeDate = toolService.stringToDate(takeTime);
        if (photoId <= 0) {
            log.error("修改照片失败：无效的照片ID={}", photoId);
            return false;
        }

        int result = photosService.updatePhoto(
                photoId,
                title,
                description,
                takeTimeDate,
                takeAt,
                cameraParameters,
                tag
        );

        if (result > 0) {
            log.info("照片修改成功：ID={}", photoId);
            return true;
        } else {
            log.error("照片修改失败：ID={}", photoId);
            return false;
        }
    }

    @GetMapping("/getPhotosByTag")
    public List<Photos> getPhotosByTag(@RequestParam String tag) {
        if (tag == null || tag.trim().isEmpty()) {
            log.error("查询照片失败：标签为空");
            return null;
        }

        List<Photos> photos = photosService.getPhotosByTag(tag);
        if (photos.isEmpty()) {
            log.info("未查询到标签为{}的照片", tag);
            return photos;
        }

        log.info("查询到标签为{}的照片共{}张", tag, photos.size());
        return photos;
    }

    @GetMapping("/getPhotosByFamilyId")
    public List<Photos> getPhotosByFamilyId(@RequestParam(required = false) Integer familyId,
                                            HttpServletRequest request,
                                            @RequestParam(required = false) Integer userIdn) {
        if (familyId == null || familyId <= 0) {
            int currentUserId = resolveUserId(request, userIdn);
            if (currentUserId <= 0) {
                log.error("查询照片失败：未指定家庭ID且无法确定有效用户ID");
                return null;
            }

            familyId = userInfoService.getFamilyIdByUserId(currentUserId);
            if (familyId <= 0) {
                log.error("查询照片失败：用户ID={}未加入任何家庭", currentUserId);
                return null;
            }
        }

        List<Photos> photos = photosService.selectByFamilyId(familyId);
        if (photos.isEmpty()) {
            log.info("未查询到家庭ID={}的照片", familyId);
            return photos;
        }

        log.info("查询到家庭ID={}的照片共{}张", familyId, photos.size());
        return photos;
    }

    @RequestMapping("/getPhotoByUserId")
    public List<Photos> getPhotoByUserId(HttpServletRequest request,
                                         @RequestParam(required = false) Integer userIdn) {
        int userId = resolveUserId(request, userIdn);
        log.debug("解析到用户ID：{}", userId);
        if (userId <= 0) {
            log.error("查询照片失败：无法确定有效用户ID");
            return null;
        }

        int familyId = userInfoService.getFamilyIdByUserId(userId);
        if (familyId <= 0) {
            log.error("查询照片失败：用户ID={}未加入任何家庭", userId);
            return null;
        }

        List<Photos> photos = photosService.selectByFamilyId(familyId);
        if (photos.isEmpty()) {
            log.info("未查询到用户ID={}的照片", userId);
            return photos;
        }
        log.info("查询到用户ID={}的照片共{}张", userId, photos.size());
        return photos;
    }

    private int resolveUserId(HttpServletRequest request, Integer userIdn) {
        if (userIdn != null && userIdn > 0) {
            log.debug("使用userIdn参数获取用户ID：{}", userIdn);
            return userIdn;
        }

        UserInfo loginUser = (UserInfo) request.getSession().getAttribute("session_user_key");
        if (loginUser != null && loginUser.getUserId() > 0) {
            log.debug("使用Session获取用户ID：{}", loginUser.getUserId());
            return loginUser.getUserId();
        }

        return -1;
    }
}