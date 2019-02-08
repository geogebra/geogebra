package com.himamis.retex.renderer.android.geom;

import com.himamis.retex.renderer.share.platform.geom.Area;
import com.himamis.retex.renderer.share.platform.geom.AreaBase;
import com.himamis.retex.renderer.share.platform.geom.Rectangle2D;
import com.himamis.retex.renderer.share.platform.geom.Shape;

import java.util.ArrayList;

public class AreaA extends AreaBase {

    AreaA(Shape shape) {
        super(shape);
    }

    private AreaA(ArrayList<Shape> shape) {
        super(shape);
    }

    AreaA() {
        super();
    }

    @Override
    protected Rectangle2D createRectangle(double x, double y, double width, double height) {
        return new Rectangle2DA(x, y, width, height);
    }

    @Override
    public Area duplicate() {
        return new AreaA(shapes);
    }

    @Override
    public void translate(double d, double e) {
    }
}
