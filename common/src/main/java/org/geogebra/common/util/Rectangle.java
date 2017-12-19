package org.geogebra.common.util;

public class Rectangle {

    private Point topLeftVertex;
    private Point bottomRightVertex;

    public Rectangle(int minX, int minY, int maxX, int maxY) {
        topLeftVertex = new Point(minX, minY);
        bottomRightVertex = new Point(maxX, maxY);
    }

    public int getMinX() {
        return topLeftVertex.getX();
    }

    public int getMinY() {
        return topLeftVertex.getY();
    }

    public int getMaxX() {
        return bottomRightVertex.getX();
    }

    public int getMaxY() {
        return bottomRightVertex.getY();
    }

    @SuppressWarnings("unused")
    public Point getTopLeftVertex() {
        return topLeftVertex;
    }

    @SuppressWarnings("unused")
    public void setTopLeftVertex(Point topLeftVertex) {
        this.topLeftVertex = topLeftVertex;
    }

    @SuppressWarnings("unused")
    public Point getBottomRightVertex() {
        return bottomRightVertex;
    }

    @SuppressWarnings("unused")
    public void setBottomRightVertex(Point bottomRightVertex) {
        this.bottomRightVertex = bottomRightVertex;
    }
}
