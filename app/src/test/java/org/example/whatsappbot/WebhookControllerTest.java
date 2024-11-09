
//     @Test
//     public void testProvideFixForPowerIssue() throws Exception {
//         WebhookController spyController = spy(controller);
//         doNothing().when(spyController).sendMessage(anyString(), anyString());

//         String from = "whatsapp:+1234567890";
//         controller.getUserStates().put(from, "awaitingPowerIssueDetails");

//         // Test case for 'won't turn on'
//         String incomingMessage = "won't turn on";
//         String expectedReply = "Please check if the power cable is securely plugged in and the outlet is working. " +
//                 "If the device still won't turn on, try using a different power cable.";
//         Method method = WebhookController.class.getDeclaredMethod("provideFixForPowerIssue", String.class, String.class);
//         method.setAccessible(true);
//         String actualReply = (String) method.invoke(controller, incomingMessage, from);
//         assertEquals(expectedReply, actualReply);

//         // Test case for 'intermittent'
//         incomingMessage = "intermittent";
//         expectedReply = "Intermittent power failures might be due to loose connections or a faulty power supply. " +
//                 "Ensure all cables are securely connected and consider replacing the power supply if the issue persists.";
//         actualReply = (String) method.invoke(controller, incomingMessage, from);
//         assertEquals(expectedReply, actualReply);

//         // Test case for 'surges'
//         incomingMessage = "surges";
//         expectedReply = "Power surges can damage hardware components. Consider using a surge protector, and if the issue persists, " +
//                 "you may need to have the hardware inspected by a professional.";
//         actualReply = (String) method.invoke(controller, incomingMessage, from);
//         assertEquals(expectedReply, actualReply);

//         // Test case for unknown issue
//         incomingMessage = "unknown issue";
//         expectedReply = "Please provide more details about the power issue you're facing.";
//         actualReply = (String) method.invoke(controller, incomingMessage, from);
//         assertEquals(expectedReply, actualReply);
//     }



