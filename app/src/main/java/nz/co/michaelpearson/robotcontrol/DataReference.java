package nz.co.michaelpearson.robotcontrol;

public class DataReference {
    private byte x1, x2, y1, y2, speed, buttons;

    public void setX1(byte x1) {
        this.x1 = x1;
    }

    public void setX2(byte x2) {
        this.x2 = x2;
    }

    public void setY1(byte y1) {
        this.y1 = y1;
    }

    public void setY2(byte y2) {
        this.y2 = y2;
    }

    public void setSpeed(byte speed) {
        this.speed = speed;
    }

    public void setButtons(byte buttons) {
        this.buttons = buttons;
    }

    public byte getX1() {
        return x1;
    }

    public byte getX2() {
        return x2;
    }

    public byte getY1() {
        return y1;
    }

    public byte getY2() {
        return y2;
    }

    public byte getSpeed() {
        return speed;
    }

    public byte getButtons() {
        return buttons;
    }
}
