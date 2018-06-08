package com.my.blog.website.controller.admin;

import com.github.pagehelper.PageInfo;
import com.my.blog.website.controller.BaseController;
import com.my.blog.website.enums.Types;
import com.my.blog.website.exception.TipException;
import com.my.blog.website.model.Vo.ContentVo;
import com.my.blog.website.model.Vo.ContentVoExample;
import com.my.blog.website.model.Vo.MetaVo;
import com.my.blog.website.service.IContentService;
import com.my.blog.website.service.ILogService;
import com.my.blog.website.service.IMetaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 发布博客文章
 * @author rfYang
 * @date 2018/6/8 13:31
 * @param
 * @return
 */
@Controller
@RequestMapping(value = "admin/article")
@Transactional(rollbackFor = TipException.class)
public class ArticleController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);
    @Resource
    private ILogService logService;
    @Resource
    private IMetaService metaService;
    @Autowired
    private IContentService contentService;


    /**
     * 文章管理的主页
     * @author rfYang
     * @date 2018/6/8 14:36
     * @param [page, limit, request]
     * @return java.lang.String
     */
    @GetMapping(value = "")
    public String articleManagementIndex(@RequestParam(value = "page",defaultValue = "1") int page,
                                         @RequestParam(value = "limit",defaultValue = "15") int limit,
                                         HttpServletRequest request) {
        ContentVoExample example = new ContentVoExample();
        example.setOrderByClause("created desc");
        example.createCriteria().andTypeEqualTo(Types.ARTICLE.getType());
        PageInfo<ContentVo> articles = contentService.getArticlesWithPage(example,page,limit);
        request.setAttribute("articles",articles);
        return "admin/article_list";
    }
    /**
     * 发布文章
     * @author rfYang
     * @date 2018/6/8 13:53
     * @param
     * @return
     */
    @GetMapping(value = "/publish")
    public ModelAndView newArticles(HttpServletRequest request, ModelAndView modelAndView){
        List<MetaVo> categorys = metaService.getMetas(Types.CATEGORY.getType());
        request.setAttribute("categories",categorys);
        modelAndView.setViewName("admin/article_edit");
        return modelAndView;
    }


}
