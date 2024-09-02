package com.jms.claim;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.jms.claim.model.Claim;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

public class ClaimManagement {
    public static void main(String[] args) throws Exception {
        InitialContext initialContext = new InitialContext();
        Queue queue = (Queue) initialContext.lookup("queue/claimQueue");
        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
             JMSContext jmsContext = cf.createContext()) {
            JMSProducer producer = jmsContext.createProducer();
//            JMSConsumer consumer = jmsContext.createConsumer(queue, "claimAmount BETWEEN 10000 AND 20000");
//            JMSConsumer consumer = jmsContext.createConsumer(queue, "doctorType IN ('neuro','psych') OR JmsPriority BETWEEN 3 and 6");
            JMSConsumer consumer = jmsContext.createConsumer(queue, "insuranceProvider IN ('blue cross','american') AND doctorName LIKE 'H%' AND claimAmount%10=0");

            ObjectMessage objectMessage = jmsContext.createObjectMessage();
//            objectMessage.setIntProperty("hospitalId", 1);
            Claim claim = new Claim(13242, "Harry John", "neuro", "blue cross", 45000d);
            objectMessage.setDoubleProperty("claimAmount", claim.getClaimAmount());
            objectMessage.setStringProperty("doctorType", "neuro");
            objectMessage.setStringProperty("doctorName", claim.getDoctorName());
            objectMessage.setStringProperty("insuranceProvider", claim.getInsuranceProvider());
            objectMessage.setObject(claim);
            producer.send(queue, objectMessage);

            System.out.println("Received claim: " + consumer.receiveBody(Claim.class).getClaimAmount());
        }
    }
}
