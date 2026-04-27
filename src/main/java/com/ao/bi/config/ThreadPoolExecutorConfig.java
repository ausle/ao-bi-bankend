package com.ao.bi.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class ThreadPoolExecutorConfig {
    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        /*
            我们使用线程池是为了解决，智能分析接口，AI处理能力有限，响应慢的问题。
            假如AI服务最多允许4个任务同时执行，建议2个任务。
            那么线程池配置如下：
                corePoolSize核心线程数: 正常情况下需要多少线程：2
                maximumPoolSize最大线程数: 极端情况需要多少线程：4
                keepAliveTime空闲线程存活时间: 1000，这表示空闲线程在没有任务的情况下，多久被删除。

            详细解释一下线程池的工作过程：
                创建threadPool后，线程池中没有线程，任务队列没有任务。
                当有请求过来，就会创建一个线程，执行任务。
                如果核心线程数都在处理任务，这时候再来一个请求，这个新任务就会被放入任务队列中。
                如果任务队列也满了，这时将会新建线程去处理任务，但总的线程数不能超过设置的最大线程。

                如果队列中的任务也满了，所有的线程都在处理任务。如果这时，又有任务过来，则该任务会直接被拒绝掉。
        */
        ArrayBlockingQueue blockingDeque=new ArrayBlockingQueue<>(4);
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                2,
                4,
                5,
                TimeUnit.SECONDS,
                blockingDeque,
                new ThreadFactory() {
                    private int count = 1;
                    @Override
                    public Thread newThread(@NotNull Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setName("线程" + count);
                        count++;
                        return thread;
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        return threadPoolExecutor;
    }


}
