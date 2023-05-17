package org.openmrs.module.eventsdemo.api.event;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Daemon;
import org.openmrs.event.EventListener;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.eventsdemo.EventsDemoConstants;
import org.openmrs.module.fhir2.api.FhirPatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Component
public class PatientListener implements EventListener {
	
	private DaemonToken daemonToken;
	
	private Log log = LogFactory.getLog(this.getClass());
	
	@Autowired
	PatientService patientService;
	
	@Autowired
	FhirPatientService fhirPatientService;
	
	@Autowired
	@Qualifier("adminService")
	AdministrationService administrationService;
	
	@Autowired
	@Qualifier("fhirR4")
	private FhirContext fhirContext;
	
	public DaemonToken getDaemonToken() {
		return daemonToken;
	}
	
	public void setDaemonToken(DaemonToken daemonToken) {
		this.daemonToken = daemonToken;
	}
	
	@Override
    public void onMessage(Message message) {
        try {
            Daemon.runInDaemonThread(() -> {
                try {
                    processMessage(message);
                }
                catch (Exception e) {
                    log.error(String.format("Failed to process Patient message!\n%s", message.toString()), e);
                }
            }, daemonToken);
        }
        catch (Exception e) {
            log.error(String.format("Failed to start Daemon thread to process message!\n%s", message.toString()), e);
        }
        
    }
	
	private void processMessage(Message message) throws JMSException {
		if (message instanceof MapMessage) {
			MapMessage mapMessage = (MapMessage) message;
			
			String uuid;
			try {
				uuid = mapMessage.getString("uuid");
				log.debug(String.format("Handling patient %s", uuid));
			}
			catch (JMSException e) {
				log.error("Exception caught while trying to get patient uuid for event.", e);
				return;
			}
			
			if (uuid == null || StringUtils.isBlank(uuid)) {
				return;
			}
			
			sendPatient(uuid);
			Patient patient = patientService.getPatientByUuid(uuid);
			
			if (mapMessage.getJMSDestination().toString().equals(EventsDemoConstants.PATIENT_UPDATE_MESSAGE_DESTINATION)) {
				System.out.print("PATIENT UPDATED ---->" + patient.getPersonName());
			} else {
				System.out.print("PATIENT CREATED --->" + patient.getPersonName());
			}
		}
	}
	
	private void sendPatient(String uuid) {
		String url = administrationService.getGlobalProperty("eventsdemo.shrUrl", "http://openhim-core:5001/fhir");
		IGenericClient client = fhirContext.newRestfulGenericClient(url);
		BasicAuthInterceptor auth = new BasicAuthInterceptor("user", "pass");
		client.registerInterceptor(auth);
		
		org.hl7.fhir.r4.model.Patient fhirPatient = fhirPatientService.get(uuid);
		System.out.println("............ debug FHIR PATIENT............");
		System.out.println(fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(fhirPatient));
		client.update().resource(fhirPatient).execute();
	}
	
}
