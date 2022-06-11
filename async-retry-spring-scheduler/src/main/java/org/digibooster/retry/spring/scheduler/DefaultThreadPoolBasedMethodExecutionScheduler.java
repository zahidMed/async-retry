package org.digibooster.retry.spring.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.digibooster.retry.manager.AsyncRetryableManager;
import org.digibooster.retry.util.TargetMethodInformation;
import org.digibooster.retry.scheduler.MethodExecutionScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.Date;

/**
 * Implementation of the @{@link MethodExecutionScheduler} based on Spring @{@link ThreadPoolTaskScheduler}
 *
 * @author Mohammed ZAHID {@literal <}zahid.med@gmail.com{@literal >}
 */
@Slf4j
public class DefaultThreadPoolBasedMethodExecutionScheduler implements MethodExecutionScheduler {

    protected ThreadPoolTaskScheduler threadPoolTaskScheduler;

    protected AsyncRetryableManager asyncRetryableManager;


    public void setThreadPoolTaskScheduler(ThreadPoolTaskScheduler threadPoolTaskScheduler) {
        this.threadPoolTaskScheduler = threadPoolTaskScheduler;
    }


    public void setAsyncRetryableManager(AsyncRetryableManager asyncRetryableManager) {
        this.asyncRetryableManager = asyncRetryableManager;
    }


    @Override
    public void schedule(TargetMethodInformation methodInformation, long period) {
        log.trace("Schedule task to be processed in {} ms using ThreadPoolTaskScheduler", period);
        threadPoolTaskScheduler.schedule(
                () -> asyncRetryableManager.process(methodInformation),
                new Date(System.currentTimeMillis() + period)
        );
    }
}
