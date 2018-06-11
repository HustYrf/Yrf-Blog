package com.my.blog.website.service;


import com.github.pagehelper.PageInfo;
import com.my.blog.website.model.Bo.CommentBo;

public interface ICommentService {

    /**
     * 获取文章下的评论
     * @param cid
     * @param page
     * @param limit
     * @return CommentBo
     */
    PageInfo<CommentBo> getComments(Integer cid, int page, int limit);
}
