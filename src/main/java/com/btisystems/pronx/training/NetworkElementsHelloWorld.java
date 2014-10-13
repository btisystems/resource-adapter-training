/**
 * Copyright (C) BTI Systems Inc. 2001-2014. All Rights Reserved.
 * <p/>
 * The information contained herein is the property of BTI Systems
 * Inc. and is strictly confidential. Except as expressly authorized
 * in writing by BTI Systems Inc., the holder shall keep all
 * information contained herein confidential, shall disclose the
 * information only to its employees with a need to know, and shall
 * protect the information, in whole or in part, from disclosure and
 * dissemination to third parties with the same degree of care it uses
 * to protect its own confidential information, but with no less than
 * reasonable care. Except as expressly authorized in writing by BTI
 * Systems Inc., the holder is granted no rights to use the
 * information contained herein.
 * <p/>
 * Unpublished. All rights reserved under the copyright laws of
 * Canada.
 * <p/>
 * $Id: $
 */
package com.btisystems.pronx.training;

import com.btisystems.pronx.ems.client.adapter.ResourceAdapter;
import com.btisystems.pronx.ems.client.adapter.exceptions.ResourceAdapterException;
import com.btisystems.pronx.ems.schemas.network.NetworkElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class NetworkElementsHelloWorld {

    private static final Logger LOG = LoggerFactory.getLogger(NetworkElementsHelloWorld.class);

    public NetworkElementsHelloWorld() {
        try {
            connectToResourceAdapter("172.27.5.230", "admin", "admin");
            getNetworkElementsSummary();
        } catch (ResourceAdapterException ex) {
            LOG.error("Problem with resource adapter.", ex);
        } finally {
            closeConnectionToResourceAdapter();
        }
    }

    private void closeConnectionToResourceAdapter() {
        ResourceAdapter.getInstance().shutdown();
        System.exit(0);  // terminating jvm otherwise have to wait on socket timeout
    }

    private void getNetworkElementsSummary() throws ResourceAdapterException {
        final List<NetworkElement> networkElements = ResourceAdapter.getInstance().getNetworkElementsAdapter().getNetworkElements();
        LOG.info("PSM Server contains {} network elements.", networkElements.size());
        for (NetworkElement networkElement : networkElements) {
            LOG.info("Network element {}, is a {}.", networkElement.getAddress(), networkElement.getType());
        }

    }

    private void connectToResourceAdapter(final String ipAddress, final String username, final String password) throws ResourceAdapterException {
        ResourceAdapter.getInstance().setupAdapter(ipAddress, username, password, null, null);
    }

    public static void main(String args[]) {
        new NetworkElementsHelloWorld();
    }
}
