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

    public int getPathColor() {
        return pathColor;
    }

    public int getPathStroke() {
        return pathStroke;
    }
}