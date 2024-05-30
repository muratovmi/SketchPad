package com.itschoolsamsung.sketchpad.modal;

// Класс-наследник InputText для хранения текста и его размера, введенных пользователем.
// Эти параметры используются для отображения текста в классе DrawingCanvas.
public class InputText extends Shape {
    private String textInput;
    private int textSize;
    private int xLocation;
    private int yLocation;
    private int textColor;
    private int textWidth;
    private int textHeight;

    public InputText(String textInput, int textSize, int xLocation, int yLocation, int textColor, int textWidth, int textHeight) {
        this.textInput = textInput;
        this.textSize = textSize;
        this.xLocation = xLocation;
        this.yLocation = yLocation;
        this.textColor = textColor;
        this.textWidth = textWidth;
        this.textHeight = textHeight;
    }

    public String getTextInput() {
        return textInput;
    }

    public int getxLocation() {
        return xLocation;
    }

    public void setxLocation(int xLocation) {
        this.xLocation = xLocation;
    }

    public int getyLocation() {
        return yLocation;
    }

    public void setyLocation(int yLocation) {
        this.yLocation = yLocation;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getTextSize() {
        return textSize;
    }

    public int getTextWidth() {
        return textWidth;
    }

    public void setTextWidth(int textWidth) {
        this.textWidth = textWidth;
    }

    public int getTextHeight() {
        return textHeight;
    }

    public void setTextHeight(int textHeight) {
        this.textHeight = textHeight;
    }
}