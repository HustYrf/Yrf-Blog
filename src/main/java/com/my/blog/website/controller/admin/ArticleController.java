package com.my.blog.website.controller.admin;

import com.github.pagehelper.PageInfo;
import com.my.blog.website.constant.WebConst;
import com.my.blog.website.controller.BaseController;
import com.my.blog.website.enums.Types;
import com.my.blog.website.exception.TipException;
import com.my.blog.website.model.Bo.RestResponseBo;
import com.my.blog.website.model.Vo.ContentVo;
import com.my.blog.website.model.Vo.ContentVoExample;
import com.my.blog.website.model.Vo.MetaVo;
import com.my.blog.website.model.Vo.UserVo;
import com.my.blog.website.service.IContentService;
import com.my.blog.website.service.ILogService;
import com.my.blog.website.service.IMetaService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 发布博客文章
 *
 * @param
 * @author rfYang
 * @date 2018/6/8 13:31
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
     *
     * @param [page, limit, request]
     * @return java.lang.String
     * @author rfYang
     * @date 2018/6/8 14:36
     */
    @GetMapping(value = "")
    public String articleManagementIndex(@RequestParam(value = "page", defaultValue = "1") int page,
                                         @RequestParam(value = "limit", defaultValue = "15") int limit,
                                         HttpServletRequest request) {
        ContentVoExample example = new ContentVoExample();
        example.setOrderByClause("created desc");
        example.createCriteria().andTypeEqualTo(Types.ARTICLE.getType());
        PageInfo<ContentVo> articles = contentService.getArticlesWithPage(example, page, limit);
        request.setAttribute("articles", articles);
        return "admin/article_list";
    }

    /**
     * 发布文章
     *
     * @param
     * @return
     * @author rfYang
     * @date 2018/6/8 13:53
     */
    @GetMapping(value = "/publish")
    public ModelAndView newArticles(HttpServletRequest request, ModelAndView modelAndView) {
        List<MetaVo> categorys = metaService.getMetas(Types.CATEGORY.getType());
        request.setAttribute("categories", categorys);
        modelAndView.setViewName("admin/article_edit");
        return modelAndView;
    }

    /**
     * 用户编辑相应博文
     *
     * @param
     * @return
     * @author rfYang
     * @date 2018/6/8 15:24
     */
    @GetMapping(value = "/{cid}")
    public String editBlog(@PathVariable(value = "cid") String cid, HttpServletRequest request) {
        ContentVo article = contentService.getContents(cid);
        request.setAttribute("contents", article);
        List<MetaVo> categorys = metaService.getMetas(Types.CATEGORY.getType());
        request.setAttribute("categories", categorys);
        request.setAttribute("active", "article");//设置对应的head头信息
        return "admin/article_edit";
    }

    /**
     * 用户发布新的博文
     *
     * @param [contentVo, request]
     * @return com.my.blog.website.model.Bo.RestResponseBo
     * @author rfYang
     * @date 2018/6/8 16:02
     */
    @PostMapping(value = "/publish")
    @ResponseBody
    public RestResponseBo publishArticle(ContentVo contentVo, HttpServletRequest request) {
        UserVo user = this.user(request);
        contentVo.setAuthorId(user.getUid());
        contentVo.setType(Types.ARTICLE.getType());
        if (StringUtils.isBlank(contentVo.getCategories())) {
            contentVo.setCategories("默认分类");
        }
        String result = contentService.publish(contentVo);
        if(!result.equals(WebConst.SUCCESS_RESULT)){
            return RestResponseBo.fail(result);
        }
        return RestResponseBo.ok();
    }

    /**
     *修改用户
     *  @author rfYang
     * @date 2018/6/11 9:00
     * @param [contentVo, request]
     * @return com.my.blog.website.model.Bo.RestResponseBo
     */
    @PostMapping(value = "/modify")
    @ResponseBody
    public RestResponseBo modifyArticle(ContentVo contentVo, HttpServletRequest request){
        UserVo userVo = this.user(request);
        contentVo.setAuthorId(userVo.getUid());
        contentVo.setType(Types.ARTICLE.getType());
        String result = contentService.updateArticle(contentVo);
        if(!WebConst.SUCCESS_RESULT.equals(result)){
            return RestResponseBo.fail(result);
        }
        return RestResponseBo.ok();
    }
}