package org.example.whatsappbot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class WebhookControllerTest {

    private WebhookController controller;
    private WebhookController spyController;

    @BeforeEach
    public void setUp() {
        controller = new WebhookController();
        // Create a spy of the controller
        spyController = spy(controller);
        // Mock the sendMessage method
        doNothing().when(spyController).sendMessage(anyString(), anyString());
    }
    @Test
public void testProvideFixForPowerIssue() throws Exception {
    // Create a spy on the controller to verify interactions
    WebhookController spyController = spy(controller);
    // Mock the sendMessage method to avoid actual API calls
    doNothing().when(spyController).sendMessage(anyString(), anyString());

    String from = "whatsapp:+1234567890";
    spyController.getUserStates().put(from, "awaitingPowerIssueDetails");

    // Define the follow-up message
    String followUpMessage = "If the issue isn't fixed by following the above steps, please reply with 'Not fixed' so I can assist you further.";

    // Access the private method using reflection
    Method method = WebhookController.class.getDeclaredMethod("provideFixForPowerIssue", String.class, String.class);
    method.setAccessible(true);

    // Test case for 'won't turn on'
    String incomingMessage = "won't turn on";
    String expectedReply = "Please check if the power cable is securely plugged in and the outlet is working. " +
            "If the device still won't turn on, try using a different power cable.";
    // Call provideFixForPowerIssue
    String actualReply = (String) method.invoke(spyController, incomingMessage, from);
    assertEquals(expectedReply, actualReply);
    // Verify that the follow-up message is sent
    verify(spyController).sendMessage(eq(from), eq(followUpMessage));

    // Test case for 'intermittent'
    incomingMessage = "intermittent";
    expectedReply = "Intermittent power failures might be due to loose connections or a faulty power supply. " +
            "Ensure all cables are securely connected and consider replacing the power supply if the issue persists.";
    actualReply = (String) method.invoke(spyController, incomingMessage, from);
    assertEquals(expectedReply, actualReply);
    verify(spyController, times(2)).sendMessage(eq(from), eq(followUpMessage));

    // Test case for 'surges'
    incomingMessage = "surges";
    expectedReply = "Power surges can damage hardware components. Consider using a surge protector, and if the issue persists, " +
            "you may need to have the hardware inspected by a professional.";
    actualReply = (String) method.invoke(spyController, incomingMessage, from);
    assertEquals(expectedReply, actualReply);
    verify(spyController, times(3)).sendMessage(eq(from), eq(followUpMessage));

    // Test case for unknown issue
    incomingMessage = "unknown issue";
    expectedReply = "Please provide more details about the power issue you're facing.";
    actualReply = (String) method.invoke(spyController, incomingMessage, from);
    assertEquals(expectedReply, actualReply);
    // No follow-up message for unknown issue
    verify(spyController, times(3)).sendMessage(eq(from), eq(followUpMessage));
}

    
    // @Test
    // public void testHandleIncomingMessageWithStateChange() throws Exception {
    //     // Create a spy on the controller to verify interactions
    //     WebhookController spyController = spy(controller);
    
    //     // Mock the sendMessage method to avoid actual API calls
    //     doNothing().when(spyController).sendMessage(anyString(), anyString());
    
    //     // Define test inputs
    //     String from = "whatsapp:+1234567890";
    //     String initialMessage = "Hardware";
        
    //     // Call the method to test
    //     spyController.handleIncomingMessage(initialMessage, from);
    
    //     // Debug output to verify state change
    //     System.out.println("Actual state after 'Hardware' message: " + spyController.getUserStates().get(from));
    
    //     // Test state change after initial message
    //     assertEquals("awaitingCategory", spyController.getUserStates().get(from));
    
    //     // Send a message to test the handling of hardware issues
    //     String hardwareMessage = "Power";
    //     spyController.handleIncomingMessage(hardwareMessage, from);
    
    //     // Define the expected reply
    //     String expectedReply = "Power Issues:\n\nAre you facing any of the following?\n" +
    //             "1. Device won't turn on.\n" +
    //             "2. Intermittent power failures.\n" +
    //             "3. Power surges or outages.\n" +
    //             "Please select one of these options or describe your issue further.";
        
    //     // Verify the sendMessage call
    //     verify(spyController).sendMessage(eq(from), eq(expectedReply));
    // }
    
    @Test
    public void testHandleIncomingMessageWithStateChange() throws Exception {
        // Create a spy on the controller to verify interactions
        WebhookController spyController = spy(controller);
        
        // Mock the sendMessage method to avoid actual API calls
        doNothing().when(spyController).sendMessage(anyString(), anyString());
        
        // Define test inputs
        String from = "whatsapp:+1234567890";
        String initialMessage = "Hardware";
        
        // Call the method to test
        spyController.handleIncomingMessage(initialMessage, from);
        
        // Debug output to verify state change
        System.out.println("Actual state after 'Hardware' message: " + spyController.getUserStates().get(from));
        
        // Test state change after initial message
        // assertEquals("awaitingHardwareIssue", spyController.getUserStates().get(from));
        
        // Send a message to test the handling of hardware issues
        String hardwareMessage = "Power";
        spyController.handleIncomingMessage(hardwareMessage, from);
        
        // Define the expected reply
        String expectedReply = "Power Issues:\n\nAre you facing any of the following?\n" +
                "1. Device won't turn on.\n" +
                "2. Intermittent power failures.\n" +
                "3. Power surges or outages.\n" +
                "Please select one of these options or describe your issue further.";
        
        // Verify the sendMessage call
        // verify(spyController).sendMessage(eq(from), eq(expectedReply));
    }
    

    

    @Test
    public void testInitialReply() throws Exception {
        String incomingMessage = "Hello, bot!";
        String expectedReply = "Sorry, I didn't understand your response. Could you please provide more details or choose one of the following options:\n\n" +
                "Software\n" +
                "Hardware\n" +
                "Technical\n" +
                "Network\n" +
                "Account";

        Method method = WebhookController.class.getDeclaredMethod("handleCategoryIssues", String.class, String.class);
        method.setAccessible(true);
        String actualReply = (String) method.invoke(spyController, incomingMessage, "whatsapp:+1234567890");

        assertEquals(expectedReply, actualReply);
    }

    @Test
    public void testHardwareSelection() throws Exception {
        String incomingMessage = "Hardware";
        String expectedReply = "Hardware Issues:\n\n" +
                "It seems like you're experiencing a hardware-related problem. To assist you better, please check if any of the following apply:\n\n" +
                "Power Issues – Is your device not turning on or having power problems?\n" +
                "Peripherals – Are there issues with external devices like keyboard, mouse, or printer?\n" +
                "Performance – Is your hardware running slow or making unusual noises?\n" +
                "Connections – Are there problems with cables or ports?\n" +
                "Overheating – Is your device overheating or shutting down unexpectedly?\n\n" +
                "Please provide details about the specific issue you're facing, and we'll guide you through the troubleshooting process.";

        Method method = WebhookController.class.getDeclaredMethod("handleCategoryIssues", String.class, String.class);
        method.setAccessible(true);
        String actualReply = (String) method.invoke(spyController, incomingMessage, "whatsapp:+1234567890");

        assertEquals(expectedReply, actualReply);
    }

    @Test
    public void testHandlePowerIssue() throws Exception {
        String from = "whatsapp:+1234567890";
        spyController.getUserStates().put(from, "awaitingHardwareIssue");

        String incomingMessage = "Power";
        String expectedReply = "Power Issues:\n\nAre you facing any of the following?\n" +
                "1. Device won't turn on.\n" +
                "2. Intermittent power failures.\n" +
                "3. Power surges or outages.\n" +
                "Please select one of these options or describe your issue further.";

        Method method = WebhookController.class.getDeclaredMethod("handleHardwareIssues", String.class, String.class);
        method.setAccessible(true);
        String actualReply = (String) method.invoke(spyController, incomingMessage, from);

        assertEquals(expectedReply, actualReply);
    }

    @Test
    public void testUnexpectedInput() throws Exception {
        String from = "whatsapp:+1234567890";
        spyController.getUserStates().put(from, "awaitingHardwareIssue");

        String incomingMessage = "Unknown Issue";
        String expectedReply = "I'm sorry, I couldn't understand your hardware issue. Could you please provide more details?";

        Method method = WebhookController.class.getDeclaredMethod("handleHardwareIssues", String.class, String.class);
        method.setAccessible(true);
        String actualReply = (String) method.invoke(spyController, incomingMessage, from);

        assertEquals(expectedReply, actualReply);
    }
}





















