package com.my.blog.website.service;

import com.my.blog.website.model.Vo.MetaVo;

import java.util.List;

public interface IMetaService {

    List<MetaVo> getMetas(String type);

    void saveMetas(Integer cid, String tags, String type);

    void saveOrUpdate(Integer cid, String name, String type);
}
