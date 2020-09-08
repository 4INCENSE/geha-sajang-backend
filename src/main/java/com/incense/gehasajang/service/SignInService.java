package com.incense.gehasajang.service;

import com.incense.gehasajang.domain.host.Host;
import com.incense.gehasajang.domain.host.HostRepository;
import com.incense.gehasajang.error.ErrorCode;
import com.incense.gehasajang.exception.*;
import com.incense.gehasajang.model.dto.signin.SignInResponseDto;
import com.incense.gehasajang.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SignInService {

    private final HostRepository hostRepository;

    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 입력받은 값과 디비 값 조회 후 매칭
     * 그리고 Token을 생성하고 등록 상태를 체크한다.
     */
    public SignInResponseDto authenticate(String account, String password) {
        Host host = hostRepository.findByAccount(account)
                .orElseThrow(() -> new NotFoundDataException(ErrorCode.HOST_NOT_FOUND));

        if (!host.checkPassword(password, passwordEncoder)) {
            throw new FailToAuthenticationException(ErrorCode.FAIL_TO_SIGN_IN);
        }

        checkHostState(host);

        return SignInResponseDto.builder()
                .accessToken(jwtUtil.createToken(host.getAccount(), host.getType()))
                .registerState(checkRegisterState(host))
                .nickname(host.getNickname())
                .profileImage(host.getProfileImage())
                .build();
    }

    private void checkHostState(Host host) {
        checkIsDeleted(host);
        checkIsActive(host);
        checkIsPassMailAuth(host);
    }

    //TODO: 2020-08-30 outer join으로 변경하면 IO를 한번으로 줄일 수 있을듯 -lynn
    //TODO: 2020-09-09 도메인에서 해결할 순 없을까? -lynn
    private String checkRegisterState(Host host) {
        if (host.isSubHost()) {
            return "staff";
        }

        if (hostRepository.findRoomByHostId(host.getId()) != null) {
            return "registered";
        }

        if (hostRepository.findHouseByHostId(host.getId()) != null) {
            return "inProgress";
        }

        return "unregistered";
    }

    /**
     * 계정 활성화 여부 확인
     */
    private void checkIsActive(Host host) {
        if (!host.isActive()) {
            throw new DisabledHostException(ErrorCode.DISABLED);
        }
    }

    /**
     * 계정 삭제 여부 확인
     */
    private void checkIsDeleted(Host host) {
        if (host.isDeleted()) {
            throw new DeletedHostException(ErrorCode.DELETED);
        }
    }

    /**
     * 호스트일 경우 이메일 인증 확인
     * 스태프는 확인하지 않는다.
     */
    private void checkIsPassMailAuth(Host host) {
        if (!host.isSubHost()) {
            boolean isPassEmailAuth = hostRepository.findEmailAuthById(host.getId());
            if (!isPassEmailAuth) {
                throw new UnAuthMailException(ErrorCode.UNAUTH_MAIL);
            }
        }
    }

}
