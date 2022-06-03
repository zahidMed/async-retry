package org.digibooster.retry.quartz;

import lombok.Data;
import org.digibooster.retry.annotation.AsyncRetryable;
import org.digibooster.retry.listener.AsyncRetryableListener;
import org.digibooster.retry.policy.FixedWindowRetryableSchedulingPolicy;
import org.digibooster.retry.quartz.config.QuartzBasedAsyncRetryableConfigAdapter;
import org.digibooster.retry.quartz.factory.QuartzSchedulerJobFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;


@Import({AsyncRetryIT.TestConfig.class})
@RunWith(SpringRunner.class)
public class AsyncRetryIT {


    @Autowired
    RetryableProcess retryableProcess;

    @Autowired
    MyRetryListener myRetryListener;

    @Test
    public void text_retry_with_quartz() {

        retryableProcess.process(1, "test");
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(Integer.valueOf(4), retryableProcess.getCounter());
        Assert.assertEquals(Arrays.asList("test", "test", "test", "test"), retryableProcess.getList());

        Assert.assertEquals(Integer.valueOf(4), myRetryListener.getBeforeRetryCount());
        Assert.assertEquals(Integer.valueOf(4), myRetryListener.getAfterRetryCount());
        Assert.assertEquals(RuntimeException.class,myRetryListener.getException().getClass());

    }


    @Configuration
    static class TestConfig extends QuartzBasedAsyncRetryableConfigAdapter {

        @Bean
        public FixedWindowRetryableSchedulingPolicy simpleRetrySchedulingPolicy() {
            return new FixedWindowRetryableSchedulingPolicy(3, 3000);
        }

        @Bean
        public RetryableProcess retryableProcessClass1() {
            return new RetryableProcess();
        }


        @Bean("asyncRetrySchedulerFactoryBean")
        public SchedulerFactoryBean schedulerFactoryBean(@Autowired QuartzSchedulerJobFactory quartzSchedulerJobFactory) {
            SchedulerFactoryBean factory = new SchedulerFactoryBean();
            factory.setOverwriteExistingJobs(true);

            Properties properties = new Properties();
            properties.put("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
            factory.setQuartzProperties(properties);
            factory.setJobFactory(quartzSchedulerJobFactory);
            return factory;
        }

        @Bean("myRetryListener")
        public MyRetryListener myRetryListener(){
            return  new MyRetryListener();
        }

    }


    @Data
    public static class RetryableProcess {

        private Integer counter = 0;

        List<String> list = new ArrayList<>();

        @AsyncRetryable(retryPolicy = "simpleRetrySchedulingPolicy",retryListener = "myRetryListener")
        public void process(Integer a, String b) {
            counter = counter + a;
            list.add(b);
            throw new RuntimeException();
        }

    }

    @Data
    public static class MyRetryListener implements AsyncRetryableListener {

        private Integer beforeRetryCount = 0;
        private Integer afterRetryCount = 0;
        private Throwable exception;

        @Override
        public void beforeRetry(Integer retryCount, Object[] args) {
            beforeRetryCount++;
        }

        @Override
        public void afterRetry(Integer retryCount, Object result, Object[] args, Throwable e) {
            afterRetryCount++;
        }

        @Override
        public void onRetryEnd(Object[] args, Throwable e) {
            this.exception = e;
        }
    }
}
