package org.digibooster.retry.manager;


import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.digibooster.retry.listener.AsyncRetryableListener;
import org.digibooster.retry.policy.AsyncRetryableSchedulingPolicy;
import org.digibooster.retry.scheduler.MethodExecutionScheduler;
import org.digibooster.retry.util.TargetMethodInformation;
import org.digibooster.retry.util.GlobalUtils;
import org.digibooster.retry.util.HierarchyCallChecker;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.MethodInvoker;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;

/**
 *  Default implementation of @{@link AsyncRetryableManager}
 * @author Mohammed ZAHID {@literal <}zahid.med@gmail.com{@literal >}
 */
@Slf4j
public class DefaultAsyncRetryableManager implements AsyncRetryableManager {


    @Setter
    protected ApplicationContext applicationContext;

    @Setter
    protected MethodExecutionScheduler methodExecutionScheduler;


	@Override
    public void process(TargetMethodInformation methodInformation){
        log.trace("Process a task");
        Object bean = applicationContext.getBean(methodInformation.getBeanClass());
        Assert.notNull(bean,"No bean found for class: "+methodInformation.getBeanClass());

        MethodInvoker methodInvoker = new MethodInvoker();
        methodInvoker.setTargetObject(bean);
        methodInvoker.setTargetMethod(methodInformation.getName());
        methodInvoker.setArguments(methodInformation.getArgs());
        try {
            methodInvoker.prepare();
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            log.error("Error while preparing method invoker for: {}",methodInformation,e);
            return;
        }

        AsyncRetryableListener listener = null;
        if(StringUtils.hasText(methodInformation.getRetryListener())){
        	listener = applicationContext.getBean(methodInformation.getRetryListener(), AsyncRetryableListener.class);
        }

        HierarchyCallChecker hierarchyCallChecker = HierarchyCallChecker.getInstance();
        if(listener!=null){
            try {
                listener.beforeRetry(methodInformation.getRetryCount(), methodInformation.getArgs());
            }catch (Throwable e){
                log.error("Error while calling before retry listener method",e);
            }
        }
        try {
            hierarchyCallChecker.setCallFlag();
            Object result =methodInvoker.invoke();
            callAfterRetryListener(listener,methodInformation.getRetryCount(),result, methodInformation.getArgs(), null);
        } catch (InvocationTargetException e){
            Throwable throwable=e.getTargetException();
            log.error("Error while executing method {}.{}",methodInformation.getBeanClass(),methodInformation.getName(),throwable);
            callAfterRetryListener(listener,methodInformation.getRetryCount(),null, methodInformation.getArgs(), throwable);

            if(GlobalUtils.instanceOf(throwable,methodInformation.getNoRetryFor())){
                log.trace("The method should not be retried for exception {}",throwable);
                callOnRetryEndListener(listener, methodInformation.getArgs(),throwable);
                return;
            }
            if( !GlobalUtils.isEmpty(methodInformation.getRetryFor()) && !GlobalUtils.instanceOf(throwable,methodInformation.getRetryFor())){
                log.trace("The method should not be retried because the exception {} doesn't belong to the retryFor list",throwable.getClass());
                callOnRetryEndListener(listener, methodInformation.getArgs(),throwable);
                return;
            }

            AsyncRetryableSchedulingPolicy asyncRetryableSchedulingPolicy = applicationContext.getBean(methodInformation.getRetryPolicy(), AsyncRetryableSchedulingPolicy.class);
            methodInformation.incrementRetryCount();
            long delay = asyncRetryableSchedulingPolicy.next(methodInformation.getRetryCount());
            if(delay>0) {
                log.trace("Reschedule method execution {}.{} in {}", methodInformation.getBeanClass(), methodInformation.getName(), delay);
                methodExecutionScheduler.schedule(methodInformation, delay);
            }
            else {
                log.trace("End of retries");
                callOnRetryEndListener(listener, methodInformation.getArgs(),throwable);
            }
        } catch (IllegalAccessException e) {
            log.error("Could not access to the target method {}.{}",methodInformation.getBeanClass(),methodInformation.getName(),e);
        }
        finally {
            hierarchyCallChecker.clear();
        }
    }

    protected void callOnRetryEndListener(AsyncRetryableListener listener, Object[] args, Throwable e){
        if(listener!=null){
            try {
                listener.onRetryEnd(args, e);
            }
            catch (Throwable t){
                log.error("Error while calling after retry listener method",t);
            }
        }
    }

    protected void callAfterRetryListener(AsyncRetryableListener listener, Integer retryCount, Object result, Object[] args, Throwable e){
        if(listener!=null){
            try {
                listener.afterRetry(retryCount,result, args, e);
            }
            catch (Throwable t){
                log.error("Error while calling after retry listener method",t);
            }
        }
    }

}
