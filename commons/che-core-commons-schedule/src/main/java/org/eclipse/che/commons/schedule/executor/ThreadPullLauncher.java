/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.commons.schedule.executor;

import org.eclipse.che.commons.schedule.Launcher;
import org.eclipse.che.inject.ConfigurationException;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

/**
 * Execute method marked with @ScheduleCron @ScheduleDelay and @ScheduleRate annotations using
 * CronThreadPoolExecutor.
 *
 * @author Sergii Kabashniuk
 */
@Singleton
public class ThreadPullLauncher implements Launcher {
    private static final Logger LOG = LoggerFactory.getLogger(CronThreadPoolExecutor.class);
    private final CronThreadPoolExecutor service;

    /**
     * @param corePoolSize
     *         the number of threads to keep in the pool, even
     *         if they are idle, unless {@code allowCoreThreadTimeOut} is set
     */
    @Inject
    public ThreadPullLauncher(@Named("schedule.core_pool_size") Integer corePoolSize) {
        this.service = new CronThreadPoolExecutor(corePoolSize,
                                                  new ThreadFactoryBuilder().setNameFormat("Annotated-scheduler-%d").setDaemon(false)
                                                                            .build());
    }


    @PreDestroy
    public void shutdown() throws InterruptedException {
        // Tell threads to finish off.
        service.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!service.awaitTermination(60, TimeUnit.SECONDS)) {
                service.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!service.awaitTermination(60, TimeUnit.SECONDS))
                    LOG.warn("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            service.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }


    @Override
    public void scheduleCron(Runnable runnable, String cron) {
        if (cron == null || cron.isEmpty()) {
            throw new ConfigurationException("Cron parameter can't be null");
        }
        CronExpression expression = new CronExpression(cron);
        service.schedule(runnable, expression);
        LOG.debug("Schedule method {} with cron  {} schedule", runnable, cron);
    }

    @Override
    public void scheduleWithFixedDelay(Runnable runnable, long initialDelay, long delay, TimeUnit unit) {
        service.scheduleWithFixedDelay(runnable, initialDelay, delay, unit);
        LOG.debug("Schedule method {} with fixed initial delay {} delay {} unit {}",
                  runnable,
                  initialDelay,
                  delay, unit);
    }

    @Override
    public void scheduleAtFixedRate(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
        service.scheduleAtFixedRate(runnable, initialDelay, period, unit);
        LOG.debug("Schedule method {} with fixed rate. Initial delay {} period {} unit {}",
                  runnable,
                  initialDelay,
                  period,
                  unit);
    }
}
