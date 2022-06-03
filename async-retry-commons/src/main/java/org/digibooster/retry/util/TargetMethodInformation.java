package org.digibooster.retry.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * Holder of the asynchronous retry information related to the annotated method and the annotation attributes' values.
 *
 * @author Mohammed ZAHID {@literal <}zahid.med@gmail.com{@literal >}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TargetMethodInformation implements Serializable{

    private static final long serialVersionUID = -4308264719613744249L;

    /**
     * The type of the class to which the annotated method belongs
     */
    private Class beanClass;

    /**
     * The annotated method name
     */
    private String name;

    /**
     * Then bean name of the retry listener
     *
     * @see org.digibooster.retry.listener.AsyncRetryableListener
     */
    private String retryListener;

    /**
     * The annotated method argument values
     */
    private Object[] args;

    /**
     * The number of times when the annotated method is called
     */
    private int retryCount=0;

    /**
     * The bean name of the retry policy
     *
     * @see org.digibooster.retry.policy.AsyncRetryableSchedulingPolicy
     */
    private String retryPolicy;

    /**
     *  Exception types that are retryable. Default to empty (the retry will be performed for all the exceptions)
     */
    private Class<? extends Throwable>[] retryFor;

    /**
     * Exception types that are not retryable.
     */
    private Class<? extends Throwable>[] noRetryFor;

    public void incrementRetryCount(){
        retryCount++;
    }

}
