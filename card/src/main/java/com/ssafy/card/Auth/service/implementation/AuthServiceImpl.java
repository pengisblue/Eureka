package com.ssafy.card.Auth.service.implementation;

import com.ssafy.card.Auth.dto.RefreshToken;
import com.ssafy.card.Auth.dto.request.MyDataRequestDto;
import com.ssafy.card.Auth.dto.request.PayRequestDto;
import com.ssafy.card.Auth.dto.response.JwtTokenResponseDto;
import com.ssafy.card.Auth.dto.response.PayTokenResponse;
import com.ssafy.card.Auth.service.AuthService;
import com.ssafy.card.JWT.JwtUtil;
import com.ssafy.card.User.entity.UserCardEntity;
import com.ssafy.card.User.entity.UserEntity;
import com.ssafy.card.User.repository.UserCardRepository;
import com.ssafy.card.User.repository.UserRepository;
import com.ssafy.card.common.CustomException;
import com.ssafy.card.common.ResponseCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserCardRepository userCardRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public JwtTokenResponseDto reIssueToken(HttpServletRequest dto) {
        JwtTokenResponseDto jwtTokenResponseDto = jwtUtil.reIssueToken(dto);

        // username값을 phoneNumber로 대체
        String username = jwtUtil.getUsername(jwtTokenResponseDto.getRefreshToken());
        System.out.println(username);


        return new JwtTokenResponseDto("Bearer ", jwtTokenResponseDto.getAccessToken(), jwtTokenResponseDto.getRefreshToken());

    }

    @Override
    public void checkUser(MyDataRequestDto myDataRequestDto) {
        UserEntity user = userRepository.findByPhoneNumber(myDataRequestDto.getPhoneNumber())
            .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));
    }

    @Override
    public JwtTokenResponseDto issueMyDataToken(MyDataRequestDto myDataRequestDto) {

        UserEntity userEntity = userRepository.findByPhoneNumber(myDataRequestDto.getPhoneNumber())
                .orElseThrow(() -> new CustomException(ResponseCode.INVALID_USER_NAME));

        String access = jwtUtil.createJwt("access", userEntity.getPhoneNumber(), null, 2400000L);
        String refresh = jwtUtil.createJwt("refresh", userEntity.getPhoneNumber(), null,86400000L);


        return new JwtTokenResponseDto("Bearer ", access, refresh);

    }

    @Override
    public PayTokenResponse issuePayToken(PayRequestDto dto) {
        String cardNumber = dto.getCardNumber();
        String cvc = dto.getCvc();
        String yy = dto.getExpired_year();
        String mm = dto.getExpired_month();
        String password = dto.getPassword();

        Optional<UserCardEntity> userCardEntity = userCardRepository.findByCardNumber(cardNumber);
        log.debug("userCardEntity : "+ userCardEntity);

        if(userCardEntity.isEmpty()) throw  new CustomException(ResponseCode.NOT_FOUND_CARD);
        if(!userCardEntity.get().getCardCvc().equals(cvc)) throw new CustomException(ResponseCode.NOT_FOUND_CARD);
        if(!userCardEntity.get().getExpired_year().equals(yy)) throw new CustomException(ResponseCode.NOT_FOUND_CARD);
        if(!userCardEntity.get().getExpired_month().equals(mm)) throw new CustomException(ResponseCode.NOT_FOUND_CARD);

        String twoPass = userCardEntity.get().getCardPassword().substring(0, 2);
        if(!twoPass.equals(password)) throw new CustomException(ResponseCode.NOT_FOUND_CARD);

        String access = jwtUtil.createJwt("access", cardNumber, null, 31536000L);

        userCardEntity.get().setToken(access);
        userCardRepository.save(userCardEntity.get());

        return new PayTokenResponse(userCardEntity.get().getCardId(), userCardEntity.get().getCardIdentifier(), "Bearer", access, null);    }


}
