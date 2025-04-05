package com.fastcampus.boardserver.mapper;

import com.fastcampus.boardserver.dto.UserDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserProfileMapper {

    public UserDTO getUserProfile(@Param("id") String id);

    int insertUserProfile(@Param("id") String id,
                          @Param("password") String password,
                          @Param("nickname") String nickname,
                          @Param("createTime") String createTime,
                          @Param("updateTime") String updateTime);

    int deleteUserProfile(@Param("id") String id);

    public UserDTO FindByIdAndPassword(@Param("id") String id, @Param("password") String password);

    int idCheck(@Param("id") String id);

    public int updatePassword(UserDTO user);


}
