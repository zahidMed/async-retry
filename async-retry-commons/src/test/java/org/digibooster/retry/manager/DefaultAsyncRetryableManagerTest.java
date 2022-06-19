package org.digibooster.retry.manager;


import lombok.Data;
import org.digibooster.retry.policy.FixedWindowRetryableSchedulingPolicy;
import org.digibooster.retry.scheduler.MethodExecutionScheduler;
import org.digibooster.retry.util.TargetMethodInformation;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author Mohammed ZAHID {@literal <}zahid.med@gmail.com{@literal >}
 */
@RunWith(SpringRunner.class)
@Import(DefaultAsyncRetryableManagerTest.TestConfig.class)
public class DefaultAsyncRetryableManagerTest {


    @Autowired
    AsyncRetryableManager asyncRetryableManager;

    @Autowired
    RetryableTestClass retryableTestClass;

    @Autowired
    MethodScheduler methodScheduler;

    @Configuration
    static class TestConfig {

        @Bean
        public MethodExecutionScheduler methodExecutionScheduler() {
            return new MethodScheduler();
        }

        @Bean
        public AsyncRetryableManager asyncRetryableManager(@Autowired ApplicationContext applicationContext,
                                                           @Autowired MethodExecutionScheduler taskScheduler){
            DefaultAsyncRetryableManager manager= new DefaultAsyncRetryableManager();
            manager.setApplicationContext(applicationContext);
            manager.setMethodExecutionScheduler(taskScheduler);
            return manager;
        }

        @Bean
        public FixedWindowRetryableSchedulingPolicy simpleRetrySchedulingPolicy() {
            return new FixedWindowRetryableSchedulingPolicy(3,5000);
        }

        @Bean
        public RetryableTestClass retryableTestClass(){
            return new RetryableTestClass();
        }

    }

    @Test
    public void test_process_simple_call(){

        methodScheduler.init();

        TargetMethodInformation methodInformation= TargetMethodInformation
                .builder()
                .beanClass(RetryableTestClass.class)
                .retryPolicy("simpleRetrySchedulingPolicy")
                .retryListener(null)
                .name("simpleMethod")
                .args(new Object[]{"test",1})
                .retryCount(0)
                .retryFor(null)
                .noRetryFor(null)
                .build();

        asyncRetryableManager.process(methodInformation);
        Assert.assertEquals(Integer.valueOf(1),retryableTestClass.getSimpleMethodCounter());
    }

    @Test
    public void test_process_with_exception(){

        methodScheduler.init();

        TargetMethodInformation methodInformation= TargetMethodInformation
                .builder()
                .beanClass(RetryableTestClass.class)
                .retryPolicy("simpleRetrySchedulingPolicy")
                .retryListener(null)
                .name("exceptionMethod")
                .args(new Object[]{"test",1})
                .retryCount(0)
                .retryFor(null)
                .noRetryFor(null)
                .build();

        asyncRetryableManager.process(methodInformation);
        Assert.assertEquals(Integer.valueOf(1),Integer.valueOf(methodScheduler.getLast().getRetryCount()));
    }

    @Test
    public void test_process_with_non_retryable_exception(){

        methodScheduler.init();

        TargetMethodInformation methodInformation= TargetMethodInformation
                .builder()
                .beanClass(RetryableTestClass.class)
                .retryPolicy("simpleRetrySchedulingPolicy")
                .retryListener(null)
                .name("exceptionMethod")
                .args(new Object[]{"test",1})
                .retryCount(0)
                .retryFor(null)
                .noRetryFor(new Class[]{IOException.class})
                .build();

        asyncRetryableManager.process(methodInformation);
        Assert.assertNull(methodScheduler.getLast());
    }

    @Test
    public void test_process_with_retryable_exception(){

        methodScheduler.init();

        TargetMethodInformation methodInformation= TargetMethodInformation
                .builder()
                .beanClass(RetryableTestClass.class)
                .retryPolicy("simpleRetrySchedulingPolicy")
                .retryListener(null)
                .name("exceptionMethod")
                .args(new Object[]{"test",1})
                .retryCount(0)
                .retryFor(new Class[]{IOException.class})
                .build();

        asyncRetryableManager.process(methodInformation);
        Assert.assertNotNull(methodScheduler.getLast());
    }

    @Test
    public void test_process_with_max_retry_exceeded(){

        methodScheduler.init();

        TargetMethodInformation methodInformation= TargetMethodInformation
                .builder()
                .beanClass(RetryableTestClass.class)
                .retryPolicy("simpleRetrySchedulingPolicy")
                .retryListener(null)
                .name("exceptionMethod")
                .args(new Object[]{"test",1})
                .retryCount(3)
                .build();

        asyncRetryableManager.process(methodInformation);
        Assert.assertNull(methodScheduler.getLast());
    }

    @Data
    public static class RetryableTestClass{

        private Integer simpleMethodCounter=0;

        public void simpleMethod(String a, Integer b){
            simpleMethodCounter=simpleMethodCounter+1;
        }

        public void exceptionMethod(String a, Integer b) throws FileNotFoundException {
            throw new FileNotFoundException();
        }
    }

    public static class MethodScheduler implements MethodExecutionScheduler{

        public TargetMethodInformation getLast() {
            return last;
        }

        private TargetMethodInformation last;

        public void init(){
            this.last=null;
        }

        @Override
        public void schedule(TargetMethodInformation methodInformation, long period) {
            this.last=methodInformation;
        }
    };
}
