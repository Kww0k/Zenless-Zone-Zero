package com.backend.utils;

import com.backend.domain.LoginUser;
import com.backend.domain.entity.Account;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * @author Silvery
 * @since 2023/8/2 16:00
 */
@Component
public class SecurityUtil {

    /**
     * 获取用户
     **/
    public LoginUser getLoginUser() {
        if (getAuthentication().getPrincipal() != null)
            return (LoginUser) getAuthentication().getPrincipal();
        else
            return new LoginUser(new Account());
    }

    /**
     * 获取Authentication
     */
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 获取用户id
     */
    public Integer getUserId() {
        return getLoginUser().getAccount().getId();
    }

    /**
     * 获取用户名
     */
    public String getUsername() {
        return getLoginUser().getUsername();
    }
}