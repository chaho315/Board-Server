package com.fastcampus.boardserver.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class UserDTO {

    public static boolean hashNullDateBeforeRegister(UserDTO userDTO){
        return userDTO.getUserId() == null || userDTO.getPassword() == null || userDTO.getNickname() == null;
    }

    public enum Status {
        DEFAULT, ADMIN, DELETED
    }

    private  String id;
    private String userId ;
    private String password;
    private String nickname;
    private boolean isAdmin;
    private Date createTime;
    private boolean isWithDraw;
    private Status status;
    private Date updateTime;

}
