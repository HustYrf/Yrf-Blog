package com.my.blog.website.controller.admin;

import com.my.blog.website.constant.WebConst;
import com.my.blog.website.controller.BaseController;
import com.my.blog.website.enums.LogActions;
import com.my.blog.website.exception.TipException;
import com.my.blog.website.model.Bo.RestResponseBo;
import com.my.blog.website.model.Bo.StatisticsBo;
import com.my.blog.website.model.Vo.CommentVo;
import com.my.blog.website.model.Vo.ContentVo;
import com.my.blog.website.model.Vo.LogVo;
import com.my.blog.website.model.Vo.UserVo;
import com.my.blog.website.service.ILogService;
import com.my.blog.website.service.ISiteService;
import com.my.blog.website.service.IUserService;
import com.my.blog.website.untils.GsonUtils;
import com.my.blog.website.untils.TaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;


@Controller(value = "adminIndexController")
@RequestMapping(value = "/admin")
@Transactional(rollbackFor = TipException.class)
public class IndexController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);
    @Autowired
    private IUserService userService;

    @Resource
    private ILogService logService;

    @Autowired
    private ISiteService siteService;

    /**
     * 到博客主页
     *
     * @param [request]
     * @return java.lang.String
     * @author rfYang
     * @date 2018/6/7 16:13
     */
    @RequestMapping(value = {"", "/index"}, method = RequestMethod.GET)
    public String index(HttpServletRequest request) {
        logger.info("Enter admin index method");
        StatisticsBo statistics = siteService.getStatistics();
        List<CommentVo> comments = siteService.recentComments(5);
        List<ContentVo> articles = siteService.recentContents(5);
        List<LogVo> logs = logService.getLogs(1, 5);

        request.setAttribute("statistics", statistics);
        request.setAttribute("comments", comments);
        request.setAttribute("articles", articles);
        request.setAttribute("logs", logs);

        logger.debug("Exit admin index method");
        return "admin/index";
    }

    /**
     * 个人设置页面跳转
     * get请求
     *
     * @param
     * @return
     * @author rfYang
     * @date 2018/6/8 9:32
     */
    @GetMapping(value = "profile")
    public ModelAndView skipPersonSettings(ModelAndView mv) {
        mv.setViewName("/admin/profile");
        return mv;
    }

    /**
     * 处理个人设置页面的post请求,主要是更改个人设置页面的显示名称和邮箱
     *
     * @param [screenName, email, response, session]
     * @return com.my.blog.website.model.Bo.RestResponseBo
     * @author rfYang
     * @date 2018/6/8 9:47
     */
    @PostMapping(value = "profile")
    @ResponseBody
    public RestResponseBo savePersonProfile(@RequestParam(value = "screenName") String screenName,
                                            @RequestParam(value = "email") String email,
                                            HttpServletRequest request, HttpSession session) {
        UserVo requestUser = this.user(request);
        if (StringUtils.isNotBlank(screenName) && StringUtils.isNotBlank(email)) {
            UserVo user = new UserVo();
            user.setUid(requestUser.getUid());
            user.setScreenName(screenName);
            user.setEmail(email);
            try {
                userService.updateById(user);
            } catch (Exception e) {
                String msg = "更新错误";
                if (e instanceof TipException) {
                    msg = e.getMessage();
                } else {
                    logger.error(msg, e);
                }
                return RestResponseBo.fail(msg);
            }
            logService.insertLog(LogActions.UP_INFO.getAction(), GsonUtils.toJsonString(user), request.getRemoteAddr(), requestUser.getUid());

            //完成插入操作后就要更新session
            UserVo sessionUser = (UserVo) session.getAttribute(WebConst.LOGIN_SESSION_KEY);
            sessionUser.setScreenName(screenName);
            sessionUser.setEmail(email);
            session.setAttribute(WebConst.LOGIN_SESSION_KEY, sessionUser);
            return RestResponseBo.ok();
        }else{
            return RestResponseBo.fail("显示名或邮箱不能为空,更新失败！");
        }

    }
}
