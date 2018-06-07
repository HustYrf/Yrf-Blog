package com.my.blog.website.service.impl;

import com.my.blog.website.dao.OptionVoMapper;
import com.my.blog.website.model.Vo.OptionVo;
import com.my.blog.website.service.IOptionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class OptionService implements IOptionService {
    @Resource
    private OptionVoMapper optionVoMapper;
    @Override
    public OptionVo getOptionByName(String name) {
        OptionVo optionVo = null;
        if(StringUtils.isNotEmpty(name)){
            optionVo = optionVoMapper.selectByPrimaryKey(name);
        }
        return optionVo;
    }
}
