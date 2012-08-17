/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.implicit;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.EquationSolverInterface;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.common.util.Cloner;

import java.util.ArrayList;
/**
 * Find asymptotes of ImplicitCurves
 * 
 * @author Darko Drakulic
 */
public class AlgoAsymptoteImplicitPoly extends AlgoElement {

	private GeoImplicitPoly ip; // input
//	private OutputHandler<GeoLine> lines;
    private GeoList g; // output
    private EquationSolverInterface solver;
    
   
    /**
     * @param c construction
     * @param label label for output
     * @param ip implicit polynomial
     */
    public AlgoAsymptoteImplicitPoly(Construction c, String label, GeoImplicitPoly ip) {
    	super(c);
        this.ip = ip;            
        solver = getKernel().getEquationSolver();
        
//    	lines=new OutputHandler<GeoLine>(new elementFactory<GeoLine>() {
//			public GeoLine newElement() {
//				GeoLine g=new GeoLine(cons);
//				g.setCoords(0, 0, 1);
//				g.setParentAlgorithm(AlgoAsymptoteImplicitPoly.this);
//				return g;
//			}
//		}, null);
        
        g = new GeoList(cons);                

        setInputOutput(); // for AlgoElement        

        g.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT);
        compute();
        
        g.setLabel(label);
        
    }
    
    @Override
	public Algos getClassName() {
        return Algos.AlgoAsymptoteImplicitPoly;
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = ip;

        setOutputLength(1);
        setOutput(0, g);
        
        setDependencies(); // done by AlgoElement
    }
    /**
     * @return list of asymptotes
     */
    public GeoList getResult() {
        return g;
    }
    
