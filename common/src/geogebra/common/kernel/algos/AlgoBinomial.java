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
import geogebra.common.util.MyMath2;

import java.math.BigDecimal;
import java.math.BigInteger;

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

    private static double Binom(double n, double r) {
		double INFINITY=Double.POSITIVE_INFINITY;
    	try {
    		if (n==0d && r==0d) return 1d;
    		if (r > n/2) r = n - r;
    		if (n<1d || r<0d || n<r) return 0d;
    		if (Math.floor(n)!=n || Math.floor(r)!=r) return 0d;
	    
    		double ncr=BinomLog(n,r);
    		if (ncr==INFINITY) return INFINITY; // check to stop needless slow calculations

    		// BinomLog is not exact for some values
    		// (determined by trial and error)
    		if (n<=37) return ncr;
    		//if (r<2.8+Math.exp((250-n)/100) && n<59000) return ncr;
	    
    		// BinomBig is more accurate but slower
    		// (but cannot be exact if the answer has more than about 16 significant digits)
    		return BinomBig(n,r);
    	}
    	catch (Exception e) {
    		return INFINITY;
    	}    
    }
    
    private static double BinomBig(double n, double r) {
	    if (r > n/2) r = n - r;
	    BigInteger ncr=BigInteger.ONE,dd=BigInteger.ONE,nn,rr;
//	    nn=BigInteger.valueOf((long)n);
//	    rr=BigInteger.valueOf((long)r);
	    
	    // need a long-winded conversion in case n>10^18
	    Double nnn=new Double(n);
	    Double rrr=new Double(r);
	    nn=(new BigDecimal(nnn.toString())).toBigInteger();
	    rr=(new BigDecimal(rrr.toString())).toBigInteger();
	    
	    while (dd.compareTo(rr)<=0) {
	    	ncr=ncr.multiply(nn);
	    	ncr=ncr.divide(dd); // dd is guaranteed to divide exactly into ncr here
	    	nn=nn.subtract(BigInteger.ONE);
	    	dd=dd.add(BigInteger.ONE);
	    }
	    return ncr.doubleValue();
	  }
	
	private static double BinomLog(double n, double r) {
		// exact for n<=37
		// also  if r<2.8+Math.exp((250-n)/100) && n<59000
		// eg Binom2(38,19) is wrong
		
		return Math.floor(0.5+Math.exp(MyMath2.logGamma(n+1d)-MyMath2.logGamma(r+1)-MyMath2.logGamma((n-r)+1)));
		
	}
	
    public AlgoBinomial(Construction cons, String label, NumberValue a, NumberValue b) {       
	  super(cons, label, a, b); 
    }   
  
    @Override
	public Algos getClassName() {
        return Algos.AlgoBinomial;
    }
    
    @Override
	public final void compute() {
    	if (input[0].isDefined() && input[1].isDefined()) {
    		double nCr=Binom(a.getDouble(), b.getDouble());
			num.setValue(nCr);
    	}
    	else num.setUndefined();
    }    
    
}
