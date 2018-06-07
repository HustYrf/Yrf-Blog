package com.my.blog.website.service;

import com.my.blog.website.model.Bo.StatisticsBo;
import com.my.blog.website.model.Vo.CommentVo;
import com.my.blog.website.model.Vo.ContentVo;

import java.util.List;

public interface ISiteService {
    /**
     * 最新收到的评论
     *
     * @param limit
     * @return
     */
    List<CommentVo> recentComments(int limit);


    /**
     * 最新发表的文章
     *
     * @param limit
     * @return
     */
    List<ContentVo> recentContents(int limit);

    /**
     * 获取后台统计数据
     *
     * @return
     */
    StatisticsBo getStatistics();
}
