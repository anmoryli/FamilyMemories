package com.anmory.familymemories.controller;

import com.anmory.familymemories.model.FamilyMembers;
import com.anmory.familymemories.model.Invitations;
import com.anmory.familymemories.model.UserInfo;
import com.anmory.familymemories.service.FamiliesService;
import com.anmory.familymemories.service.FamilyMembersService;
import com.anmory.familymemories.service.InvitationsService;
import com.anmory.familymemories.service.UserInfoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/family")
public class FamilyController {
    @Autowired
    private FamiliesService familiesService;
    @Autowired
    private FamilyMembersService familyMembersService;
    @Autowired
    private InvitationsService invitationsService;
    @Autowired
    private UserInfoService userInfoService;

    /**
     * 创建家庭
     */
    @PostMapping("/create")
    public boolean createFamily(@RequestParam String familyName,
                                @RequestParam String familyDescription,
                                HttpServletRequest request,
                                @RequestParam(required = false) Integer creatorId,
                                @RequestParam(required = false) Integer userIdn) {
        // 参数校验
        if (familyName == null || familyName.trim().isEmpty()) {
            log.error("创建家庭失败：家庭名称不能为空");
            return false;
        }
        if (familyDescription == null || familyDescription.trim().isEmpty()) {
            log.error("创建家庭失败：家庭描述不能为空");
            return false;
        }

        // 确定创建者ID
        int actualCreatorId = resolveUserId(request, creatorId, userIdn);
        if (actualCreatorId <= 0) {
            log.error("创建家庭失败：无法确定有效用户ID");
            return false;
        }

        // 创建家庭
        int result = familiesService.createFamily(familyName, familyDescription, actualCreatorId);
        int familyId = familiesService.getFamilyIdByFamilyNameAndFamilyDescription(familyName, familyDescription);
        if (result > 0) {
            log.info("家庭创建成功：名称={}, 创建者ID={}", familyName, actualCreatorId);
            // 添加创建者为家庭成员
            familyMembersService.addMember(familyId, actualCreatorId, "admin");
            return true;
        } else {
            log.error("家庭创建失败：名称={}, 创建者ID={}", familyName, actualCreatorId);
            return false;
        }
    }

    @RequestMapping("/getFamilyIdByUserId")
    public int getFamilyIdByUserId(int userIdn) {
        return familyMembersService.getFamilyIdByUserId(userIdn);
    }

    @RequestMapping("/isFamilyMember")
    public boolean isFamilyMember(@RequestParam int familyId,
                                  @RequestParam int userIdn) {
        if (familyId <= 0 || userIdn <= 0) {
            log.error("检查家庭成员失败：无效的家庭ID或用户ID");
            return false;
        }

        FamilyMembers member = familyMembersService.getMemberByFamilyAndUserId(familyId, userIdn);
        boolean isMember = (member != null);
        log.info("用户ID={}是否为家庭ID={}的成员：{}", userIdn, familyId, isMember);
        return isMember;
    }
    /**
     * 删除家庭
     */
    @PostMapping("/delete")
    public boolean deleteFamily(@RequestParam int familyId) {
        if (familyId <= 0) {
            log.error("删除家庭失败：无效的家庭ID={}", familyId);
            return false;
        }

        int result = familiesService.deleteFamily(familyId);
        if (result > 0) {
            log.info("家庭删除成功：ID={}", familyId);
            familyMembersService.deleteByFamilyId(familyId);
            return true;
        } else {
            log.error("家庭删除失败：ID={}（可能不存在）", familyId);
            return false;
        }
    }

    /**
     * 修改家庭信息
     */
    @PostMapping("/update")
    public boolean updateFamily(@RequestParam int familyId,
                                @RequestParam String familyName,
                                @RequestParam String familyDescription) {
        if (familyId <= 0) {
            log.error("修改家庭失败：无效的家庭ID={}", familyId);
            return false;
        }
        if (familyName == null || familyName.trim().isEmpty()) {
            log.error("修改家庭失败：家庭名称不能为空");
            return false;
        }
        if (familyDescription == null || familyDescription.trim().isEmpty()) {
            log.error("修改家庭失败：家庭描述不能为空");
            return false;
        }

        int result = familiesService.updateFamily(familyId, familyName, familyDescription);
        if (result > 0) {
            log.info("家庭修改成功：ID={}, 新名称={}", familyId, familyName);
            return true;
        } else {
            log.error("家庭修改失败：ID={}", familyId);
            return false;
        }
    }

