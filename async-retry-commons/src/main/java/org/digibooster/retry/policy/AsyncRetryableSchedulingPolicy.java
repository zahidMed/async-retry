package org.digibooster.retry.policy;

/**
 * Responsible of providing the period of time when the annotated method should be called again in millisecond.
 *
 * @author Mohammed ZAHID {@literal <}zahid.med@gmail.com{@literal >}
 */
public interface AsyncRetryableSchedulingPolicy {

    /**
     * Calculates the period to wait before the next call of the annotated method according to the number of made retries.
     * The first execution should correspond to the currentRetryCount with 0 value
     * @param currentRetryCount the number of times the annotated method was called
     * @return the value of period to wait before invoking the annotated method. negative value means never.
     */
    long next(Integer currentRetryCount);
}
