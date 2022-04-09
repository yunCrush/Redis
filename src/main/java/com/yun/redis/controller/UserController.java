package com.yun.redis.controller;

import cn.hutool.core.util.IdUtil;
import com.yun.redis.entities.User;
import com.yun.redis.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Random;
/**
 * Author: yunCrush
 * Date:2022/4/8 20:39
 * Description:
 */

@Api(description = "用户User接口")
@RestController
@Slf4j
public class UserController
{
    @Resource
    private UserService userService;

    @ApiOperation("数据库初始化5条数据")
    @RequestMapping(value = "/user/add", method = RequestMethod.POST)
    public void addUser() {
        for (int i = 0; i < 5; i++) {
            User user = new User();

            user.setUsername("yuncrush" + i);
            user.setPassword(IdUtil.simpleUUID().substring(0,6));
            user.setSex((byte) new Random().nextInt(2));

            userService.addUser(user);
        }
    }
    @ApiOperation("删除某条数据")
    @RequestMapping(value = "/user/del/{id}", method = RequestMethod.POST)
    public void deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
    }

    @ApiOperation("修改某条数据")
    @RequestMapping(value = "/user/update", method = RequestMethod.POST)
    public void updateUser(@RequestBody User userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        userService.updateUser(user);
    }

    @ApiOperation("单个用户查询，按userid查用户信息")
    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public User findUserById(@PathVariable int id) {
        return this.userService.findUserById(id);
    }

    //==============================直接再ie地址栏里面干
    /*http://localhost:5555/user/update2?id=主键ID&username=？？？&password=？？？
    @RequestMapping(value = "/user/update2")
    public void updateUser2(User userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        userService.updateUser(user);
    }*/

}

