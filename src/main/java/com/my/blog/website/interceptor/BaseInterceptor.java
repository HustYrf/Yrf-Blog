package com.my.blog.website.interceptor;

import com.my.blog.website.constant.WebConst;
import com.my.blog.website.enums.Types;
import com.my.blog.website.model.Vo.OptionVo;
import com.my.blog.website.model.Vo.UserVo;
import com.my.blog.website.service.IOptionService;
import com.my.blog.website.service.IUserService;
import com.my.blog.website.untils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义类拦截器
 *
 * @param
 * @author rfYang
 * @date 2018/6/7 20:06
 * @return
 */
@Component
public class BaseInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(BaseInterceptor.class);
    private static final String USER_AGENT = "user_agent";
    MapCache cache = MapCache.single();
    @Resource
    private Commons commons;
    @Resource
    private IUserService userService;
    @Autowired
    private AdminCommons adminCommons;
    @Autowired
    private IOptionService iOptionService;


    /**
     * 前置拦截
     *
     * @param [httpServletRequest, httpServletResponse, o]
     * @return boolean
     * @author rfYang
     * @date 2018/6/7 20:14
     */
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String contextPath = httpServletRequest.getContextPath();//返回当前页面所在的应用的名字
        String requestUri = httpServletRequest.getRequestURI();//资源标识符
        logger.info("UserAgent: {}", httpServletRequest.getHeader(USER_AGENT));
        logger.info("用户访问地址: {}, 来路地址: {}", requestUri, IPKit.getIpAddrByRequest(httpServletRequest));

        UserVo user = TaleUtils.getLoginUser(httpServletRequest);
        if (user == null) {
            Integer userId = TaleUtils.getCookieUid(httpServletRequest);
            if (userId != null) {
                user = userService.queryUserById(userId);
                httpServletRequest.getSession().setAttribute(WebConst.LOGIN_SESSION_KEY, user);
            }
        }
        //如果用户的请求是空
        if (requestUri.startsWith(contextPath + "/admin") && !requestUri.startsWith(contextPath + "/admin/login") && user == null) {
            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/admin/login");
            return false;
        }
        //设置get请求的token
        if (httpServletRequest.getMethod().equalsIgnoreCase("get")) {
            String csrf_token = UUID.UU64();
            cache.hset(Types.CSRF_TOKEN.getType(), csrf_token, requestUri, 30 * 60);//半个小时
            httpServletRequest.setAttribute("_csrf_token", csrf_token);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        OptionVo optionVo = iOptionService.getOptionByName("site_record");
        httpServletRequest.setAttribute("commons", commons);//一些工具类和公共方法
        httpServletRequest.setAttribute("option", optionVo);
        httpServletRequest.setAttribute("adminCommons", adminCommons);
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
