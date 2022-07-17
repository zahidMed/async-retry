package org.digibooster.retry.quartz.config;

import org.digibooster.retry.config.AsyncRetryableCommonConfiguration;
import org.digibooster.retry.quartz.factory.QuartzSchedulerJobFactory;
import org.digibooster.retry.quartz.scheduler.DefaultQuartzBasedMethodExecutionScheduler;
import org.digibooster.retry.scheduler.MethodExecutionScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * Abstract class configuration that should be extended in order to to provide quartz @{@link SchedulerFactoryBean}
 * based in memory configuration
 *
 * @author Mohammed ZAHID {@literal <}zahid.med@gmail.com{@literal >}
 */
public abstract class DefaultQuartzBasedAsyncRetryableConfigAdapter extends AsyncRetryableCommonConfiguration{

    @Bean("quartzSchedulerJobFactory")
    public QuartzSchedulerJobFactory quartzSchedulerJobFactory(@Autowired ApplicationContext applicationContext){
        QuartzSchedulerJobFactory jobFactory = new QuartzSchedulerJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean("asyncRetryMethodExecutionScheduler")
    public MethodExecutionScheduler asyncRetryMethodExecutionScheduler(@Autowired @Lazy SchedulerFactoryBean schedulerFactoryBean){
        DefaultQuartzBasedMethodExecutionScheduler methodExecutionScheduler= new DefaultQuartzBasedMethodExecutionScheduler();
        methodExecutionScheduler.setSchedulerFactoryBean(schedulerFactoryBean);
        return methodExecutionScheduler;
    }
}
