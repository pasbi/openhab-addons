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

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerFactory;
import org.eclipse.smarthome.io.net.http.HttpClientFactory;
import org.openhab.binding.dlinksmarthome.internal.handler.DLinkMotionSensorHandler;
import org.openhab.binding.dlinksmarthome.internal.handler.DLinkSmartPlugHandler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * The {@link DLinkSmartHomeHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Mike Major - Initial contribution
 * @author Pascal Bies - Add DSP-W215 thing type
 */
@Component(service = ThingHandlerFactory.class, configurationPid = "binding.dlinksmarthome")
public class DLinkSmartHomeHandlerFactory extends BaseThingHandlerFactory {

    private final HttpClient httpClient;

    @Activate
    public DLinkSmartHomeHandlerFactory(@Reference final HttpClientFactory httpClientFactory) {
        httpClient = httpClientFactory.getCommonHttpClient();
    }

    @Override
    public boolean supportsThingType(final ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected ThingHandler createHandler(final Thing thing) {
        final ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (thingTypeUID.equals(THING_TYPE_DCHS150)) {
            return new DLinkMotionSensorHandler(thing);
        } else if (thingTypeUID.equals(THING_TYPE_DSPW215)) {
            return new DLinkSmartPlugHandler(thing, httpClient);
        }

        return null;
    }
}