    /**
     * 邀请成员加入家庭
     */
    @PostMapping("/inviteMember")
    public int inviteMember(@RequestParam int familyId,
                            @RequestParam(required = false) Integer inviterId,
                            @RequestParam int inviteeId,
                            @RequestParam(required = false) String status,
                            @RequestParam(required = false) Integer userIdn,
                            HttpServletRequest request) {
        if (familyId <= 0 || inviteeId <= 0) {
            log.error("邀请成员失败：家庭ID或被邀请者ID无效");
            return -1;
        }

        // 确定邀请者ID
        int actualInviterId = resolveUserId(request, inviterId, userIdn);
        if (actualInviterId <= 0) {
            log.error("邀请成员失败：无法确定有效邀请者ID");
            return -1;
        }

        // 处理状态默认值
        String actualStatus = (status == null || status.trim().isEmpty()) ? "pending" : status;

        // 创建邀请
        int invitationId = invitationsService.createInvitation(familyId, actualInviterId, inviteeId, actualStatus);
        if (invitationId > 0) {
            log.info("成员邀请成功：邀请ID={}, 家庭ID={}", invitationId, familyId);
            return invitationId;
        } else {
            log.error("成员邀请失败：家庭ID={}", familyId);
            return -1;
        }
    }

    /**
     * 从家庭中删除成员
     */
    @PostMapping("/removeMember")
    public boolean removeMember(@RequestParam int familyMemberId) {
        if (familyMemberId <= 0) {
            log.error("删除成员失败：无效的家庭成员ID={}", familyMemberId);
            return false;
        }

        int result = familyMembersService.removeMember(familyMemberId);
        if (result > 0) {
            log.info("成员删除成功：ID={}", familyMemberId);
            return true;
        } else {
            log.error("成员删除失败：ID={}", familyMemberId);
            return false;
        }
    }

    /**
     * 获取当前用户收到的邀请
     */
    @GetMapping("/getMemberRequests")
    public List<Invitations> getMemberRequests(HttpServletRequest request,
                                               @RequestParam(required = false) Integer userIdn) {
        int currentUserId = resolveUserId(request, null, userIdn);
        if (currentUserId <= 0) {
            log.error("获取邀请失败：无效用户ID");
            return new ArrayList<>();
        }

        List<Invitations> invitations = invitationsService.selectByInviteeId(currentUserId);
        log.info("用户ID={}的邀请数量：{}", currentUserId, invitations.size());
        return invitations;
    }

    /**
     * 接受邀请
     */
    @PostMapping("/acceptMemberRequest")
    public boolean acceptMemberRequest(@RequestParam int invitationId,
                                       HttpServletRequest request,
                                       @RequestParam(required = false) Integer userIdn) {
        if (invitationId <= 0) {
            log.error("接受邀请失败：无效邀请ID={}", invitationId);
            return false;
        }

        int currentUserId = resolveUserId(request, null, userIdn);
        if (currentUserId <= 0) {
            log.error("接受邀请失败：无效用户ID");
            return false;
        }

        // 查询邀请信息
        Invitations invitation = invitationsService.getInvitation(invitationId);
        if (invitation == null) {
            log.error("接受邀请失败：邀请ID={}不存在", invitationId);
            return false;
        }

        // 校验邀请归属
        if (invitation.getInviteeId() != currentUserId) {
            log.error("接受邀请失败：邀请不属于用户ID={}", currentUserId);
            return false;
        }

        // 校验邀请状态
        if (!"pending".equals(invitation.getStatus())) {
            log.error("接受邀请失败：邀请状态不为待处理（当前状态={}", invitation.getStatus());
            return false;
        }

        // 更新邀请状态
        int updateResult = invitationsService.updateStatus(invitationId, "accepted");
        if (updateResult <= 0) {
            log.error("接受邀请失败：更新状态失败");
            return false;
        }

        // 添加为家庭成员
        int addResult = familyMembersService.addMember(invitation.getFamilyId(), currentUserId, "member");
        if (addResult <= 0) {
            log.error("接受邀请失败：添加成员失败");
            return false;
        }

        log.info("邀请接受成功：邀请ID={}, 家庭ID={}", invitationId, invitation.getFamilyId());
        return true;
    }

