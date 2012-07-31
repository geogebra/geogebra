/**
 * 
 */
package geogebra.common.kernel.locusequ.elements;

import static geogebra.common.kernel.locusequ.arith.EquationArithHelper.abs;
import static geogebra.common.kernel.locusequ.arith.EquationArithHelper.diff;
import static geogebra.common.kernel.locusequ.arith.EquationArithHelper.div;
import static geogebra.common.kernel.locusequ.arith.EquationArithHelper.equation;
import static geogebra.common.kernel.locusequ.arith.EquationArithHelper.pow;
import static geogebra.common.kernel.locusequ.arith.EquationArithHelper.sqrt;
import static geogebra.common.kernel.locusequ.arith.EquationArithHelper.sum;
import static geogebra.common.kernel.locusequ.arith.EquationArithHelper.times;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationList;
import geogebra.common.kernel.locusequ.EquationPoint;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.kernel.locusequ.SymbolicVector;
import geogebra.common.kernel.locusequ.arith.Equation;
import geogebra.common.kernel.locusequ.arith.EquationExpression;
import geogebra.common.kernel.locusequ.arith.EquationNumericValue;
/**
 * @author sergio
 * Generic base class for lines.
 */
public abstract class EquationGenericLine extends EquationElement {

	private SymbolicVector vector;
	private EquationPoint pequ;
	private EquationGenericLine lequ;
	private EquationExpression a,b,c;

	/**
	 * Empty constructor in case any subclass needs it.
	 */
	protected EquationGenericLine() {}

	/**
	 * General constructor
	 * @param line {@link GeoElement}
	 * @param scope {@link EquationScope}
	 */
	public EquationGenericLine(final GeoElement line, final EquationScope scope) {
		super(line, scope);
	}

	public EquationGenericLine getEquationLine() { return this.lequ; }

	/**
	 * @param point in line.
	 */
	protected void setPoint(final GeoPoint point) {
		this.pequ = this.getScope().getPoint(point);
	}

	/**
	 * @param point in line.
	 */
	protected void setPoint(final EquationPoint point) {
		this.pequ = point;
	}

	public GeoPoint getPoint() { return this.getEquationPoint().getPoint(); }

	public EquationPoint getEquationPoint() { return this.pequ; }

	/**
	 * @return a director vector.
	 */
	public SymbolicVector getVector() {
		if(this.vector == null) {
			this.vector = this.getVectorFromABC();
		}
		return this.vector;
	}

	/**
	 * @param vector director vector.
	 */
	protected void setVector(final SymbolicVector vector) {
		this.vector = vector;
	}

	/**
	 * @return true if second coordinate of vector is zero.
	 */
	public boolean isHorizonta() {
		return this.getVector().isSecondCoordinateZero();
	}

	/**
	 * @return true if second coordinate of vector is not zero.
	 */
	public boolean isNotHorizontal() {
		return !this.isHorizonta();
	}

	/**
	 * @return true if first coordinate of vector is zero.
	 */
	public boolean isVertical() {
		return this.getVector().isFirstCoordinateZero();
	}

	/**
	 * @return true if first coordinate of vector is not zero.
	 */
	public boolean isNotVertical() {
		return !this.isVertical();
	}

	@Override
	public EquationList forPointImpl(EquationPoint point) {
		EquationExpression expr;
		if(this.isVertical()){
			expr = diff(point.getX(), this.pequ.getX());
		} else if(this.isHorizonta()) {
			expr = diff(point.getY(), this.pequ.getY());
		} else {
			expr = diff(times(this.vector.getY(),
					diff(point.getX(), this.pequ.getX())),
					times(this.vector.getX(),
							diff(point.getY(), this.pequ.getY())));
		}

		return equation(expr).toList();
	}

	@Override
	public boolean isAlgebraic() {
		return true;
	}

	protected void setA(EquationExpression a){ this.a = a; }
	protected void setB(EquationExpression b){ this.b = b; }
	protected void setC(EquationExpression c){ this.c = c; }

	public EquationExpression getA() {
		if(this.a == null) {
			if(this.isVertical()) {
				this.a = EquationNumericValue.from(1.0);
			} else if (this.isHorizonta()) {
				this.a = EquationNumericValue.from(0.0);
			} else {
				this.a = this.getVector().getY();
			}
		}
		return this.a;
	}

	public EquationExpression getB() {
		if(this.b == null) {
			if(this.isHorizonta()) {
				this.b = EquationNumericValue.from(1.0);
			} else if (this.isVertical()) {
				this.b = EquationNumericValue.from(0.0);
			} else {
				this.b = this.getVector().getX().getOpposite();
			}
		}
		return this.b;
	}

	public EquationExpression getC() {
		if(this.c == null) {
			if(this.isHorizonta()) {
				this.c = this.getEquationPoint().getY().getOpposite();
			} else if (this.isVertical()) {
				this.c = this.getEquationPoint().getX().getOpposite();
			} else {
				this.c = diff(	times(this.getVector().getX(), this.getEquationPoint().getY()),
								times(this.getVector().getY(), this.getEquationPoint().getX()));
			}
		}
		return this.c;
	}

	/**
	 * @param p external point
	 * @return distance from this line to p.
	 */
	public Equation distToPoint(final EquationPoint p) {
		return equation(div(abs(sum(times(this.getA(), p.getXExpression()),
				times(this.getB(), p.getYExpression()),
				this.getC())),
				sqrt(sum(pow(this.getA(), EquationNumericValue.from(2)),
						pow(this.getB(), EquationNumericValue.from(2))))));
	}

	/**
	 * @return a director vector from coefficients of line.
	 */
	protected SymbolicVector getVectorFromABC() {
		return new SymbolicVector(EquationPoint.ORIGIN, EquationPoint.fromCoordinates(getB().getOpposite(), getA()));
	}
	
	public EquationExpression getSlope() {
		SymbolicVector vec = this.getVector();
		return div(vec.getY(), vec.getX());
	}
}
