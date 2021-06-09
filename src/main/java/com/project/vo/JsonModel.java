package com.project.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @Auther: zhangjuntao
 * @Date: 2021/5/5 - 05 - 05 - 10:03
 * @Description :com.yc.vo
 * @Version: 1.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL) //在生成json字符串中排除空属性
public class JsonModel {
    private Integer code;
    private String msg;
    private Object obj;
    private String url;
}
