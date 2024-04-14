package com.byebug.automation.api.param;


import lombok.Data;
import java.util.HashMap;
import java.util.Map;

/**
 * 上传文件的请求param
 *      - filePath: 上传的文件相对于工程的路径，如upload/**.csv
 *      - fileKey: 上传的文件使用的key，如file或files登
 *      - map: 其他的 key-value 参数列表
 */
@Data
public class UploadFileParam extends BaseParam {

    private String filePath;
    private String fileKey = "file";
    private Map<String, String> map =new HashMap<>();

}