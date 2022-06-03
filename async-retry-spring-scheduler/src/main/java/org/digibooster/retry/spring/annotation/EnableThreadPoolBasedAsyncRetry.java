package org.digibooster.retry.spring.annotation;

import org.digibooster.retry.spring.config.SpringSchedulerAsyncRetryableConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Global enabler for @{@link org.digibooster.retry.annotation.AsyncRetryable} annotations
 * based on @{@link org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler}.
 * If this is declared on any <code>@Configuration</code> in the context then beans that have
 * retryable methods will be proxied and the retry handled according to the metadata in
 * the annotations.
 *
 * @author Mohammed ZAHID {@literal <}zahid.med@gmail.com{@literal >}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Import(SpringSchedulerAsyncRetryableConfiguration.class)
public @interface EnableThreadPoolBasedAsyncRetry {
}
