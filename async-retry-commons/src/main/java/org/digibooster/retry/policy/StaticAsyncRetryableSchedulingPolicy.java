package org.digibooster.retry.policy;


import org.springframework.util.Assert;

import java.util.Collections;

/**
 * Implementation of the retry policy according to a defined values
 *
 * @author Mohammed ZAHID {@literal <}zahid.med@gmail.com{@literal >}
 */
public class StaticAsyncRetryableSchedulingPolicy implements AsyncRetryableSchedulingPolicy {

    /**
     * Arrays of periods to wait between the annotated method invocations
     */
    private long[] periods;

    public StaticAsyncRetryableSchedulingPolicy(long[] periods){
        Assert.notEmpty(Collections.singleton(periods),"Period should not be empty");
        this.periods = periods;
    }

    @Override
    public long next(Integer currentRetryCount) {
        return currentRetryCount>=periods.length?-1L:periods[currentRetryCount];
    }
}
