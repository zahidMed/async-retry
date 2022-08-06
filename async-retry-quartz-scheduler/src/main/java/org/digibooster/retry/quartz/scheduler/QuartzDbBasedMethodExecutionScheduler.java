package org.digibooster.retry.quartz.scheduler;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.digibooster.retry.scheduler.MethodExecutionScheduler;
import org.quartz.*;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Implementation of @{@link MethodExecutionScheduler} based on quartz library with database configuration.
 * This class creates a new transaction in order to store the job trigger in order to avoid the transaction
 * roll-back in case of failure inside tha annotated method
 *
 * @author Mohammed ZAHID {@literal <}zahid.med@gmail.com{@literal >}
 */
@Slf4j
public class QuartzDbBasedMethodExecutionScheduler extends DefaultQuartzBasedMethodExecutionScheduler {

    @Setter
    protected PlatformTransactionManager transactionManager;


    @Override
    protected void schedule(JobDetail jobDetail,Trigger trigger){
        DefaultTransactionDefinition transactionDefinition= new DefaultTransactionDefinition();
        transactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus transactionStatus=null;
        TransactionTemplate transactionTemplate= new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        try {
            transactionStatus= transactionManager.getTransaction(transactionDefinition);
            schedulerFactoryBean.getScheduler().scheduleJob(jobDetail, trigger);
            transactionManager.commit(transactionStatus);
        } catch (Exception e) {
            log.error("Could not schedule async-retryable job",e);
            if(transactionStatus!=null){
                transactionManager.rollback(transactionStatus);
            }
        }

    }
}
