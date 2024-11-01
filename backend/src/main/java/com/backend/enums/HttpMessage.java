package com.backend.enums;

import lombok.Getter;

import static com.backend.constants.HttpStatus.*;

@Getter
public enum HttpMessage {

    SUCCESS(HTTP_STATUS_200, "操作成功"),
    USERNAME_PASSWORD_EXCEPTION(HTTP_STATUS_401, "用户名或密码错误"),
    GITLAB_AUTH_EXCEPTION(HTTP_STATUS_401, "gitlab认证错误"),
    HAVA_JOIN_EXCEPTION(HTTP_STATUS_400, "您已加入该团队"),
    LOGOUT_ERROR(HTTP_STATUS_401, "登出时出现异常"),
    EMAIL_HAS_BEEN_REGISTER(HTTP_STATUS_401, "邮箱已被注册"),
    CANT_CHANGE_USERNAME(HTTP_STATUS_401, "用户名已更改过，无法再次进行修改"),
    INSERT_STUDENT_ERROR(HTTP_STATUS_500, "新增学生信息时出现异常"),
    DELETE_STUDENT_ERROR(HTTP_STATUS_500, "删除学生信息时出现异常"),
    COULD_NOT_DO_QUEST_ERROR(HTTP_STATUS_500, "请先完成该任务的前置任务："),
    CODE_ERROR(HTTP_STATUS_500, "验证码错误"),
    REPO_EXCEPTION(HTTP_STATUS_500, "注意，您访问的仓库为空"),
    SYSTEM_ERROR(HTTP_STATUS_500, "未知错误");

    final Integer code;

    final String message;


    HttpMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }
}