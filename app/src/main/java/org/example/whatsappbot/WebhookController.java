package org.example.whatsappbot;

import org.springframework.web.bind.annotation.*;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.util.HashMap;
import java.util.Map;

@RestController
public class WebhookController {

    // Twilio credentials
    public static final String ACCOUNT_SID = "AC19860b0adc196d470bac20ea678b36df"; // Replace with your accound sID
    public static final String AUTH_TOKEN = "7cfe5157cf55b6abb6d8aa10cb09c9d8"; // Replace with your Auth Token
    public static final String TWILIO_PHONE_NUMBER = "whatsapp:+14155238886"; // Replace with your Twilio phone number for WhatsApp

    static {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    // In-memory store to keep track of user states
    private Map<String, String> userStates = new HashMap<>();

    public Map<String, String> getUserStates() {
        return userStates;
    }
    
    @PostMapping("/incoming")
    public void handleIncomingMessage(@RequestParam("Body") String body, @RequestParam("From") String from) {
        System.out.println("Received message from " + from + ": " + body);
    
        String replyMessage;
        String userState = userStates.getOrDefault(from, "initial");
    
        if (userState.equals("initial") || body.toLowerCase().contains("help")) {
            replyMessage = "Assalam o Alaikum! Fauji Helpdesk is here to assist you 24/7. Please let us know the issue you're facing by selecting one of the following options:\n\n" +
                "Software – Issues related to software applications or programs.\n" +
                "Hardware – Problems with physical devices or components.\n" +
                "Technical – Technical difficulties or troubleshooting.\n" +
                "Network – Issues with internet or network connectivity.\n" +
                "Account – Problems with your account or login details.\n\n" +
                "Feel free to choose the relevant option or provide any additional details so we can assist you better.";
            userStates.put(from, "awaitingCategory");
        } else {
            // Handle the specific issue based on the user's response
            switch (userState) {
                case "awaitingHardwareIssue":
                    replyMessage = handleHardwareIssues(body, from);
                    break;
                case "awaitingPowerIssueDetails":
                    // Get the fix suggestion
                    replyMessage = provideFixForPowerIssue(body, from);
                    
                    // Check if we need to send a follow-up message
                    if (replyMessage.equals("Please provide more details about the power issue you're facing.")) {
                        // Send the initial response
                        sendMessage(from, replyMessage);
                    } else {
                        // Send the fix suggestion
                        sendMessage(from, replyMessage);
                        
                        // Send the follow-up message
                        String followUpMessage = "If the issue isn't fixed by following the above steps, please reply with 'Not fixed' so I can assist you further.";
                        sendMessage(from, followUpMessage);
                    }
                    
                    // Return early to avoid sending the same message again
                    return;
                    
                case "awaitingPeripheralIssueDetails":
                    replyMessage = provideFixForPeripheralIssue(body, from);
                    // Check if we need to send a follow-up message
                    if (replyMessage.equals("Please provide more details about the perippheral issue you're facing.")) {
                        // Send the initial response
                        sendMessage(from, replyMessage);
                    } else {
                        // Send the fix suggestion
                        sendMessage(from, replyMessage);
                        
                        // Send the follow-up message
                        String followUpMessage = "If the issue isn't fixed by following the above steps, please reply with 'Not fixed' so I can assist you further.";
                        sendMessage(from, followUpMessage);
                    }
                    
                    // Return early to avoid sending the same message again
                    return;
                    
                default:
                    replyMessage = handleCategoryIssues(body, from);
            }
        }
    
        System.out.println("Reply: " + replyMessage);
        
        // Send the response only if no follow-up messages were sent
        if (userState.equals("awaitingPowerIssueDetails") || userState.equals("awaitingPeripheralIssueDetails") ) {
            // Do nothing here, as messages are already sent in the case above
        } else {
            sendMessage(from, replyMessage);
        }
    }

    private String handleCategoryIssues(String incomingMessage, String from) {
        // Normalize the message to lower case
        String message = incomingMessage.toLowerCase();

        if (message.contains("hardware")) {
            userStates.put(from, "awaitingHardwareIssue");
            return "Hardware Issues:\n\n" +
                "It seems like you're experiencing a hardware-related problem. To assist you better, please check if any of the following apply:\n\n" +
                "Power Issues – Is your device not turning on or having power problems?\n" +
                "Peripherals – Are there issues with external devices like keyboard, mouse, or printer?\n" +
                "Performance – Is your hardware running slow or making unusual noises?\n" +
                "Connections – Are there problems with cables or ports?\n" +
                "Overheating – Is your device overheating or shutting down unexpectedly?\n\n" +
                "Please provide details about the specific issue you're facing, and we'll guide you through the troubleshooting process.";
        } else if (message.contains("software")) {
            return "Software Issues:\n\n" +
                "It seems like you're experiencing a software-related problem. To assist you better, please let us know if any of the following issues apply:\n\n" +
                "Installation – Are you having trouble installing or updating software?\n" +
                "Crashes – Is the software crashing or freezing unexpectedly?\n" +
                "Errors – Are you encountering specific error messages or codes?\n" +
                "Functionality – Are there features or functions not working as expected?\n" +
                "Compatibility – Is there an issue with software compatibility or integration?\n\n" +
                "Please provide details about the specific software problem you're facing, and we'll help you resolve it.";
        } else if (message.contains("technical")) {
            return "Technical Issues:\n\n" +
                "It seems like you're facing technical difficulties. Please provide details about the issue you're encountering, and we'll assist you with troubleshooting steps.";
        } else if (message.contains("network")) {
            return "Network Issues:\n\n" +
                "It seems like you're experiencing network-related problems. To assist you better, please let us know if any of the following issues apply:\n\n" +
                "Connection – Are you unable to connect to the internet?\n" +
                "Speed – Is your internet connection slower than expected?\n" +
                "Stability – Are you experiencing frequent disconnections or interruptions?\n\n" +
                "Please provide details about the network issue you're facing, and we'll help you resolve it.";
        } else if (message.contains("account")) {
            return "Account Issues:\n\n" +
                "It seems like you're facing issues with your account. To assist you better, please let us know if you have any of the following issues:\n\n" +
                "Login – Are you having trouble logging into your account?\n" +
                "Password – Do you need to reset your password or have trouble with authentication?\n" +
                "Profile – Are you experiencing issues with your account profile or settings?\n\n" +
                "Please provide details about the account issuepoiur you're facing, and we'll help you resolve it.";
        } else {
            return "Sorry, I didn't understand your response. Could you please provide more details or choose one of the following options:\n\n" +
                "Software\n" +
                "Hardware\n" +
                "Technical\n" +
                "Network\n" +
                "Account";
        }
    }

    private String handleHardwareIssues(String incomingMessage, String from) {
        // Normalize the message to lower case
        String message = incomingMessage.toLowerCase();

        if (message.contains("power")) {
            userStates.put(from, "awaitingPowerIssueDetails");
            return "Power Issues:\n\nAre you facing any of the following?\n" +
                "1. Device won't turn on.\n" +
                "2. Intermittent power failures.\n" +
                "3. Power surges or outages.\n" +
                "Please select one of these options or describe your issue further.";
        } else if (message.contains("peripherals")) {
            userStates.put(from, "awaitingPeripheralIssueDetails");
            return "Peripheral Issues:\n\nAre you having trouble with any external devices like keyboard, mouse, or printer? " +
                "Please describe the issue you're facing.";
        } else if (message.contains("performance")) {
            // Handle performance issues here
            return "Performance Issues:\n\nIs your hardware running slow or making unusual noises? " +
                "Please provide more details about the performance issue you're facing.";
        } else if (message.contains("connections")) {
            // Handle connection issues here
            return "Connection Issues:\n\nAre there problems with cables or ports? " +
                "Please provide more details about the connection issue you're facing.";
        } else if (message.contains("overheating")) {
            // Handle overheating issues here
            return "Overheating Issues:\n\nIs your device overheating or shutting down unexpectedly? " +
                "Please provide more details about the overheating issue you're facing.";
        } else {
            return "I'm sorry, I couldn't understand your hardware issue. Could you please provide more details?";
        }
    }

   
    
    private String provideFixForPowerIssue(String incomingMessage, String from) {
        incomingMessage = incomingMessage.toLowerCase();
        String replyMessage;
    
        if (incomingMessage.contains("won't turn on")) {
            replyMessage = "Please check if the power cable is securely plugged in and the outlet is working. " +
                "If the device still won't turn on, try using a different power cable.";
        } else if (incomingMessage.contains("intermittent")) {
            replyMessage = "Intermittent power failures might be due to loose connections or a faulty power supply. " +
                "Ensure all cables are securely connected and consider replacing the power supply if the issue persists.";
        } else if (incomingMessage.contains("surges")) {
            replyMessage = "Power surges can damage hardware components. Consider using a surge protector, and if the issue persists, " +
                "you may need to have the hardware inspected by a professional.";
        } else {
            replyMessage = "Please provide more details about the power issue you're facing.";
        }
    
        // Combine the fix suggestion with the follow-up message
      //  replyMessage += "\n\nIf the issue isn't fixed by following the above steps, please reply with 'Not fixed' so I can assist you further.";
    
        // Update the user state to avoid repetition
        userStates.put(from, "awaitingFurtherAssistance");
    
        return replyMessage;
    }
    
    private String provideFixForPeripheralIssue(String incomingMessage, String from) {
        incomingMessage = incomingMessage.toLowerCase();
        String replyMessage;
    
        if (incomingMessage.contains("keyboard")) {
            replyMessage = "If your keyboard isn't working, try the following:\n" +
                "1. Check if the keyboard is properly connected to the computer.\n" +
                "2. Try using a different USB port.\n" +
                "3. Test the keyboard on another computer to see if it works there.\n" +
                "If the issue persists, you may need to replace the keyboard.";
        } else if (incomingMessage.contains("mouse")) {
            replyMessage = "If your mouse isn't working, try the following:\n" +
                "1. Check if the mouse is properly connected to the computer.\n" +
                "2. Try using a different USB port.\n" +
                "3. Test the mouse on another computer to see if it works there.\n" +
                "If the issue persists, you may need to replace the mouse.";
        } else if (incomingMessage.contains("printer")) {
            replyMessage = "If your printer is having issues, try the following:\n" +
                "1. Check if the printer is properly connected and powered on.\n" +
                "2. Make sure there is paper in the tray and the ink or toner is sufficient.\n" +
                "3. Restart the printer and try printing again.\n" +
                "If the issue persists, consult the printer's manual or contact support.";
        } else {
            replyMessage = "Please provide more details about the peripheral issue you're facing, such as the type of device and the specific problem.";
        }
    
        // Combine the fix suggestion with the follow-up message
      //  replyMessage += "\n\nIf the issue isn't fixed by following the above steps, please reply with 'Not fixed' so I can assist you further.";
    
        // Update the user state to avoid repetition
        userStates.put(from, "awaitingFurtherAssistance");
    
        return replyMessage;
    }
    
    

    protected void sendMessage(String to, String message) {
        Message.creator(
            new PhoneNumber(to), // To
            new PhoneNumber(TWILIO_PHONE_NUMBER), // From
            message
        ).create();
    }
}


//this below code can be modified accordingly

// package org.example.whatsappbot;

// import org.springframework.web.bind.annotation.*;

// import com.twilio.Twilio;
// import com.twilio.rest.api.v2010.account.Message;
// import com.twilio.type.PhoneNumber;

// @RestController
// public class WebhookController {

//     // Twilio credentials
//     public static final String ACCOUNT_SID = "your_account_sid";
//     public static final String AUTH_TOKEN = "your_auth_token";

//     static {
//         Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
//     }

//     @PostMapping("/webhook")
//     public void handleIncomingMessage(@RequestParam("Body") String body, @RequestParam("From") String from) {
//         System.out.println("Received message from " + from + ": " + body);
//         String replyMessage = generateReply(body);
//         System.out.println("Reply: " + replyMessage);
//     }

//     public String generateReply(String incomingMessage) {
//         // Simple echo bot for demonstration
//         return "You said: " + incomingMessage;
//     }
// }
