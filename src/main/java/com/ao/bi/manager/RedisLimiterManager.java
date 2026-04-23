package com.ao.bi.manager;

import com.ao.bi.common.ErrorCode;
import com.ao.bi.exception.BusinessException;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 专门提供 RedisLimiter 限流基础服务的（提供了通用的能力）
 */
@Service
public class RedisLimiterManager {

    @Resource
    private RedissonClient redissonClient;

    /**
     * 限流操作
     *
     * @param key 区分不同的限流器，比如不同的用户 id 应该分别统计
     */
    public void doRateLimit(String key) {
        // 创建一个限流器。不同名字的限流器互不干扰。限流的设置只针对同一个名字的限流器。
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        // 设置整体的速率，每1秒允许访问2次
        // RateType.OVERALL：全局限流。
        rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);

        // 请求过来后，尝试获取许可，如果获取失败，会阻塞在这里，直到下一个时间开始，在尝试获取许可。
//        rateLimiter.tryAcquire();

        // 请求过来后，尝试获取1个许可。如果获取失败，立刻false，不会阻塞。成功则继续执行。
        boolean canOp = rateLimiter.tryAcquire(1);
        if (!canOp) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }
    }
}
