package com.fastcampus.boardserver.service.impl;

import com.fastcampus.boardserver.dto.UserDTO;
import com.fastcampus.boardserver.exception.DuplicateIdException;
import com.fastcampus.boardserver.mapper.UserProfileMapper;
import com.fastcampus.boardserver.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.fastcampus.boardserver.utils.SHA256Util.encryptSHA256;

@Service
@Log4j2
public class UserServiceImpl implements UserService {
    @Autowired
    private UserProfileMapper userProfileMapper;

    public UserServiceImpl(UserProfileMapper userProfileMapper) {
        this.userProfileMapper = userProfileMapper;

    }

    @Override
    public void register(UserDTO userProfile) {
        boolean duplicateIdResult = isDuplicatedId(userProfile.getUserId());

        if(duplicateIdResult){
            throw new DuplicateIdException("중복된 아이디입니다.");
        }
        userProfile.setCreateTime(new Date());
        userProfile.setPassword(encryptSHA256(userProfile.getPassword()));
        int insertCount = userProfileMapper.register(userProfile);

        if(insertCount != 1){
            log.error("insertMember{}",userProfile);
            throw new RuntimeException("insertUser ERROR" + userProfile);
        }
    }

    @Override
    public UserDTO login(String id, String password) {
        String cryptPassword = encryptSHA256(password);
        UserDTO userDTO = userProfileMapper.findByIdAndPassword(id, cryptPassword);
        return userDTO;
    }

    @Override
    public boolean isDuplicatedId(String id) {
        return userProfileMapper.idCheck(id) == 1;
    }

    @Override
    public UserDTO getUserInfo(String userId) {
        return null;
    }

    @Override
    public void updatePassword(String id, String beforePassword, String afterPassword) {
        String cryptPassword = encryptSHA256(beforePassword);
        UserDTO memberInfo = userProfileMapper.findByIdAndPassword(id, cryptPassword);

        if(memberInfo != null){
            memberInfo.setPassword(encryptSHA256(afterPassword));
            int insertCount = userProfileMapper.updatePassword(memberInfo);
        }else{
            log.error("updatePassword ERROR{}",memberInfo);
            throw new RuntimeException("비밀번호가 틀렸습니다.");
        }
    }

    @Override
    public void deleteId(String id, String password) {
        String cryptPassword = encryptSHA256(password);
        UserDTO memberInfo = userProfileMapper.findByIdAndPassword(id, cryptPassword);
        if(memberInfo != null){
            int deleteCount = userProfileMapper.deleteUserProfile(id);
        }else{
            throw new RuntimeException("비밀번호가 일치하지 않습니다."+id);
        }
    }
}
