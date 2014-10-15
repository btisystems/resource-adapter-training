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
import com.btisystems.pronx.ems.schemas.network.OlFiberList;
import com.btisystems.pronx.ems.schemas.network.OsSite;
import com.btisystems.pronx.ems.schemas.network.ShelfList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DemoUsefulGets {

    private static final Logger LOG = LoggerFactory.getLogger(DemoUsefulGets.class);

    public DemoUsefulGets() {
        try {
            connectToResourceAdapter("172.27.5.101", "admin", "admin");
            printActiveAlarms();
            printNeDetails();
            printTopologyDetails();

        } catch (AuthenticationException ex) {
            LOG.error("Authentication problem", ex);
        } catch (BadRequestException ex) {
            LOG.error("BadRequest problem", ex);
        } catch (ClientMismatchException ex) {
            LOG.error("ClientMismatch problem", ex);
        } catch (ConnectionException ex) {
            LOG.error("Connection problem", ex);
        } catch (MethodNotAllowedException ex) {
            LOG.error("MethodNotAllowed problem", ex);
        } catch (NotFoundException ex) {
            LOG.error("Resource not found problem", ex);
        } catch (OtherResourceException ex) {
            LOG.error("Other Resource problem", ex);
        } catch (RequestTimeoutException ex) {
            LOG.error("RequestTimeout problem", ex);
        } catch (ResourceAdapterException ex) {
            // User can just handle this exception if desired.
            LOG.error("RA problem", ex);
        } finally {
            closeConnectionToResourceAdapter();
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
        new DemoUsefulGets();
    }

    private void printActiveAlarms() throws ResourceAdapterException {
        final List<NetworkAlarm> alarms = ResourceAdapter.getInstance().getAlarmsAdapter().getAlarms();
        LOG.info("All Alarms = {}", alarms);

    }

    private void printNeDetails() throws ResourceAdapterException {
        final List<NetworkElement> NEs = ResourceAdapter.getInstance().getNetworkElementsAdapter().getNetworkElements();

        for (NetworkElement element : NEs) {
            LOG.info("NE={}", element);
            final ShelfList shelfListForNe = ResourceAdapter.getInstance().getNetworkElementsAdapter().getShelfList(element.getAddress());
            LOG.info("Shelf Info={}", shelfListForNe);

            // shelfListForNe includes DOL for that NE, whereas for Fixed Optical separate invocation ...
            final OsSite fixedOpticalLayer
                    = ResourceAdapter.getInstance().getOpticalAdapter().getStaticOpticalLayerResources(element.getAddress());
            LOG.info("Fixed Optical Layer = {}", fixedOpticalLayer);

        }

    }

    private void printTopologyDetails() throws ResourceAdapterException {
        final OlFiberList fibers = ResourceAdapter.getInstance().getOpticalAdapter().getTopologyByFibers();
        LOG.info("Fibers = {}", fibers);

    }

}
