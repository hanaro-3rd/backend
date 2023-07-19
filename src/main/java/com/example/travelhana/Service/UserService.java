package com.example.travelhana.Service;

import com.example.travelhana.Domain.User;
import com.example.travelhana.Dto.DeviceDto;
import com.example.travelhana.Dto.SignupRequestDto;
import com.example.travelhana.Dto.UserResponseDto;
import com.example.travelhana.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final SaltUtil saltUtil;


    public DeviceDto isExistDevice(String deviceId)
    {
        User user=userRepository.findUserByDeviceId(deviceId);

        Boolean isRegistrate;
        String name;
        if(user==null) {
            isRegistrate=false;
            name="none";
        }
        else {
            isRegistrate=true;
            name=user.getName();
        }
        return new DeviceDto(isRegistrate,name);

    }

    public List<UserResponseDto> userExist()
    {
        List<UserResponseDto> dtos=new ArrayList<>();
        List<User> user= userRepository.findAll();
        for(int i=0;i<user.size();i++)
        {
            UserResponseDto dto=new UserResponseDto(user.get(i));
            dtos.add(dto);


        }
        return dtos;
    }

    public void signup(SignupRequestDto dto)
    {
        if(isValidUser(dto))
        {
            String salt=saltUtil.generateSalt();
            User user=new User().builder()
                    .password(saltUtil.encodePassword(salt,dto.getPassword()))
                    .pattern(saltUtil.encodePassword(salt,dto.getPattern()))
                    .phoneNum(dto.getPhonenum())
                    .deviceId(dto.getDeviceId())
                    .salt(salt)
                    .isWithdrawl(false) //탈퇴했는지
                    .name(dto.getName())
                    .build();
            userRepository.save(user);
        }



    }

    public Boolean isValidUser(SignupRequestDto dto)
    {
            if(dto.getPassword().length()!=6)
            {
                System.out.println(dto.getPassword().length());
                throw new IllegalArgumentException("비밀번호는 6자리의 숫자로 구성해주세요.");
            }
            if(!dto.getPassword().matches("\\d+"))
            {
                throw new IllegalArgumentException("숫자로만 구성해주세요");
            }
            if(dto.getName().length()>15)
            {
                throw new IllegalArgumentException("이름은 15글자 이내로 입력해주세요.");
            }

        return true;
    }

}
