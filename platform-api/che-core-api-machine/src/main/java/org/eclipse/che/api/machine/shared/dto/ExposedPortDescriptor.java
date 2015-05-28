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
package org.eclipse.che.api.machine.shared.dto;

import org.eclipse.che.dto.shared.DTO;

/**
 * @author Alexander Garagatyi
 */
@DTO
public interface ExposedPortDescriptor {
    String getProtocol();

    void setProtocol(String protocol);

    ExposedPortDescriptor withProtocol(String protocol);

    int getExternalPort();

    void setExternalPort(int externalPort);

    ExposedPortDescriptor withExternalPort(int externalPort);

    int getInternalPort();

    void setInternalPort(int internalPort);

    ExposedPortDescriptor withInternalPort(int internalPort);

    String getHost();

    void setHost(String host);

    ExposedPortDescriptor withHost(String host);
}