//    public GeoLine[] getAsymptotes(){
//    	return lines.getOutput(new GeoLine[0]);
//    }
    
    private void makeLines(ArrayList<Double> p,double a, double b){
    	double[] tRoots=new double[p.size()];
		for (int j=0;j<p.size();j++){
			tRoots[j]=p.get(p.size()-1-j);
		}
		int tn=solver.polynomialRoots(tRoots,false);
		int shift=0;
		for (int j=1;j<tn;j++){
			if (Kernel.isEqual(tRoots[j-shift-1],tRoots[j])){
				shift++;
			}else{
				if (shift>0){
					tRoots[j-shift]=tRoots[j];
				}
			}
		}
		
//		int start=lines.size();
//		lines.augmentOutputSize(tn-shift);
		for (int j=0;j<tn-shift;j++){
			GeoLine line=new GeoLine(cons);
			line.setCoords(a,b, -tRoots[j]);
			line.setParentAlgorithm(this);
    		g.add(line);
		}
    }

    @Override
	public final void compute() {       
        if (!ip.isDefined()) {
//        	lines.adjustOutputSize(0);
        	g.setUndefined();
        	return;
        }
        
        int deg=ip.getDeg();
        double[] roots=new double[deg+1];
        
        double[][] coeff=ip.getCoeff();
        
        for (int i=0;i<=deg;i++){
        	if (coeff.length>i&&coeff[i].length>deg-i)
        		roots[i]=coeff[i][deg-i];
        	else
        		roots[i]=0;
        }
        
        ArrayList<double[]> homogenPolys=new ArrayList<double[]>();
        homogenPolys.add(Cloner.clone(roots));
        
        int n=solver.polynomialRoots(roots,false);
//        StringBuilder sb=new StringBuilder();
//        for (int i=0;i<n;i++){
//        	sb.append(roots[i]);
//        	sb.append(",");
//        }
//        Application.debug(sb);
        
        g.clear();
        
        double last=Double.NaN;
        for (int i=0;i<n;i++){
        	if (!Kernel.isEqual(last, roots[i])){
        		int r=Integer.MAX_VALUE;
        		ArrayList<Double> p=new ArrayList<Double>();
        		double[] divisor=new double[]{-roots[i],1};
        		double rk=Double.NaN;
        		for (int k=0;k<=r;k++){
        			double[] pk=null;
        			if (homogenPolys.size()>k){
        				pk=homogenPolys.get(k);
        			}else{
        				double[] c=new double[deg-k+1];
        				for (int j=0;j<=deg-k;j++){
        					if (coeff.length>j&&coeff[j].length>deg-j-k)
        		        		c[j]=coeff[j][deg-j-k];
        		        	else
        		        		c[j]=0;
        				}
        				pk=c;
        				homogenPolys.add(pk);
        			}
        			double ev=PolynomialUtils.eval(pk, roots[i]);
        			rk=(((deg-k)&1)==0?ev:-ev);
        			if (r==k)
        				break;
        			int l=0;
        			if (PolynomialUtils.getDegree(pk)<0){
        				if (r==Integer.MAX_VALUE) //if highest degree polynomial is zero, something is wrong.
        					throw new Error("Zero Polynomial");
        				l=r-k;
        			}else{
        				while(Kernel.isZero(rk)){
	        				if (r-k<=l){
	        					rk=0;
	        					break;
	        				}
	        				pk=PolynomialUtils.polynomialDivision(pk, divisor);
	        				l++;
	        				ev=PolynomialUtils.eval(pk, roots[i]);
	        				rk=(((deg-k+l)&1)==0?ev:-ev); //division reduces degree by one
        				}
        			}
        			if (r==Integer.MAX_VALUE){
        				r=l;
        			}
        			if (r-k<=l)
        				p.add(rk);
        			else{
        				p.clear();
        				r=l+k;
        				if (l>0)
        					p.add(rk);
        			}
        		}
        		p.add(rk);
        		makeLines(p,1,-roots[i]);
        	}
        	last=roots[i];
        }
        
        //find the asymptotes parallel to the x-axis
        double[] pk=homogenPolys.get(0);
        if (PolynomialUtils.getDegree(pk)<deg){
	        int r=Integer.MAX_VALUE;
	        ArrayList<Double> p=new ArrayList<Double>();
	        for (int k=0;k<=r;k++){
	        	if (homogenPolys.size()>k){
					pk=homogenPolys.get(k);
				}else{
					double[] c=new double[deg-k+1];
					for (int j=0;j<=deg-k;j++){
						if (coeff.length>j&&coeff[j].length>deg-j-k)
			        		c[j]=coeff[j][deg-j-k];
			        	else
			        		c[j]=0;
					}
					pk=c;
	//				homogenPolys.add(pk); //we won't need it anymore
				}
	        	if (r==k){
	        		p.add(pk[deg-k]);
	        		break;
	        	}
	        	int l=0;
	        	int d=PolynomialUtils.getDegree(pk);
	        	if (d<deg-r){
	        		l=r-k;
	        	}else{
	        		l=deg-k-d;
	        	}
	        	if (r==Integer.MAX_VALUE){
					r=l;
				}
				if (r-k<=l)
					p.add(pk[deg-k-l]);
				else{
					p.clear();
					p.add(pk[deg-k-l]);
					r=l+k;
				}
	        }
	        makeLines(p, 0, 1);
        }
//        if (true)
//			lines.updateLabels();
//        ArrayList<double[]> asymptotes = new ArrayList<double[]>();
//        
//        
//    	int deg = ip.getDeg();
//    	
//    	sb.setLength(0);
//	    sb.append("{");
//		
//	    double [][] coeff = new double[deg+1][deg+1];
//	    for(int i=0; i<ip.getCoeff().length; i++)
//	    	for(int j=0; j<ip.getCoeff()[0].length; j++)
//	    	    	coeff[i][j] = ip.getCoeff()[i][j];
//	    
//	    
//    	double [] coeffk = new double[deg+1];
//    	double [] diag = new double[deg+1];
//    	double [] upDiag = new double[deg];
//    	
//    	
//    	for(int i=deg, k=0, m=0; i>=0; i--)
//    		for(int j=deg; j>=0; j--)
//    			if(i+j == deg)
//    			{
//    				coeffk[k] = coeff[i][j];
//    				diag[k++] = coeff[i][j];
//    			}
//    			else if(i+j == deg-1)
//    				upDiag[m++] = coeff[i][j];
//    	
//		
//	
//		/**
//		 * Asymptotes parallel to x-axe and y-axe
//		 */
//		
//    	double[] parallelCoeff = new double[deg+1];
//    	
//		// parallel with x-axe
//		if(coeff[0][deg] == 0)
//		{
//			for(int i=deg-1; i>=0; i--)
//				if(sumRow(coeff, i, 2) != 0)
//				{
//					for(int j=0; j<deg; j++)
//						parallelCoeff[j] = coeff[j][i];
//					int numx = solver.polynomialRoots(parallelCoeff);
//					for(int k=0; k<numx; k++)
//					{
//						double [] asy = {1.0, 0.0, -parallelCoeff[k]};
//						asymptotes.add(asy);
//					}
//					break;
//				}
//		}
//		
//		// parallel with y-axe
//		if(coeff[deg][0] == 0)
//		{
//			for(int i=deg; i>=0; i--)
//				if(sumRow(coeff, i, 1) != 0)
//				{
//					for(int j=0; j<deg; j++)
//						parallelCoeff[j] = coeff[i][j];
//					int numx = solver.polynomialRoots(parallelCoeff);
//					for(int k=0; k<numx; k++)
//					{
//						double [] asy = {0.0, 1.0, -parallelCoeff[k]};
//						asymptotes.add(asy);
//					}
//					break;
//				}
//		}
//		
//		
//		/**
//		 * Other asymptotes
//		 */
//	
//    	int numk = solver.polynomialRoots(coeffk);
//    	
//    	for(int i=0; i<numk; i++)
//		{
//			double down = 0, up = 0;
//			for(int j=0; j<upDiag.length; j++)
//			{	
//				up += upDiag[j]*Math.pow(coeffk[i], j);
//				down += (j+1)*diag[j+1]*Math.pow(coeffk[i], j);
//			}
//			if(down == 0)
//				continue;
//			
//			double [] asy = {-coeffk[i], 1, up/down};
//			asymptotes.add(asy);
//		}
//    	
//    	for(int i=0; i<asymptotes.size(); i++)
//    		for(int j=i+1; j<asymptotes.size(); j++)
//    	    	if(Math.abs(Math.abs(asymptotes.get(i)[0]) - Math.abs(asymptotes.get(j)[0])) < 1E-2 &&
//    	    			Math.abs(Math.abs(asymptotes.get(i)[1]) - Math.abs(asymptotes.get(j)[1])) < 1E-2 &&
//    	    			Math.abs(Math.abs(asymptotes.get(i)[2]) - Math.abs(asymptotes.get(j)[2])) < 1E-2 )
//    	    		asymptotes.remove(j--);
//    		
//    	for(int i=0; i<asymptotes.size(); i++)
//        	sb.append(asymptotes.get(i)[0] + "*x + " + asymptotes.get(i)[1] + "*y + " + asymptotes.get(i)[2] + "=0,");
//    		
//    	if(sb.length() > 1)
//			sb.deleteCharAt(sb.length()-1);
//        
//    	sb.append("}");

        
//		g.set(kernel.getAlgebraProcessor().evaluateToList(sb.toString()));	
//		g.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT);
    }
    
//    /**
//     * compute a sum of elements from i-th row or column if matrix mat
//     * rc = 1 for rows, rc = 2 for columns
//     */
//    private double sumRow(double [][] mat, int i, int rc)
//    {
//    	double sum = 0;
//    	for(int j=0; j<mat.length; j++)
//    		if(rc == 1)
//    			sum += mat[i][j];
//    		else
//    			sum += mat[j][i];
//    	return sum;
//    }

	// TODO Consider locusequability
 
}
