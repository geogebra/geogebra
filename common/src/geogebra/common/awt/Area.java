package geogebra.common.awt;

public interface Area {

	void subtract(geogebra.common.awt.Area shape);
	void intersect(geogebra.common.awt.Area shape);
	void exclusiveOr(geogebra.common.awt.Area shape);
	void add(geogebra.common.awt.Area shape);

}
