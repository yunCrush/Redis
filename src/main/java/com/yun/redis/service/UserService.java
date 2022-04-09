package com.yun.redis.service;

import com.yun.redis.entities.User;
import com.yun.redis.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * Author: yunCrush
 * Date:2022/4/8 20:12
 * Description:
 */
@Service
@Slf4j
public class UserService {

    public static final String CACHE_KEY_USER = "user:";

    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisTemplate redisTemplate;

    public void addUser(User user) {
        log.info("插入之前user:{}", user);
        int i = userMapper.insertSelective(user);
        log.info("插入之后user:{}", user);
        log.info("===========");
        System.out.println();
        if (i > 0) {
            //到数据库里面，重新捞出新数据出来，做缓存
            user = this.userMapper.selectByPrimaryKey(user.getId());
            //缓存key
            String key = CACHE_KEY_USER + user.getId();
            //往mysql里面插入成功随后再从mysql查询出来，再插入redis
            redisTemplate.opsForValue().set(key, user);
        }
    }

    public void deleteUser(Integer id) {
        //1.先直接删除数据库
        int i = userMapper.deleteByPrimaryKey(id);
        if (i > 0) {
            //2.再修改缓存
            String key = CACHE_KEY_USER + id;
            redisTemplate.delete(key);
        }
    }

    public void updateUser(User user) {
        //1.先直接修改数据库
        int i = this.userMapper.updateByPrimaryKeySelective(user);
        if (i > 0) {
            //2.再修改缓存
            //缓存key
            String key = CACHE_KEY_USER + user.getId();
            user = this.userMapper.selectByPrimaryKey(user.getId());
            //修改也是用SET命令，重新设置，Redis没有update操作，都是重新设置新值
            redisTemplate.opsForValue().set(key, user);
        }
    }

    /**
     * 先去redis里面找数据 ，找到就直接返回，找不到再去查询mysql
     *
     * @param userId
     * @return
     */
    public User findUserById(Integer userId) {
        User user = null;
        //缓存key
        String key = CACHE_KEY_USER + userId;
        //1 查询redis
        user = (User) redisTemplate.opsForValue().get(key);
        //redis无，进一步查询mysql
        if (user == null) {
            //从mysql查出来user
            user = this.userMapper.selectByPrimaryKey(userId);
            // mysql有，redis无
            if (user != null) {
                //把mysql捞到的数据写入redis，方便下次查询能redis命中。
                redisTemplate.opsForValue().set(key, user);
            }
        }
        return user;
    }

    /**
     * 先去redis里面找数据 ，找到就直接返回，找不到再去查询mysql
     *
     * @param userId
     * @return DCL思想解决缓存击穿问题
     */
    public User findUserById2(Integer userId) {
        User user = null;
        //缓存key
        String key = CACHE_KEY_USER + userId;
        //1 查询redis
        user = (User) redisTemplate.opsForValue().get(key);
        //redis无，进一步查询mysql
        if (user == null) {
            // DCL 思想 保证第一个线程进来
            synchronized (UserService.class) {
                user = (User) redisTemplate.opsForValue().get(key);
                if (user == null) {
                    user = userMapper.selectByPrimaryKey(userId);
                    if (user == null) {
                        return user;
                    } else {
                        // 写到Redis
                        redisTemplate.opsForValue().setIfAbsent(key, user, 7L, TimeUnit.DAYS);
                    }
                }
            }
        }
        return user;
    }
}
