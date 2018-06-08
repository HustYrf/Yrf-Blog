package com.my.blog.website.service.impl;

import com.my.blog.website.dao.MetaVoMapper;
import com.my.blog.website.model.Vo.MetaVo;
import com.my.blog.website.model.Vo.MetaVoExample;
import com.my.blog.website.service.IMetaService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
/**
 * metaService
 * @author rfYang
 * @date 2018/6/8 14:04
 * @param
 * @return
 */
@Service
public class MetaServiceImpl implements IMetaService {
    private static final Logger logger = LoggerFactory.getLogger(MetaServiceImpl.class);
    @Resource
    private MetaVoMapper metaVoMapper;

    /**
     * 返回类型为type的所有元信息
     * @param type
     * @return
     */
    @Override
    public List<MetaVo> getMetas(String type) {
        if(StringUtils.isNotBlank(type)){
            MetaVoExample example = new MetaVoExample();
            example.setOrderByClause("sort desc,mid desc");
            example.createCriteria().andTypeEqualTo(type);
            return metaVoMapper.selectByExample(example);
        }
        return null;
    }
}
