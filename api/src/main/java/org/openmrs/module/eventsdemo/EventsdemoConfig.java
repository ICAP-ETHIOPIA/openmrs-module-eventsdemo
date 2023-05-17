/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.eventsdemo;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.event.Event;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.eventsdemo.api.event.EncounterListener;
import org.openmrs.module.eventsdemo.api.event.PatientListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Contains module's config.
 */
@Component("eventsdemo.EventsdemoConfig")
public class EventsdemoConfig {
	private Log log = LogFactory.getLog(this.getClass());
	private final AtomicBoolean isRunning = new AtomicBoolean(false);
	private DaemonToken daemonToken;

	@Autowired
	private EncounterListener encounterListener;

	@Autowired
	private PatientListener patientListener;
	
	public final static String MODULE_PRIVILEGE = "Eventsdemo Privilege";

	public void setDaemonToken(DaemonToken daemonToken) {
		this.daemonToken = daemonToken;
	}

	public void subscribeToEvents() {
		log.info("Enabling Events Subscription");
		
		if (daemonToken != null) {
			patientListener.setDaemonToken(daemonToken);
			encounterListener.setDaemonToken(daemonToken);
		}
		
		if (!isRunning.get()) {
			Event.subscribe(Patient.class, Event.Action.CREATED.toString(), patientListener);
			Event.subscribe(Patient.class, Event.Action.UPDATED.toString(), patientListener);

			Event.subscribe(Encounter.class, Event.Action.CREATED.toString(), encounterListener);
			Event.subscribe(Encounter.class, Event.Action.UPDATED.toString(), encounterListener);
		}
		
		isRunning.set(true);
	}
	
	public void unSubscribetoEvents() {
		log.info("Disabling Events Subscription");
		
		if (isRunning.get()) {
			Event.unsubscribe(Patient.class, Event.Action.CREATED, patientListener);
			Event.unsubscribe(Patient.class, Event.Action.UPDATED, patientListener);

			Event.unsubscribe(Encounter.class, Event.Action.CREATED, encounterListener);
			Event.unsubscribe(Encounter.class, Event.Action.UPDATED, encounterListener);
		}
		
		isRunning.set(false);
	}
}
