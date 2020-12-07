package ACP;

public class Atom {
    public Atom() {
    }

    public static void main(String[] Args) {
        MotherCell handler = new MotherCell();
        handler.connectToDataField();
        handler.outputStream();
        handler.reportAliveStatus();

        try {
            handler.getMessage();
        } catch (ClassNotFoundException var3) {
            var3.printStackTrace();
        }

    }
}
