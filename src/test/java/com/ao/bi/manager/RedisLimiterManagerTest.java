package com.ao.bi.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class RedisLimiterManagerTest {

    @Resource
    private RedisLimiterManager redisLimiterManager;

    @Test
    void doRateLimit() throws InterruptedException {
        String userId = "1";
        for (int i = 0; i < 5; i++) {
            try {
                redisLimiterManager.doRateLimit(userId);
                System.out.println("成功");
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        Thread.sleep(1000);
        System.out.println("=================================");
        for (int i = 0; i < 5; i++) {
            redisLimiterManager.doRateLimit(userId);
            System.out.println("成功");
        }
    }
}