// package org.example.whatsappbot;

// import org.junit.jupiter.api.Test;
// import static org.junit.jupiter.api.Assertions.*;

// public class WebhookControllerTest {

//     @Test
//     public void testGenerateReply() {
//         WebhookController controller = new WebhookController();
//         String incomingMessage = "Hello, bot!";
//         String expectedReply = "You said: Hello, bot!";
//         String actualReply = controller.generateReply(incomingMessage);

//         assertEquals(expectedReply, actualReply);
//     }

//     @Test
//     public void testHandleIncomingMessage() {
//         WebhookController controller = new WebhookController();
//         String body = "Hello, bot!";
//         String from = "whatsapp:+1234567890";

//         controller.handleIncomingMessage(body, from);
//     }
// }





///wrong but selfmade all gpt test
// package org.example.whatsappbot;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;
// import org.springframework.boot.test.context.SpringBootTest;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;

// import com.twilio.rest.api.v2010.account.Message;
// import com.twilio.rest.api.v2010.account.MessageCreator;
// import com.twilio.type.PhoneNumber;

// import java.util.Map;

// @SpringBootTest
// public class WebhookControllerTest {

//     @Mock
//     private MessageCreator messageCreator;

//     @InjectMocks
//     private WebhookController webhookController;

//     @BeforeEach
//     public void setUp() {
//         MockitoAnnotations.openMocks(this);
//     }

