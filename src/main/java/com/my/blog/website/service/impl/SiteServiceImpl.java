package com.my.blog.website.service.impl;

import com.github.pagehelper.PageHelper;
import com.my.blog.website.dao.AttachVoMapper;
import com.my.blog.website.dao.CommentVoMapper;
import com.my.blog.website.dao.ContentVoMapper;
import com.my.blog.website.dao.MetaVoMapper;
import com.my.blog.website.enums.Types;
import com.my.blog.website.model.Bo.StatisticsBo;
import com.my.blog.website.model.Vo.*;
import com.my.blog.website.service.ISiteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SiteServiceImpl implements ISiteService {
    //DEBUG < INFO < WARN < ERROR < FATAL
    private static final Logger logger = LoggerFactory.getLogger(SiteServiceImpl.class);
    @Autowired
    private ContentVoMapper contentVoMapper;

    @Resource
    private CommentVoMapper commentVoMapper;

    @Autowired
    private AttachVoMapper attachVoMapper;

    @Resource
    private MetaVoMapper metaVoMapper;//元信息
    /**
     * limit条数的评论
     * @author rfYang
     * @date 2018/6/7 18:39
     * @param [limit]
     * @return java.util.List<com.my.blog.website.model.Vo.CommentVo>
     */

    @Override
    public List<CommentVo> recentComments(int limit) {
        logger.debug("Enter recentComments method:limit={}", limit);
        if(limit<1||limit>10){
            limit=10;
        }
        CommentVoExample commentVoExample = new CommentVoExample();
        commentVoExample.setOrderByClause("created DESC");
        PageHelper.startPage(1,limit);
        List<CommentVo> list= commentVoMapper.selectByExampleWithBLOBs(commentVoExample);
        logger.debug("Exit recentComments method");
        return list;
    }
    /**
     * limit条数的文章
     * @author rfYang
     * @date 2018/6/7 18:40
     * @param [limit]
     * @return java.util.List<com.my.blog.website.model.Vo.ContentVo>
     */
    @Override
    public List<ContentVo> recentContents(int limit) {
        logger.debug("Enter recentContents method");
        if (limit < 0 || limit > 10) {
            limit = 10;
        }
        ContentVoExample example = new ContentVoExample();
        ContentVoExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(Types.PUBLISH.getType()).andTypeEqualTo(Types.ARTICLE.getType());
        example.setOrderByClause("created desc");
        PageHelper.startPage(1,limit);
        List<ContentVo> list = contentVoMapper.selectByExample(example);
        logger.debug("exit recentContents method!");
        return list;
    }
   /**
    * 各种数据
    * @author rfYang
    * @date 2018/6/7 18:40
    * @param []articles,comments.links,attachs
    * @return com.my.blog.website.model.Bo.StatisticsBo
    */
    @Override
    public StatisticsBo getStatistics() {
        logger.debug("Enter getStatistics method");
        StatisticsBo statisticsBo = new StatisticsBo();
        ContentVoExample example = new ContentVoExample();
        ContentVoExample.Criteria criteria = example.createCriteria();
        criteria.andTypeEqualTo(Types.ARTICLE.getType()).andTypeEqualTo(Types.PUBLISH.getType());
        long articles = contentVoMapper.countByExample(example);
        long comments =  commentVoMapper.countByExample(new CommentVoExample());
        long attachs = attachVoMapper.countByExample(new AttachVoExample());

        MetaVoExample metaVoExample = new MetaVoExample();

        MetaVoExample.Criteria metaCriteria =  metaVoExample.createCriteria();
        metaCriteria.andTypeEqualTo(Types.LINK.getType());
        long links = metaVoMapper.countByExample(metaVoExample);

        statisticsBo.setArticles(articles);
        statisticsBo.setAttachs(attachs);
        statisticsBo.setComments(comments);
        statisticsBo.setLinks(links);
        logger.debug("exit getStatistics method!");
        return statisticsBo;
    }
}
