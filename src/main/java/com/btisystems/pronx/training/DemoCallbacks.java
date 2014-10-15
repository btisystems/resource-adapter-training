/**
 * Copyright (C) BTI Systems Inc. 2001-2014. All Rights Reserved.
 * <p/>
 * The information contained herein is the property of BTI Systems Inc. and is
 * strictly confidential. Except as expressly authorized in writing by BTI
 * Systems Inc., the holder shall keep all information contained herein
 * confidential, shall disclose the information only to its employees with a
 * need to know, and shall protect the information, in whole or in part, from
 * disclosure and dissemination to third parties with the same degree of care it
 * uses to protect its own confidential information, but with no less than
 * reasonable care. Except as expressly authorized in writing by BTI Systems
 * Inc., the holder is granted no rights to use the information contained
 * herein.
 * <p/>
 * Unpublished. All rights reserved under the copyright laws of Canada.
 * <p/>
 * $Id: $
 */
package com.btisystems.pronx.training;

import com.btisystems.pronx.ems.client.adapter.ResourceAdapter;
import com.btisystems.pronx.ems.client.adapter.callbacks.IEventsCallback;
import com.btisystems.pronx.ems.client.adapter.callbacks.ITasksCallback;
import com.btisystems.pronx.ems.client.adapter.exceptions.AuthenticationException;
import com.btisystems.pronx.ems.client.adapter.exceptions.BadRequestException;
import com.btisystems.pronx.ems.client.adapter.exceptions.ClientMismatchException;
import com.btisystems.pronx.ems.client.adapter.exceptions.ConnectionException;
import com.btisystems.pronx.ems.client.adapter.exceptions.MethodNotAllowedException;
import com.btisystems.pronx.ems.client.adapter.exceptions.NotFoundException;
import com.btisystems.pronx.ems.client.adapter.exceptions.OtherResourceException;
import com.btisystems.pronx.ems.client.adapter.exceptions.RequestTimeoutException;
import com.btisystems.pronx.ems.client.adapter.exceptions.ResourceAdapterException;
import com.btisystems.pronx.ems.schemas.network.NetworkAlarm;
import com.btisystems.pronx.ems.schemas.network.NetworkElement;
import com.btisystems.pronx.ems.schemas.network.NetworkEvent;
import com.btisystems.pronx.ems.schemas.network.OlFiberList;
import com.btisystems.pronx.ems.schemas.network.OsSite;
import com.btisystems.pronx.ems.schemas.network.ShelfList;
import com.btisystems.pronx.ems.schemas.network.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DemoCallbacks {

    private static final Logger LOG = LoggerFactory.getLogger(DemoCallbacks.class);

    public DemoCallbacks() {
        try {
            connectToResourceAdapter("172.27.5.101", "admin", "admin");

            ResourceAdapter.getInstance().subscribeToEvents(new IEventsCallback() {
                @Override
                public void handleEvent(final NetworkEvent evt) {
                    LOG.info("Event received={}", evt);
                }
            });

            ResourceAdapter.getInstance().subscribeToTasks(new ITasksCallback() {
                @Override
                public void handleTaskStatus(final TaskStatus taskStatus) {
                    LOG.info("Task status={}", taskStatus);
                }
            });

            ResourceAdapter.getInstance().refreshComplete(null);

        } catch (ResourceAdapterException ex) {
            // User can just handle this exception if desired.
            LOG.error("RA problem", ex);
            closeConnectionToResourceAdapter();

        } catch (Exception ex) {
            LOG.error("Other problem", ex);
        }
    }

    private void closeConnectionToResourceAdapter() {
        ResourceAdapter.getInstance().shutdown();
        System.exit(0);
    }

    private void connectToResourceAdapter(final String ipAddress, final String username, final String password) throws ResourceAdapterException {
        ResourceAdapter.getInstance().setupAdapter(ipAddress, username, password, null, null);
    }

    public static void main(String args[]) {

        final DemoCallbacks app = new DemoCallbacks();

        try {
            System.out.println("Keep main thread around for 100 seconds ...");
            Thread.sleep(1000 * 100);
        } catch (InterruptedException e) {
        }
        System.out.println("Main Thread ending, so close RA connection ...");
        app.closeConnectionToResourceAdapter();
    }

}
