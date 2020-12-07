package ACP.Operations;

import Objects.Message;
import Services.Substract;
import fileHandler.Clonation;
import fileHandler.fileValidation;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.UnknownHostException;

public class subHandler {
    int Min = 5000;
    int Max = 5010;
    int localPort;
    Socket localSocket;
    ObjectOutputStream oos;
    ObjectInputStream ois;
    String fingerPrint = getProcessId();
    String temporalOperaton = "";
    double numSub = 0.0D;
    String fileFldr = "C:/temp/Services/Substract";
    String file = "C:/temp/Services/Substract/Substract.jar";
    String rawFile = "C:/temp/Services/Substract/Substract-Clone";

    public subHandler() {
        this.numSub = (double)fileValidation.getNumServices(this.fileFldr);
    }

    public void outputStream() {
        try {
            this.oos = new ObjectOutputStream(this.localSocket.getOutputStream());
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }

    public void connectToDataField() {
        for(int port = this.Min; port <= this.Max; ++port) {
            try {
                this.localSocket = new Socket("127.0.0.1", port);
                this.localPort = port;
                break;
            } catch (UnknownHostException var3) {
                var3.printStackTrace();
            } catch (IOException var4) {
                var4.printStackTrace();
            }
        }

    }

    public void reportAliveStatus() {
        Message msg = new Message(3);
        this.forwardMessage(msg);
        System.out.println("Sent initial message");
    }

    public void forwardMessage(Message msg) {
        try {
            this.oos.writeObject(msg);
            System.out.println("A message was sent with the msg being " + msg.getMsg());
        } catch (IOException var3) {
            System.out.println("Could not send message");
            var3.printStackTrace();
        }

    }

    public void getMessage() throws ClassNotFoundException {
        System.out.println("Listening for messages");

        try {
            this.ois = new ObjectInputStream(this.localSocket.getInputStream());
        } catch (IOException var3) {
            System.out.println("Lost connection");
        }

        while(true) {
            while(true) {
                try {
                    Message receivedMessage = (Message)this.ois.readObject();
                    System.out.println("Received message " + receivedMessage.getMsg() + " CC " + receivedMessage.getContentCode());
                    this.processObject(receivedMessage);
                } catch (IOException var4) {
                    System.out.println("Error reading message");
                }
            }
        }
    }

    public void processObject(Message msg) {
        if (this.temporalOperaton != msg.getEventId()) {
            this.temporalOperaton = msg.getEventId();
        }

        switch(msg.getContentCode()) {
        case 6:
            double result = substractionController(msg.getFirst(), msg.getSecond());
            System.out.println("Substract.jar has received the parameters: " + msg.getFirst() + " " + msg.getSecond());
            Message resp = new Message(result, msg);
            resp.setEventId(this.temporalOperaton);
            this.forwardMessage(resp);
            break;
        case 11:
            System.out.println("Got a request for sub acknowledgement ");
            Message ackMessage = new Message();
            ackMessage.setContentCode(4);
            ackMessage.setEventId(this.fingerPrint);
            ackMessage.setFirst(this.numSub);
            ackMessage.setMsg("r");
            this.forwardMessage(ackMessage);
            break;
        case 14:
            int tempCells = 0;
            String var3;
            switch((var3 = msg.getMsg()).hashCode()) {
            case 114:
                if (var3.equals("r")) {
                    tempCells = (int)this.numSub;
                }
            }

            if (msg.getFirst() > this.numSub) {
                for(int i = tempCells; (double)i < msg.getFirst() - this.numSub; ++i) {
                    File from = new File(this.file);
                    File to = Clonation.validateClone(this.file, this.rawFile);

                    try {
                        Clonation.copy(from, to);
                    } catch (IOException var8) {
                        var8.printStackTrace();
                    }
                }
            }

            this.numSub = (double)fileValidation.getNumServices(this.fileFldr);
        }

    }

    public static double substractionController(double val1, double val2) {
        double ans = 0.0D;
        double num1 = val1;
        double num2 = val2;

        try {
            URL[] classLoaderUrls = new URL[]{new URL("file:C:/temp/Services/Substract")};
            URLClassLoader urlClassLoader = new URLClassLoader(classLoaderUrls);
            Class<?> subClass = urlClassLoader.loadClass("Services.Substract");
            Substract subFunction = (Substract)subClass.newInstance();
            ans = subFunction.substractOperation(num1, num2);
            System.out.println(ans);
        } catch (MalformedURLException var14) {
            var14.printStackTrace();
        } catch (ClassNotFoundException var15) {
            var15.printStackTrace();
        } catch (IllegalArgumentException var16) {
            var16.printStackTrace();
        } catch (InstantiationException var17) {
            var17.printStackTrace();
        } catch (IllegalAccessException var18) {
            var18.printStackTrace();
        }

        return ans;
    }

    private static String getProcessId() {
        String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        int index = jvmName.indexOf(64);
        if (index < 1) {
            return "";
        } else {
            try {
                return Long.toString(Long.parseLong(jvmName.substring(0, index)));
            } catch (NumberFormatException var3) {
                return "";
            }
        }
    }
}

