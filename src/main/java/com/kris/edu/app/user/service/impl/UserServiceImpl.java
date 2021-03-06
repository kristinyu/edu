package com.kris.edu.app.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kris.edu.app.user.bean.UserBean;
import com.kris.edu.app.user.dao.UserMapper;
import com.kris.edu.app.user.service.IUserService;
import com.kris.edu.framwork.BaseResult.PageResult;
import com.kris.edu.utils.AESUtils;
import com.kris.edu.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements IUserService {

    @Value("${user.list.redis.string}")
    private String USER_LIST_STRING;

    @Value("${user.list.redis.string.time}")
    private long USER_LIST_STRING_TIME_DURATION;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public PageResult getUserCountList(Integer pageSize,Integer pageNum) {
//        if (redisTemplate.hasKey(USER_LIST_STRING)){
//            return JSON.parseObject(redisTemplate.opsForValue().get(USER_LIST_STRING),PageResult.class);
//        }else{
//            int total = userMapper.selectUserCount();
//            List<UserBean> list = userMapper.selectUserList();
//            redisTemplate.opsForValue().set(USER_LIST_STRING,JSON.toJSONString(PageResult.success(total, list)),USER_LIST_STRING_TIME_DURATION, TimeUnit.SECONDS);
//            return PageResult.success(total, list);
//        }
        PageInfo<UserBean> userBeanLists = PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(() -> userMapper.selectUserList());
        userBeanLists.getList().stream().forEach(a->a.setPhone(AESUtils.decript(a.getPhone())));
        return PageResult.success( userBeanLists.getTotal(), userBeanLists.getList());
    }

    @Override
    public Integer addNewUser(String username, String password, String nickname,String phone) {
        String usernameEnCode = MD5Util.encoder(username);
        String passwordEnCode = MD5Util.encoder(password);
        String asePhone = AESUtils.encript(phone);
        List<UserBean> userBean = userMapper.validateUser(usernameEnCode);
        if (userBean.size()>=1){
            return 3;
        }else{
            return userMapper.addNewUser(usernameEnCode, passwordEnCode, nickname,asePhone) > 0 ? 1 : 2;
        }
    }
}
