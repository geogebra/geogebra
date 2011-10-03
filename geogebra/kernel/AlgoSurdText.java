/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.main.Application;
import geogebra.util.Unicode;
import org.apache.commons.math.util.MathUtils;
import org.mathpiper.builtin.functions.core.Gcd;

public class AlgoSurdText extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoNumeric num; //input
    private GeoText text; //output	
    
    protected StringBuilder sb = new StringBuilder();
    
    AlgoSurdText(Construction cons, String label, GeoNumeric num) {
    	this(cons, num);
        text.setLabel(label);
    }

    AlgoSurdText(Construction cons, GeoNumeric num) {
        super(cons);
        this.num = num;
               
        text = new GeoText(cons);
		text.setLaTeX(true, false);
		text.setIsTextCommand(true); // stop editing as text
		
        setInputOutput();
        compute();
    }

    public AlgoSurdText(Construction cons) {
		super(cons);
	}

	public String getClassName() {
        return "AlgoSurdText";
    }

    protected void setInputOutput(){
        input = new GeoElement[1];
        input[0] = num;

        setOutputLength(1);
        setOutput(0, text);
        setDependencies(); // done by AlgoElement
    }

    GeoText getResult() {
        return text;
    }

    protected void compute() {
    	
    	
		if (input[0].isDefined()) {
			
			sb.setLength(0);
			
			double decimal = num.getDouble();
			
			if ( Kernel.isEqual(decimal - Math.round(decimal) , 0.0, Kernel.MAX_PRECISION))
				sb.append(kernel.format(Math.round(decimal)));
			else {
				double[] frac = AlgoFractionText.DecimalToFraction(decimal, Kernel.EPSILON);
				/*if (frac[1]<10000)
					Fractionappend(sb, (int)frac[0], (int)frac[1]);
				else*/
					PSLQappend(sb, decimal);
			}
				
			
			text.setTextString(sb.toString());
			text.setLaTeX(true, false);
			
		} else {
			text.setUndefined();
		}
			
	}
    protected void Fractionappend(StringBuilder sb, int numer, int denom) {
		if (denom<0) {
			denom= -denom;
			numer= -numer;
		}
		
		if (denom == 1) { // integer
			sb.append(kernel.format(numer));				
		} else if (denom == 0) { // 1 / 0 or -1 / 0
			sb.append( numer < 0 ? "-"+Unicode.Infinity : ""+Unicode.Infinity);				
		} else {
	    	sb.append("{\\frac{");
	    	sb.append(kernel.format(numer));
	    	sb.append("}{");
	    	sb.append(kernel.format(denom));
	    	sb.append("}}");
	    	
		}
    }
    protected void PSLQappend(StringBuilder sb, double num) {
		double[] numPowers = {num * num, num, 1.0};
		int[] coeffs = PSLQ(numPowers,Kernel.STANDARD_PRECISION,10);
		
		if (coeffs[0] == 0 && coeffs[1] == 0 && coeffs[2] == 0 ) {
			sb.append("\\text{"+app.getPlain("undefined")+"}");
		} else if (coeffs[0] == 0) {
			//coeffs[1]: denominator;  coeffs[2]: numerator
			int denom = coeffs[1];
			int numer = -coeffs[2];
			Fractionappend(sb, numer, denom);
			
		} else {
			
			//coeffs, if found, shows the equation coeffs[2]+coeffs[1]x+coeffs[0]x^2=0"
			//We want x=\frac{a +/- b1\sqrt{b2}}{c}
			//where  c=coeffs[0], a=-coeffs[1], b=coeffs[1]^2 - 4*coeffs[0]*coeffs[2]
			int a = -coeffs[1];
			int b2 = coeffs[1]*coeffs[1] - 4*coeffs[0]*coeffs[2];
			int b1 =1;
			int c = 2*coeffs[0];

			if (b2 <= 0) { //should not happen!
				sb.append("\\text{"+app.getPlain("undefined")+"}");
				return;
			}
			
			//free the squares of b2
			while (b2 % 4==0) {
				b2 = b2 / 4;
				b1 = b1 * 2;
			}
			for (int s = 3; s<=Math.sqrt(b2); s+=2)
				while (b2 % (s*s) ==0) {
					b2 = b2 / (s*s);
					b1 = b1 * s;
				}
			
			if (c<0) {
				a=-a;
				c=-c;
			}
			
			boolean positive;
			if (num > (a+0.0)/c) {
				positive=true;
				if (b2==1) {
					a+=b1;
					b1=0;
					b2=0;
				}
			} else {
				positive=false;
				if (b2==1) {
					a-=b1;
					b1=0;
					b2=0;
				}
			}
			

			int gcd = MathUtils.gcd(MathUtils.gcd(a,b1),c);
			if (gcd!=1) {
				a=a/gcd;
				b1=b1/gcd;
				c=c/gcd;
			}
			
			//when fraction is needed
			if (c!=1) sb.append("\\frac{");
			
			if (a!=0) sb.append(kernel.format(a));
			
			//when the radical is surd
			if (b2!=0) {
				if (positive) {
					if (a!=0) sb.append("+");
				} else {
					sb.append("-");
				}
				
				if (b1!=1)
					sb.append(kernel.format(b1));
				sb.append("\\sqrt{");
				sb.append(kernel.format(b2));
				sb.append("}");
			}
			
			//when fraction is needed
			if (c!=1) {
				sb.append("}{");
				sb.append(kernel.format(c));
		    	sb.append("}");
			}
		}

    }
  
    /*	Algorithm PSLQ
	* from Ferguson and Bailey (1992)
     */
	private int[] PSLQ(double[] x, double AccuracyFactor, int bound) {
		
		int n = x.length;
		int[] coeffs = new int[n];

		double normX;
		double[] ss;
		double[][] H, P, newH;
		int[][] D, E, A, B, newAorB;
		double[][][] G;
		int [][][] R;
		double gamma, deltaSq;
		
		for (int i=0; i<n; i++) {
			coeffs[i]=0; 
		}
		
		if (n<=1)
			return coeffs;
		
		for (int i=0; i<n; i++) {
			if (Double.isNaN(x[i])) return coeffs; 
		}
		
		//normalize x
		normX = 0;
		for (int i=0; i<n; i++) {
			normX += x[i] * x[i];
		}
		normX = Math.sqrt(normX);
		for (int i=0; i<n; i++) {
			x[i] = x[i]/normX;
		}
		
		//partial sums of squares
		ss = new double[n];
		ss[n-1] = x[n-1] * x[n-1];
		for (int i = n-2; i>=0; i--) {
			ss[i] = ss[i+1] + x[i] * x[i];
		}
		for (int i = 0; i<n; i++) {
			ss[i] = Math.sqrt(ss[i]);
		}
		
		//pre-calculate ss[j]*ss[j+1]
		double[] Pss = new double[n-1];
		for (int i=0; i<n-1; i++) {
			Pss[i] = ss[i] * ss[i+1];
		}
		
		//initialize Matrix H (lower trapezoidal
		H = new double[n][n-1];
		for (int i = 0; i<n; i++) {
			for (int j=0; j<i; j++) {
				H[i][j] = -x[i]*x[j]/Pss[j];
			}
			
			if (i<n-1)
				H[i][i] = ss[i+1]/ss[i];
			
			for (int j=i+1; j<n-1; j++) {
				H[i][j] = 0;
			}
		}
		
		//test property of H: the n-1 columns are orthogonal
		/*
		for (int i =0 ; i<n-1; i++) {
			for (int j=0; j<n-1; j++) {
				double sum = 0;
				for (int k=0; k<n; k++) {
					sum += H[k][i]*H[k][j];
				}
				System.out.println(sum);
			}
		}*/
					
	
		//matrix P = In - x.x
		P = new double[n][n];
		for (int i=0; i<n; i++)
			for (int j=0; j<n; j++)
				P[i][j] = -x[i]*x[j];
		for (int i=0; i<n; i++)
			P[i][i]+=1;
		
		//debug: |P|^2=|H|^2 = n-1
		Application.debug("Frobenius Norm Squares: \n"
				+ "|P|^2 = " + frobNormSq(P,n,n)
				+ "|H|^2 = " + frobNormSq(H,n,n-1)
				);
		

		//initialize matrices R
		R = new int[n-1][n][n];
		for (int j=0; j<n-1; j++) {
			for (int i=0; i<n; i++)
				for (int k=0; k<n; k++)
					R[j][i][k]=0;
			for (int i=0; i<n; i++)
				R[j][i][i]=1;
			R[j][j][j]=0;
			R[j][j][j+1]=1;
			R[j][j+1][j]=1;
			R[j][j+1][j+1]=0;
		}
		
		gamma = 1.5;
		deltaSq = 3.0/4 - (1.0/gamma)/gamma;
		
		//initialize A, B = I_n
		A = new int[n][n];
		for (int i=0; i<n; i++)
			for (int j=0; j<n; j++)
				A[i][j]=0;
		for (int i=0; i<n; i++)
			A[i][i]=1;
		B = new int[n][n];
		for (int i=0; i<n; i++)
			for (int j=0; j<n; j++)
				B[i][j]=0;
		for (int i=0; i<n; i++)
			B[i][i]=1;
		
		//iteration
		int itCount = 0;
		double itBound = 2.0*gamma/deltaSq * n*n*(n+1)*Math.log(Math.sqrt(bound*bound*n)*n*n)/Math.log(2);
		Application.debug("itBound = " + itBound);
		while (itCount < itBound){
			
			//0. test if we have found a relation in a column of B
			double[] xB = new double[n];
			for (int i=0; i<n; i++) {
				xB[i]=0;
				for (int k=0; k<n; k++)
					xB[i]+= x[k]*B[k][i];
				if (Kernel.isEqual(xB[i],0,AccuracyFactor)) {
					for (int k=0; k<n; k++)
						coeffs[k] = B[k][i];
					return coeffs;
				}
			}
					
			//0.5. calculate D, E
			//matrix D
			D = new int[n][n];
			double[][] D0 = new double[n][n]; //testing
			for (int i=0; i<n; i++) {
				//define backwards. the 0's and 1's should be defined first.
				for (int j=n-1; j>=i+1; j--) {
					D[i][j]=0;
					D0[i][j]=0;
				}
				D[i][i]=1;
				D0[i][i]=1;
				
				for (int j=i-1; j>=0; j--) {
					double sum = 0;
					double sum0 = 0;
					for (int k=j+1; k<=i; k++) {
						sum+=D[i][k]*H[k][j];
						sum0+=D0[i][k]*H[k][j];
					}
					
					D[i][j]=(int) Math.floor(-1.0/H[j][j]*sum + 0.5);
					D0[i][j]=-1.0/H[j][j]*sum0;
				}
			
			}
			
			//matrix E = D^{-1}
			E = new int[n][n];
			for (int i=0; i<n; i++) {
				//define backwards. the 0's and 1's should be defined first.
				for (int j=n-1; j>=i+1; j--) {
					E[i][j]=0;
				}
				E[i][i]=1;
				for (int j=i-1; j>=0; j--) {
					int sum = 0;
					for (int k=j+1; k<=i; k++)
						sum+=E[i][k]*D[k][j];
					
					E[i][j]= -sum;
				}
				
			}
			
			//1. replace H by DH
			newH = new double[n][n-1];
			double[][] newH0 = new double[n][n-1];
			for (int i = 0; i<n; i++) {
				for (int j=0; j<n-1; j++) {
					newH[i][j]=0;
					newH0[i][j]=0;
					for (int k=0; k<n; k++) {
						newH[i][j]+=D[i][k]*H[k][j];
						newH0[i][j]+=D0[i][k]*H[k][j];
					}
					
				}
			}
			
			for (int i = 0; i<n; i++)
				for (int j=0; j<n-1; j++)
					H[i][j]=newH[i][j];
			
			
			
			//2. find j to maximize gamma^j |h_jj|
			double gammaPow = 1;
			double temp;
			double max=0;
			int index=0;
			
			for (int j=0; j<n-1; j++) {
				gammaPow *= gamma;
				temp = gammaPow * Math.abs(H[j][j]);
				if (max<temp) {
					max = temp;
					index = j;
				}
			}
		
			//2.5 calculate matrices G[0], G[1],... G[n-2]
			G = new double[n-1][n-1][n-1];
			for (int i=0; i<n-1; i++)
				for (int k=0; k<n-1; k++)
					G[n-2][i][k] = 0;
			for (int i=0; i<n-1; i++)
				G[n-2][i][i]=1;
				
			
			for (int j=0; j<n-2; j++) {
				double a = H[j][j];
				double b = H[j+1][j];
				double c = H[j+1][j+1];
				double d = Math.sqrt(b*b+c*c);
				for (int i=0; i<n-2; i++)
					for (int k=0; k<n-2; k++)
						G[j][i][k]=0;
				for (int i=0; i<j; i++)
					G[j][i][i]=1;
				for (int i=j+2; i<n-1; i++)
					G[j][i][i]=1;
				G[j][j][j]=b/d;
				G[j][j][j+1]=-c/d;
				G[j][j+1][j]=-G[j][j][j+1]; // =c/d
				G[j][j+1][j+1]=G[j][j][j]; // = b/d
			}
			
			
			//3. replace H by R_jHG_j, A by R_jDA, B by BER_j
			newH = new double[n][n-1];
			for (int i = 0; i<n; i++) {
				for (int j=0; j<n-1; j++) {
					newH[i][j]=0;
					for (int k=0; k<n; k++)
						for (int l=0; l<n-1; l++)
							newH[i][j]+=R[index][i][k]*H[k][l]*G[index][l][j];
				}
			}
			for (int i = 0; i<n; i++)
				for (int j=0; j<n-1; j++)
					H[i][j]=newH[i][j];
			
			newAorB = new int[n][n];
			for (int i = 0; i<n; i++) {
				for (int j=0; j<n; j++) {
					newAorB[i][j]=0;
					for (int k=0; k<n; k++)
						for (int l=0; l<n; l++)
							newAorB[i][j]+=R[index][i][k]*D[k][l]*A[l][j];
				}
			}
			for (int i = 0; i<n; i++)
				for (int j=0; j<n; j++)
					A[i][j]=newAorB[i][j];
			
			for (int i = 0; i<n; i++) {
				for (int j=0; j<n; j++) {
					newAorB[i][j]=0;
					for (int k=0; k<n; k++)
						for (int l=0; l<n; l++)
							newAorB[i][j]+=B[i][k]*E[k][l]*R[index][l][j];
				}
			}
			for (int i = 0; i<n; i++)
				for (int j=0; j<n; j++)
					B[i][j]=newAorB[i][j];
			
			itCount++;
		}
		
		
		return coeffs;
	}

	
	
	double frobNormSq(double[][] matrix, int m, int n) {
		//m is number of rows; n is number of columns
		double ret = 0;
		
		if (m==0 || n==0)
			return ret;
		
		for (int i=0; i<m; i++)
			for (int j=0; j<n; j++)
				ret += matrix[i][j] * matrix[i][j];
		
		return ret;
	}
}
