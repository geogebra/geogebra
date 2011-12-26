package geogebra.common.awt;

public interface Area extends Shape{

	void subtract(Area shape);
	void intersect(Area shape);
	void exclusiveOr(Area shape);
	void add(Area shape);

}