    /**
     * 获取指定家庭的所有成员
     */
    @GetMapping("/members")
    public List<FamilyMembers> getFamilyMembers(@RequestParam(required = false) Integer userIdn,
                                                HttpServletRequest request) {
        // 权限校验：仅家庭成员可查看
        int currentUserId = resolveUserId(request, null, userIdn);
        if (currentUserId <= 0) {
            log.error("获取家庭成员失败：无效用户ID");
            return new ArrayList<>();
        }

        // 获取家庭ID
        int familyId = familyMembersService.getFamilyIdByUserId(currentUserId);
        // 校验当前用户是否属于该家庭
        FamilyMembers currentMember = familyMembersService.getMemberByFamilyAndUserId(familyId, currentUserId);
        if (currentMember == null) {
            log.error("获取家庭成员失败：用户ID={}不属于家庭ID={}", currentUserId, familyId);
            return new ArrayList<>();
        }

        // 查询所有成员
        List<FamilyMembers> members = familyMembersService.getAllMembersByFamilyId(familyId);
        List<FamilyMembers> memberList = members != null ? members : new ArrayList<>();

        log.info("家庭ID={}的成员数量：{}", familyId, memberList.size());
        return memberList;
    }

    /**
     * 获取家庭成员详情（包含用户信息）
     */
    @GetMapping("/member/detail")
    public MemberDetailVO getMemberDetail(@RequestParam int familyMemberId,
                                          @RequestParam(required = false) Integer userIdn,
                                          HttpServletRequest request) {
        // 权限校验
        int currentUserId = resolveUserId(request, null, userIdn);
        if (currentUserId <= 0) {
            log.error("获取成员详情失败：无效用户ID");
            return null;
        }

        // 查询家庭成员关联信息
        FamilyMembers member = familyMembersService.getMember(familyMemberId);
        if (member == null) {
            log.error("获取成员详情失败：家庭成员ID={}不存在", familyMemberId);
            return null;
        }

        // 校验当前用户是否同属一个家庭
        FamilyMembers currentMember = familyMembersService.getMemberByFamilyAndUserId(member.getFamilyId(), currentUserId);
        if (currentMember == null) {
            log.error("获取成员详情失败：无权查看家庭ID={}的成员", member.getFamilyId());
            return null;
        }

        // 查询用户基本信息
        UserInfo user = userInfoService.getUserById(member.getUserId());
        if (user == null) {
            log.error("获取成员详情失败：用户ID={}不存在", member.getUserId());
            return null;
        }

        // 封装返回结果
        MemberDetailVO detailVO = new MemberDetailVO();
        detailVO.setFamilyMemberId(member.getFamilyMemberId());
        detailVO.setFamilyId(member.getFamilyId());
        detailVO.setUserId(user.getUserId());
        detailVO.setUsername(user.getUsername());
        detailVO.setRole(member.getRole());
        detailVO.setJoinTime(member.getJoinedAt()); // 假设FamilyMembers有createdAt字段

        log.info("获取成员详情成功：ID={}", familyMemberId);
        return detailVO;
    }

    /**
     * 解析用户ID（优先级：userIdn > Session > 方法参数）
     */
    private int resolveUserId(HttpServletRequest request, Integer methodParam, Integer userIdn) {
        // 1. 优先使用userIdn参数
        if (userIdn != null && userIdn > 0) {
            log.debug("使用userIdn获取用户ID：{}", userIdn);
            return userIdn;
        }

        // 2. 从Session获取
        UserInfo loginUser = (UserInfo) request.getSession().getAttribute("session_user_key");
        if (loginUser != null && loginUser.getUserId() > 0) {
            log.debug("使用Session获取用户ID：{}", loginUser.getUserId());
            return loginUser.getUserId();
        }

        // 3. 使用方法参数
        if (methodParam != null && methodParam > 0) {
            log.debug("使用方法参数获取用户ID：{}", methodParam);
            return methodParam;
        }

        // 无效ID
        return -1;
    }

    /**
     * 成员详情VO（整合家庭成员关联信息和用户基本信息）
     */
    public static class MemberDetailVO {
        private int familyMemberId;
        private int familyId;
        private int userId;
        private String username;
        private String role;
        private Date joinTime;

        // Getter和Setter
        public int getFamilyMemberId() { return familyMemberId; }
        public void setFamilyMemberId(int familyMemberId) { this.familyMemberId = familyMemberId; }
        public int getFamilyId() { return familyId; }
        public void setFamilyId(int familyId) { this.familyId = familyId; }
        public int getUserId() { return userId; }
        public void setUserId(int userId) { this.userId = userId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public Date getJoinTime() { return joinTime; }
        public void setJoinTime(Date joinTime) { this.joinTime = joinTime; }
    }
}