package com.itschoolsamsung.sketchpad.modal;

// Класс-наследник Ellipse для хранения размеров овала, введенных пользователем.
// Эти размеры используются для рисования овала в классе DrawingCanvas.
public class Ellipse extends Shape {
    private int top;
    private int bottom;
    private int left;
    private int right;
    private int color;

    public Ellipse(int top, int bottom, int left, int right, int color) {
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
        this.color = color;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}