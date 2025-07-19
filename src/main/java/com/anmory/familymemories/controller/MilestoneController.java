package com.anmory.familymemories.controller;

/**
 * @author Anmory
 * @description TODO
 * @date 2025-07-18 上午2:12
 */


import com.anmory.familymemories.model.Milestones;
import com.anmory.familymemories.model.Photos;
import com.anmory.familymemories.model.UserInfo;
import com.anmory.familymemories.service.*;
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
@RequestMapping("/milestone")
public class MilestoneController {
    @Autowired
    private MilestonesService milestonesService;
    @Autowired
    private PhotosService photosService;
    @Autowired
    private PhotoMilestonesService photoMilestonesService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    ToolService toolService;

    /**
     * 创建里程碑
     */
    @PostMapping("/create")
    public int createMilestone(@RequestParam int familyId,
                               @RequestParam String title,
                               @RequestParam String description,
                               @RequestParam String eventDate,
                               HttpServletRequest request,
                               @RequestParam(required = false) Integer userIdn) {
        Date eventDateFinal = toolService.stringToDate(eventDate);
        // 校验用户权限（可选）
        int currentUserId = resolveUserId(request, userIdn);
        if (currentUserId <= 0) {
            log.error("创建里程碑失败：无法确定有效用户ID");
            return -1;
        }

        // 创建里程碑
        int milestoneId = milestonesService.createMilestone(familyId, title, description, eventDateFinal);
        if (milestoneId > 0) {
            log.info("里程碑创建成功：ID={}, 家庭ID={}", milestoneId, familyId);
            return milestoneId;
        } else {
            log.error("里程碑创建失败：家庭ID={}", familyId);
            return -1;
        }
    }

    /**
     * 关联照片到里程碑
     */
    @PostMapping("/associatePhoto")
    public boolean associatePhotoWithMilestone(@RequestParam int photoId,
                                               @RequestParam int milestoneId,
                                               HttpServletRequest request,
                                               @RequestParam(required = false) Integer userIdn) {
        // 校验用户权限（可选）
        int currentUserId = resolveUserId(request, userIdn);
        if (currentUserId <= 0) {
            log.error("关联照片失败：无法确定有效用户ID");
            return false;
        }

        // 校验照片和里程碑是否存在（可选）
        Photos photo = photosService.getPhoto(photoId);
        Milestones milestone = milestonesService.getMilestone(milestoneId);
        if (photo == null || milestone == null) {
            log.error("关联照片失败：照片ID={}或里程碑ID={}不存在", photoId, milestoneId);
            return false;
        }

        // 校验照片和里程碑是否属于同一家庭（可选）
        if (photo.getFamilyId() != milestone.getFamilyId()) {
            log.error("关联照片失败：照片ID={}和里程碑ID={}不属于同一家庭", photoId, milestoneId);
            return false;
        }

        // 关联照片到里程碑
        int result = photoMilestonesService.associatePhotoWithMilestone(photoId, milestoneId);
        if (result > 0) {
            log.info("照片关联成功：照片ID={}, 里程碑ID={}", photoId, milestoneId);
            return true;
        } else {
            log.error("照片关联失败：照片ID={}, 里程碑ID={}", photoId, milestoneId);
            return false;
        }
    }

    /**
     * 上传并关联照片到里程碑
     */
    @PostMapping("/uploadAndAssociate")
    public boolean uploadAndAssociatePhoto(@RequestParam("file") MultipartFile file,
                                           @RequestParam int milestoneId,
                                           @RequestParam String title,
                                           @RequestParam String description,
                                           @RequestParam(required = false) java.util.Date takeTime,
                                           @RequestParam String takeAt,
                                           @RequestParam(required = false) String cameraParameters,
                                           @RequestParam String tag,
                                           HttpServletRequest request,
                                           @RequestParam(required = false) Integer userIdn) throws IOException {
        // 校验用户权限（可选）
        int currentUserId = resolveUserId(request, userIdn);
        if (currentUserId <= 0) {
            log.error("上传照片失败：无法确定有效用户ID");
            return false;
        }

        // 获取里程碑信息
        Milestones milestone = milestonesService.getMilestone(milestoneId);
        if (milestone == null) {
            log.error("上传照片失败：里程碑ID={}不存在", milestoneId);
            return false;
        }

        // 处理文件上传
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

        // 上传照片信息到数据库
        int familyId = milestone.getFamilyId();
        int photoId = photosService.uploadPhoto(
                familyId,
                currentUserId,
                filePath,
                title,
                description,
                takeTime,
                takeAt,
                cameraParameters,
                tag
        );

        if (photoId <= 0) {
            log.error("照片信息保存失败：用户ID={}, 家庭ID={}", currentUserId, familyId);
            return false;
        }

        // 关联照片到里程碑
        int result = photoMilestonesService.associatePhotoWithMilestone(photoId, milestoneId);
        if (result > 0) {
            log.info("照片上传并关联成功：照片ID={}, 里程碑ID={}", photoId, milestoneId);
            return true;
        } else {
            log.error("照片关联失败：照片ID={}, 里程碑ID={}", photoId, milestoneId);
            // 可选择回滚文件上传
            return false;
        }
    }

    /**
     * 删除里程碑（自动解除关联的照片）
     */
    @PostMapping("/delete")
    public boolean deleteMilestone(@RequestParam int milestoneId,
                                   HttpServletRequest request,
                                   @RequestParam(required = false) Integer userIdn) {
        // 校验用户权限（可选）
        int currentUserId = resolveUserId(request, userIdn);
        if (currentUserId <= 0) {
            log.error("删除里程碑失败：无法确定有效用户ID");
            return false;
        }

        // 删除里程碑
        int result = milestonesService.deleteMilestone(milestoneId);
        if (result > 0) {
            log.info("里程碑删除成功：ID={}", milestoneId);
            return true;
        } else {
            log.error("里程碑删除失败：ID={}（可能不存在）", milestoneId);
            return false;
        }
    }

    /**
     * 查看单个里程碑详情
     */
    @GetMapping("/detail")
    public Milestones getMilestone(@RequestParam int milestoneId) {
        return milestonesService.getMilestone(milestoneId);
    }

    /**
     * 查看家庭下所有里程碑
     */
    @GetMapping("/listByFamily")
    public List<Milestones> listByFamily(@RequestParam int familyId) {
        return milestonesService.getAllMilestonesByFamilyId(familyId);
    }

    /**
     * 查看里程碑关联的所有照片
     */
    @GetMapping("/photos")
    public List<Photos> getMilestonePhotos(@RequestParam int milestoneId) {
        // 1. 查询关联的照片ID列表（需补充PhotoMilestonesMapper方法）
        List<Integer> photoIds = photoMilestonesService.getPhotoIdsByMilestoneId(milestoneId);

        // 2. 根据ID列表查询照片详情
        return photosService.getPhotosByIds(photoIds);
    }

    /**
     * 解析用户ID（优先级：userIdn参数 > Session）
     */
    private int resolveUserId(HttpServletRequest request, Integer userIdn) {
        // 1. 优先使用userIdn参数
        if (userIdn != null && userIdn > 0) {
            log.debug("使用userIdn参数获取用户ID：{}", userIdn);
            return userIdn;
        }

        // 2. 从Session获取
        UserInfo loginUser = (UserInfo) request.getSession().getAttribute("session_user_key");
        if (loginUser != null && loginUser.getUserId() > 0) {
            log.debug("使用Session获取用户ID：{}", loginUser.getUserId());
            return loginUser.getUserId();
        }

        // 3. 无效用户ID
        return -1;
    }
}
