package org.apache.activemq.artemis.jms.example;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class QueueBrowserExample {

    public static void main(String[] args) throws NamingException, JMSException {
        
        Connection connection = null;
        InitialContext initialContext = null;
        try {
           // Step 1. Create an initial context to perform the JNDI lookup.
           initialContext = new InitialContext();

           // Step 2. Perfom a lookup on the queue
           Queue queue = (Queue) initialContext.lookup("queue/exampleQueue");

           // Step 3. Perform a lookup on the Connection Factory
           ConnectionFactory cf = (ConnectionFactory) initialContext.lookup("ConnectionFactory");

           // Step 4.Create a JMS Connection
           connection = cf.createConnection();

           // Step 5. Create a JMS Session
           Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

           // Step 6. Create a JMS Message Producer
           MessageProducer producer = session.createProducer(queue);

           for(int i = 0 ; i < 10 ; i ++ ) {
               TextMessage message = session.createTextMessage("This is a text message");
               System.out.println("Sent message: " + message.getText());
               producer.send(message);
           }
           
           QueueBrowser browser = session.createBrowser(queue);
           
           System.out.println(browser.getMessageSelector());

        } finally {
           // Step 12. Be sure to close our JMS resources!
           if (initialContext != null) {
              initialContext.close();
           }
           if (connection != null) {
              connection.close();
           }
        }
    }

}
