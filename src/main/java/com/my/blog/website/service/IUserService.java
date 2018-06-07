package com.my.blog.website.service;

import com.my.blog.website.model.Vo.UserVo;

public interface IUserService {

    UserVo login(String username, String password);

    /**
     * 通过uid查找对象
     * @param uid
     * @return
     */
    UserVo queryUserById(Integer uid);
}
