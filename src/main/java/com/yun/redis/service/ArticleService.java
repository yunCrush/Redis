package com.yun.redis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Author: yunCrush
 * Date:2022/4/9 21:18
 * Description:
 */
@Service
@Slf4j
public class ArticleService {
    public static final String ARTICLE = "article:";
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public void likeArticle(String articleId) {
        String key = ARTICLE + articleId;
        // QPS高的大厂不建议这样做，最多10W，容易打包CPU,优化：添加>=10W 不操作，降温处理
        Long likeNumber = Long.valueOf(stringRedisTemplate.opsForValue().get(key));
        if (likeNumber >= 10 * 10000) {
            //不操作
        } else {
            likeNumber = stringRedisTemplate.opsForValue().increment(key);
        }
        log.info("文章编号:{},喜欢数:{}", key, likeNumber);
    }
}
