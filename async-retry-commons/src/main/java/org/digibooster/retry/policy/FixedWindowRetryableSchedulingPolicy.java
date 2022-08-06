package org.digibooster.retry.policy;

import lombok.AllArgsConstructor;

/**
 * Implementation of retry policy with fixed period
 *
 * @author Mohammed ZAHID {@literal <}zahid.med@gmail.com{@literal >}
 */
public class FixedWindowRetryableSchedulingPolicy implements AsyncRetryableSchedulingPolicy {

    /**
     * Max retry attempts
     */
    private int maxAttempts;

    /**
     * fixed period between each retry in milliseconds
     */
    private long period;

    /**
     * The first execution delay
     */
    private long firstExecutionDelay = 0;

    public FixedWindowRetryableSchedulingPolicy(long firstExecutionDelay, Integer maxAttempts, long period){
        this.firstExecutionDelay = firstExecutionDelay;
        this.maxAttempts=maxAttempts;
        this.period=period;
    }

    public FixedWindowRetryableSchedulingPolicy(Integer maxAttempts, long period){
        this(0,maxAttempts,period);
    }

    @Override
    public long next(Integer currentRetryCount) {
        if(currentRetryCount==0) return firstExecutionDelay;
        return (currentRetryCount>maxAttempts)?-1L: period;
    }
}
