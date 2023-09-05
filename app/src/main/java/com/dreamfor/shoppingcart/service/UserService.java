package com.dreamfor.shoppingcart.service;

import com.dreamfor.shoppingcart.domain.User;

public interface UserService {
    // 增加用户
    boolean insertUser(User user);

    // 根据用户ID查询用户信息
    User queryUserByID(int id);
}
