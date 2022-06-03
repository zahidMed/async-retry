package org.digibooster.retry.quartz.config;

import org.digibooster.retry.config.AsyncRetryableCommonConfiguration;
import org.digibooster.retry.quartz.factory.QuartzSchedulerJobFactory;
import org.digibooster.retry.quartz.scheduler.QuartzBasedMethodExecutionScheduler;
import org.digibooster.retry.scheduler.MethodExecutionScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * Abstract class configuration that should be extended in order to to provide quartz @{@link SchedulerFactoryBean}
 * based on the needed quartz configuration: Memory or JDBC
 *
 * @author Mohammed ZAHID {@literal <}zahid.med@gmail.com{@literal >}
 */
public abstract class QuartzBasedAsyncRetryableConfigAdapter extends AsyncRetryableCommonConfiguration{


    @Bean("quartzSchedulerJobFactory")
    public QuartzSchedulerJobFactory quartzSchedulerJobFactory(@Autowired ApplicationContext applicationContext){
        QuartzSchedulerJobFactory jobFactory = new QuartzSchedulerJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean("asyncRetryMethodExecutionScheduler")
    public MethodExecutionScheduler asyncRetryMethodExecutionScheduler(@Autowired @Lazy SchedulerFactoryBean schedulerFactoryBean){
        QuartzBasedMethodExecutionScheduler methodExecutionScheduler= new QuartzBasedMethodExecutionScheduler();
        methodExecutionScheduler.setSchedulerFactoryBean(schedulerFactoryBean);
        return methodExecutionScheduler;
    }
}
