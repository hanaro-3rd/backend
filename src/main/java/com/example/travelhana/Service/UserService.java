package com.example.travelhana.Service;

import com.example.travelhana.Domain.User;
import com.example.travelhana.Dto.DeviceDto;
import com.example.travelhana.Dto.SignupRequestDto;
import com.example.travelhana.Dto.UserResponseDto;
import com.example.travelhana.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;


    public DeviceDto isExistDevice(String deviceId)
    {
        User user=userRepository.findUserByDeviceId(deviceId);

        Boolean isRegistrate;
        String name;
        if(user==null)
        {
            isRegistrate=false;
            name="none";
        }
        else
        {
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

    }

}
