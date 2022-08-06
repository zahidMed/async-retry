package org.digibooster.retry.annotation;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.digibooster.retry.manager.AsyncRetryableManager;
import org.digibooster.retry.policy.AsyncRetryableSchedulingPolicy;
import org.digibooster.retry.scheduler.MethodExecutionScheduler;
import org.digibooster.retry.util.TargetMethodInformation;
import org.digibooster.retry.util.HierarchyCallChecker;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;

import java.io.Serializable;


/**
 * AOP Alliance {@link MethodInterceptor} that processes method invocations asynchronously using the given method scheduler.
 * This interceptor is invoked once when calling the annotated method. Since then the execution and retries are performed
 * by the @{@link MethodExecutionScheduler} and @{@link AsyncRetryableManager}.
 *
 * @author Mohammed ZAHID {@literal <}zahid.med@gmail.com{@literal >}
 */
@Slf4j
public class AsyncRetryableInterceptor implements MethodInterceptor, Serializable {
    private static final long serialVersionUID = -4423476148929355960L;

    @Setter
    protected MethodExecutionScheduler methodExecutionScheduler;

    @Setter
    protected ApplicationContext applicationContext;


    /**
     * Extracts information about the intercepted method and schedules it's execution immediately (first time execution).
     * This method check first if it's not invoked by {@link AsyncRetryableManager} in order to avoid a scheduling infinite loop
     * @param invocation the method invocation to schedule
     * @return returns the invoked method result
     * @throws Throwable an exception
     */
	@Nullable
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        if(HierarchyCallChecker.getInstance().checkFlagExists()){
            log.trace("This retryable method is already called by the task manager, do not call scheduler to avoid scheduling loop");
            return invocation.proceed();
        }
        AsyncRetryable annotation= AnnotatedElementUtils.findMergedAnnotation(invocation.getMethod(), AsyncRetryable.class);

        Object[] argument= invocation.getArguments();

        TargetMethodInformation methodInformation= TargetMethodInformation
                .builder()
                .beanClass(invocation.getThis().getClass())
                .retryPolicy(annotation.retryPolicy())
                .retryListener(annotation.retryListener())
                .name(invocation.getMethod().getName())
                .args(argument)
                .retryCount(0)
                .retryFor(annotation.retryFor())
                .noRetryFor(annotation.noRetryFor())
                .build();

        AsyncRetryableSchedulingPolicy asyncRetryableSchedulingPolicy = applicationContext.getBean(methodInformation.getRetryPolicy(), AsyncRetryableSchedulingPolicy.class);
        long firstExecutionDelay=asyncRetryableSchedulingPolicy.next(0);
        //execute immediately for the first time
        methodExecutionScheduler.schedule(methodInformation,firstExecutionDelay);
        return null;
    }


}
