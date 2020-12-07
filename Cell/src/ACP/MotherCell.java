package ACP;

import Objects.Message;
import Services.Add;
import Services.Divide;
import Services.Multiply;
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

public class MotherCell {
    int Min = 5000;
    int Max = 5100;
    int localPort;
    Socket localSocket;
    ObjectOutputStream oos;
    ObjectInputStream ois;
    String fingerPrint = getProcessId();
    String temporalOperaton = "";
    double numDiv = 0.0D;
    double numSum = 0.0D;
    double numSub = 0.0D;
    double numMul = 0.0D;
    boolean sumEnabled = false;
    boolean subEnabled = false;
    boolean mulEnabled = false;
    boolean divEnabled = false;
    String fileFldr = "";
    String file = "";
    String rawFile = "";
    String fileFldrAdd = "C:/temp/Services/Add";
    String fileAdd = "C:/temp/Services/Add/Add.jar";
    String rawFileAdd = "C:/temp/Services/Add/Add-Clone";
    String fileFldrSub = "C:/temp/Services/Substract";
    String fileSub = "C:/temp/Services/Substract/Substract.jar";
    String rawFileSub = "C:/temp/Services/Substract/Substract-Clone";
    String fileFldrMul = "C:/temp/Services/Multiply";
    String fileMul = "C:/temp/Services/Multiply/Multiply.jar";
    String rawFileMul = "C:/temp/Services/Multiply/Multiply-Clone";
    String fileFldrDiv = "C:/temp/Services/Divide";
    String fileDiv = "C:/temp/Services/Divide/Divide.jar";
    String rawFileDiv = "C:/temp/Services/Divide/Divide-Clone";

