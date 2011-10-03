package geogebra.kernel.implicit;

import geogebra.kernel.Construction;
import geogebra.kernel.AlgoElement;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;

public class AlgoImplicitPolyTangentCurve extends AlgoElement {
	
	protected GeoImplicitPoly poly;
	protected GeoPoint point;
	protected String label;
	
	protected GeoImplicitPoly tangentPoly;

	public AlgoImplicitPolyTangentCurve(Construction c,GeoImplicitPoly poly,GeoPoint point,String label,boolean addToConstructionList,boolean calcPath) {
		super(c,addToConstructionList);
		this.poly=poly;
		this.point=point;
		tangentPoly=new GeoImplicitPoly(c);
		if (!calcPath){
			tangentPoly.preventPathCreation();
		}
		setInputOutput();
		compute();
		if (label!=null){
			tangentPoly.setLabel(label);
		}
	}
	

	@Override
	protected void compute() {
		
		/*
		 *  calculate tangent curve:
		 *  dF/dx * x_p + dF/dy * y_p + u_{n-1} + 2*u_{n-2} + ... + n*u_0
		 *  where u_i are the terms of poly with total degree of i.
		 */
		double [][] coeff = poly.getCoeff();
		
		double x = point.getX();
		double y = point.getY();
	
		double [][] newCoeff = new double[coeff.length][];
		
		int maxDeg = poly.getDeg();
		
		for (int i=0;i<coeff.length;i++){
			newCoeff[i]=new double[coeff[i].length];
			for (int j=0;j<coeff[i].length;j++){
				newCoeff[i][j]=(maxDeg-(i+j))*coeff[i][j];
				if (i+1<coeff.length&&j<coeff[i+1].length){
					newCoeff[i][j]+=x*(i+1)*coeff[i+1][j];
				}
				if (j+1<coeff[i].length){
					newCoeff[i][j]+=y*(j+1)*coeff[i][j+1];
				}
			}
		}
		tangentPoly.setCoeff(PolynomialUtils.coeffMinDeg(newCoeff));
	}

	@Override
	protected void setInputOutput() {
		input=new GeoElement[]{poly,point};
		setOutputLength(1);
		setOutput(0, tangentPoly);
		setDependencies();
	}

	@Override
	public String getClassName() {
		return "AlgoImplicitPolyTangentCurve";
	}
	
	public GeoImplicitPoly getTangentCurve(){
		return tangentPoly;
	}

}
