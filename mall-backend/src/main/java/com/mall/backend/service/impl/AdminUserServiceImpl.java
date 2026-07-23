package com.mall.backend.service.impl;

import com.mall.backend.entity.MallUser;
import com.mall.backend.mapper.MallUserMapper;
import com.mall.backend.security.PasswordUtil;
import com.mall.backend.service.AdminUserService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    private final MallUserMapper mapper;

    public AdminUserServiceImpl(MallUserMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<Map<String, Object>> list() {
        List<MallUser> users = mapper.selectAllUsers();
        return users.stream()
            .map(this::toMap)
            .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> detail(Long id) {
        MallUser u = mapper.selectById(id);
        if (u == null) return null;
        return toMap(u);
    }

    @Override
    public void update(Long id, Map<String, Object> body) {
        MallUser u = mapper.selectById(id);
        if (u == null) return;
        if (body.containsKey("nickname")) u.setNickname((String) body.get("nickname"));
        if (body.containsKey("phone")) u.setPhone((String) body.get("phone"));
        if (body.containsKey("role")) u.setRole(((Number) body.get("role")).intValue());
        if (body.containsKey("email")) u.setEmail((String) body.get("email"));
        mapper.updateById(u);
    }

    @Override
    public Map<String, Object> create(Map<String, Object> body) {
        String phone = (String) body.get("phone");
        String nickname = (String) body.get("nickname");
        String password = (String) body.get("password");
        if (phone == null || nickname == null || password == null)
            return null;
        if (mapper.findByPhone(phone) != null)
            return null;
        MallUser user = MallUser.builder()
            .phone(phone)
            .nickname(nickname)
            .password(PasswordUtil.encode(password))
            .avatar("https://picsum.photos/seed/avatar" + System.currentTimeMillis() + "/100/100")
            .role(body.containsKey("role") ? ((Number) body.get("role")).intValue() : 0)
            .email((String) body.getOrDefault("email", ""))
            .build();
        mapper.insert(user);
        return toMap(user);
    }

    @Override
    public void updatePassword(Long id, String password) {
        MallUser u = mapper.selectById(id);
        if (u == null) return;
        u.setPassword(PasswordUtil.encode(password));
        mapper.updateById(u);
    }

    @Override
    public void delete(Long id) {
        mapper.deleteById(id);
    }

    private Map<String, Object> toMap(MallUser u) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", u.getId());
        m.put("nickname", u.getNickname());
        m.put("phone", u.getPhone());
        m.put("avatar", u.getAvatar());
        m.put("email", u.getEmail());
        m.put("role", u.getRole() != null ? u.getRole() : 0);
        return m;
    }
}
