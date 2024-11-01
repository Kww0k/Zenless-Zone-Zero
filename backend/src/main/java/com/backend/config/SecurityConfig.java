package com.backend.config;

import com.backend.domain.LoginUser;
import com.backend.domain.RestBean;
import com.backend.domain.vo.AccountAuthVO;
import com.backend.domain.vo.AuthVO;
import com.backend.filter.JwtAuthFilter;
import com.backend.utils.BeanCopyUtils;
import com.backend.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;

import static com.backend.constants.HttpStatus.HTTP_STATUS_401;
import static com.backend.constants.OtherConstants.AUTH_TOKEN;
import static com.backend.enums.HttpMessage.LOGOUT_ERROR;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtils jwtUtils;

    private final JwtAuthFilter jwtAuthFilter;

    private final BeanCopyUtils beanCopyUtils;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // 设置那些请求放行，哪些需要认证
                .authorizeHttpRequests(conf -> conf
                        .requestMatchers("/login", "/account/test", "/file/download/*").permitAll()
                        .anyRequest().authenticated())
                // 表单登陆相关配置
                .formLogin(conf -> conf
                        .loginProcessingUrl("/login")
                        .successHandler(this::onAuthenticationSuccess)
                        .failureHandler(this::onAuthenticationFailure))
                // 退出登陆相关配置
                .logout(conf -> conf.logoutUrl("/logout")
                        .logoutSuccessHandler(this::onLogoutSuccess))
                // 异常处理
                .exceptionHandling(conf -> conf
                        .authenticationEntryPoint(this::commence))
                // 关闭csrf
                .csrf(AbstractHttpConfigurer::disable)
                .cors(conf -> conf.configurationSource(configurationSource()))
                // 把session设置为无状态
                .sessionManagement(conf -> conf
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 注册过滤器在认证器前
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * 异常处理
     *
     * @author Silvery
     * @since 2023/8/18 11:55
     * @param request 请求
     * @param response 响应
     * @param authException 发生的错误
     */
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        response.getWriter().write(RestBean.failure(HTTP_STATUS_401, authException.getMessage()).toJsonString());
    }

    /**
     * 登陆的逻辑
     *
     * @author Silvery
     * @since 2023/8/18 11:56
     * @param request 请求
     * @param response 响应
     * @param authentication 认证信息
     */
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        // 获取登陆的用户信息
        LoginUser user = (LoginUser) authentication.getPrincipal();

        // 生成jwt
        String token = jwtUtils.createJwt(user);
        AccountAuthVO accountAuthVO = beanCopyUtils.copyBean(user.getAccount(), AccountAuthVO.class);
        AuthVO authVO = new AuthVO(accountAuthVO, token);

        // 返回
        response.getWriter().write(RestBean.success(authVO).toJsonString());
    }

    /**
     * 登陆失败的处理
     *
     * @author Silvery
     * @since 2023/8/18 11:56
     * @param request 请求
     * @param response 响应
     * @param exception 发生的错误
     */
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(RestBean.failure(HTTP_STATUS_401, exception.getMessage()).toJsonString());
    }

    /**
     * 退出登陆做的操作
     *
     * @author Silvery
     * @since 2023/8/18 11:57
     * @param request 请求
     * @param response 响应
     * @param authentication 认证信息
     */
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        // 获取token
        String authorization = request.getHeader(AUTH_TOKEN);
        if (jwtUtils.invalidateJwt(authorization))
            response.getWriter().write(RestBean.success().toJsonString());
        else
            response.getWriter().write(RestBean.failure(LOGOUT_ERROR).toJsonString());
    }

    private CorsConfigurationSource configurationSource() {
        CorsConfiguration cors = new CorsConfiguration();
        cors.addAllowedOriginPattern("*");
        cors.setAllowCredentials(true);
        cors.addAllowedHeader("*");
        cors.addAllowedMethod("*");
        cors.addExposedHeader("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);
        return source;
    }
}