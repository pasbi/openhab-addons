/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.dlinksmarthome.internal;

import static org.openhab.binding.dlinksmarthome.internal.DLinkSmartHomeBindingConstants.*;
import static org.openhab.binding.dlinksmarthome.internal.DLinkThingConfig.IP_ADDRESS;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.jmdns.ServiceInfo;

import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.config.discovery.mdns.MDNSDiscoveryParticipant;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link DLinkSmartHomeDiscoveryParticipant} is responsible for discovering devices through UPnP.
 *
 * @author Mike Major - Initial contribution
 * @author Pascal Bies - Add DSP-W215 discovery
 *
 */
@Component(immediate = true)
public class DLinkSmartHomeDiscoveryParticipant implements MDNSDiscoveryParticipant {

    private static final String SERVICE_TYPE = "_dhnap._tcp.local.";
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Set<ThingTypeUID> getSupportedThingTypeUIDs() {
        return SUPPORTED_THING_TYPES_UIDS;
    }

    @Override
    public String getServiceType() {
        return SERVICE_TYPE;
    }

    private String thingName(ThingTypeUID uid) {
        if (uid == THING_TYPE_DCHS150) {
            return "Motion Sensor";
        } else if (uid == THING_TYPE_DSPW215) {
            return "Smart Plug";
        } else {
            return null;
        }
    }

    @Override
    public DiscoveryResult createResult(final ServiceInfo serviceInfo) {
        final ThingUID thingUID = getThingUID(serviceInfo);

        if (thingUID == null) {
            return null;
        }

        final ThingTypeUID thingTypeUID = getThingType(serviceInfo);

        if (SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID)) {
            final String host = serviceInfo.getHostAddresses()[0];
            final String mac = serviceInfo.getPropertyString("mac");

            final Map<String, Object> properties = new HashMap<>();
            properties.put(IP_ADDRESS, host);

            logger.debug("{} found: {}", thingUID.getAsString(), host);

            return DiscoveryResultBuilder.create(thingUID).withThingType(thingTypeUID).withProperties(properties)
                    .withLabel(thingName(thingTypeUID) + " (" + mac + ")").build();
        } else {
            return null;
        }
    }

    @Override
    public ThingUID getThingUID(final ServiceInfo serviceInfo) {
        final ThingTypeUID thingTypeUID = getThingType(serviceInfo);

        if (thingTypeUID != null) {
            final String mac = serviceInfo.getPropertyString("mac").replace(":", "").toLowerCase();
            return new ThingUID(thingTypeUID, mac);
        } else {
            return null;
        }
    }

    private ThingTypeUID getThingType(final ServiceInfo serviceInfo) {
        final String model = serviceInfo.getPropertyString("model_number");

        if (model == null) {
            return null;
        } else if (model.equals("DCH-S150")) {
            return THING_TYPE_DCHS150;
        } else if (model.equals("DSP-W215")) {
            return THING_TYPE_DSPW215;
        } else {
            logger.debug("D-Link HNAP Type: {}", model);
            return null;
        }
    }
}
