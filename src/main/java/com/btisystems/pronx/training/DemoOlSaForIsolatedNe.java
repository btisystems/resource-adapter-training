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
import com.btisystems.pronx.ems.client.adapter.exceptions.ResourceAdapterException;
import com.btisystems.pronx.ems.schemas.network.NetworkEvent;
import com.btisystems.pronx.ems.schemas.network.OlChannelType;
import com.btisystems.pronx.ems.schemas.network.OlServiceDefinition;
import com.btisystems.pronx.ems.schemas.network.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.btisystems.pronx.ems.client.common.channels.DwdmChannelInfo;
import com.btisystems.pronx.ems.schemas.network.OlIdx;
import com.btisystems.pronx.ems.schemas.network.OlOrderedIdx;
import com.btisystems.pronx.ems.schemas.network.OlOrderedIdxList;

/**
 * This is to demonstrate creation of a service with a single XC.
 * This is needed e.g. if only one DOL NEs is under management.
 * Typically however this will not normally be the case, and services will be created between
 * different ol-groups (which will normally be on different NEs).
 *
 * @author kmp
 */
public class DemoOlSaForIsolatedNe {

    private static final Logger LOG = LoggerFactory.getLogger(DemoOlSaForIsolatedNe.class);

    public DemoOlSaForIsolatedNe() {
        try {
            //connectToResourceAdapter("172.27.5.101", "admin", "admin");
            connectToResourceAdapter("127.0.0.1", "admin", "admin");

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

            createOlService();

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

        final DemoOlSaForIsolatedNe app = new DemoOlSaForIsolatedNe();

        try {
            System.out.println("Keep main thread around for 100 seconds ...");
            Thread.sleep(1000 * 100);
        } catch (InterruptedException e) {
        }
        System.out.println("Main Thread ending, so close RA connection ...");
        app.closeConnectionToResourceAdapter();
    }

    private void createOlService() throws ResourceAdapterException {

        // No need to do lookup routing options GET /NetworkOpticalServices/RoutingOptions for these single group scenarion ...
        final OlServiceDefinition osd = new OlServiceDefinition();

        // This represents this XSD type ...
        // <channels optical-number="240"><wavelength>1558.17</wavelength><frequency>192.4</frequency><bti7000-number>3</bti7000-number>
        osd.setChannel(DwdmChannelInfo.C_240.getByOlChannelType());
        osd.setAlienAdddropEnd(Boolean.FALSE);
        osd.setAlienAdddropStart(Boolean.FALSE);

        // This is a typical service buy we only want the start to demo single XS creation :
        // 10.1.209.6 G6D1 =  A degree in a 2 degree ROADM
        // 10.1.209.6 G5 = Line Equalizing node with D1 and D2
        // 10.1.209.2 G3D4 =  A degree in a 4 degree ROADM
        //
        // ... snip ...
        // <ol-ordered-idx-list>
        // <ol-ordered-idx order-id="0"><ol-idx ol-degree="1" ol-group="6" ip-address="10.1.209.6"/></ol-ordered-idx>
        // <ol-ordered-idx order-id="1"><ol-idx ol-degree="2" ol-group="5" ip-address="10.1.209.6"/></ol-ordered-idx>
        // <ol-ordered-idx order-id="2"><ol-idx ol-degree="1" ol-group="5" ip-address="10.1.209.6"/></ol-ordered-idx>
        // <ol-ordered-idx order-id="3"><ol-idx ol-degree="4" ol-group="3" ip-address="10.1.209.2"/></ol-ordered-idx>
        // </ol-ordered-idx-list>

        // Create single add/drop XC at the ROADM
        osd.setName("DEMO_SINGLE_ADD_DROP");
         final OlOrderedIdx oidx = new OlOrderedIdx().withOrderId(0).withOlIdx(
                new OlIdx().withIpAddress("10.1.209.6").withOlGroup(6).withOlDegree(1));
        osd.setOlOrderedIdxList(new OlOrderedIdxList().withOlOrderedIdx(oidx));
        ResourceAdapter.getInstance().getOpticalAdapter().createServiceWithDefaultTasksCallback(osd);

        // This will fail correctly with following Task Status ...
        // CHANNEL_EQUALIZING_LINE_NODE with only one degree is invalid. Idx=0"
        osd.setName("DEMO_HALF_EXPRESS_IN_CEN");
        osd.setChannel(DwdmChannelInfo.C_600.getByOlChannelType());
        final OlOrderedIdx oidx2 = new OlOrderedIdx().withOrderId(0).withOlIdx(
                new OlIdx().
                withIpAddress("10.1.209.6").
                withOlGroup(5).
                withOlDegree(1));
        osd.setOlOrderedIdxList(new OlOrderedIdxList().withOlOrderedIdx(oidx2));
        ResourceAdapter.getInstance().getOpticalAdapter().createServiceWithDefaultTasksCallback(osd);

        osd.setName("EXPESS_EXPRESS_IN_CEN");
        osd.setChannel(DwdmChannelInfo.C_600.getByOlChannelType());
        final OlOrderedIdx express1 = new OlOrderedIdx().withOrderId(0).withOlIdx(
                new OlIdx().
                withIpAddress("10.1.209.6").
                withOlGroup(5).
                withOlDegree(1));
        final OlOrderedIdx express2 = new OlOrderedIdx().withOrderId(0).withOlIdx(
                new OlIdx().
                withIpAddress("10.1.209.6").
                withOlGroup(5).
                withOlDegree(2));
        osd.setOlOrderedIdxList(new OlOrderedIdxList().withOlOrderedIdx(express1, express2));
        ResourceAdapter.getInstance().getOpticalAdapter().createServiceWithDefaultTasksCallback(osd);

    }

}
