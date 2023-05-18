package org.openmrs.module.eventsdemo.api;

import org.openmrs.api.AdministrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;

@Component
public class EventsDemoClient {
	
	@Autowired
	@Qualifier("adminService")
	AdministrationService administrationService;
	
	@Autowired
	@Qualifier("fhirR4")
	private FhirContext fhirContext;
	
	public IGenericClient geteventsDemoClient() {
		String url = administrationService.getGlobalProperty("eventsdemo.shrUrl", "http://openhim-core:5001/fhir");
		IGenericClient client = fhirContext.newRestfulGenericClient(url);
		BasicAuthInterceptor auth = new BasicAuthInterceptor("user", "pass");
		client.registerInterceptor(auth);
		return client;
	}
	
	public FhirContext getFhirContext() {
		return fhirContext;
	}
}
