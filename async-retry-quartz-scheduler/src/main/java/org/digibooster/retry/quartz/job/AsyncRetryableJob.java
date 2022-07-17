package org.digibooster.retry.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.digibooster.retry.manager.AsyncRetryableManager;
import org.digibooster.retry.quartz.scheduler.DefaultQuartzBasedMethodExecutionScheduler;
import org.digibooster.retry.util.TargetMethodInformation;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Quartz job that invokes the annotated method using the @{@link AsyncRetryableManager}
 *
 * @author Mohammed ZAHID {@literal <}zahid.med@gmail.com{@literal >}
 */
@Slf4j
public class AsyncRetryableJob implements Job {

    @Autowired
    protected AsyncRetryableManager asyncRetryableManager;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.trace("Start quartz job for async-retryable");
        TargetMethodInformation methodInformation = (TargetMethodInformation) jobExecutionContext.getMergedJobDataMap().get(DefaultQuartzBasedMethodExecutionScheduler.JOB_MI_PARAM);
        asyncRetryableManager.process(methodInformation);
    }
}
