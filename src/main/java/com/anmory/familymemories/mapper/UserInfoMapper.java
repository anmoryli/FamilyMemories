package com.anmory.familymemories.mapper;

import com.anmory.familymemories.model.UserInfo;
import org.apache.ibatis.annotations.*;

/**
 * @author Anmory
 * @description TODO
 * @date 2025-07-18 上午12:28
 */

@Mapper
public interface UserInfoMapper {
    @Insert("INSERT INTO user_info (username, password, email, created_at, updated_at) " +
            "VALUES (#{username}, #{password}, #{email}, NOW(), NOW())")
    int insert(String username,
               String password,
               String email);

    @Delete("DELETE FROM user_info WHERE user_id = #{userId}")
    int deleteById(int userId);

    @Update("UPDATE user_info SET " +
            "username = #{username}, " +
            "password = #{password}, " +
            "email = #{email}, " +
            "updated_at = NOW() " +
            "WHERE user_id = #{userId}")
    int update(@Param("userId") int userId,
               @Param("username") String username,
               String password,
               String email);

    @Select("SELECT * FROM user_info WHERE user_id = #{userId}")
    UserInfo selectById(int userId);

    @Select("SELECT * FROM user_info WHERE username = #{username}")
    UserInfo getUserByUsername(String username);

    @Select("SELECT * FROM user_info WHERE user_id = #{userId}")
    UserInfo getUserById(int userId);

    @Select("SELECT family_id FROM family.family_members WHERE user_id = #{userId}")
    int getFamilyIdByUserId(int userId);
}
