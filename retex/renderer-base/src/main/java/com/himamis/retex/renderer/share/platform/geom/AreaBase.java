package com.himamis.retex.renderer.share.platform.geom;

import java.util.ArrayList;

/**
 *
 * Just needs to hold serveal objects eg Arrow + extension (Shape +
 * Rectangle2D) or eg (Arrow + extension + Arrow)
 *
 */
public abstract class AreaBase implements Area {

    protected ArrayList<Shape> shapes = new ArrayList<>();

    protected double scale = 1;

    public AreaBase(Shape shape) {
        shapes.add(shape);
    }

    public AreaBase(ArrayList<Shape> shape) {
        shapes.addAll(shape);
    }

    public AreaBase() {
        //
    }

    @Override
    public Rectangle2D getBounds2DX() {

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;

        for (Shape shape : shapes) {
            Rectangle2D bounds = shape.getBounds2DX();

            minX = Math.min(minX, bounds.getX());
            minY = Math.min(minY, bounds.getY());
            maxX = Math.max(maxX, bounds.getX() + bounds.getWidth());
            maxY = Math.max(maxY, bounds.getY() + bounds.getHeight());

        }

        return createRectangle(minX * scale, minY * scale,
                (maxX - minX) * scale, (maxY - minY) * scale);
    }

    protected abstract Rectangle2D createRectangle(double x, double y, double width, double height);

    @Override
    public void add(Area a) {
        shapes.addAll(((AreaBase) a).getShapes());
    }

    public ArrayList<Shape> getShapes() {
        return shapes;
    }

    /**
     * not needed in web
     */
    @Override
    public void scale(double x) {
        scale *= x;
    }
}
