package MVC;

import Objects.Message;

public class Model {

    public Message acknowledgementMessage(String operator) {
    	Message acknowledgementRequest = null;
    	
    	switch(operator) {
	    	case "+":
	        {
	        	acknowledgementRequest = new Message (10 , 0); 
	            break;
	        }
	        case "-":
	        {
	        	acknowledgementRequest = new Message(11 , 0);
	            break;
	        }
	        case "*":
	        {
	        	acknowledgementRequest = new Message(12 , 0);
	            break;
	        }
	        case "/":
	        {
	            acknowledgementRequest = new Message(13 , 0);
	            break;
	        }
    	}
    	return acknowledgementRequest;
    }
	
	public Message operationMessage(long number1, long number2, String operator) {
        Message operationRequest = null;
    	switch (operator) {
            case "+":
            {
                operationRequest = new Message (5, number1, number2, operator); 
                break;
            }
            case "-":
            {
                operationRequest = new Message(6, number1, number2, operator);
                break;
            }
            case "*":
            {
                operationRequest = new Message(7,number1, number2, operator);
                break;
            }
            case "/":
            {
                if (number2 == 0) 
                {
                	operationRequest = new Message(-2 ,number1, number2, operator);
                	operationRequest.updateOriginal();
                }
                operationRequest = new Message(8, number1, number2, operator);
            }
            break;
        }

        //System.out.println("Unknown operator - " + operator);
    	
        return operationRequest;
    }
}
