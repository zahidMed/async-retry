package org.digibooster.retry.policy;

import lombok.AllArgsConstructor;

/**
 * Implementation of retry policy with fixed period
 *
 * @author Mohammed ZAHID {@literal <}zahid.med@gmail.com{@literal >}
 */
@AllArgsConstructor
public class FixedWindowRetryableSchedulingPolicy implements AsyncRetryableSchedulingPolicy {

    /**
     * Max retry attempts
     */
    private Integer maxAttempts;

    /**
     * fixed period between each retry in milliseconds
     */
    private long period;

    @Override
    public long next(Integer currentRetryCount) {
        if(currentRetryCount==0) return 0;
        return (currentRetryCount>maxAttempts)?-1L: period;
    }
}
