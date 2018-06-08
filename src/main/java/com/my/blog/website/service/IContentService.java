package com.my.blog.website.service;

import com.github.pagehelper.PageInfo;
import com.my.blog.website.model.Vo.ContentVo;
import com.my.blog.website.model.Vo.ContentVoExample;

public interface IContentService {

    PageInfo<ContentVo> getArticlesWithPage(ContentVoExample example, int page, int limit);
}
