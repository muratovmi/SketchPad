package com.itschoolsamsung.sketchpad.modal;

// Класс-наследник Circle для хранения размеров окружности, введенных пользователем.
// Эти размеры используются для рисования окружности в классе DrawingCanvas.
public class Circle extends Shape {
    private int centerX;
    private int centerY;
    private int radius;
    private int circleColor;

    public Circle(int centerX, int centerY, int radius, int circleColor) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
        this.circleColor = circleColor;
    }

    public int getCenterX() {
        return centerX;
    }

    public void setCenterX(int centerX) {
        this.centerX = centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public void setCenterY(int centerY) {
        this.centerY = centerY;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getCircleColor() {
        return circleColor;
    }

    public void setCircleColor(int circleColor) {
        this.circleColor = circleColor;
    }
}