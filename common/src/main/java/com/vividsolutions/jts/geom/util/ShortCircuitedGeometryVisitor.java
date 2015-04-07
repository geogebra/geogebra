package com.vividsolutions.jts.geom.util;

import com.vividsolutions.jts.geom.*;

/**
 * A visitor to {@link Geometry} elements which can
 * be short-circuited by a given condition
 *
 * @version 1.7
 */
public abstract class ShortCircuitedGeometryVisitor
{
  private boolean isDone = false;

  public ShortCircuitedGeometryVisitor() {
  }

  public void applyTo(Geometry geom) {
    for (int i = 0; i < geom.getNumGeometries() && ! isDone; i++) {
      Geometry element = geom.getGeometryN(i);
      if (! (element instanceof GeometryCollection)) {
        visit(element);
        if (isDone()) {
          isDone = true;
          return;
        }
      }
      else
        applyTo(element);
    }
  }

  protected abstract void visit(Geometry element);

  protected abstract boolean isDone();
}