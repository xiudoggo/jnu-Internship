package com.mall.backend.config;

import com.mall.backend.entity.MallUser;
import com.mall.backend.mapper.MallUserMapper;
import com.mall.backend.security.PasswordUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 启动时自动将明文密码迁移为 BCrypt 密文（仅执行一次）
 */
@Component
public class PasswordMigrationRunner implements CommandLineRunner {

    private final MallUserMapper userMapper;

    public PasswordMigrationRunner(MallUserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public void run(String... args) {
        List<MallUser> users = userMapper.selectAllUsers();
        int migrated = 0;
        for (MallUser u : users) {
            String pwd = u.getPassword();
            // BCrypt 密文以 $2a$ 开头，明文不含此特征
            if (pwd != null && !pwd.startsWith("$2a$")) {
                u.setPassword(PasswordUtil.encode(pwd));
                userMapper.updateById(u);
                migrated++;
            }
        }
        if (migrated > 0) {
            System.out.println("🔐 密码迁移完成: " + migrated + " 个用户已从明文转为 BCrypt 密文");
        }
    }
}
