/*
 * Riemann.java - Program providing the Riemann zeta function.
 *
 * Copyright (C) 2004 Andreas de Vries
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301  USA
 */
package geogebra.common.util;

import geogebra.common.kernel.Kernel;

/**
 *  This class provides the Riemann zeta function &#950;(<i>s</i>) for any
 *  comlex number <i>s</i> &#8712; <span style="font-size:large;">&#8450;</span>.
 *  In this version, the absolute  approximation error is about 1E-6 for <i>s</i>
 *  with relatively small absolute value, |<i>s</i>| smaller than 50.
 *  On the real line, the accuracy should be in that range even for much greater
 *  or smaller values of <i>s</i>, and on the "critical strip" 
 *  0 &lt; Re <i>s</i> &lt; 1 the Riemann-Siegel formula is used, which
 *  guarantees a high accuracy especially for very high values of Im<i>s</i>.
 *
 *  @author  Andreas de Vries
 *  @version 1.1
 */
public class Riemann {
   // Suppresses default constructor, ensuring non-instantiability.
   private Riemann() {
   }
   
   /** 
    * The predefined accuracy up to which infinite sums are approximated.
    * @see #zeta(double[])
    */
   public static final double EPSILON = Kernel.STANDARD_PRECISION;

   /**
    *  Returns the value &#967;(<i>s</i>) for a complex number 
    *  <i>s</i> &#8712; <span style="font-size:large;">&#8450;</span>,
    *  such that &#950;(<i>s</i>) = &#967;(<i>s</i>) &#950;(1 - <i>s</i>).
    *  It is defined as
   <p align="center"> 
   <table align="center" border="0">
     <tr>
       <td> 
         &#967;(<i>s</i>)    
       </td><td align="center"> 
         &nbsp; = &nbsp;
       </td><td align="center"> 
         &#960;<sup><i>s</i> - 1/2</sup>
       </td><td> 
         <table border="0"> 
           <tr><td align="center">&#915;((1 - <i>s</i>)/2)</td></tr> 
           <tr><td height="1"><hr/></td></tr> 
           <tr><td align="center">&#915;(<i>s</i>/2)</td></tr> 
         </table>
       </td>
     </tr>
   </table>
   </p>
    *  We have &#967;(<i>s</i>) &#967;(1 - <i>s</i>) = 1.
    *  [Eqs. (2.1.10)-(2.1.12) in E.C. Titchmarsh: <i>The Theory of the Riemann
    *  Zeta-function.</i> 2nd Edition, Oxford University Press, Oxford 1986].
    *  @param s a complex value
    *  @return &#967;(<i>s</i>)
    *  @see #zeta(double[])
    *  @see Complex#gamma(double[])
    */
   public static double[] chi( double[] s ) {
      // /*
      if ( s[0] > 0.5 ) { // gamma is approximated fast only for Re s > 0.
         return Complex.divide( 1.0, chi( Complex.subtract(Complex.ONE_, s) ) );
      }
      double[] result;
      if ( Math.PI * Math.abs(s[1]) > 709 ) {  // for large imaginary parts use log-versions
         // s ln 2 + (s-1) ln pi + lnGamma(1-s) + lnSin (pi s/2)
         result = Complex.multiply( Math.log(2), s );
//System.out.println("### 1) result = " + Complex.toString(result) + ", s=" + Complex.toString(s));
         result = Complex.add( result, Complex.multiply( Math.log(Math.PI), Complex.subtract( s, Complex.ONE_ ) ) );
//System.out.println("### 2) result = " + Complex.toString(result));
         result = Complex.add( result, Complex.lnGamma( Complex.subtract( Complex.ONE_, s ) ) );
//System.out.println("### 3) result = " + Complex.toString(result));
         result = Complex.add( result, Complex.lnSin( Complex.multiply( Math.PI/2, s ) ) );
//System.out.println("### 4) result = " + Complex.toString(result));
         return Complex.exp(result);
      }
      // 2^s pi^(s-1) Gamma(1-s) sin(pi s/2):
      result = Complex.gamma( Complex.subtract( Complex.ONE_, s ) );
//System.out.println("### 1) result = " + Complex.toString(result) + ", s=" + Complex.toString(s));
//System.out.println("### 1a) sin = " + Complex.toString( Complex.sin( Complex.multiply(Math.PI/2, s) ) ) + ", pi s/2=" + Complex.toString(Complex.multiply(Math.PI/2, s)));
      result = Complex.multiply( result, Complex.sin( Complex.multiply(Math.PI/2, s) ) );
//System.out.println("### 2) result = " + Complex.toString(result));
      result = Complex.multiply( result, Complex.power(Math.PI, Complex.subtract(s, Complex.ONE_)) );
//System.out.println("### 3) result = " + Complex.toString(result));
      result = Complex.multiply( result, Complex.power(2, s) );
      // */
      /*
      // pi^(s-1/2) Gamma( 1/2 (1 - s) ) / Gamma(s/2):
      double[] result = Complex.gamma( Complex.multiply(.5, Complex.subtract( Complex.ONE_, s ) ) );
      result = Complex.divide( result, Complex.gamma( Complex.multiply(.5, s) ) );
      double[] exponent = Complex.subtract( s, Complex.multiply(.5, Complex.ONE_) );
      result = Complex.multiply( result, Complex.power(Math.PI, exponent) );
      */
//System.out.println("### chi(" + Complex.toString(s) + ") = " + Complex.toString(result));
      return result;
   }
   
