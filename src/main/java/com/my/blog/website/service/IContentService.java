package com.my.blog.website.service;

import com.github.pagehelper.PageInfo;
import com.my.blog.website.model.Vo.ContentVo;
import com.my.blog.website.model.Vo.ContentVoExample;

import java.util.List;

public interface IContentService {

    PageInfo<ContentVo> getArticlesWithPage(ContentVoExample example, int page, int limit);
    /**
     * 根据id或slug获取文章
     *
     * @param id
     * @return ContentVo
     */
    ContentVo getContents(String cid);

    String publish(ContentVo contentVo);

    String updateArticle(ContentVo contentVo);

    String deleteByCid(Integer cid);

    void updateContentByCid(ContentVo temp);

    PageInfo<ContentVo> getContents(int p, int limit);
}
