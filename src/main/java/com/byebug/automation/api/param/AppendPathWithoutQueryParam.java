package com.byebug.automation.api.param;

import lombok.Data;

/**
 *  当参数是直接拼在url-path后，且不带Query的表达式
 *  如获取ID为123的用户详情的GET接口：
 *      不带Query示例: user/123
 *      带Query示例：user?id=123
 */
@Data
public class AppendPathWithoutQueryParam extends BaseParam{
    private String appendPathWithoutQuery;
}