    public MotherCell() {
        this.numSum = (double)fileValidation.getNumServices(this.fileFldrAdd);
        this.numSub = (double)fileValidation.getNumServices(this.fileFldrSub);
        this.numMul = (double)fileValidation.getNumServices(this.fileFldrMul);
        this.numDiv = (double)fileValidation.getNumServices(this.fileFldrDiv);
        System.out.println("Empty Cell instanced");
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

    public void forwardMessage(Message msg) {
        try {
            this.oos.writeObject(msg);
            System.out.println("A message was sent with the msg being " + msg.getMsg());
        } catch (IOException var3) {
            System.out.println("Could not send message");
            var3.printStackTrace();
        }

    }

    public void processObject(Message msg) {
        if (this.temporalOperaton != msg.getEventId()) {
            this.temporalOperaton = msg.getEventId();
        }

        System.out.println("Received message " + msg.getMsg() + "CC " + msg.getContentCode());
        Message ackMessage;
        Message resp;
        double result;
        switch(msg.getContentCode()) {
        case 5:
            if (this.sumEnabled) {
                result = addController(msg.getFirst(), msg.getSecond());
                System.out.println("Add.jar has received the parameters: " + msg.getFirst() + " " + msg.getSecond());
                resp = new Message(result, msg);
                resp.setEventId(this.temporalOperaton);
                this.forwardMessage(resp);
            } else {
                System.out.println("The operation " + msg.getMsg() + " is not corrently supported by this cell");
            }
            break;
        case 6:
            if (this.subEnabled) {
                result = substractionController(msg.getFirst(), msg.getSecond());
                System.out.println("Substract.jar has received the parameters: " + msg.getFirst() + " " + msg.getSecond());
                resp = new Message(result, msg);
                resp.setEventId(this.temporalOperaton);
                this.forwardMessage(resp);
            } else {
                System.out.println("The operation " + msg.getMsg() + " is not corrently supported by this cell");
            }
            break;
        case 7:
            if (this.mulEnabled) {
                result = multiplyController(msg.getFirst(), msg.getSecond());
                System.out.println("Multiply.jar has received the parameters: " + msg.getFirst() + " " + msg.getSecond());
                resp = new Message(result, msg);
                resp.setEventId(this.temporalOperaton);
                this.forwardMessage(resp);
            } else {
                System.out.println("The operation " + msg.getMsg() + " is not corrently supported by this cell");
            }
            break;
        case 8:
            if (this.divEnabled) {
                if (msg.getSecond() != 0.0D) {
                    result = divideController(msg.getFirst(), msg.getSecond());
                    System.out.println("Divide.jar has received the parameters: " + msg.getFirst() + " " + msg.getSecond());
                    resp = new Message(result, msg);
                    resp.setEventId(this.temporalOperaton);
                    this.forwardMessage(resp);
                }
            } else {
                System.out.println("The operation " + msg.getMsg() + " is not corrently supported by this cell");
            }
        case 9:
        default:
            break;
        case 10:
            System.out.println("Got a request for sum acknowledgement ");
            ackMessage = new Message();
            ackMessage.setContentCode(4);
            ackMessage.setEventId(this.fingerPrint);
            ackMessage.setFirst(this.numSum);
            ackMessage.setMsg("s");
            this.forwardMessage(ackMessage);
            break;
        case 11:
            System.out.println("Got a request for sub acknowledgement ");
            ackMessage = new Message();
            ackMessage.setContentCode(4);
            ackMessage.setEventId(this.fingerPrint);
            ackMessage.setFirst(this.numSub);
            ackMessage.setMsg("r");
            this.forwardMessage(ackMessage);
            break;
        case 12:
            System.out.println("Got a request for mul acknowledgement ");
            ackMessage = new Message();
            ackMessage.setContentCode(4);
            ackMessage.setEventId(this.fingerPrint);
            ackMessage.setFirst(this.numMul);
            ackMessage.setMsg("m");
            this.forwardMessage(ackMessage);
            break;
        case 13:
            System.out.println("Got a request for div acknowledgement ");
            ackMessage = new Message();
            ackMessage.setContentCode(4);
            ackMessage.setEventId(this.fingerPrint);
            ackMessage.setFirst(this.numDiv);
            ackMessage.setMsg("d");
            this.forwardMessage(ackMessage);
            break;
        case 14:
            int tempCells = 0;
            boolean requestType = true;
            String var4;
            switch((var4 = msg.getMsg()).hashCode()) {
            case 100:
                if (var4.equals("d")) {
                    tempCells = (int)this.numDiv;
                    this.file = this.fileDiv;
                    this.rawFile = this.rawFileDiv;
                    this.fileFldr = this.fileFldrDiv;
                }
                break;
            case 109:
                if (var4.equals("m")) {
                    tempCells = (int)this.numMul;
                    this.file = this.fileMul;
                    this.rawFile = this.rawFileMul;
                    this.fileFldr = this.fileFldrMul;
                }
                break;
            case 114:
                if (var4.equals("r")) {
                    tempCells = (int)this.numSub;
                    this.file = this.fileSub;
                    this.rawFile = this.rawFileSub;
                    this.fileFldr = this.fileFldrSub;
                }
                break;
            case 115:
                if (var4.equals("s")) {
                    tempCells = (int)this.numSum;
                    this.file = this.fileAdd;
                    this.rawFile = this.rawFileAdd;
                    this.fileFldr = this.fileFldrAdd;
                }
            }

            if (msg.getFirst() > (double)tempCells) {
                for(int i = tempCells; (double)i < msg.getFirst(); ++i) {
                    File from = new File(this.file);
                    File to = Clonation.validateClone(this.file, this.rawFile);

                    try {
                        Clonation.copy(from, to);
                    } catch (IOException var9) {
                        var9.printStackTrace();
                    }
                }

                this.numSum = (double)fileValidation.getNumServices(this.fileFldrAdd);
                this.numSub = (double)fileValidation.getNumServices(this.fileFldrSub);
                this.numMul = (double)fileValidation.getNumServices(this.fileFldrMul);
                this.numDiv = (double)fileValidation.getNumServices(this.fileFldrDiv);
            }
            break;

        case 15:
            char[] msgArray = msg.getMsg().toCharArray();
            if (msgArray[0] == '1') {
                this.sumEnabled = true;
                System.out.println("Addition has been enabled for the cell " + this.fingerPrint);
            } else {
                this.sumEnabled = false;
                System.out.println("Addition has been disabled for the cell " + this.fingerPrint);
            }

            if (msgArray[1] == '1') {
                this.subEnabled = true;
                System.out.println("Substraction has been enabled for the cell " + this.fingerPrint);
            } else {
                this.subEnabled = false;
                System.out.println("Substraction has been disabled for the cell " + this.fingerPrint);
            }

            if (msgArray[2] == '1') {
                this.mulEnabled = true;
                System.out.println("Multiplication has been enabled for the cell " + this.fingerPrint);
            } else {
                this.mulEnabled = false;
                System.out.println("Multiplication has been disabled for the cell " + this.fingerPrint);
            }

            if (msgArray[3] == '1') {
                this.divEnabled = true;
                System.out.println("Divition has been enabled for the cell " + this.fingerPrint);
            } else {
                this.divEnabled = false;
                System.out.println("Divition has been disabled for the cell " + this.fingerPrint);
            }
        }

    }

    public static double addController(double val1, double val2) {
        double ans = 0.0D;
        double num1 = val1;
        double num2 = val2;

        try {
            URL[] classLoaderUrls = new URL[]{new URL("file:C:/temp/Services/Add")};
            URLClassLoader urlClassLoader = new URLClassLoader(classLoaderUrls);
            Class<?> addClass = urlClassLoader.loadClass("Services.Add");
            Add addFunction = (Add)addClass.newInstance();
            ans = addFunction.addOperation(num1, num2);
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

    public static double multiplyController(double val1, double val2) {
        double ans = 0.0D;
        double num1 = val1;
        double num2 = val2;

        try {
            URL[] classLoaderUrls = new URL[]{new URL("file:C:/temp/Services/Multiply")};
            URLClassLoader urlClassLoader = new URLClassLoader(classLoaderUrls);
            Class<?> mulClass = urlClassLoader.loadClass("Services.Multiply");
            Multiply mulFunction = (Multiply)mulClass.newInstance();
            ans = mulFunction.multiplyOperation(num1, num2);
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

    public static double divideController(double val1, double val2) {
        double ans = 0.0D;
        double num1 = val1;
        double num2 = val2;

        try {
            URL[] classLoaderUrls = new URL[]{new URL("file:C:/temp/Services/Divide")};
            URLClassLoader urlClassLoader = new URLClassLoader(classLoaderUrls);
            Class<?> divClass = urlClassLoader.loadClass("Services.Divide");
            Divide divFunction = (Divide)divClass.newInstance();
            ans = divFunction.divideOperation(num1, num2);
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


