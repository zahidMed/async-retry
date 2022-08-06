package org.digibooster.retry.quartz.scheduler;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.digibooster.retry.quartz.job.AsyncRetryableJob;
import org.digibooster.retry.scheduler.MethodExecutionScheduler;
import org.digibooster.retry.util.TargetMethodInformation;
import org.quartz.*;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Date;
import java.util.UUID;

/**
 * Implementation of @{@link MethodExecutionScheduler} based on quartz library
 *
 * @author Mohammed ZAHID {@literal <}zahid.med@gmail.com{@literal >}
 */
@Slf4j
public class DefaultQuartzBasedMethodExecutionScheduler implements MethodExecutionScheduler {

    public static final String JOB_GROUP = "ASYNC_RETRY_GROUP";
    public static final String JOB_MI_PARAM = "TARGET_METHOD_INFO";

    @Setter
    protected SchedulerFactoryBean schedulerFactoryBean;

    /**
     * Schedules the job @{@link AsyncRetryableJob} with the annotated method information as parameter in the next period (milliseconds)
     *
     * @param methodInformation the annotated method information
     * @param period            the period in milliseconds to wait before invoking the annotated method
     */
    @Override
    public void schedule(TargetMethodInformation methodInformation, long period) {
        log.trace("Schedule method execution {} in {}", methodInformation, period);
        String jobId = UUID.randomUUID().toString();
        JobDataMap dataMap = new JobDataMap();
        dataMap.put(JOB_MI_PARAM, methodInformation);
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobId, JOB_GROUP)
                .usingJobData(dataMap)
                .startAt(new Date(System.currentTimeMillis()+ period))
                .build();
        JobDetail jobDetail = JobBuilder.newJob(AsyncRetryableJob.class)
                .withIdentity(new JobKey(jobId, JOB_GROUP))
                .build();
        schedule(jobDetail, trigger);

    }

    protected void schedule(JobDetail jobDetail, Trigger trigger) {
        try {
            schedulerFactoryBean.getScheduler().scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            log.error("Could not schedule async-retryable job", e);
        }
    }
}
