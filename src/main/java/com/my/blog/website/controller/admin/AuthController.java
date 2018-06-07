package com.my.blog.website.controller.admin;


import com.my.blog.website.constant.WebConst;
import com.my.blog.website.controller.BaseController;
import com.my.blog.website.enums.LogActions;
import com.my.blog.website.exception.TipException;
import com.my.blog.website.model.Bo.RestResponseBo;
import com.my.blog.website.model.Vo.UserVo;
import com.my.blog.website.service.ILogService;
import com.my.blog.website.service.IUserService;
import com.my.blog.website.untils.TaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 用户后台登陆/登出
 *
 * @param
 * @author rfYang
 * @date 2018/6/6 15:45
 * @return
 */
@Controller
@RequestMapping(value = "/admin")
@Transactional(rollbackFor = TipException.class)
public class AuthController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Resource
    private IUserService iUserService;

    @Autowired
    private ILogService iLogService;

    @GetMapping(value = "/login")
    public String login() {
        return "admin/login";
    }

    /**
     * 登陆
     *
     * @param [username, password, remeber_me, request, response]
     * @return com.my.blog.website.model.Bo.RestResponseBo
     * @author rfYang
     * @date 2018/6/7 15:49
     */
    @PostMapping(value = "login")
    @ResponseBody
    public RestResponseBo doLogin(@RequestParam(value = "username") String username,
                                  @RequestParam String password,
                                  @RequestParam(required = false) String remeber_me,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        Integer error_count = cache.get("login_error_count");
        try {
            UserVo user = iUserService.login(username, password);
            request.getSession().setAttribute(WebConst.LOGIN_SESSION_KEY, user);//自定义session
            if (StringUtils.isNotBlank(remeber_me)) {
                TaleUtils.setCookie(response, user.getUid());
            }
            iLogService.insertLog(LogActions.LOGIN.getAction(), null, request.getRemoteAddr(), user.getUid());
        } catch (Exception e) {
            error_count = null == error_count ? 1 : error_count + 1;
            if (error_count > 3) {
                return RestResponseBo.fail("您已经输错了三次密码，10min后再来重试");
            }
            cache.set("login_error_count", error_count + 1, 60 * 10);//过期时间10s
            String msg = "登陆失败";
            if (e instanceof TipException) {
                msg = e.getMessage();
            } else {
                logger.error(msg, e);
            }
            return RestResponseBo.fail(msg);
        }

        return RestResponseBo.ok();
    }

    /**
     * 登出
     *
     * @param [session, response, request]
     * @return void
     * @author rfYang
     * @date 2018/6/7 15:43
     */
    @RequestMapping(value = "/logout")
    public void logout(HttpSession session, HttpServletResponse response, HttpServletRequest request) {
        session.removeAttribute("WebConst.LOGIN_SESSION_KEY");
        Cookie cookie = new Cookie(WebConst.USER_IN_COOKIE, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setValue(null);
        response.addCookie(cookie);
        try {
            response.sendRedirect("/admin/login");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("注销失败", e);
        }

    }

}
