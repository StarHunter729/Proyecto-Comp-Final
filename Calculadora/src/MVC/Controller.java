package MVC;

import Objects.Message;
import Objects.acknowlegmentTable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class Controller {

    @FXML
    private Text output;
    @FXML
    private Text outputDesc;
    private long number1 = 0;
    private String operator = "";
    private boolean start = true;
    private acknowlegmentTable table;
    private Model model = new Model();
    private boolean threadUp = false;
    private interfaceHandler handler = new interfaceHandler(this);
   
    @FXML
    private void processNumpad(ActionEvent event) {
    	checkHandler();
    	if ( start ) {
            output.setText("");
            start = false;
        }
        String value = ((Button)event.getSource()).getText();
        output.setText(output.getText() + value);
    }

    private void checkHandler() {
    	if( !threadUp ) {
    		new Thread(handler).start();
    		threadUp = true;
		}		
    }

    
	@FXML
    private void processOperator(ActionEvent event) {
        String value = ((Button)event.getSource()).getText();

        if (!"=".equals(value)) {
            if (!operator.isEmpty())
                return;

            operator = value;
            number1 = Long.parseLong(output.getText());
            Message msg = model.acknowledgementMessage(operator);
            handler.forwardMessage(msg);
            setDesc("A request for '" + operator + "' \nacknowledgements has been sent");
            output.setText("");
        }
        else {
            if (operator.isEmpty())
                return;
            Message msg = model.operationMessage(number1, Long.parseLong(output.getText()), operator);
            handler.setOperation(msg.getEventId());
            handler.forwardMessage(msg);
            setDesc(msg.getMsg());
            operator = "";
            start = true;
        }
    }
	
    @FXML
	public void setAnswer(String s)
	{
		Platform.runLater(new Runnable()
		{
			@Override
			public void run() {
				setDesc("Got the result of the operation ");
				output.setText(s);
			}
		});
	}
	
    public void setDesc(String s)
	{
		Platform.runLater(new Runnable()
		{
			@Override
			public void run() {
				outputDesc.setText(outputDesc.getText() + '\n' +s);
			}
		});
	}
}