   /**
    * Riemann zeta function &#950;(<i>s</i>) 
    * for <i>s</i> &#8712; <span style="font-size:large;">&#8450;</span>.
    * It is computed by
   <p align="center"> 
   <table align="center" border="0">
     <tr>
       <td> 
         &#950;(<i>s</i>)    
       </td><td align="center"> 
         &nbsp; = &nbsp;     
       </td><td> 
         <table border="0"> 
           <tr><td align="center">1</td></tr> 
           <tr><td height="1"><hr/></td></tr> 
           <tr><td>1 - 2<sup>1 - <i>s</i></sup></td></tr> 
         </table>
       </td><td> 
         <table border="0"> 
           <tr><td align="center" class="small">&#8734;</td></tr> 
           <tr><td align="center" style="font-size:xx-large;">&#931;</td></tr> 
           <tr><td align="center" class="small"><i>n</i>=1</td></tr> 
         </table> 
       </td><td>
         <table border="0"> 
           <tr><td align="center">(-1)<sup><i>n</i> - 1</sup></td></tr> 
           <tr><td height="1"><hr/></td></tr> 
           <tr><td align="center"><i>n<sup>s</sup></i><sub>&nbsp;</sub></td></tr> 
         </table>
       </td><td colspan="2">
       </td><td>
         &nbsp; &nbsp; &nbsp; if Re <i>s</i> &gt; 0,
       </td>
     </tr>
     <tr>
       <td> &nbsp; </td>
     </tr>
     <tr>
       <td> 
         &#950;(<i>s</i>)    
       </td><td align="center"> 
         &nbsp; = &nbsp;     
       </td><td> 
         <table border="0"> 
           <tr><td align="center">1</td></tr> 
           <tr><td height="1"><hr/></td></tr> 
           <tr><td>1 - 2<sup>1 - <i>s</i></sup></td></tr> 
         </table>
       </td><td> 
         <table border="0"> 
           <tr><td align="center" class="small">&#8734;</td></tr> 
           <tr><td align="center" style="font-size:xx-large;">&#931;</td></tr> 
           <tr><td align="center" class="small"><i>n</i>=1</td></tr> 
         </table> 
       </td><td> 
         <table border="0"> 
           <tr><td align="center">1</td></tr> 
           <tr><td height="1"><hr/></td></tr> 
           <tr><td>2<sup><i>n</i> + 1</sup></td></tr> 
         </table>
       </td><td> 
         <table border="0"> 
           <tr><td align="center" class="small"><i>n</i></td></tr> 
           <tr><td align="center" style="font-size:xx-large;">&#931;</td></tr> 
           <tr><td align="center" class="small"><i>k</i>=0</td></tr> 
         </table>
       </td><td>
         <table border="0"> 
           <tr>
             <td align="center">(-1)<sup><i>k</i></sup></td>
             <td style="font-size:xx-large;">(</td>
             <td>
               <table border="0">
                 <tr><td><i>n</i></td></tr>
                 <tr><td><i>k</i></td></tr>
               </table>
             </td>
             <td style="font-size:xx-large;">)</td>
             <td align="center">(<i>k</i> + 1)<sup><i>-s</i></sup></td>
         </table>
       </td><td>
         &nbsp; &nbsp; &nbsp; otherwise.
       </td>
     </tr>
   </table>
   </p>
   <p>
     Note that the second formula holds for all <i>s</i> &#8800; 1.
     However, the first sum converges faster.
     On the critical strip 0 &lt; <i>s</i> &lt; 1, the Riemann-Siegel formula
     is even faster. (It is not implemented yet.)
     For details see <a href="http://mathworld.wolfram.com/RiemannZetaFunction.html" target="_top">http://mathworld.wolfram.com/RiemannZetaFunction.html</a>    
   </p>    
    */
   public static double[] zeta( double[] s ) {
      //int nMax = 2000;
      
      double[] sum = new double[2];
      double[] tmp = new double[2];         
      double[] exponent = { 1 - s[0], -s[1] };
//System.out.println("### zeta invoked with zeta(" + Complex.toString(s) + ")");
      
      if ( Math.abs(s[0] - 1) < EPSILON && Math.abs(s[1]) < EPSILON ) {
         sum[0] = Double.POSITIVE_INFINITY;
      } else if ( Math.abs(s[0]) < EPSILON && Math.abs(s[1]) < EPSILON ) {
         // zeta(0) = -1/2:
         sum[0] = -.5;
         return sum;
      } else if ( s[0] < 0 && Math.abs( s[0] % 2 ) < EPSILON && Math.abs(s[1]) < EPSILON ) {
         // negative odds return zero, zeta(-2n) = 0:
         return sum;
      // /*
      } else if ( s[0] > 0 && s[0] < 1 && Math.abs(s[1]) > 45 ) { // the "critical strip"
         // use Riemann-Siegel formula sum ( n^(-s) + chi(s) n^(s-1):
         int m = (int) Math.sqrt( Math.abs(s[1]) / (2*Math.PI) );
         double[] chi = chi(s);
         exponent = Complex.subtract( s, Complex.ONE_); // = (s-1)
//System.out.println("### chi=" + Complex.toString(chi) + ", exponent=" + Complex.toString(exponent) );
         for ( int n = 1; n <= m; n++ ) {
            sum = Complex.add( sum, Complex.divide(1, Complex.power(n,s)) );
            sum = Complex.add( sum, Complex.multiply( chi, Complex.power(n, exponent) ) );
         }
         return sum;
       // */
      } else if ( s[0] < 0.0 ) { // actually: s[0] < 0.5, but the result is unsatisfactory...
         // use the reflection formula zeta(s) = chi(s) zeta(1-s):
//System.out.println("### use reflection formula!");
         return Complex.multiply( chi(s), zeta( Complex.subtract(Complex.ONE_, s) ) );
/*
      } else if ( s[0] > 6 ) { // Euler's formula:
         int nMax = (int) Math.pow( EPSILON, -1/s[0] );
         int maxIntern = (int) 5e6+1;
         if ( nMax <=0 || nMax > maxIntern ) {
            System.out.println("== Accuracy +/- " + EPSILON + 
                ", requires the zeta sum to be summed up to n=" + nMax + ";");
            nMax = maxIntern;
         }
         System.out.println("== Compute the zeta sum up to n=" + nMax + " ==");
         exponent[0] = -s[0];
         exponent[1] = -s[1];
         for ( int n = 1; n <= nMax; n++ ) {
            sum = Complex.add( sum, Complex.power(n, exponent) );
         }
System.out.println("### used Euler's formula!");
         return sum;
*/
// /*
      } else if ( s[0] > 6 ) {
         int nMax = (int) Math.pow( EPSILON, -1/s[0] );
         int maxIntern = (int) 5e6+1;
         if ( nMax <=0 || nMax > maxIntern ) {
            System.out.println("== Accuracy +/- " + EPSILON + 
                ", requires the zeta sum to be summed up to n=" + nMax + ";");
            nMax = maxIntern;
         }
//System.out.println("== Compute the zeta sum up to n=" + nMax + " ==");
         for ( int n = 1; n <= nMax; n++ ) {
            tmp[0] = Math.pow( n, -s[0] ) * Math.cos( -s[1] * Math.log( n ) );
            tmp[1] = Math.pow( n, -s[0] ) * Math.sin( -s[1] * Math.log( n ) );
            if ( n % 2 == 0 ) {
               sum[0] -= tmp[0];
               sum[1] -= tmp[1];
            } else {
               sum[0] += tmp[0];
               sum[1] += tmp[1];
            }
         }
         tmp = Complex.power( 2.0, exponent );
         tmp[0] = 1 - tmp[0];
         tmp[1] = -tmp[1];
//System.out.println("### tmp = " + tmp[0] + " + " + tmp[1] + " i" );
//System.out.println("### exponent = " + exponent[0] + " + " + exponent[1] + " i" );
         sum = Complex.divide( sum, tmp );
//System.out.println("### sum = " + sum[0] + " + " + sum[1] + " i" );
//      */
      } else { // else - s[0] < 0 (?)
//System.out.println("### im else-Zweig fuer s = " + Complex.toString(s));
         int nMax = 30;
         /*
         if ( s[0] > 0 ) {
            nMax = 500;
            //nMax = (int) Math.abs(s[1]); // korrekt, aber lang!!!!!
//System.out.println(" nMax =" + nMax);
         }
         */
         // factor = 1 / ( 1 - 2 (1-s) ):
         double[] factor = Complex.power( 2., exponent );
         factor[0] = 1 - factor[0];
         factor[1] *= -1;
         factor = Complex.divide( Complex.ONE_, factor );
//System.out.println("### factor = " + factor[0] + " + " + factor[1] + " i" );
         
         double[] h = new double[2]; // auxiliary/temporary number;
         exponent[0] = -s[0];
         //if ( nMax > 1029 ) nMax = 1029; // this is the maximal n for ( n choose k )!
         if ( s[0] < 0 && nMax > -s[0] ) nMax = (int) Math.floor(-s[0]) + 1; // this seems to be best for negative n (small absolute value) ...
         //if ( nMax > 30 ) nMax = (int) 30; // this seems to be best for negative n (small absolute value) ...
//System.out.println("== Compute the zeta sum up to n=" + nMax + " ==");
         for ( int n = 0; n <= nMax; n++ ) {
            h = Complex.multiply( Math.pow(2, -(n+1) ), factor );
//if ( n == 5 ) {
//System.out.println("--- n=" + n + ", exponent = " + exponent[0] + " + " + exponent[1] + " i" );
//}
            for ( int k = 0; k <= n; k++ ) {
               tmp = Complex.power( k+1, exponent );
               tmp = Complex.multiply( MyMath.binomial(n, k), tmp );
//if ( n <= 4 ) {
//System.out.println("### n=" + n + ", k="+k+", tmp = " + tmp[0] + " + " + tmp[1] + " i" );
//System.out.println("    h=" + h[0] + " + " + h[1] + " i" );
//}
               tmp = Complex.multiply( h, tmp );
               if ( k % 2 == 0   ) {
                  sum[0] += tmp[0];
                  sum[1] += tmp[1];
               } else {
                  sum[0] -= tmp[0];
                  sum[1] -= tmp[1];
               }

/*
if ( ( Math.abs(sum[0]) <= .1 ||
       n == -s[0] || n == -s[0] + 1 || n == -s[0] - 1 ) && k == n ) {
System.out.println("### n=" + n + ", k="+k+", tmp = " + tmp[0] + " + " + tmp[1] + " i" );
System.out.println("    h=" + h[0] + " + " + h[1] + " i" );
System.out.println("    n über k = " + Combinatorics.binomial(n,k) );
double[] exp = { -s[0], -s[1]};
double[] y = Complex.power( k+1, exp );
System.out.println("    (k+1) hoch (-s) = " + y[0] + " + " + y[1] + " i" );
y = Complex.multiply( Combinatorics.binomial(n, k), y );
System.out.println("    h * (k+1) hoch (-s) * ( n über k ) = " + 
        (h[0]*y[0] - h[1]*y[1]) + " + " + (h[0]*y[1] + h[1]*y[0]) + " i" 
);
System.out.println("  sum=" + sum[0] + " + " + sum[1] + " i" );
}
*/
/*
if ( tmp[1] != 0 ) {
System.out.println("### n=" + n + ", k="+k+", tmp = " + tmp[0] + " + " + tmp[1] + " i" );
System.out.println("    h=" + h[0] + " + " + h[1] + " i" );
System.out.println("    n ber k = " + Combinatorics.binomial(n,k) );
double[] y = Complex.power( k+1, exponent );
System.out.println("    (k+1) hoch (-s) = " + y[0] + " + " + y[1] + " i" );
return sum;
}
*/
            }
         }
      }   
      return sum;
   }
   
