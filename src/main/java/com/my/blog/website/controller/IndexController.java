package com.my.blog.website.controller;


import com.github.pagehelper.PageInfo;
import com.my.blog.website.constant.WebConst;
import com.my.blog.website.enums.Types;
import com.my.blog.website.model.Bo.CommentBo;
import com.my.blog.website.model.Vo.ContentVo;
import com.my.blog.website.service.ICommentService;
import com.my.blog.website.service.IContentService;
import com.my.blog.website.untils.Commons;
import com.my.blog.website.untils.IPKit;
import com.my.blog.website.untils.TaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
public class IndexController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexController.class);
    @Resource
    private IContentService contentService;
    @Autowired
    private ICommentService commentService;

    //返回首页，To-Do
    @GetMapping(value = "")
    public String Index(HttpServletRequest request, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        return this.index(request,1,limit);
    }

   /**
    * @author rfYang
    * @date 2018/6/14 10:53  
    * @param [request, p, limit]  
    * @return java.lang.String  
    */  
    @GetMapping(value = "page/{p}")
    public String index(HttpServletRequest request, @PathVariable int p, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        p = p < 1 || p > WebConst.MAX_PAGE ? 1 : p;
        PageInfo<ContentVo> contentVoPageInfo = contentService.getContents(p, limit);
        request.setAttribute("articles",contentVoPageInfo);
        if(p>1){
            this.title(request,"第"+p+"页");
        }
        return this.render("index");
    }

    /**
     * 登出
     *
     * @param [request, response]
     * @return void
     * @author rfYang
     * @date 2018/6/14 10:19
     */
    @GetMapping(value = "/logout")
    public void logout(HttpSession session, HttpServletResponse response) {
        TaleUtils.logout(session,response);
    }

    @GetMapping(value = {"article/{cid}/preview", "article/{cid}"})
    public String articlePreview(@PathVariable(value = "cid") String cid, HttpServletRequest request) {
        ContentVo contentVo = contentService.getContents(cid);
        if (contentVo == null) {
            return this.render_404();
        }
        request.setAttribute("article", contentVo);
        request.setAttribute("is_post", true);
        completeArticle(request, contentVo);
        if (!checkHitsFrequency(request, cid)) {
            updateArticleHit(contentVo.getCid(), contentVo.getHits());
        }
        return this.render("post");
    }

    /**
     * 抽取公共方法
     *
     * @param request
     * @param contents
     */
    private void completeArticle(HttpServletRequest request, ContentVo contents) {
        if (contents.getAllowComment()) {
            String cp = request.getParameter("cp");
            if (StringUtils.isBlank(cp)) {
                cp = "1";
            }
            request.setAttribute("cp", cp);
            PageInfo<CommentBo> commentsPaginator = commentService.getComments(contents.getCid(), Integer.parseInt(cp), 6);
            request.setAttribute("comments", commentsPaginator);
        }
    }

    /**
     * 检查同一个ip地址是否在2小时内访问同一文章
     *
     * @param request
     * @param cid
     * @return
     */
    private boolean checkHitsFrequency(HttpServletRequest request, String cid) {
        String val = IPKit.getIpAddrByRequest(request) + ":" + cid;
        Integer count = cache.hget(Types.HITS_FREQUENCY.getType(), val);
        if (null != count && count > 0) {
            return true;
        }
        cache.hset(Types.HITS_FREQUENCY.getType(), val, 1, WebConst.HITS_LIMIT_TIME);
        return false;
    }

    /**
     * 更新文章的点击率
     *
     * @param cid
     * @param chits
     */
    private void updateArticleHit(Integer cid, Integer chits) {
        Integer hits = cache.hget("article" + cid, "hits");
        if (chits == null) {
            chits = 0;
        }
        hits = null == hits ? 1 : hits + 1;
        if (hits >= WebConst.HIT_EXCEED) {
            ContentVo temp = new ContentVo();
            temp.setCid(cid);
            temp.setHits(chits + hits);
            contentService.updateContentByCid(temp);
            cache.hset("article" + cid, "hits", 1);
        } else {
            cache.hset("article" + cid, "hits", hits);
        }
    }

    /**
     * 预览功能
     *
     * @param
     * @return
     * @author rfYang
     * @date 2018/6/14 9:44
     */
    @GetMapping(value = "/{pagename}")
    public String pageReview(@PathVariable(value = "pagename") String slug, HttpServletRequest request) {
        ContentVo contentVo = contentService.getContents(slug);
        if (contentVo == null) {
            return this.render_404();
        }
        if (contentVo.getAllowComment()) {
            String cp = request.getParameter("cp");
            if (StringUtils.isBlank(cp)) {
                cp = "1";
            }
            PageInfo<CommentBo> commentsPaginator = commentService.getComments(contentVo.getCid(), Integer.parseInt(cp), 6);
            request.setAttribute("comments", commentsPaginator);
        }
        request.setAttribute("article", contentVo);
        if (!checkHitsFrequency(request, String.valueOf(contentVo.getCid()))) {
            updateArticleHit(contentVo.getCid(), contentVo.getHits());
        }
        return this.render("page");
    }
}
