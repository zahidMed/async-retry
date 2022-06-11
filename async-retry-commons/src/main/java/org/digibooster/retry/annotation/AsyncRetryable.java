package org.digibooster.retry.annotation;

import java.lang.annotation.*;

/**
 * Annotation for a method invocation that is retryable.
 *
 * @author Mohammed ZAHID {@literal <}zahid.med@gmail.com{@literal >}
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AsyncRetryable {

    /**
     * Retry policy bean name to be applied to the retryable method
     *
     * @return the name of the bean
     */
    String retryPolicy();

    /**
     * Method listener bean name that is called for each execution
     *
     * @return the name of the bean
     */
    String retryListener() default "";


    /**
     * Exception types that are not retryable.
     * @return
     */
    Class<? extends Throwable>[] noRetryFor() default {};

    /**
     * Exception types that are retryable. Default to empty (the retry will be performed for all the exceptions)
     *
     * @return exception types to retry
     */
    Class<? extends Throwable>[] retryFor() default {};
}
