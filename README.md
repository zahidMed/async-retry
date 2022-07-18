# Welcom to async-retry

[![maven.repo](https://mvnrepository.com/assets/images/392dffac024b9632664e6f2c0cac6fe5-logo.png)](https://mvnrepository.com/artifact/org.digibooster.retryable/async-retry)

This project aims to provide a declarative non-blocking retry support for methods in Spring applications using annotations.

The retry processing has two main implementations:
- **Thread pool task based implementation**: Similar to the combination of `@Async` and `@Retryable`, this implementation is based on `ThreadPoolTaskExecutor` without keeping the task executor thread busy during the whole retry processing.
- **Quartz job based implementation**: This implementation is based on quartz library, it can provide clustering, load-balancing, failover and persistence if configured with JDBC-JobStore. So even if one node in the cluster fail, the others can perform the retries.

## Common functions

This section provides a quick introduction to getting started with Async Retry.

### @AsyncRetryable annotation use
The following example shows how to use `@AsyncRetryable` in its declarative style:
```java
@Bean
public class Service {
    
    @AsyncRetryable(retryPolicy = "fixedWindowRetryableSchedulingPolicy", retryFor = IOException.class, noRetryFor={ArithmeticException.class}, retryListener = "retryListener")
    public void method(String arg1, Object arg2){
        // ...do something
    }
}
```
This example calls the service method and, if it fails with a `IOException`, retries according to the retry policy. If the method fails with a `ArithmeticException`, no retry will be made.

All the events that happened during the method call are reported to the retry listener.
### Retry Policy
The retry policy allows defining the next execution time for each failing retry attempt. Indeed, when the annotated method throw a retryable exception, the retry policy bean is called in order to get the period to wait before called the method again.

This library provides three basic implementations:
- `FixedWindowRetryableSchedulingPolicy`: This policy is used for retry with fixed period.
- `StaticAsyncRetryableSchedulingPolicy`: This policy accepts an array of execution periods, and the retry will be performed according to those values
- `LinearAsyncRetryableSchedulingPolicy`: This policy multiplies each time the previous waiting period by a coefficient in order to increase the duration of the next one. The coefficient default value is 2.

The following example shows how to configure a `FixedWindowRetryableSchedulingPolicy` that will execute the annotated method for the first time in 10 seconds, then retries it 3 times max within a period of 20 seconds each.
```java
@Configuration
public class ConfigurationClass {
    
    ...

    @Bean
    public FixedWindowRetryableSchedulingPolicy fixedWindowRetryableSchedulingPolicy() {
        return new FixedWindowRetryableSchedulingPolicy(10000,3,20000);
    }
}
```
**Remark**: You can customize your own retry policy by implementing the interface `AsyncRetryableSchedulingPolicy` 
### Retry Listener
The retry listener is used to detect events during the retry processing life cycle.
The `AsyncRetryableListener` interface is defined as bellow:
```java
public interface AsyncRetryableListener<T> {

	/**
	 * Called before each retry attempt including the first direct call of the target method.
	 * @param retryCount the number of retries. 0 means the first direct call
	 * @param args the target method arguments
	 */
	void beforeRetry(Integer retryCount, Object[] args);

	/**
	 * Called after each retry attempt including the first direct call of the target method.
	 * @param retryCount the number of retries. 0 means the first direct call
	 * @param result the Object returned by the target method if no exception is thrown
	 * @param args the target method arguments
	 * @param e the exception if thrown
	 */
	void afterRetry(Integer retryCount,T result, Object[] args, Throwable e);

	/**
	 * Called when the retry reaches the max attempt count
	 * @param args the target method arguments
	 * @param e the exception if thrown
	 */
	void onRetryEnd(Object[] args, Throwable e);

}
```
The `beforeRetry` and `afterRetry` methods are invoked respectively before and after the call of the annotated method, and `onRetryEnd` is invoked at the end of retry process.

The methods defined above are called nether the annotated method succeeds or fails.

The current retry count can be obtained from the parameter `retryCount`, the annotated method arguments from the parameter `args` in the same order.

The afterRetry method may receive the annotated method returned value in case of success.

The afterRetry and onRetryEnd methods may receive a `Throwable` if a retryable exception is thrown.

## Usage of Thread Pool Task based implementation
In order to use the asynchronous retry feature based on Spring Thread pool task scheduler, all you have to do is to add the following dependency:

```xml
        <dependency>
            <artifactId>async-retry-spring-scheduler</artifactId>
            <groupId>org.digibooster.retryable</groupId>
            <version>1.0.2</version>
        </dependency>
```

and add the annotation `EnableThreadPoolBasedAsyncRetry` to the configuration class

```java
@Configuration
@EnableThreadPoolBasedAsyncRetry
public class ConfigurationClass {
    
    ...

    @Bean
    public FixedWindowRetryableSchedulingPolicy fixedWindowRetryableSchedulingPolicy() {
        return new FixedWindowRetryableSchedulingPolicy(10000,3,20000);
    }
}
```

## Usage of quartz based implementation
The quartz implementation can be configured using two ways (for further information see quartz documentation):
### In memory quartz configuration
This configuration use the RAM in order to store the retries. This configuration is not persistent and not compliant with clustering mode, so retries will be lost if the server restarts and there is not failover support.

In order to user this implementation, add the following dependencies

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-quartz</artifactId>
        </dependency>
        <dependency>
            <groupId>org.quartz-scheduler</groupId>
            <artifactId>quartz</artifactId>
        </dependency>
        <dependency>
            <artifactId>async-retry-quartz-scheduler</artifactId>
            <groupId>org.digibooster.retryable</groupId>
            <version>1.0.2</version>
        </dependency>
```
extend the configuration class `DefaultQuartzBasedAsyncRetryableConfigAdapter`

```java
@Configuration
public class ConfigurationClass extends DefaultQuartzBasedAsyncRetryableConfigAdapter{

    @Bean
    public FixedWindowRetryableSchedulingPolicy fixedWindowRetryableSchedulingPolicy() {
        return new FixedWindowRetryableSchedulingPolicy(10000,3,20000);
    }

    @Bean("schedulerFactoryBean")
    public SchedulerFactoryBean schedulerFactoryBean(@Autowired QuartzSchedulerJobFactory quartzSchedulerJobFactory,
                                                     @Autowired QuartzProperties quartzProperties) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        Properties properties = new Properties();
        properties.putAll(quartzProperties.getProperties());
        factory.setQuartzProperties(properties);
        factory.setJobFactory(quartzSchedulerJobFactory);
        return factory;
    }

}
```
and add the following configuration to the application.yml file

```yaml
spring:
  quartz:
    auto-startup: true
    job-store-type: memory
```
### JDBC quartz configuration
This configuration use the database in order to store the retries. It is persistent and compliant with clustering mode, so retries will not be lost if the server restarts and it supports failover.

In order to user this implementation, add the following dependencies

```xml
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-tx</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-quartz</artifactId>
        </dependency>
        <dependency>
            <groupId>org.quartz-scheduler</groupId>
            <artifactId>quartz</artifactId>
        </dependency>
        <dependency>
            <artifactId>async-retry-quartz-scheduler</artifactId>
            <groupId>org.digibooster.retryable</groupId>
            <version>1.0.2</version>
        </dependency>
```
extend the configuration class `QuartzDBBasedAsyncRetryableConfigAdapter`

```java
@Configuration
public class ConfigurationClass extends QuartzDBBasedAsyncRetryableConfigAdapter {

    @Autowired
    PlatformTransactionManager transactionManager;

    @Override
    public PlatformTransactionManager getTransactionManager() {
        return transactionManager;
    }

    @Bean
    public FixedWindowRetryableSchedulingPolicy fixedWindowRetryableSchedulingPolicy() {
        return new FixedWindowRetryableSchedulingPolicy(10000,3,20000);
    }

    @Bean("schedulerFactoryBean")
    public SchedulerFactoryBean schedulerFactoryBean(@Autowired QuartzSchedulerJobFactory quartzSchedulerJobFactory,
                                                     @Autowired QuartzProperties quartzProperties,
                                                     @Autowired DataSource dataSource) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        Properties properties = new Properties();
        properties.putAll(quartzProperties.getProperties());
        factory.setQuartzProperties(properties);
        factory.setJobFactory(quartzSchedulerJobFactory);
        factory.setDataSource(dataSource);
        return factory;
    }

}
```
and add the following configuration to the application.yml file

```yaml
spring:
  quartz:
    auto-startup: true
    job-store-type: jdbc
    properties:
      org.quartz.jobStore.isClustered: true
      org.quartz.scheduler.instanceName: RetryInstance # optional
      org.quartz.scheduler.instanceId: AUTO # optional
    jdbc:
      initialize-schema: always # optional
```

**Remark**: when using quartz with database, the retry will be executed with a delay due to quartz implementation. To decrease the delay you can decrease the value of the property `org.quartz.jobStore.clusterCheckinInterval` see [quartz documentation](http://www.quartz-scheduler.org/documentation/2.4.0-SNAPSHOT/configuration.html)