package com.itschoolsamsung.sketchpad.modal;

// Класс-наследник Rectangle для хранения размеров прямоугольника, введенных пользователем.
// Эти размеры используются для рисования прямоугольника в классе DrawingCanvas.
public class Rectangle extends Shape {
    private int top;
    private int bottom;
    private int left;
    private int right;
    private int color;

    public Rectangle(int top, int bottom, int left, int right, int color) {
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