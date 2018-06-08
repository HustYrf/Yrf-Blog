package com.my.blog.website.service.impl;

import com.my.blog.website.dao.UserVoMapper;
import com.my.blog.website.exception.TipException;
import com.my.blog.website.model.Vo.UserVo;
import com.my.blog.website.model.Vo.UserVoExample;
import com.my.blog.website.service.IUserService;
import com.my.blog.website.untils.TaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserVoMapper userVoMapper;

    /**
     * 根据用户id查询用户
     *
     * @param [uid]
     * @return com.my.blog.website.model.Vo.UserVo
     * @author rfYang
     * @date 2018/6/7 20:33
     */
    @Override
    public UserVo queryUserById(Integer uid) {
        UserVo userVo = null;
        if (uid != null) {
            userVo = userVoMapper.selectByPrimaryKey(uid);
        }
        return userVo;
    }

    /**
     * 登陆service
     *
     * @param [username, password]
     * @return com.my.blog.website.model.Vo.UserVo
     * @author rfYang
     * @date 2018/6/7 19:01
     */
    @Override
    public UserVo login(String username, String password) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw new TipException("用户名和密码不能为空");
        }
        UserVoExample userVoExample = new UserVoExample();
        UserVoExample.Criteria criteria = userVoExample.createCriteria();
        criteria.andUsernameEqualTo(username);
        long count = userVoMapper.countByExample(userVoExample);
        if (count < 1) {
            throw new TipException("没有该用户");
        }
        String encodePass = TaleUtils.MD5encode(username + password);
        criteria.andPasswordEqualTo(encodePass);
        List<UserVo> list = userVoMapper.selectByExample(userVoExample);
        if (list.size() != 1)
            throw new TipException("用户的密码不对");
        return list.get(0);
    }

    /**
     * 根据用户的id来更新
     *
     * @param [user]
     * @return void
     * @author rfYang
     * @date 2018/6/8 10:00
     */
    @Override
    public void updateById(UserVo user) {
        if (user == null || user.getUid() == null) {
            throw new TipException("userVo is null");
        }
        int count = userVoMapper.updateByPrimaryKeySelective(user);
        if (count != 1) {
            throw new TipException("update user is not return one");
        }
    }
}
