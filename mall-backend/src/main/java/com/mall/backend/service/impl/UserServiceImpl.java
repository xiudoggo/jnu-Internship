package com.mall.backend.service.impl;

import com.mall.backend.entity.MallUser;
import com.mall.backend.mapper.MallUserMapper;
import com.mall.backend.security.JwtUtil;
import com.mall.backend.security.PasswordUtil;
import com.mall.backend.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private final MallUserMapper mapper;

    public UserServiceImpl(MallUserMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Map<String, Object> login(String phone, String password, HttpServletResponse response) {
        MallUser u = mapper.findByPhone(phone);
        if (u != null && PasswordUtil.matches(password, u.getPassword())) {
            Integer role = u.getRole() != null ? u.getRole() : 0;
            String token = JwtUtil.generate(u.getId(), u.getPhone(), role);

            ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Lax")
                .build();
            response.addHeader("Set-Cookie", cookie.toString());

            Map<String, Object> userInfo = new LinkedHashMap<>();
            userInfo.put("id", u.getId());
            userInfo.put("nickname", u.getNickname());
            userInfo.put("phone", u.getPhone());
            userInfo.put("avatar", u.getAvatar());
            userInfo.put("role", role);

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("userInfo", userInfo);
            return data;
        }
        return null;
    }

    @Override
    public void logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("token", "")
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(0)
            .sameSite("Lax")
            .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    @Override
    public void register(String phone, String nickname, String password) {
        MallUser user = MallUser.builder()
            .phone(phone)
            .nickname(nickname)
            .password(PasswordUtil.encode(password))
            .avatar("https://picsum.photos/seed/avatar" + System.currentTimeMillis() + "/100/100")
            .build();
        mapper.insert(user);
    }

    @Override
    public boolean verifyPassword(Long userId, String oldPassword) {
        MallUser u = mapper.selectById(userId);
        if (u == null) return false;
        return PasswordUtil.matches(oldPassword, u.getPassword());
    }

    @Override
    public boolean updatePassword(Long userId, String oldPassword, String newPassword) {
        MallUser u = mapper.selectById(userId);
        if (u == null) return false;
        if (!PasswordUtil.matches(oldPassword, u.getPassword())) return false;
        u.setPassword(PasswordUtil.encode(newPassword));
        mapper.updateById(u);
        return true;
    }

    @Override
    public Map<String, Object> info(Long userId) {
        MallUser u = mapper.selectById(userId);
        if (u == null) return null;
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", u.getId());
        data.put("nickname", u.getNickname());
        data.put("phone", u.getPhone());
        data.put("avatar", u.getAvatar());
        data.put("email", u.getEmail());
        data.put("role", u.getRole() != null ? u.getRole() : 0);
        return data;
    }
}
