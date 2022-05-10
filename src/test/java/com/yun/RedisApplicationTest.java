package com.yun;

import com.yun.pojo.SimpleBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * Author: yunCrush
 * Date:2022/4/13 21:15
 * Description:
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisApplicationTest {

    @Autowired
    private SimpleBean simpleBean;
    @Test
     public void contextLoads() {
        System.out.println(simpleBean);
        System.out.println("id: "+simpleBean.getId());
        System.out.println("name: "+simpleBean.getName());
    }
}