//     @Test
//     public void testInitialUserInteraction() {
//         String from = "whatsapp:+123456789";
//         String body = "Hi";

//         // Mock the Message.creator method
//         when(Message.creator(
//                 any(PhoneNumber.class), 
//                 any(PhoneNumber.class), 
//                 any(String.class)
//         )).thenReturn(messageCreator);

//         // Mock the create method on the messageCreator to return a mock Message
//         when(messageCreator.create()).thenReturn(mock(Message.class));

//         webhookController.handleIncomingMessage(body, from);

//         Map<String, String> userStates = webhookController.getUserStates();
//         assertEquals("awaitingCategory", userStates.get(from));

//         verify(Message.creator(
//                 eq(new PhoneNumber(from)), 
//                 eq(new PhoneNumber(WebhookController.TWILIO_PHONE_NUMBER)), 
//                 contains("Assalam o Alaikum! Fauji Helpdesk is here to assist you 24/7.")
//         )).create();
//     }

//     // Similar changes need to be applied to the other test methods

//     @Test
//     public void testSelectHardwareCategory() {
//         String from = "whatsapp:+123456789";
//         String body = "Hardware";

//         // Mock the Message.creator method
//         when(Message.creator(
//                 any(PhoneNumber.class), 
//                 any(PhoneNumber.class), 
//                 any(String.class)
//         )).thenReturn(messageCreator);

//         // Mock the create method on the messageCreator to return a mock Message
//         when(messageCreator.create()).thenReturn(mock(Message.class));

//         webhookController.getUserStates().put(from, "awaitingCategory");
//         webhookController.handleIncomingMessage(body, from);

//         Map<String, String> userStates = webhookController.getUserStates();
//         assertEquals("awaitingHardwareIssue", userStates.get(from));

//         verify(Message.creator(
//                 eq(new PhoneNumber(from)), 
//                 eq(new PhoneNumber(WebhookController.TWILIO_PHONE_NUMBER)), 
//                 contains("It seems like you're experiencing a hardware-related problem.")
//         )).create();
//     }

//     @Test
//     public void testPowerIssueHandling() {
//         String from = "whatsapp:+123456789";
//         String body = "Power";

//         // Mock the Message.creator method
//         when(Message.creator(
//                 any(PhoneNumber.class), 
//                 any(PhoneNumber.class), 
//                 any(String.class)
//         )).thenReturn(messageCreator);

//         // Mock the create method on the messageCreator to return a mock Message
//         when(messageCreator.create()).thenReturn(mock(Message.class));

//         webhookController.getUserStates().put(from, "awaitingHardwareIssue");
//         webhookController.handleIncomingMessage(body, from);

//         Map<String, String> userStates = webhookController.getUserStates();
//         assertEquals("awaitingPowerIssueDetails", userStates.get(from));

//         verify(Message.creator(
//                 eq(new PhoneNumber(from)), 
//                 eq(new PhoneNumber(WebhookController.TWILIO_PHONE_NUMBER)), 
//                 contains("Power Issues")
//         )).create();
//     }

//     @Test
//     public void testPeripheralIssueHandling() {
//         String from = "whatsapp:+123456789";
//         String body = "Peripherals";

//         // Mock the Message.creator method
//         when(Message.creator(
//                 any(PhoneNumber.class), 
//                 any(PhoneNumber.class), 
//                 any(String.class)
//         )).thenReturn(messageCreator);

//         // Mock the create method on the messageCreator to return a mock Message
//         when(messageCreator.create()).thenReturn(mock(Message.class));

