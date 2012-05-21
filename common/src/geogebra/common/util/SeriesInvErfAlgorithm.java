package geogebra.common.util;

//Copyright (C) 2005  Daniel P. Dougherty
//
//This library is free software; you can redistribute it and/or
//modify it under the terms of the GNU Lesser General Public
//License as published by the Free Software Foundation; either
//version 2.1 of the License, or (at your option) any later version.
//
//This library is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//Lesser General Public License for more details.
//
//You should have received a copy of the GNU Lesser General Public
//License along with this library; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//
//$Id: SeriesInvErfAlgorithm.java,v 1.1 2007/05/29 05:57:46 mock_turtle Exp $
//

/**
Series approximation to the Inverse Incomplete Gamma function.
 */
public class SeriesInvErfAlgorithm
{
	private static int maxiter = 100;
	private static double eps = 1E-9;

	private static double[] logCk = new double[maxiter];
	private static double sqrtPi2 = Math.sqrt(Math.PI) / (2.0);


	/**
	 * @param x x
	 * @return inverse error function inverf(x)
	 */
	public Double invErf(Double x)
	{

		Double sum = null;

		//System.out.println("Using series approximation.");
		int k = 0;
		sum = new Double(0.0);
		double term = eps;
		while ((k < maxiter) && (term >= (eps)))
		{       

			term = getLogCk(k) - (Math.log(2*k+1));
			term = term + (Math.log(sqrtPi2 * (x)) * (2*k+1));
			term = Math.exp(term);
			sum = sum + (term);
			//sum.show("sum");
			k++;
		}
		//System.out.println("Needed to use "+k+" terms in series.");


		return(sum);
	}


	private Double getLogCk(int k)
	{

		//System.out.println("Getting Log Ck:"+k);
		double out = Double.NaN;
		if (logCk[k] != 0) //Already have found this one.
		{
			out = logCk[k];
		}
		else //Find and add to the list of knowns.
		{
			if (k == 0)
			{
				out = 1;
			}
			else
			{
				out = 0;
				Double term = null;
				for (int m=0;m<=k-1;m++)
				{
					term = getLogCk(m) - (Math.log(m+1));
					term = Math.exp(term + (getLogCk(k-1-m) - (Math.log(2*m+1))));

					//term.show("term"+k+","+m);


					out = out + (term);        
				} 

			}
			out = Math.log(out);
			//System.out.println("Setting k="+k);
			logCk[k] = out;
		}

		return(out);        
	}
}