   /** 
    * Riemann-Siegel Z-function <i>Z</i>(<i>t</i>).
    * It is determined by the formula
   <p align="center"> 
   <table align="center" border="0">
     <tr>
       <td> 
         <i>Z</i>(<i>t</i>)    
       </td><td align="center"> 
         &nbsp; = &nbsp;     
       </td><td>
         2
       </td><td> 
         <table border="0"> 
           <tr><td align="center" class="small"><i>m</i></td></tr> 
           <tr><td align="center" style="font-size:xx-large;">&#931;</td></tr> 
           <tr><td align="center" class="small"><i>n</i>=1</td></tr> 
         </table> 
       </td><td>
         <table border="0"> 
           <tr><td align="center">cos[&#952;(<i>t</i>) - <i>t</i> ln <i>n</i>]</td></tr> 
           <tr><td height="1"><hr/></td></tr> 
           <tr><td align="center">&#8730;<i>n</i></td></tr> 
         </table>
       </td><td colspan="2">
       </td><td>
         &nbsp; + <i>O</i>(|<i>t</i>|<sup>-1/4</sup>).
       </td>
     </tr>
    </table>
   </p>
   <p>
     where
   </p>
   <p align="center"> 
   <table align="center" border="0">
     <tr>
       <td> 
         <i>m</i> &nbsp; = &nbsp; <i>m</i>(<i>t</i>)    
       </td><td align="center"> 
         &nbsp; = &nbsp;     
       </td><td>
         <span style="font-size:xx-large;">&#9123;</span>
       </td><td>
         <table border="0"> 
           <tr><td align="center">|<i>t</i>|</td></tr> 
           <tr><td height="1"><hr/></td></tr> 
           <tr><td align="center">2 &#960;</td></tr> 
         </table>
       </td><td>
         <span style="font-size:xx-large;">&#9126;</span>
       </td>
    </table>
   </p>
   <p>
     and
   </p>
   <p align="center"> 
   <table align="center" border="0">
     <tr>
       <td> 
         <i>&#952;</i>(<i>t</i>)    
       </td><td align="center"> 
         &nbsp; = &nbsp;     
       </td><td> 
         <table border="0"> 
           <tr><td align="center"><i>t</i></td></tr> 
           <tr><td height="1"><hr/></td></tr> 
           <tr><td align="center">2</td></tr> 
         </table>
       </td><td>
         ln
       </td>
       </td><td> 
         <table border="0"> 
           <tr><td align="center"><i>t</i></td></tr> 
           <tr><td height="1"><hr/></td></tr> 
           <tr><td align="center">2 &#960;</td></tr> 
         </table>
       </td><td align="center"> 
         &nbsp; - &nbsp;     
       </td><td> 
         <table border="0"> 
           <tr><td align="center"><i>t</i></td></tr> 
           <tr><td height="1"><hr/></td></tr> 
           <tr><td align="center">2</td></tr> 
         </table>
       </td><td align="center"> 
         &nbsp; - &nbsp;
       </td><td> 
         <table border="0"> 
           <tr><td align="center">&#960;</td></tr> 
           <tr><td height="1"><hr/></td></tr> 
           <tr><td align="center">8</td></tr> 
         </table>
       </td><td align="center"> 
         &nbsp; + &nbsp;
       </td><td>
         <table border="0"> 
           <tr><td align="center">1</td></tr> 
           <tr><td height="1"><hr/></td></tr> 
           <tr><td align="center">48<i>t</i></td></tr> 
         </table>
       </td><td>
         &nbsp; + <i>R</i>(<i>t</i>)
         &nbsp; &nbsp; with |<i>R</i>(<i>t</i>)| &lt;
       </td><td>
         <table border="0"> 
           <tr><td align="center">7</td></tr> 
           <tr><td height="1"><hr/></td></tr> 
           <tr><td align="center">5760 <i>t</i><sup>3</sup></td></tr> 
         </table>
       </td><td>
         +
       </td><td>
         <table border="0"> 
           <tr><td align="center">2</td></tr> 
           <tr><td height="1"><hr/></td></tr> 
           <tr><td align="center"><i>t</i><sup>5</sup></td></tr> 
         </table>
       </td>
     </tr>
    </table>
    </p>
    * <p>
    * We have the equality <i>Z</i>(<i>t</i>) = e<sup>i &#952;(<i>t</i>)</sup> &#950;(&#189; + i<i>t</i>).
    * Cf. §6.5 in H.M. Edwards: <i>Riemann's Zeta Function.</i> Academic Press, New York 1974.
    * </p>
    * @param t value on the critical line <i>s</i> = &#189; + i<i>t</i>.
    * @return <i>Z</i>(<i>t</i>)
    * @see #zeta(double[])
    */
   public static double Z ( double t ) {
      if ( Math.abs(t) < 10 ) {
         double[] s = {0.5, t};
         return -Complex.abs( zeta(s) );
      }
      double theta = t/2 * Math.log( t/ (2*Math.PI) ) - t/2 - Math.PI/8 + 1/(48*t);
      long m = (long) Math.floor( Math.sqrt( Math.abs(t)/(2*Math.PI) ) );
      double sum = 0;
      for ( long n = 1; n <= m; n++ ) {
         sum += Math.cos( theta - t * Math.log(n) ) / Math.sqrt(n);
      }
      return 2*sum;
   }

