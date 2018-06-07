package com.my.blog.website.controller.admin;

import com.my.blog.website.controller.BaseController;
import com.my.blog.website.exception.TipException;
import com.my.blog.website.model.Bo.StatisticsBo;
import com.my.blog.website.model.Vo.CommentVo;
import com.my.blog.website.model.Vo.ContentVo;
import com.my.blog.website.model.Vo.LogVo;
import com.my.blog.website.service.ILogService;
import com.my.blog.website.service.ISiteService;
import com.my.blog.website.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
     * @author rfYang
     * @date 2018/6/7 16:13
     * @param [request]
     * @return java.lang.String
     */
    @RequestMapping(value = {"","/index"},method = RequestMethod.GET)
    public String index(HttpServletRequest request){
        logger.info("Enter admin index method");
        StatisticsBo statistics = siteService.getStatistics();
        List<CommentVo> comments  = siteService.recentComments(5);
        List<ContentVo> articles = siteService.recentContents(5);
        List<LogVo> logs = logService.getLogs(1,5);

        request.setAttribute("statistics",statistics);
        request.setAttribute("comments",comments);
        request.setAttribute("articles",articles);
        request.setAttribute("logs",logs);

        logger.debug("Exit admin index method");
        return "admin/index";
    }

}
