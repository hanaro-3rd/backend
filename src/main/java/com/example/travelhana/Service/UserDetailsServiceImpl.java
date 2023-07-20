//package com.example.travelhana.Service;
//
//import com.example.travelhana.Domain.User;
//import com.example.travelhana.Repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.Collection;
//
//@Service
//@RequiredArgsConstructor
//public class UserDetailsServiceImpl implements UserDetailsService {
//
//    private final UserRepository userRepository;
//
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        User user = userRepository.findUserByDeviceId(username);
//        if (user == null) {
//            throw new UsernameNotFoundException("User not found");
//        }
//
//        return new org.springframework.security.core.userdetails.User(
//                user.getDeviceId(), user.getPassword(),
//                user.isEnabled(), true, true, !user.getIsEnabled(),
//                user.getRole().getAuthority()); // 여기서 getAuthority()를 사용합니다.
//    }
//}