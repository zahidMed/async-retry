package org.digibooster.retry.quartz.config;

import org.digibooster.retry.config.AsyncRetryableCommonConfiguration;
import org.digibooster.retry.quartz.factory.QuartzSchedulerJobFactory;
import org.digibooster.retry.quartz.scheduler.QuartzDbBasedMethodExecutionScheduler;
import org.digibooster.retry.scheduler.MethodExecutionScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Abstract class configuration that should be extended in order to to provide quartz @{@link SchedulerFactoryBean}
 * based on the JDBC configuration
 *
 * @author Mohammed ZAHID {@literal <}zahid.med@gmail.com{@literal >}
 */
public abstract class QuartzDBBasedAsyncRetryableConfigAdapter extends AsyncRetryableCommonConfiguration{

    //use SpringBeanJobFactory instead
    @Bean("quartzSchedulerJobFactory")
    public QuartzSchedulerJobFactory quartzSchedulerJobFactory(@Autowired ApplicationContext applicationContext){
        QuartzSchedulerJobFactory jobFactory = new QuartzSchedulerJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean("asyncRetryMethodExecutionScheduler")
    public MethodExecutionScheduler asyncRetryMethodExecutionScheduler(@Autowired @Lazy SchedulerFactoryBean schedulerFactoryBean){
        QuartzDbBasedMethodExecutionScheduler methodExecutionScheduler= new QuartzDbBasedMethodExecutionScheduler();
        methodExecutionScheduler.setSchedulerFactoryBean(schedulerFactoryBean);
        methodExecutionScheduler.setTransactionManager(getTransactionManager());
        return methodExecutionScheduler;
    }

    public abstract PlatformTransactionManager getTransactionManager();
}
