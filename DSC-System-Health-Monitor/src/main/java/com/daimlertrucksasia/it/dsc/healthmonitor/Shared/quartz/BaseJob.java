package com.daimlertrucksasia.it.dsc.healthmonitor.Shared.quartz;



import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Base Quartz job that executes {@link #process} inside a Spring-managed
 * transaction and with full access to the application context.
 *
 * Sub‑classes simply implement {@link #process} and can {@code @Autowired}
 * any Spring bean as usual.
 */
public abstract class BaseJob implements Job {

    @Autowired                       // injected automatically by Spring
    private ApplicationContext ctx;

    @Autowired
    private PlatformTransactionManager txManager;

    @Override
    public final void execute(JobExecutionContext context) throws JobExecutionException {
        TransactionTemplate tx = new TransactionTemplate(txManager);

        try {
            tx.execute(status -> {          // ➊ transactional boundary
                process(context);           // ➋ your job logic
                return null;
            });
        } catch (Exception ex) {
            // Quartz wants every failure wrapped in JobExecutionException
            throw new JobExecutionException(ex);
        }
    }

    /**
     * Implement your job’s logic here.
     * This method runs inside a Spring transaction.
     */
    protected abstract void process(JobExecutionContext context);
}

