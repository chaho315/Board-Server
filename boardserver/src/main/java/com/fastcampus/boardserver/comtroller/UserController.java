package com.fastcampus.boardserver.comtroller;

import com.fastcampus.boardserver.dto.UserDTO;
import com.fastcampus.boardserver.dto.request.UserDeleteId;
import com.fastcampus.boardserver.dto.request.UserLoginRequest;
import com.fastcampus.boardserver.dto.request.UserUpdatePasswordRequest;
import com.fastcampus.boardserver.dto.response.LoginResponse;
import com.fastcampus.boardserver.dto.response.UserInfoResponse;
import com.fastcampus.boardserver.service.UserService;
import com.fastcampus.boardserver.service.impl.UserServiceImpl;
import com.fastcampus.boardserver.utils.SessionUtil;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Log4j2
public class UserController {

    private final UserService userService;


    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("sing-up")
    @ResponseStatus(HttpStatus.CREATED)
    public void signUp(@RequestBody UserDTO userDTO){
        if(UserDTO.hashNullDateBeforeRegister(userDTO)){
            throw new RuntimeException("회원가입 정보를 확인해주세요");
        }
        userService.register(userDTO);
    }

    @PostMapping("sign-in")
    public HttpStatus login(@RequestBody UserLoginRequest loginRequest,
                            HttpSession session) {
        ResponseEntity<LoginResponse> responseEntity = null;
        String userId = loginRequest.getUserId();
        String password = loginRequest.getPassword();
        UserDTO userInfo = userService.login(userId, password);
        String id = userInfo.getId();

        if (userInfo == null) {
            return HttpStatus.NOT_FOUND;
        } else if (userInfo != null) {
            LoginResponse loginResponse = LoginResponse.success(userInfo);
            if (userInfo.getStatus() == (UserDTO.Status.ADMIN))
                SessionUtil.setLoginAdminId(session, id);
            else
                SessionUtil.setLoginMemberId(session, id);

            responseEntity = new ResponseEntity<LoginResponse>(loginResponse, HttpStatus.OK);
        } else {
            throw new RuntimeException("Login Error! 유저 정보가 없거나 지워진 유저 정보입니다.");
        }

        return HttpStatus.OK;
    }

    @GetMapping("my-info")
    public UserInfoResponse memberInfo(HttpSession session) {
        String id = SessionUtil.getLoginMemberId(session);
        if (id == null) id = SessionUtil.getLoginAdminId(session);
        UserDTO memberInfo = userService.getUserInfo(id);
        return new UserInfoResponse(memberInfo);
    }

    @PutMapping("logout")
    public void logout(HttpSession session) {
        SessionUtil.clear(session);
    }

    @PatchMapping("password")
    //@LoginCheck(type = LoginCheck.UserType.USER
    public ResponseEntity<LoginResponse> updateUserPassword(String accountId, @RequestBody UserUpdatePasswordRequest userUpdatePasswordRequest,
                                                            HttpSession session) {
        ResponseEntity<LoginResponse> responseEntity = null;

        String Id = accountId;
        String beforePassword = userUpdatePasswordRequest.getBeforePassword();
        String afterPassword = userUpdatePasswordRequest.getAfterPassword();

        try {
            userService.updatePassword(Id, beforePassword, afterPassword);
            UserDTO userInfo = userService.login(Id, afterPassword);
            LoginResponse loginResponse = LoginResponse.success(userInfo);
            ResponseEntity.ok(new ResponseEntity<LoginResponse>(loginResponse, HttpStatus.OK));
        } catch (IllegalArgumentException e) {
            log.error("updatePassword 실패", e);
            responseEntity = new ResponseEntity<LoginResponse>(HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }

    @DeleteMapping
    public ResponseEntity<LoginResponse> deleteId(@RequestBody UserDeleteId userDeleteId,
                                                  HttpSession session) {
        ResponseEntity<LoginResponse> responseEntity = null;
        String Id = SessionUtil.getLoginMemberId(session);

        try {
            UserDTO userInfo = userService.login(Id, userDeleteId.getPassword());
            userService.deleteId(Id, userDeleteId.getPassword());
            LoginResponse loginResponse = LoginResponse.success(userInfo);
            responseEntity = new ResponseEntity<LoginResponse>(loginResponse, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("deleteID 실패");
            responseEntity = new ResponseEntity<LoginResponse>(HttpStatus.BAD_REQUEST);;
        }
        return responseEntity;
    }
}
