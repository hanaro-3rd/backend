package com.example.travelhana.Dto;

import com.example.travelhana.Domain.User;
import com.example.travelhana.Domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Delegate;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class UserDto {
    //로그인할 떄 입력받을 값
    private String deviceId;
    private String password;
    private final UserRole role = UserRole.USER;

}
