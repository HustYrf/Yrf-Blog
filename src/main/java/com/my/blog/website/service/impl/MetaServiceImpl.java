package com.my.blog.website.service.impl;

import com.my.blog.website.dao.MetaVoMapper;
import com.my.blog.website.exception.TipException;
import com.my.blog.website.model.Vo.MetaVo;
import com.my.blog.website.model.Vo.MetaVoExample;
import com.my.blog.website.model.Vo.RelationshipVoKey;
import com.my.blog.website.service.IMetaService;
import com.my.blog.website.service.IRelationshipService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * metaService
 *
 * @param
 * @author rfYang
 * @date 2018/6/8 14:04
 * @return
 */
@Service
public class MetaServiceImpl implements IMetaService {
    private static final Logger logger = LoggerFactory.getLogger(MetaServiceImpl.class);
    @Resource
    private MetaVoMapper metaVoMapper;

    @Resource
    private IRelationshipService relationshipService;
    /**
     * 返回类型为type的所有元信息
     *
     * @param type
     * @return
     */
    @Override
    public List<MetaVo> getMetas(String type) {
        if (StringUtils.isNotBlank(type)) {
            MetaVoExample example = new MetaVoExample();
            example.setOrderByClause("sort desc,mid desc");
            example.createCriteria().andTypeEqualTo(type);
            return metaVoMapper.selectByExample(example);
        }
        return null;
    }

    /**
     * @param
     * @return
     * @author rfYang
     * @date 2018/6/8 16:45
     */
    @Override
    public void saveMetas(Integer cid, String tags, String type) {
        if (null == cid) {
            throw new TipException("项目关联id不能为空");
        }
        if (StringUtils.isNotBlank(tags) && StringUtils.isNotBlank(type)) {
            String[] nameArr = StringUtils.split(tags, ",");
            for (String name : nameArr) {
                this.saveOrUpdate(cid, name, type);
            }
        }
    }

    /**
     * @param
     * @return
     * @author rfYang
     * @date 2018/6/8 16:47
     */
    @Override
    public void saveOrUpdate(Integer cid, String name, String type) {
        MetaVoExample metaVoExample = new MetaVoExample();
        metaVoExample.createCriteria().andTypeEqualTo(type).andNameEqualTo(name);
        List<MetaVo> metaVos = metaVoMapper.selectByExample(metaVoExample);
        int mid;
        MetaVo metas;
        if (metaVos.size() == 1) {
            metas = metaVos.get(0);
            mid = metas.getMid();
        } else if (metaVos.size() > 1) {
            throw new TipException("查询到多条数据");
        } else {
            metas = new MetaVo();
            metas.setSlug(name);
            metas.setName(name);
            metas.setType(type);
            metaVoMapper.insertSelective(metas);
            mid = metas.getMid();
        }
        if (mid != 0) {
            Long count = relationshipService.countById(cid, Integer.valueOf(mid));
            if (count == 0) {
                RelationshipVoKey relationships = new RelationshipVoKey();
                relationships.setCid(cid);
                relationships.setMid(mid);
                relationshipService.insertVo(relationships);
            }
        }
    }
}
