package Objects;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

public class Message implements Serializable {
    private static final long serialVersionUID = 7350392272249743103L;
    private static final AtomicLong identifier = new AtomicLong();
    private String eventId;
    private String msg;
    private int contentCode;
    private double first;
    private double second;
    private boolean original;
    private String idOriginalSender;
    private boolean isAcknowledgement;

    public Message() {
        this.original = true;
    }

    public Message(int firstContactCase) {
        this.isAcknowledgement = false;
        this.msg = "First Contact";
        switch(firstContactCase) {
        case 1:
            this.msg = "Connection From Node";
            this.contentCode = 1;
        case 2:
            this.msg = "Connection From Interface";
            this.contentCode = 2;
        case 3:
            this.msg = "Connection From Cell";
            this.contentCode = 3;
        default:
        }
    }

    public Message(int contentCode, double first, double second, String msg) {
        this.eventId = String.valueOf(uniqueCurrentTimeMS());
        this.contentCode = contentCode;
        this.first = first;
        this.second = second;
        this.msg = msg;
        this.original = true;
        this.isAcknowledgement = false;
    }

    public Message(String eventId) {
        this.eventId = eventId;
        this.isAcknowledgement = true;
    }

    public Message(double result, Message msg) {
        this.contentCode = 9;
        this.idOriginalSender = msg.idOriginalSender;
        this.original = true;
        this.msg = Double.toString(result);
    }

    public static long uniqueCurrentTimeMS() {
        long now = System.currentTimeMillis();

        long lastTime;
        do {
            lastTime = identifier.get();
            if (lastTime >= now) {
                now = lastTime + 1L;
            }
        } while(!identifier.compareAndSet(lastTime, now));

        return now;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String m) {
        this.msg = m;
    }

    public int getContentCode() {
        return this.contentCode;
    }

    public double getFirst() {
        return this.first;
    }

    public void setFirst(Double d) {
        this.first = d;
    }

    public double getSecond() {
        return this.second;
    }

    public boolean getOriginal() {
        return this.original;
    }

    public String getEventId() {
        return this.eventId;
    }

    public String getOriginalSender() {
        return this.idOriginalSender;
    }

    public boolean getIsAcknowledgement() {
        return this.isAcknowledgement;
    }

    public void updateOriginal() {
        this.original = false;
    }

    public void setContentCode(int i) {
        this.contentCode = i;
    }

    public void setEventId(String s) {
        this.eventId = s;
    }
}
