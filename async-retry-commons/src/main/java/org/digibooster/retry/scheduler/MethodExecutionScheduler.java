package org.digibooster.retry.scheduler;


import org.digibooster.retry.util.TargetMethodInformation;

/**
 * Interface for the scheduler that schedules the execution of the annotated method represented by @{@link TargetMethodInformation}
 *
 * @author Mohammed ZAHID {@literal <}zahid.med@gmail.com{@literal >}
 */
public interface MethodExecutionScheduler {

    /**
     * Schedules the execution of the annotated method represented by @{@link TargetMethodInformation} in the next period in milliseconds
     * This method should not block the current thread.
     * @param methodInformation the annotated method information
     * @param period the period in milliseconds to wait before invoking the annotated method
     */
    void schedule(TargetMethodInformation methodInformation, long period);
}
