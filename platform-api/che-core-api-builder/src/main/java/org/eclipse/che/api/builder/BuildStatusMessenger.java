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

import org.eclipse.che.api.builder.internal.BuilderEvent;
import org.eclipse.che.api.core.notification.EventSubscriber;
import org.eclipse.che.dto.server.DtoFactory;
import org.everrest.core.impl.provider.json.JsonUtils;
import org.everrest.websockets.WSConnectionContext;
import org.everrest.websockets.message.ChannelBroadcastMessage;
import org.slf4j.LoggerFactory;

/**
 * Created by panthro on 25/08/15.
 */
public class BuildStatusMessenger implements EventSubscriber<BuilderEvent> {
    private BuildQueue buildQueue;

    public BuildStatusMessenger(BuildQueue buildQueue) {
        this.buildQueue = buildQueue;
    }

    @Override
    public void onEvent(BuilderEvent event) {
        try {
            final ChannelBroadcastMessage bm = new ChannelBroadcastMessage();
            final long id = event.getTaskId();
            switch (event.getType()) {
                case BEGIN:
                case DONE:
                    bm.setChannel(String.format("builder:status:%d", id));
                    bm.setBody(DtoFactory.getInstance().toJson(buildQueue.getTask(id)));
                    break;
                case MESSAGE_LOGGED:
                    final BuilderEvent.LoggedMessage message = event.getMessage();
                    if (message != null) {
                        bm.setChannel(String.format("builder:output:%d", id));
                        bm.setBody(String.format("{\"num\":%d, \"line\":%s}",
                                message.getLineNum(), JsonUtils.getJsonString(message.getMessage())));
                    }
                    break;
            }
            WSConnectionContext.sendMessage(bm);
        } catch (Exception e) {
            LoggerFactory.getLogger(BuildStatusMessenger.class).warn(e.getMessage(), e);
        }
    }
}
