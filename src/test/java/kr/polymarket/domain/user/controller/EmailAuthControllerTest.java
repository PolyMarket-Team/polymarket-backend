package kr.polymarket.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.polymarket.domain.user.dto.EmailAuthRequestDto;
import kr.polymarket.domain.user.dto.EmailAuthResultDto;
import kr.polymarket.domain.user.exception.UserAlreadySignUpException;
import kr.polymarket.domain.user.service.EmailAuthService;
import kr.polymarket.global.config.security.jwt.JwtTokenProvider;
import kr.polymarket.global.util.slack.SlackLoggingUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmailAuthController.class)
public class EmailAuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private EmailAuthService emailAuthService;

    @MockBean
    private SlackLoggingUtil slackLoggingUtil;

    @Test
    void sendEmailAuthCode_200() throws Exception {
        // given
        final String email = "test@email.com";
        final String authCode = "123456";
        final EmailAuthRequestDto emailAuthRequestDto = EmailAuthRequestDto.builder()
                .email(email)
                .build();

        given(emailAuthService.sendAuthCodeToEmail(any()))
                .willReturn(EmailAuthResultDto.builder()
                        .email(email)
                        .authCode(authCode)
                        .build());

        // when
        ResultActions resultActions = requestSendEmailAuthCode(emailAuthRequestDto);

        // then
        resultActions.andExpect(status().isOk());
    }

    @Test
    void sendEmailAuthCode_400() throws Exception {
        // given
        final String email = "invalid_email"; // invalid email format
        final EmailAuthRequestDto invalidEmailAuthReq = EmailAuthRequestDto.builder()
                .email(email)
                .build();
        final EmailAuthRequestDto emptyEmailAuthReq = EmailAuthRequestDto.builder()
                .build();

        // when
        ResultActions invalidEmailReqResultActions = requestSendEmailAuthCode(invalidEmailAuthReq);
        ResultActions emptyEmailAuthReqResultActions = requestSendEmailAuthCode(emptyEmailAuthReq);

        // then
        invalidEmailReqResultActions.andExpect(status().isBadRequest());
        emptyEmailAuthReqResultActions.andExpect(status().isBadRequest());
    }

    @Test
    void sendEmailAuthCode_409() throws Exception {
        // given
        final String email = "test@email.com";
        final EmailAuthRequestDto emailAuthRequestDto = EmailAuthRequestDto.builder()
                .email(email)
                .build();

        given(emailAuthService.sendAuthCodeToEmail(any()))
                .willThrow(new UserAlreadySignUpException());

        // when
        ResultActions resultActions = requestSendEmailAuthCode(emailAuthRequestDto);

        // then
        resultActions.andExpect(status().isConflict());
    }

    private ResultActions requestSendEmailAuthCode(EmailAuthRequestDto emailAuthRequestDto) throws Exception {
        return mvc.perform(post("/users/send-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailAuthRequestDto)))
                .andDo(print());
    }

}