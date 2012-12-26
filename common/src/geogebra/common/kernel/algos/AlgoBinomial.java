/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.util.MyMath;

//import org.apache.commons.math.special.Gamma;

//from http://www.javajungle.de/math/primes/PrimeNumberSieve.html
//import de.luschny.math.primes.PrimeSieve;
//import de.luschny.math.primes.IPrimeIteration;


/**
 * Computes Binomial[a, b]
 * @author  Michael Borcherds 2007-10-09
 * @version 
 */
public class AlgoBinomial extends AlgoTwoNumFunction {

	
	
	/*
	public static BigInteger binomial(int n, int k)
	{
	  if (0 > k || k > n)
	  {
	      throw new ArithmeticException(
	      "Binomial: 0 <= k and k <= n required, but n was "
	      + n + " and k was " + k );
	  }

	  if(k > n / 2) { k = n - k ; }

	  int rootN = (int) Math.floor(Math.sqrt(n));
	  BigInteger binom = BigInteger.ONE ;

	  IPrimeIteration pIter = new PrimeSieve(n).getIteration();

	  for (int prime : pIter) // equivalent to a nextPrime() function.
	  {                       // prime runs through the prime numbers 1 < prime <= n
	      if(prime > n - k)
	      {
	          binom = binom.multiply(BigInteger.valueOf(prime));
	          continue;
	      }

	      if(prime > n / 2)
	      {
	          continue;
	      }

	      if(prime > rootN)
	      {
	          if(n % prime < k % prime)
	          {
	              binom = binom.multiply(BigInteger.valueOf(prime));
	          }
	          continue;
	      }

	      int exp = 0, r = 0, N = n, K = k;

	      while (N > 0)
	      {
	          r = (N % prime) < (K % prime + r) ? 1 : 0;
	          exp += r;
	          N /= prime;
	          K /= prime;
	      }

	      if (exp > 0)
	      {
	          binom = binom.multiply(BigInteger.valueOf(prime).pow(exp));
	      }
	  }
	  return binom;
	}*/


	
    public AlgoBinomial(Construction cons, String label, NumberValue a, NumberValue b) {       
	  super(cons, label, a, b); 
    }   
  
    @Override
	public Commands getClassName() {
		return Commands.Binomial;
	}
    
    @Override
	public final void compute() {
    	if (input[0].isDefined() && input[1].isDefined()) {
    		double nCr=MyMath.binomial(a.getDouble(), b.getDouble());
			num.setValue(nCr);
    	}
    	else num.setUndefined();
    }    
    
}
