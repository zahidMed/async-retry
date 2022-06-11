package org.digibooster.retry.annotation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;

/**
 * Advisor that activates asynchronous retryable method execution through the @{@link AsyncRetryable} annotation.
 *
 * @author Mohammed ZAHID {@literal <}zahid.med@gmail.com{@literal >}
 */
@Slf4j
@AllArgsConstructor
public class AsyncRetryableAnnotationAdvisor extends AbstractPointcutAdvisor {

    private static final long serialVersionUID = 2927880965700415815L;

    private final Advice advice;

    private final Pointcut pointcut;

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    @Override
    public Advice getAdvice() {
        return advice;
    }
}
