package org.openmrs.module.eventsdemo.api.scheduler;

import java.util.List;

import org.hl7.fhir.r4.model.Patient;
import org.openmrs.api.handler.PatientDataUnvoidHandler;
import org.openmrs.module.eventsdemo.Item;
import org.openmrs.module.eventsdemo.api.EventsDemoClient;
import org.openmrs.module.eventsdemo.api.EventsdemoService;
import org.openmrs.module.fhir2.api.FhirPatientService;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class DemoTask extends AbstractTask implements ApplicationContextAware {
	
	private static ApplicationContext applicationContext;
	
	@Autowired
	EventsdemoService eventsdemoService;
	
	@Autowired
	EventsDemoClient demoClient;
	
	@Autowired
	FhirPatientService fhirPatientService;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	@Override
	public void execute() {
		try {
			applicationContext.getAutowireCapableBeanFactory().autowireBean(this);
		} 
		catch (Exception e) {
			// return;
		}
		System.out.println(">>> EVENTS DEMO TASK EXECUTING");

        List<Item> items = eventsdemoService.getAllItems();

        items.forEach( item -> {
            Patient patient = fhirPatientService.get(item.getFailedPatientUuid());
            System.out.println(">> failed taient UUID" + item.getFailedPatientUuid());
            try{
                demoClient.geteventsDemoClient().update().resource(patient).execute();
                eventsdemoService.deleteItem(item);
            }catch(Exception e){
            }
            
        });
        
	}
}
