package com.my.blog.website.service;

import com.my.blog.website.model.Vo.RelationshipVoKey;

public interface IRelationshipService {

    Long countById(Integer cid, Integer mid);

    void insertVo(RelationshipVoKey relationships);


    void deleteById(Integer cid, Integer mid);
}