   /** For test purposes.*/
   /*
   public static void main ( String args[] ) {
      if ( args != null && args.length > 0 && args[0].equalsIgnoreCase("Z") ) {
         double t;
         long start = System.currentTimeMillis();
         for ( int i=0; i<=100; i++ ) {
            //t = 14.0 + i / 100.0;
            //t = 7005.05 + i / 1000.0;
            t = 6.0 + i / 5.0;
            System.out.println("Z("+t+") = " + Z(t) );
         }
         long zeit = System.currentTimeMillis();
         System.out.println("Needed running time:"+( zeit - start )/1000.0 + " sec" );
         System.exit(0); // Ende!
      }
      // Eingabefeld:
      javax.swing.JTextField feld1 = new javax.swing.JTextField(".5");
      javax.swing.JTextField feld2 = new javax.swing.JTextField("1000");
    
      Object[] msg = {"Re s:", feld1, "Im s:", feld2};
      javax.swing.JOptionPane optionPane = new javax.swing.JOptionPane( msg );
    
      optionPane.createDialog(null,"Eingabe").setVisible(true);

      double[] s = { Double.parseDouble( feld1.getText() ), Double.parseDouble( feld2.getText() ) }; 
      
      double[] result = {0.,.0};
      java.text.DecimalFormat digit = new java.text.DecimalFormat( "#,###.#############" );
      
      String ausgabe = "";           // Ausgabestring
        
      long start = System.currentTimeMillis();
      
      result = zeta( s );
      
      long zeit = System.currentTimeMillis();

      ausgabe += "\n  \u03B6(" + Complex.toString( s, digit );
      ausgabe += ") = ";
      ausgabe += Complex.toString( result, digit );
      ausgabe += "   (" + digit.format(zeit - start) + " ms)";
      
      ausgabe += "\nProbe:";
      ausgabe += "\n  \u03B6(-21) = " + digit.format( -281.46 ) + " [according to Mathematica]";
      ausgabe += "\n  \u03B6(-7) = " + digit.format( 1. / 240. );
      ausgabe += "\n  \u03B6(-5) =" + digit.format( -1. / 252. );
      ausgabe += "\n  \u03B6(-3) = " + digit.format( 1. / 120. );
      ausgabe += "\n  \u03B6(-1) =" + digit.format( - 1. / 12. );
      ausgabe += "\n  \u03B6(0)  = " + digit.format( - 1. / 2. );
      ausgabe += "\n  \u03B6(\u00BD) = " + digit.format( -1.46035 ) + " [according to Mathematica]";
      ausgabe += "\n  \u03B6(2)  = " + digit.format( Math.pow(Math.PI, 2) / 6 );
      ausgabe += "\n  \u03B6(3)  = " + digit.format( 1.2020569032 );
      ausgabe += "\n  \u03B6(4)  = " + digit.format( Math.pow(Math.PI, 4) / 90 );
      ausgabe += "\n  \u03B6(6)  = " + digit.format( Math.pow(Math.PI, 6) / 945 );
      ausgabe += "\n  \u03B6(8)  = " + digit.format( Math.pow(Math.PI, 8) / 9450 );
      ausgabe += "\n  \u03B6(10) = " + digit.format( Math.pow(Math.PI,10) / 93555 );
      
      ausgabe += "\n\n  \u03C7(" + Complex.toString(s) + ") \u03C7(";
      ausgabe += Complex.toString( Complex.subtract(Complex.ONE_, s) );
      ausgabe += ") = ";
      ausgabe += Complex.toString( Complex.multiply( chi(s), chi(Complex.subtract(Complex.ONE_, s)) ) );
      
      double[] t = {s[1],0};
      ausgabe += "\n\n  Z(" + Complex.toString(t) + ") = ";
      start = System.currentTimeMillis();
      t[0] = Z(s[1]);
      t[1] = 0;
      ausgabe += Complex.toString( t );
      zeit = System.currentTimeMillis();
      ausgabe += "   (" + digit.format(zeit - start) + " ms)";

      // Ausgabe auf dem Bildschirm:
      javax.swing.JOptionPane.showMessageDialog( null, ausgabe, "Ergebnis \u03B6(s)", javax.swing.JOptionPane.PLAIN_MESSAGE );
        
      System.exit( 0 );
   }
   // */
}