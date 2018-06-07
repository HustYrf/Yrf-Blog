package com.my.blog.website.service;

import com.my.blog.website.model.Vo.OptionVo;

public interface IOptionService {
    OptionVo getOptionByName(String name);
}
