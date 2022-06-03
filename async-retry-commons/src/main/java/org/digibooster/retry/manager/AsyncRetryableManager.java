package org.digibooster.retry.manager;

import org.digibooster.retry.util.TargetMethodInformation;

/**
 * The central class for managing asynchronous and retryable request processing.
 * This class has the responsibility of calling the target, firing the listeners, managing exceptions
 * and rescheduling the next execution according the the retry policy
 *
 * @author Mohammed ZAHID {@literal <}zahid.med@gmail.com{@literal >}
 */
public interface AsyncRetryableManager {

    /**
     * invokes the retryable method and reschedule it in case of error that match the retry error policy.
     * @param methodInformation the holder of the target method and retry policy
     */
    void process(TargetMethodInformation methodInformation);
}
