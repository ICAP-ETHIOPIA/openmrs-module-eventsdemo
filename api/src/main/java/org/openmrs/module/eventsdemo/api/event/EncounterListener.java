package org.openmrs.module.eventsdemo.api.event;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

import org.openmrs.Encounter;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Daemon;
import org.openmrs.event.EventListener;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.eventsdemo.EventsDemoConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Component
public class EncounterListener implements EventListener {

    private DaemonToken daemonToken;
    
    private Log log = LogFactory.getLog(this.getClass());

    @Autowired
    EncounterService patientService;
    
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
                log.debug(String.format("Handling encounter %s", uuid));
            }
            catch (JMSException e) {
                log.error("Exception caught while trying to get encounter uuid for event.", e);
                return;
            }
            
            if (uuid == null || StringUtils.isBlank(uuid)) {
                return;
            }
            
            Encounter encounter;
            encounter = patientService.getEncounterByUuid(uuid);
            
            if (mapMessage.getJMSDestination().toString().equals(EventsDemoConstants.PATIENT_UPDATE_MESSAGE_DESTINATION)) {
               
            } else {
               
            }
        }
    }
    
}