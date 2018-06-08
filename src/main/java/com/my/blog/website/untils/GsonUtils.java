package com.my.blog.website.untils;

import com.google.gson.Gson;

/**
 * json转换工具
 * @author rfYang
 * @date 2018/6/8 10:15
 * @param
 * @return
 */
public class GsonUtils {

    private static final Gson gson = new Gson();

    public static String toJsonString(Object object){
      return object==null?null:gson.toJson(object);
    }
}
