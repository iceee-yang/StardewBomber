package com.stardewbombers.shared.util;

//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;

//public class JsonUtil {
//    private static final Gson gson = new GsonBuilder()
//            .setPrettyPrinting()
//            .create();
//
//    public static String toJson(Object obj) {
//        return gson.toJson(obj);
//    }
//
//    public static <T> T fromJson(String json, Class<T> clazz) {
//        try {
//            return gson.fromJson(json, clazz);
//        } catch (Exception e) {
//            System.err.println("JSON解析失败: " + e.getMessage());
//            return null;
//        }
//    }
//}

public class JsonUtil {
    public static String toJson(Object obj) {
        return obj.toString(); // 临时方案
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        System.out.println("收到: " + json);
        return null; // 临时返回null
    }
}
