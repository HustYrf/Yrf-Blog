package com.my.blog.website.controller;

import com.my.blog.website.model.Vo.UserVo;
import com.my.blog.website.untils.MapCache;
import com.my.blog.website.untils.TaleUtils;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;

@Controller
public abstract class BaseController {
    public static String THEME = "themes/default";
    protected MapCache cache = MapCache.single();

    /**
     * 主页的页面主题
     */

    public String render(String ViewName) {
        return THEME + "/" + ViewName;
    }


    public BaseController title(HttpServletRequest request, String title) {
        request.setAttribute("title", title);
        return this;
    }

    public BaseController keywords(HttpServletRequest request, String keywords) {
        request.setAttribute("keywords", keywords);
        return this;
    }

    /**
     * 获取请求绑定的登录对象
     * @param request
     * @return
     */
    public UserVo user(HttpServletRequest request) {
        return TaleUtils.getLoginUser(request);
    }

    public Integer getUid(HttpServletRequest request){
        return this.user(request).getUid();
    }

    public String render_404() {
        return "comm/error_404";
    }
}
