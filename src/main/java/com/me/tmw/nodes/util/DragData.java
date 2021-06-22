package com.me.tmw.nodes.util;

public class DragData {

    private double startXPos;
    private double startYPos;
    private double startScreenX;
    private double startScreenY;

    public DragData() {

    }

    public DragData(double startXPos, double startYPos, double startScreenX, double startScreenY) {
        this.startXPos = startXPos;
        this.startYPos = startYPos;
        this.startScreenX = startScreenX;
        this.startScreenY = startScreenY;
    }

    public double getStartXPos() {
        return startXPos;
    }
    public void setStartXPos(double startXPos) {
        this.startXPos = startXPos;
    }

    public double getStartYPos() {
        return startYPos;
    }
    public void setStartYPos(double startYPos) {
        this.startYPos = startYPos;
    }

    public double getStartScreenX() {
        return startScreenX;
    }
    public void setStartScreenX(double startScreenX) {
        this.startScreenX = startScreenX;
    }

    public double getStartScreenY() {
        return startScreenY;
    }
    public void setStartScreenY(double startScreenY) {
        this.startScreenY = startScreenY;
    }

    public double calculateX(double screenPos) {
        return startXPos + (screenPos - startScreenX);
    }
    public double calculateY(double screenPos) {
        return startYPos + (screenPos - startScreenY);
    }

}
