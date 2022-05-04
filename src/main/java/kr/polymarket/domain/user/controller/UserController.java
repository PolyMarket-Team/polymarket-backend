package kr.polymarket.domain.user.controller;

import kr.polymarket.domain.user.dto.LoginRequestDto;
import kr.polymarket.domain.user.dto.SignUpDto;
import kr.polymarket.domain.user.service.UserAuthService;
import kr.polymarket.domain.user.service.UserService;
import kr.polymarket.global.result.ResultCode;
import kr.polymarket.global.result.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("users")
public class UserController {

    private final UserService userService;
    private final UserAuthService userAuthService;

    @PostMapping("/signup")
    public ResponseEntity<ResultResponse> signUp(@RequestBody SignUpDto signUpDto) {
        userService.createUser(signUpDto);

        ResultResponse result = ResultResponse.of(ResultCode.SIGNUP_SUCCESS, signUpDto);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @PostMapping("/signin")
    public ResponseEntity<ResultResponse> signin(@RequestBody LoginRequestDto loginRequestDto) {
        userAuthService.userSignIn(loginRequestDto);

        ResultResponse result = ResultResponse.of(ResultCode.SIGNIN_SUCCESS, loginRequestDto);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }


}
