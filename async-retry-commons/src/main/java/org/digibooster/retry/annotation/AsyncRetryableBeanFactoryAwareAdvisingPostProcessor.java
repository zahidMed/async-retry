package org.digibooster.retry.annotation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.framework.autoproxy.AbstractBeanFactoryAwareAdvisingPostProcessor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.BeanFactory;


/**
 * Bean post-processor that applies automatically asynchronous and retryable invocation behavior
 * to any bean that carries @{@link AsyncRetryable} annotation at method level by adding @{@link AsyncRetryableAnnotationAdvisor}
 * to the exposed proxy.
 *
 * @author Mohammed ZAHID {@literal <}zahid.med@gmail.com{@literal >}
 */
@Slf4j
@AllArgsConstructor
public class AsyncRetryableBeanFactoryAwareAdvisingPostProcessor extends AbstractBeanFactoryAwareAdvisingPostProcessor {
    private static final long serialVersionUID = -6127378705200901143L;

    protected final MethodInterceptor retryableTaskInterceptor;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        super.setBeanFactory(beanFactory);
        this.advisor = new AsyncRetryableAnnotationAdvisor(retryableTaskInterceptor, new AnnotationMatchingPointcut(null, AsyncRetryable.class,true));
    }

}
