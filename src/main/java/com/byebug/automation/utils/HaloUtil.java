package com.byebug.automation.utils;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import com.byebug.automation.api.param.BaseParam;

import java.lang.reflect.Field;
import java.util.*;

public class HaloUtil {

    /**
     * 共用sleep方法
     * @param second 传入sleep的秒数
     */
    public static void sleep(int second) {
        try {
            Thread.sleep(second * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过反射获取对象的key-value对应的map
     * @param cls
     * @param baseParam
     * @return
     */
    public static Map<String, String> getObjectMapByReflex(Class cls, BaseParam baseParam) {
        Map<String, String> maps = new HashMap<>();

        List<Field> fls = new ArrayList<Field>();
        Class tmpClass = cls;
        while(tmpClass != null && !tmpClass.getName().toLowerCase().equals("java.lang.object")) {
            fls.addAll(Arrays.asList(tmpClass.getDeclaredFields()));
            tmpClass = tmpClass.getSuperclass();
        }
        for (Field fl : fls) {
            String attributeName = fl.getName();
            try {
                // 获取原来地访问控制权限
                boolean accessFlag = fl.isAccessible();
                // 修改访问控制权限
                fl.setAccessible(true);
                // 获取在对象f中属性fields[i]对应的对象中的变量
                Object value = fl.get(baseParam);
                // 恢复访问控制权限
                fl.setAccessible(accessFlag);
                if(value != null) {
                    maps.put(attributeName, value.toString());
                }
            } catch (Exception e) {
                ReportUtil.log("通过类" + cls.getCanonicalName() + "的反射获取Key和Value异常：" + e);
            }
        }

        return maps;
    }

    public static <T> T extractObjectByJSONPath(String sourceJson, String jsonPath, Class<T> cls) {
        jsonPath = jsonPath.trim();
        if (jsonPath.startsWith("$.")) {
            Object findObject = JSONPath.read(sourceJson, jsonPath);
            if(findObject != null) {
                return JSON.parseObject(findObject.toString(), cls);
            }
        }
        return null;
    }

    public static String extractByJSONPath(String sourceJson, String jsonPath) {
        jsonPath = jsonPath.trim();
        if (!jsonPath.startsWith("$.")) {
            return jsonPath;
        }
        return extractObjectByJSONPath(sourceJson, jsonPath, String.class);
    }

    /**
     * 从字符串的尾部替换掉指定长度的字符串
     * @param originString 被替换的字符串
     * @param replaceLastSize 被替换掉的字符长度
     * @return
     */
    public static String replaceLastCharacters(String originString, int replaceLastSize) {
        if(StrUtil.isEmpty(originString)) {
            return "";
        }

        if(originString.length() <= replaceLastSize) {
            return RandomUtil.randomString(originString.length());
        }

        return originString.substring(0, originString.length() - replaceLastSize) +
                RandomUtil.randomString(replaceLastSize);
    }

}
