package MVC;

import Objects.Message;
import Objects.acknowlegmentTable;
import Objects.readConfig;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class interfaceHandler implements Runnable {
    public Socket connectedSocket;
    public Controller fatherInterface;
    public String currentOperation;
    public ObjectOutputStream oos;
    public ObjectInputStream ois;
    boolean isAlive = true;
    int Min = 5000;
    int Max = 5100;
    acknowlegmentTable ack = new acknowlegmentTable();
    int requiredSum;
    int requiredSub;
    int requiredMul;
    int requiredDiv;

    public interfaceHandler(Controller c) {
        this.fatherInterface = c;
        readConfig newConfiguration = new readConfig();

        try {
            int[] initialConfig = newConfiguration.loadConfig();
            this.requiredSum = initialConfig[0];
            this.requiredSub = initialConfig[1];
            this.requiredMul = initialConfig[2];
            this.requiredDiv = initialConfig[3];
        } catch (FileNotFoundException var4) {
            var4.printStackTrace();
        }

    }

    public void connectToDataField() {
        for(int port = this.Min; port <= this.Max; ++port) {
            try {
                this.connectedSocket = new Socket("127.0.0.1", port);
                System.out.println("Found connection at port " + port);
                break;
            } catch (UnknownHostException var3) {
            } catch (IOException var4) {
            }
        }

    }

    public void run() {
        this.connectToDataField();

        try {
            this.ois = new ObjectInputStream(this.connectedSocket.getInputStream());
            this.oos = new ObjectOutputStream(this.connectedSocket.getOutputStream());
            this.reportAliveStatus();
        } catch (IOException var3) {
            var3.printStackTrace();
        }

        try {
            this.getMessage();
        } catch (ClassNotFoundException var2) {
            System.out.println("Coudln't listen for another message");
        }

    }

    public void reportAliveStatus() {
        Message msg = new Message(2);
        System.out.println("Sent initial Message with the content " + msg.getMsg());
        this.forwardMessage(msg);
    }

    public void setOperation(String s) {
        this.currentOperation = s;
        this.fatherInterface.setDesc("Id: " + this.currentOperation + "\n was assigned to the current operation. ");
    }

    public void forwardMessage(Message msg) {
        try {
            Message cloneReq;
            switch(msg.getContentCode()) {
            case 5:
                if (this.ack.getSumAcknowledgements() < this.requiredSum) {
                    System.out.println("The number of acknowledgements is inferior to the required one. ");
                    cloneReq = new Message();
                    cloneReq.setContentCode(14);
                    cloneReq.setMessage("s");
                    cloneReq.setFirst((double)this.requiredSum);
                    this.oos.writeObject(cloneReq);

                    try {
                        TimeUnit.SECONDS.sleep(1L);
                    } catch (InterruptedException var7) {
                        var7.printStackTrace();
                    }
                }
                break;
            case 6:
                if (this.ack.getSumAcknowledgements() < this.requiredSub) {
                    System.out.println("The number of acknowledgements is inferior to the required one. ");
                    cloneReq = new Message();
                    cloneReq.setContentCode(14);
                    cloneReq.setMessage("r");
                    cloneReq.setFirst((double)this.requiredSub);
                    this.oos.writeObject(cloneReq);

                    try {
                        TimeUnit.SECONDS.sleep(1L);
                    } catch (InterruptedException var6) {
                        var6.printStackTrace();
                    }
                }
                break;
            case 7:
                if (this.ack.getSumAcknowledgements() < this.requiredMul) {
                    System.out.println("The number of acknowledgements is inferior to the required one. ");
                    cloneReq = new Message();
                    cloneReq.setContentCode(14);
                    cloneReq.setMessage("d");
                    cloneReq.setFirst((double)this.requiredMul);
                    this.oos.writeObject(cloneReq);

                    try {
                        TimeUnit.SECONDS.sleep(1L);
                    } catch (InterruptedException var5) {
                        var5.printStackTrace();
                    }
                }
                break;
            case 8:
                if (this.ack.getSumAcknowledgements() < this.requiredDiv) {
                    System.out.println("The number of acknowledgements is inferior to the required one. ");
                    cloneReq = new Message();
                    cloneReq.setContentCode(14);
                    cloneReq.setMessage("d");
                    cloneReq.setFirst((double)this.requiredDiv);
                    this.oos.writeObject(cloneReq);

                    try {
                        TimeUnit.SECONDS.sleep(1L);
                    } catch (InterruptedException var4) {
                        var4.printStackTrace();
                    }
                }
            }

            this.oos.writeObject(msg);
        } catch (IOException var8) {
            var8.printStackTrace();
        }

    }

    public void getMessage() throws ClassNotFoundException {
        System.out.println("Listening for messages");

        while(this.isAlive) {
            try {
                Message receivedMessage = (Message)this.ois.readObject();
                System.out.println("Received message " + receivedMessage.getMsg());
                this.processObject(receivedMessage);
            } catch (IOException var3) {
                System.out.println("Error reading message");
                break;
            }
        }

    }

    public void processObject(Message msg) {
        int objCase = msg.getContentCode();
        switch(objCase) {
        case -1:
            System.out.println("Message received " + msg.getMsg());
        case 0:
        default:
            break;
        case 1:
        case 2:
        case 3:
        case 5:
        case 6:
        case 7:
        case 8:
            System.out.println("Message arrived with the content +" + msg.getMsg());
            System.out.println("Message was dropped");
            break;
        case 4:
            System.out.println("Message received acknowledment from: \n" + msg.getEventId());
            String var3;
            switch((var3 = msg.getMsg()).hashCode()) {
            case 100:
                if (var3.equals("d")) {
                    this.ack.divList(msg.getEventId(), (int)msg.getFirst());
                }

                return;
            case 109:
                if (var3.equals("m")) {
                    this.ack.mulList(msg.getEventId(), (int)msg.getFirst());
                }

                return;
            case 114:
                if (var3.equals("r")) {
                    this.ack.subList(msg.getEventId(), (int)msg.getFirst());
                }

                return;
            case 115:
                if (var3.equals("s")) {
                    this.ack.sumList(msg.getEventId(), (int)msg.getFirst());
                }

                return;
            default:
                return;
            }
        case 9:
            if (msg.getEventId().equals(this.currentOperation)) {
                this.fatherInterface.setAnswer(msg.getMsg());
            } else {
                System.out.println(" The id " + msg.getEventId() + " =! " + this.currentOperation);
            }

            System.out.println("Message arrived with the response of a previous operation: " + msg.getMsg());
        }

    }
}
