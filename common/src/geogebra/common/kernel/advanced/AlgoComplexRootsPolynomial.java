package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoRootsPolynomial;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.PolyFunction;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoFunction;

import java.util.Iterator;
import java.util.LinkedList;

public class AlgoComplexRootsPolynomial extends AlgoRootsPolynomial{

    double[] curComplexRoots;

   
	public AlgoComplexRootsPolynomial(Construction cons, String[] labels,
			GeoFunction f) {
		super(cons, labels, f);
	}
    @Override
	public void compute() {
    	
    	computeComplexRoots();

        setRootPoints(curRoots, curComplexRoots, curRealRoots);
    }
    
    @Override
	public Commands getClassName() {
        return Commands.ComplexRoot;
    }
    
    // roots of f
    private void computeComplexRoots() {
        if (f.isDefined()) {
            Function fun = f.getFunction();
            // get polynomial factors anc calc roots
            calcComplexRoots(fun);
        } else {
            curRealRoots = 0;
        }
    }

    final void calcComplexRoots(Function fun) {  
    	LinkedList<PolyFunction> factorList;    	
    	
    	// get polynomial factors for this function

    		factorList = fun.getPolynomialFactors(true);    		
    	
        double[] real, complex;
        int noOfRoots;
        curRealRoots = 0; // reset curRoots index 
        
    	// we got a list of polynomial factors
        if (factorList != null) { 
        	 // compute the roots of every single factor              
            Iterator<PolyFunction> it = factorList.iterator();
            while (it.hasNext()) {
            	PolyFunction polyFun = it.next();                       
            	
                //  update the current coefficients of polyFun
            	// (this is needed for SymbolicPolyFunction objects)
                if (!polyFun.updateCoeffValues()) {
                    //  current coefficients are not defined
                    curRealRoots = 0;
                    return;
                }

                // now let's compute the roots of this factor           
                //  compute all roots of polynomial polyFun
                real = polyFun.getCoeffsCopy();   
                complex = new double[real.length];
                noOfRoots = eqnSolver.polynomialComplexRoots(real, complex);  
                addToCurrentRoots(real, complex, noOfRoots);                            
            }
        }         
        else 
			return;                      

        /*
        if (curRealRoots > 1) {
            // sort roots and eliminate duplicate ones
            Arrays.sort(curRoots, 0, curRealRoots);

            // eliminate duplicate roots
            double maxRoot = curRoots[0];            
            int maxIndex = 0;
            for (int i = 1; i < curRealRoots; i++) {
                if ((curRoots[i] - maxRoot) >  AbstractKernel.MIN_PRECISION) {
                	maxRoot = curRoots[i];
	                maxIndex++;
	                curRoots[maxIndex] = maxRoot;
                }
            }
            curRealRoots = maxIndex + 1;
        }*/
        
    }

    // add first number of doubles in roots to current roots
    private void addToCurrentRoots(double[] real, double[] complex, int number) {
        int length = curRealRoots + number;
        if (length >= curRoots.length) { // ensure space
            double[] temp = new double[2 * length];
            double[] temp2 = new double[2 * length];
            for (int i = 0; i < curRealRoots; i++) {
                temp[i] = curRoots[i];
                temp2[i] = curComplexRoots[i];
            }
            curRoots = temp;
            curComplexRoots = temp2;
        }
        
        if (curComplexRoots == null) curComplexRoots = new double[curRoots.length];

        // insert new roots
        for (int i = 0; i < number; i++) {
            curRoots[curRealRoots + i] = real[i];
            curComplexRoots[curRealRoots + i] = complex[i];
        }
        curRealRoots += number;
    }

    // roots array and number of roots
    final private void setRootPoints(double[] real, double[] complex, int number) {
        initRootPoints(number);

        // now set the new values of the roots
        for (int i = 0; i < number; i++) {
            rootPoints[i].setCoords(real[i], complex[i], 1.0); // root point
            rootPoints[i].setComplex();
        }

        // all other roots are undefined
        for (int i = number; i < rootPoints.length; i++) {
            rootPoints[i].setUndefined();
        }

        if (setLabels)
            updateLabels(number);
    }


}
