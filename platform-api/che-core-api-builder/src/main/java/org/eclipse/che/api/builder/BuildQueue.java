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
import org.eclipse.che.api.builder.dto.BuildTaskDescriptor;
import org.eclipse.che.api.builder.dto.BuilderServerLocation;
import org.eclipse.che.api.builder.dto.BuilderServerRegistration;
import org.eclipse.che.api.core.ForbiddenException;
import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.rest.ServiceContext;

import javax.ws.rs.core.Response;
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
     * @return BuildTaskDescriptor
     */
    BuildTaskDescriptor scheduleBuild(String wsId, String project, ServiceContext serviceContext, BuildOptions buildOptions)
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
    BuildTaskDescriptor scheduleDependenciesAnalyze(String wsId, String project, String type, ServiceContext serviceContext,
                                                    BuildOptions buildOptions)
            throws BuilderException;


    /**
     * Get all tasks for a single project in a workspace
     * @param workspace
     * @param project
     * @return
     */
    List<BuildTaskDescriptor> getTasks(String workspace, String project);

    /**
     * return a task descriptor by ID
     *
     * @param id the task id
     * @param context the ServiceContext
     * @return
     * @throws NotFoundException  in case the task does not exists
     * @throws ForbiddenException in case the current user does not have access
     */
    BuildTaskDescriptor getTask(Long id, ServiceContext context) throws NotFoundException, ForbiddenException;

    /**
     * return a task descriptor by ID
     *
     * @param id the task id
     * @return
     * @throws NotFoundException  in case the task does not exists
     * @throws ForbiddenException in case the current user does not have access
     */
    BuildTaskDescriptor getTask(Long id) throws NotFoundException, ForbiddenException;

    /**
     * Tries to cancel a running or queued task
     *
     * @param id the task id
     * @param context the ServiceContext
     * @return
     * @throws NotFoundException  in case the task does not exsits
     * @throws ForbiddenException
     */
    BuildTaskDescriptor cancel(Long id, ServiceContext context) throws NotFoundException, ForbiddenException;

    /**
     * Writes the log of the task to the outputstream
     *
     * @param id
     * @throws NotFoundException  in case the task does not exists
     * @throws ForbiddenException in case the current user does not have access to the task
     * @throws ServerException        if an error occur writing to the response
     */
    Response writeLog(Long id) throws NotFoundException, ForbiddenException, ServerException;


    /**
     * @param id
     * @param path
     * @throws NotFoundException
     * @throws ForbiddenException
     * @throws ServerException
     */
    Response readFile(Long id, String path) throws NotFoundException, ForbiddenException, ServerException;


    /**
     * @param id
     * @param path
     * @throws NotFoundException
     * @throws ForbiddenException
     * @throws ServerException
     */
    Response downloadFile(Long id, String path) throws NotFoundException, ForbiddenException, ServerException;

    /**
     * @param id
     * @param arch
     * @throws NotFoundException
     * @throws ForbiddenException
     * @throws ServerException
     */
    Response downloadResultArchive(Long id, String arch) throws NotFoundException, ForbiddenException, ServerException;
}
