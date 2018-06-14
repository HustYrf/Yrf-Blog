package com.my.blog.website.controller.admin;

import com.github.pagehelper.PageInfo;
import com.my.blog.website.constant.WebConst;
import com.my.blog.website.controller.BaseController;
import com.my.blog.website.enums.LogActions;
import com.my.blog.website.enums.Types;
import com.my.blog.website.model.Bo.RestResponseBo;
import com.my.blog.website.model.Vo.ContentVo;
import com.my.blog.website.model.Vo.ContentVoExample;
import com.my.blog.website.model.Vo.UserVo;
import com.my.blog.website.service.IContentService;
import com.my.blog.website.service.ILogService;
import com.my.blog.website.untils.GsonUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "admin/page")
public class PageController extends BaseController {
    @Resource
    private IContentService contentService;
    @Resource
    private ILogService logService;

    /**
     * 到页面管理主界面
     *
     * @param [request]
     * @return java.lang.String
     * @author rfYang
     * @date 2018/6/12 10:03
     */
    @GetMapping("")
    public String pageIndex(HttpServletRequest request) {
        ContentVoExample contentVoExample = new ContentVoExample();
        contentVoExample.setOrderByClause("created desc");
        ContentVoExample.Criteria criteria = contentVoExample.createCriteria();
        criteria.andTypeEqualTo(Types.PAGE.getType());
        PageInfo<ContentVo> articles = contentService.getArticlesWithPage(contentVoExample, 1, WebConst.MAX_PAGE);
        request.setAttribute("articles", articles);
        return "admin/page_list";
    }

    /**
     * 编辑页面
     *
     * @param [cid, request]
     * @return java.lang.String
     * @author rfYang
     * @date 2018/6/12 10:12
     */
    @GetMapping(value = "/{cid}")
    public String editPage(@PathVariable(value = "cid") String cid, HttpServletRequest request) {
        ContentVo contentVo = contentService.getContents(cid);
        request.setAttribute("contents", contentVo);
        return "admin/page_edit";
    }

    /**
     * 修改page页面
     *
     * @param [cid, status, content, title, slug, allowComment, allowPing, request]
     * @return com.my.blog.website.model.Bo.RestResponseBo
     * @author rfYang
     * @date 2018/6/12 10:35
     */
    @PostMapping(value = "/modify")
    @ResponseBody
    public RestResponseBo modifyPage(@RequestParam(value = "cid") Integer cid,
                                     @RequestParam(value = "status") String status,
                                     @RequestParam(value = "content") String content,
                                     @RequestParam(value = "title") String title,
                                     @RequestParam(value = "slug") String slug,
                                     @RequestParam(required = false) Integer allowComment,
                                     @RequestParam(required = false) Integer allowPing, HttpServletRequest request) {
        UserVo userVo = this.user(request);
        ContentVo contentVo = new ContentVo();
        contentVo.setCid(cid);
        contentVo.setType(Types.PAGE.getType());
        contentVo.setAuthorId(userVo.getUid());
        contentVo.setSlug(slug);
        contentVo.setStatus(status);
        contentVo.setContent(content);
        contentVo.setTitle(title);
        contentVo.setAllowComment(true);
        if (allowPing != null) {
            contentVo.setAllowPing(allowPing == 1);
        }
        String result = contentService.updateArticle(contentVo);
        logService.insertLog(LogActions.UP_PAGE.getAction(), GsonUtils.toJsonString(contentVo), request.getRemoteAddr(), userVo.getUid());
        if (!result.equals(WebConst.SUCCESS_RESULT)) {
            return RestResponseBo.fail(result);
        }
        return RestResponseBo.ok();
    }

    /**
     * 添加新页面
     *
     * @param [mv]
     * @return org.springframework.web.servlet.ModelAndView
     * @author rfYang
     * @date 2018/6/12 11:03
     */
    @GetMapping(value = "/new")
    public ModelAndView newPage(ModelAndView mv) {
        mv.setViewName("admin/page_edit");
        return mv;
    }
    /**
     * 发布新页面
     * @author rfYang
     * @date 2018/6/12 11:10
     * @param [title, content, status, slug, allowComment, allowPing, request]
     * @return com.my.blog.website.model.Bo.RestResponseBo
     */
    @PostMapping(value = "/publish")
    @ResponseBody
    public RestResponseBo publishPage(@RequestParam String title, @RequestParam String content,
                                      @RequestParam String status, @RequestParam String slug,
                                      @RequestParam(required = false) Integer allowComment, @RequestParam(required = false) Integer allowPing, HttpServletRequest request) {
        UserVo userVo = this.user(request);
        UserVo users = this.user(request);
        ContentVo contents = new ContentVo();
        contents.setTitle(title);
        contents.setContent(content);
        contents.setStatus(status);
        contents.setSlug(slug);
        contents.setType(Types.PAGE.getType());
        contents.setAllowComment(true);
        if (null != allowPing) {
            contents.setAllowPing(allowPing == 1);
        }
        contents.setAuthorId(users.getUid());
        String result = contentService.publish(contents);
        logService.insertLog(LogActions.UP_PAGE.getAction(), GsonUtils.toJsonString(contents), request.getRemoteAddr(), userVo.getUid());
        if (!WebConst.SUCCESS_RESULT.equals(result)) {
            return RestResponseBo.fail(result);
        }
        return RestResponseBo.ok();
    }
    /**
     * 删除page页面
     * @author rfYang
     * @date 2018/6/14 9:18
     * @param [request, cid]
     * @return com.my.blog.website.model.Bo.RestResponseBo
     */
    @PostMapping(value="/delete")
    @ResponseBody
    public RestResponseBo deletePage(HttpServletRequest request,@RequestParam Integer cid){
        String result = contentService.deleteByCid(cid);
        logService.insertLog(LogActions.DELE_ARTICLE.getAction(),String.valueOf(cid),request.getRemoteAddr(),this.user(request).getUid());
        if(!result.equals(WebConst.SUCCESS_RESULT)){
            return RestResponseBo.fail(result);
        }
        return RestResponseBo.ok();
    }
}
