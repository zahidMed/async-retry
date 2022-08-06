package org.digibooster.retry.policy;

import lombok.Data;

/**
 * Implementation of the retry policy with period that increase linearly
 *
 * @author Mohammed ZAHID {@literal <}zahid.med@gmail.com{@literal >}
 */
@Data
public class LinearAsyncRetryableSchedulingPolicy implements AsyncRetryableSchedulingPolicy {

    public LinearAsyncRetryableSchedulingPolicy(Integer maxAttempts, long period){
        this(maxAttempts,period,2.0);
    }

    public LinearAsyncRetryableSchedulingPolicy(Integer maxAttempts, long period, double backoffCoefficient){
        this(0,maxAttempts,period,backoffCoefficient);
    }

    public LinearAsyncRetryableSchedulingPolicy(long firstExecutionDelay, Integer maxAttempts, long period, double backoffCoefficient){
        this.firstExecutionDelay = firstExecutionDelay;
        this.maxAttempts=maxAttempts;
        this.period = period;
        this.backoffCoefficient = backoffCoefficient;
    }

    /**
     * Max retry attempts
     */
    private Integer maxAttempts;

    /**
     * fixed period between each retry in milliseconds
     */
    private long period;

    /**
     * Coefficient to use for exponential retry policy.
     * <p>
     * The retry interval will be multiplied by this coefficient after each
     * subsequent failure.  Default is 2.0.
     */
    private double backoffCoefficient;

    /**
     * The first execution delay
     */
    private long firstExecutionDelay = 0;

    @Override
    public long next(Integer currentRetryCount) {
        if(currentRetryCount==0) return firstExecutionDelay;
        return (currentRetryCount>maxAttempts)?-1L: (long)(currentRetryCount*period*backoffCoefficient);
    }
}
