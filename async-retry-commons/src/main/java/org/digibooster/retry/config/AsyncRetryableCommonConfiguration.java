package org.digibooster.retry.config;

import org.digibooster.retry.annotation.AsyncRetryableBeanFactoryAwareAdvisingPostProcessor;
import org.digibooster.retry.annotation.AsyncRetryableInterceptor;
import org.digibooster.retry.manager.DefaultAsyncRetryableManager;
import org.digibooster.retry.manager.AsyncRetryableManager;
import org.digibooster.retry.scheduler.MethodExecutionScheduler;
import org.springframework.aop.framework.autoproxy.AbstractBeanFactoryAwareAdvisingPostProcessor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

/**
 * @author Mohammed ZAHID {@literal <}zahid.med@gmail.com{@literal >}
 */
public class AsyncRetryableCommonConfiguration {

    @Bean
    public AsyncRetryableManager retryManager(@Autowired ApplicationContext applicationContext,
                                              @Autowired @Lazy @Qualifier("asyncRetryMethodExecutionScheduler") MethodExecutionScheduler asyncRetryMethodExecutionScheduler){
    	DefaultAsyncRetryableManager retryManager= new DefaultAsyncRetryableManager();
    	retryManager.setApplicationContext(applicationContext);
    	retryManager.setMethodExecutionScheduler(asyncRetryMethodExecutionScheduler);
    	return retryManager;
    }

    @Bean
    public AbstractBeanFactoryAwareAdvisingPostProcessor retryPointcutAdvisor(@Autowired AsyncRetryableInterceptor taskInterceptor,
                                                                              @Autowired BeanFactory beanFactory){
        AsyncRetryableBeanFactoryAwareAdvisingPostProcessor postProcessor= new AsyncRetryableBeanFactoryAwareAdvisingPostProcessor(taskInterceptor);
        postProcessor.setBeanFactory(beanFactory);
        return  postProcessor;
    }

    @Bean
    public AsyncRetryableInterceptor retryableTaskInterceptor(@Autowired @Lazy @Qualifier("asyncRetryMethodExecutionScheduler") MethodExecutionScheduler asyncRetryMethodExecutionScheduler){
    	AsyncRetryableInterceptor asyncRetryableInterceptor = new AsyncRetryableInterceptor();
    	asyncRetryableInterceptor.setMethodExecutionScheduler(asyncRetryMethodExecutionScheduler);
    	return asyncRetryableInterceptor;
    }
}
