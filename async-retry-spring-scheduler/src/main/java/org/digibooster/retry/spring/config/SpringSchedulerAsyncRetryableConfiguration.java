package org.digibooster.retry.spring.config;

import org.digibooster.retry.config.AsyncRetryableCommonConfiguration;
import org.digibooster.retry.manager.AsyncRetryableManager;
import org.digibooster.retry.scheduler.MethodExecutionScheduler;
import org.digibooster.retry.spring.scheduler.DefaultThreadPoolBasedMethodExecutionScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * Configuration class of async retry based on @{@link ThreadPoolTaskScheduler}
 *
 * @author Mohammed ZAHID {@literal <}zahid.med@gmail.com{@literal >}
 */
@Configuration
public class SpringSchedulerAsyncRetryableConfiguration extends AsyncRetryableCommonConfiguration{

    @Bean
    public ThreadPoolTaskScheduler asyncRetryableTaskExecutor(){
        ThreadPoolTaskScheduler threadPoolTaskScheduler
                = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(10);
        threadPoolTaskScheduler.setThreadNamePrefix(
                "RetryableThreadPoolTaskScheduler");
        return threadPoolTaskScheduler;
    }

    @Bean("asyncRetryMethodExecutionScheduler")
    public MethodExecutionScheduler asyncRetryMethodExecutionScheduler(@Autowired ThreadPoolTaskScheduler asyncRetryableTaskExecutor,
                                                  @Lazy @Autowired AsyncRetryableManager asyncRetryableManager){
    	DefaultThreadPoolBasedMethodExecutionScheduler taskScheduler= new DefaultThreadPoolBasedMethodExecutionScheduler();
    	taskScheduler.setAsyncRetryableManager(asyncRetryableManager);
    	taskScheduler.setThreadPoolTaskScheduler(asyncRetryableTaskExecutor);
    	return taskScheduler;
    }
}
