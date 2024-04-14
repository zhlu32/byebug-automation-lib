package com.byebug.automation.sample.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

public class JSONSample {

    public static void main(String[] args) {
        Group group = new Group();
        group.setId(0L);
        group.setName("admin");

        User guestUser = new User();
        guestUser.setId(2L);
        guestUser.setName("guest");

        User rootUser = new User();
        rootUser.setId(3L);
        rootUser.setName("root");

        group.addUser(guestUser);
        group.addUser(rootUser);

        // 对象转String
        String jsonString = JSON.toJSONString(group);
        System.out.println("-------对象转String-------");
        System.out.println(jsonString);

        // String转对象
        Group parsedGroup = JSON.parseObject(jsonString, Group.class);
        System.out.println("\n-------String转对象-------");
        System.out.println(parsedGroup);
        System.out.println("Group Name = " + parsedGroup.getName());

        // JSONPath功能
        System.out.println("\n-------JSONPath功能-------");
        System.out.println("$.name=" + JSONPath.read(jsonString, "$.name"));
        System.out.println("$.users=" + JSONPath.read(jsonString, "$.users"));
        System.out.println("$.users.name=" + JSONPath.read(jsonString, "$.users.name"));
        System.out.println("$.users[id=2].name=" + JSONPath.read(jsonString, "$.users[id=2].name"));
        System.out.println("$.users[name='root'].name=" + JSONPath.read(jsonString, "$.users[name='root'].name"));

        // JSONPath的Object可以直接返回String或简单对象的List
        System.out.println("\n-------可以直接转成List简单数组（如List<Integer>或List<String>）-------");
        List<String> nameObjList = (List<String>)JSONPath.read(jsonString, "$.users.name");
        System.out.println(nameObjList);
        System.out.println(nameObjList.get(0));

        // 错误示范 -- 要将接口返回的 JSON 字符串转换为类的对象，通常需要使用JSON解析库
        System.out.println("\n-------错误示范-直接转成对象-------");
//        User userObj = (User)JSONPath.extract(jsonString, "$.users[0]");
//        System.out.println(userObj.getName());
        // 使用json解析的方法，可以转换成User对象
        User user =JSON.parseObject(JSONPath.read(jsonString, "$.users[0]").toString(), User.class);
        System.out.println(user.getName());
    }

    @Data
    public static class Group {
        private Long       id;
        private String     name;
        private List<User> users = new ArrayList<User>();

        public void addUser(User user) {
            users.add(user);
        }
    }

    @Data
    public static class User {
        private Long   id;
        private String name;
    }
}
