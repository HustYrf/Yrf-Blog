package com.my.blog.website.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.my.blog.website.dao.ContentVoMapper;
import com.my.blog.website.model.Vo.ContentVo;
import com.my.blog.website.model.Vo.ContentVoExample;
import com.my.blog.website.service.IContentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ContentServiceImpl implements IContentService {
    private static final Logger logger = LoggerFactory.getLogger(ContentServiceImpl.class);
    @Resource
    private ContentVoMapper contentVoMapper;
    @Override
    public PageInfo<ContentVo> getArticlesWithPage(ContentVoExample example, int page, int limit) {
        PageHelper.startPage(page,limit);
        List<ContentVo> contentVoList = contentVoMapper.selectByExampleWithBLOBs(example);
        return new PageInfo<>(contentVoList);
    }
}
