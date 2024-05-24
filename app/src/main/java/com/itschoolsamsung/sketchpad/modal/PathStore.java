package com.itschoolsamsung.sketchpad.modal;

import android.graphics.Path;

// Класс PathStore для хранения "пути", нарисованного пользователем.
// "Путь" позже используется для рисования линии в классе DrawingCanvas.
public class PathStore {
    private Path drawnPath;
    private int pathColor;
    private int pathStroke;

    public PathStore(Path drawnPath, int pathColor, int pathStroke) {
        this.drawnPath = drawnPath;
        this.pathColor = pathColor;
        this.pathStroke = pathStroke;
    }

    public Path getDrawnPath() {
        return drawnPath;
    }

    public void setDrawnPath(Path drawnPath) {
        this.drawnPath = drawnPath;
    }

    public int getPathColor() {
        return pathColor;
    }

    public void setPathColor(int pathColor) {
        this.pathColor = pathColor;
    }

    public int getPathStroke() {
        return pathStroke;
    }

    public void setPathStroke(int pathStroke) {
        this.pathStroke = pathStroke;
    }
}