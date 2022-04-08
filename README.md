# Redis
1. 基础  
　　3.0是单线程，4.0支持混合持久，多线程异步删除，6.0支持多线程io
redis单线程是指网络的IO与键值的读写是一个线程执行的读取socket->解析请求->处理->写入socket.
3.0单线程快的原因：使用多路复用与非阻塞IO，多路复用监听多个socket连接客户端，这样可以使用一个线程来处理多个请求，避免上下
文的切换，单线程模型避免了加锁阻塞，省去了时间和性能上面的开销。大key删除导致阻塞，引入了异步概念。   
　　6.0的redis通过使用多个线程处理网络IO，单个线程处理主要的工作，既解决了网络IO问题，也保证了不用上下文切换的开销。默认是关
闭多线程的``io-threads-do-reads no`` 可开启  
2. 基础
    - 2.1 mysql与redis一致：先写(update)mysql,从mysql查询出数据再将查询结果写入到redis(先操作mysql再操作redis),查询的时候
    先查询redis,有则直接返回，无则查询mysql;查询mysql无，直接返回null,有则更新redis，保证下一次的缓存命中率。
    ``redis-cli --raw``解决客户端乱码
    - 2.2 借鉴DCL思想，解决缓存击穿问题
        ```$xslt
        public User findUserById2(Integer userId) {
                User user = null;
                //缓存key
                String key = CACHE_KEY_USER + userId;
                //1 查询redis
                user = (User) redisTemplate.opsForValue().get(key);
                //redis无，进一步查询mysql
                if (user == null) {
                    // DCL思想 保证第一个线程进来
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
        ```