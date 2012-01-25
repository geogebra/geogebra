package geogebra.web.awt;

public interface Shape extends geogebra.common.awt.Shape {
	public geogebra.web.openjdk.awt.geom.Shape getGawtShape();
}
