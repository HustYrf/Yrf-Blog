package com.my.blog.website.service.impl;

import com.github.pagehelper.PageHelper;
import com.my.blog.website.constant.WebConst;
import com.my.blog.website.dao.LogVoMapper;
import com.my.blog.website.model.Vo.LogVo;
import com.my.blog.website.model.Vo.LogVoExample;
import com.my.blog.website.service.ILogService;
import com.my.blog.website.untils.DateKit;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LogServiceImpl implements ILogService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LogServiceImpl.class);

    @Resource
    private LogVoMapper logVoMapper;

    @Override
    public void insertLog(LogVo logVo) {
        logVoMapper.insert(logVo);
    }

    @Override
    public void insertLog(String action, String data, String ip, Integer authorId) {
        LogVo logVo = new LogVo();
        logVo.setAction(action);
        logVo.setAuthorId(authorId);
        logVo.setIp(ip);
        logVo.setData(data);
        logVo.setCreated(DateKit.getCurrentUnixTime());
        logVoMapper.insertSelective(logVo);
    }

    @Override
    public List<LogVo> getLogs(int page, int limit) {
        logger.debug("Enter getLogs method:page={},linit={}",page,limit);
        if (page <= 0) {
            page = 1;
        }
        if (limit < 1 || limit > WebConst.MAX_POSTS) {
            limit = 10;
        }
        LogVoExample example = new LogVoExample();
        example.setOrderByClause("id desc");
        PageHelper.startPage((page-1)*limit,limit);
        List<LogVo> list = logVoMapper.selectByExample(example);
        logger.debug("exit getLogs method");
        return list;


    }
}