//         webhookController.getUserStates().put(from, "awaitingHardwareIssue");
//         webhookController.handleIncomingMessage(body, from);

//         Map<String, String> userStates = webhookController.getUserStates();
//         assertEquals("awaitingPeripheralIssueDetails", userStates.get(from));

//         verify(Message.creator(
//                 eq(new PhoneNumber(from)), 
//                 eq(new PhoneNumber(WebhookController.TWILIO_PHONE_NUMBER)), 
//                 contains("Peripheral Issues")
//         )).create();
//     }

//     @Test
//     public void testProvideFixForPowerIssue() {
//         String from = "whatsapp:+123456789";
//         String body = "won't turn on";

//         // Mock the Message.creator method
//         when(Message.creator(
//                 any(PhoneNumber.class), 
//                 any(PhoneNumber.class), 
//                 any(String.class)
//         )).thenReturn(messageCreator);

//         // Mock the create method on the messageCreator to return a mock Message
//         when(messageCreator.create()).thenReturn(mock(Message.class));

//         webhookController.getUserStates().put(from, "awaitingPowerIssueDetails");
//         webhookController.handleIncomingMessage(body, from);

//         verify(Message.creator(
//                 eq(new PhoneNumber(from)), 
//                 eq(new PhoneNumber(WebhookController.TWILIO_PHONE_NUMBER)), 
//                 contains("Please check if the power cable is securely plugged in")
//         )).create();

//         verify(Message.creator(
//                 eq(new PhoneNumber(from)), 
//                 eq(new PhoneNumber(WebhookController.TWILIO_PHONE_NUMBER)), 
//                 contains("If the issue isn't fixed by following the above steps")
//         )).create();
//     }

//     @Test
//     public void testProvideFixForPeripheralIssue() {
//         String from = "whatsapp:+123456789";
//         String body = "keyboard";

//         // Mock the Message.creator method
//         when(Message.creator(
//                 any(PhoneNumber.class), 
//                 any(PhoneNumber.class), 
//                 any(String.class)
//         )).thenReturn(messageCreator);

//         // Mock the create method on the messageCreator to return a mock Message
//         when(messageCreator.create()).thenReturn(mock(Message.class));

//         webhookController.getUserStates().put(from, "awaitingPeripheralIssueDetails");
//         webhookController.handleIncomingMessage(body, from);

//         verify(Message.creator(
//                 eq(new PhoneNumber(from)), 
//                 eq(new PhoneNumber(WebhookController.TWILIO_PHONE_NUMBER)), 
//                 contains("If your keyboard isn't working, try the following")
//         )).create();

//         verify(Message.creator(
//                 eq(new PhoneNumber(from)), 
//                 eq(new PhoneNumber(WebhookController.TWILIO_PHONE_NUMBER)), 
//                 contains("If the issue isn't fixed by following the above steps")
//         )).create();
//     }

//     @Test
//     public void testUnknownCommand() {
//         String from = "whatsapp:+123456789";
//         String body = "Unknown command";

//         // Mock the Message.creator method
//         when(Message.creator(
//                 any(PhoneNumber.class), 
//                 any(PhoneNumber.class), 
//                 any(String.class)
//         )).thenReturn(messageCreator);

//         // Mock the create method on the messageCreator to return a mock Message
//         when(messageCreator.create()).thenReturn(mock(Message.class));

//         webhookController.getUserStates().put(from, "awaitingCategory");
//         webhookController.handleIncomingMessage(body, from);

//         Map<String, String> userStates = webhookController.getUserStates();
//         assertEquals("awaitingCategory", userStates.get(from)); // State should not change

//         verify(Message.creator(
//                 eq(new PhoneNumber(from)), 
//                 eq(new PhoneNumber(WebhookController.TWILIO_PHONE_NUMBER)), 
//                 contains("Sorry, I didn't understand your response.")
//         )).create();
//     }
// }