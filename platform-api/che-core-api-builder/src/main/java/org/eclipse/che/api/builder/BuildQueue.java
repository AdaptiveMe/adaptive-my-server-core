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
package org.eclipse.che.api.builder;

import org.eclipse.che.api.builder.dto.BuildOptions;
import org.eclipse.che.api.builder.dto.BuilderServerLocation;
import org.eclipse.che.api.builder.dto.BuilderServerRegistration;
import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.rest.ServiceContext;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

/**
 * Created by panthro on 21/08/15.
 */
public interface BuildQueue {
    /**
     * Get total size of queue of tasks.
     *
     * @return total size of queue of tasks
     */
    int getTotalNum();

    /**
     * Get number of tasks which are waiting for processing.
     *
     * @return number of tasks which are waiting for processing
     */
    int getWaitingNum();

    List<RemoteBuilderServer> getRegisterBuilderServers();

    /**
     * Register remote SlaveBuildService which can process builds.
     *
     * @param registration
     *         BuilderServerRegistration
     * @return {@code true} if set of available Builders changed as result of the call
     * @throws BuilderException
     *         if an error occurs
     */
    boolean registerBuilderServer(BuilderServerRegistration registration) throws BuilderException;

    /**
     * Unregister remote SlaveBuildService.
     *
     * @param location
     *         BuilderServerLocation
     * @return {@code true} if set of available Builders changed as result of the call
     * @throws BuilderException
     *         if an error occurs
     */
    boolean unregisterBuilderServer(BuilderServerLocation location) throws BuilderException;

    /**
     * Schedule new build.
     *
     * @param wsId
     *         id of workspace to which project belongs
     * @param project
     *         name of project
     * @param serviceContext
     *         ServiceContext
     * @return BuildQueueTask
     */
    BuildQueueTask scheduleBuild(String wsId, String project, ServiceContext serviceContext, BuildOptions buildOptions)
            throws BuilderException;

    /**
     * Schedule new dependencies analyze.
     *
     * @param wsId
     *         id of workspace to which project belongs
     * @param project
     *         name of project
     * @param type
     *         type of analyze dependencies. Depends to implementation of slave-builder.
     * @param serviceContext
     *         ServiceContext
     * @param buildOptions
     * @return BuildQueueTask
     */
    BuildQueueTask scheduleDependenciesAnalyze(String wsId, String project, String type, ServiceContext serviceContext,
                                               BuildOptions buildOptions)
            throws BuilderException;

    /**
     * Return tasks of this queue.
     */
    List<BuildQueueTask> getTasks();

    BuildQueueTask getTask(Long id) throws NotFoundException;

    @PostConstruct
    void start();

    @PreDestroy
    void stop();
}
