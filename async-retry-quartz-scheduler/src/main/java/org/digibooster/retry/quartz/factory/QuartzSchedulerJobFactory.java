package org.digibooster.retry.quartz.factory;

import lombok.extern.slf4j.Slf4j;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

/**
 * Quartz Job factory bean that creates job instances and interprets spring annotations like @{@link org.springframework.beans.factory.annotation.Autowired}
 * in order to inject bean dependencies
 * @author Mohammed ZAHID {@literal <}zahid.med@gmail.com{@literal >}
 */
@Slf4j
public class QuartzSchedulerJobFactory extends SpringBeanJobFactory implements ApplicationContextAware {

    private AutowireCapableBeanFactory beanFactory;

    @Override
    public void setApplicationContext(final ApplicationContext context) {
        beanFactory = context.getAutowireCapableBeanFactory();
    }

    @Override
    protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
        log.info("Create new quartz Job bean {}",bundle.getJobDetail().getJobClass());
        final Object job = super.createJobInstance(bundle);
        beanFactory.autowireBean(job);
        return job;
    }
}
