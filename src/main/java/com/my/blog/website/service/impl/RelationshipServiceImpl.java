package com.my.blog.website.service.impl;

import com.my.blog.website.dao.RelationshipVoMapper;
import com.my.blog.website.model.Vo.RelationshipVoExample;
import com.my.blog.website.model.Vo.RelationshipVoKey;
import com.my.blog.website.service.IRelationshipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RelationshipServiceImpl implements IRelationshipService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RelationshipServiceImpl.class);
    @Resource
    private RelationshipVoMapper relationshipVoMapper;

    @Override
    public Long countById(Integer cid, Integer mid) {
        LOGGER.debug("Enter countById method:cid={},mid={}",cid,mid);
        RelationshipVoExample relationshipVoExample = new RelationshipVoExample();
        RelationshipVoExample.Criteria criteria = relationshipVoExample.createCriteria();
        if (cid != null) {
            criteria.andCidEqualTo(cid);
        }
        if (mid != null) {
            criteria.andMidEqualTo(mid);
        }
        long num = relationshipVoMapper.countByExample(relationshipVoExample);
        LOGGER.debug("Exit countById method return num={}",num);
        return num;
    }

    @Override
    public void insertVo(RelationshipVoKey relationships) {
        relationshipVoMapper.insert(relationships);
    }
}
