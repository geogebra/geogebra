/*
 * Complex.java - Class providing static methods for complex numbers.
 *
 * Copyright (C) 2004-2008 Andreas de Vries
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
/**
 * This class enables the creation of objects representing complex numbers, 
 * as well the implementation of mathematical functions of complex numbers 
 * by static methods.
 * A complex number <i>z</i> &#8712; <span style="font-size:large;">&#8450;</span>
 * is uniquely determined by <i>z</i> = <i>x</i> + i<i>y</i>, where
 * <i>x</i> and <i>y</i> are real numbers and i = &#8730;-1.
 * In the class <code>Complex</code>, a complex number <i>z</i> is internally
 * represented by a <code>double</code>-array of length 2, where
 * <code>z[0]</code> = <i>x</i> = Re <i>z</i>, and
 * <code>z[1]</code> = <i>y</i> = Im <i>z</i>.
 * In the sequel this representation is called <i>array representation</i>
 * of complex numbers. It is the purpose of the static methods to provide
 * this fast representation directly without generating complex number objects.
 * @author  Andreas de Vries
 * @version 1.0
 */
public class Complex {
   private static final long serialVersionUID = -1679819632;   
   
   /** Euler-Mascheroni constant &#947; = 0.577215664901532860605.
    *  @see BigNumbers#GAMMA
    */
   public static final double GAMMA = 0.577215664901532860605;

   /** Accuracy up to which equality of double values are computed in
    *  methods of this class. Its current value is {@value}.
    *  It is used, for instance, in the methods {@link #gamma(Complex)},
    *  {@link #lnGamma(Complex)}, or {@link #pow(Complex)}.
    */
   public static final double ACCURACY = 1e-10;
   /** Constant 0 &#8712; <span style="font-size:large;">&#8450;</span>.*/
   public static final Complex ZERO = new Complex(0., 0.);
   /** Constant 1 &#8712; <span style="font-size:large;">&#8450;</span>.*/
   public static final Complex ONE  = new Complex(1., 0.);
   /** Constant i &#8712; <span style="font-size:large;">&#8450;</span>.*/
   public static final Complex   I  = new Complex(.0, 1.);
   /** Constant 0 &#8712; <span style="font-size:large;">&#8450;</span> in the array representation.*/
   static final double[] ZERO_ = {0.,0.};
   /** Constant 1 &#8712; <span style="font-size:large;">&#8450;</span> in the array representation.*/
   static final double[] ONE_  = {1.,0.};
   /** Constant i &#8712; <span style="font-size:large;">&#8450;</span> in the array representation.*/
   static final double[] I_    = {.0,1.};
   
   /** Object attribute representing a complex number.*/
   private double[] z;
   
   /** Creates a complex number <i>z</i> = <i>x</i> + i<i>y</i> 
    *  with real part <i>x</i> and imaginary part <i>y</i>.
    */
   public Complex(double x, double y) {
      z = new double[]{x,y};
   }
   
   /** Creates a complex number <i>z</i> = <i>z</i>[0] + i<i>z</i>[1]
    *  from the "array representation," i.e.,
    *  with real part <i>z</i>[0] and imaginary part <i>z</i>[1].
    */
   public Complex(double[] z) {
      this.z = new double[z.length];
      for (int i = 0; i < z.length; i++) {
         this.z[i] = z[i];
      }
   }

   /** Returns the real part Re<i>z</i> of this complex number <i>z</i>.
    *  For <i>z</i> = <i>x</i> + i<i>y</i> it is defined as 
    *  Re<i>z</i> = <i>x</i>.
    *  @return Re<i>z</i>
    */
   public double getRe() {
      return z[0];
   }
   
   /** Returns the imaginary part Im<i>z</i> of this complex number <i>z</i>.
    *  For <i>z</i> = <i>x</i> + i<i>y</i> it is defined as 
    *  Im<i>z</i> = <i>y</i>.
    *  @return Im<i>z</i>
    */
   public double getIm() {
      return z[1];
   }
   
   /** Returns the absolute value, or complex modulus,
    *  |<i>z</i>| of <i>z</i> &#8712; <span style="font-size:large;">&#8450;</span>.
    *  For <i>z</i> = <i>x</i> + i<i>y</i> it is defined as 
    *  |<i>z</i>| = &#8730;(<i>x</i><sup>2</sup> + <i>y</i><sup>2</sup>).
    *  @param z the complex number <i>z</i> in the array representation
    *  @return |<i>z</i>|
    */
   public static double abs( double[] z ) {
      double x = 0.0;
      double h;

      if ( Math.abs(z[0]) == 0 && Math.abs(z[1]) == 0 ) {
         x = 0.0;
      } else if ( Math.abs(z[0]) >= Math.abs(z[1]) ) {
         h = z[1]/z[0];
         x = Math.abs( z[0] ) * Math.sqrt( 1 + h*h );
      } else {
         h = z[0]/z[1];
         x = Math.abs( z[1] ) * Math.sqrt( 1 + h*h );
      }
      
      return x;
   }

   /** Returns the absolute value, or complex modulus,
    *  |<i>z</i>| of <i>z</i> &#8712; <span style="font-size:large;">&#8450;</span>
    *  of the complex number <i>z</i>.
    *  For <i>z</i> = <i>x</i> + i<i>y</i> it is defined as 
    *  |<i>z</i>| = &#8730;(<i>x</i><sup>2</sup> + <i>y</i><sup>2</sup>).
    *  @param z a complex number
    *  @return |<i>z</i>|
    */
   public static double abs(Complex z) {
      return abs(new double[] {z.z[0],z.z[1]});
   }

   /** Returns the absolute value, or complex modulus,
    *  |<i>z</i>| of <i>z</i> &#8712; <span style="font-size:large;">&#8450;</span>
    *  of this complex number <i>z</i>.
    *  For <i>z</i> = <i>x</i> + i<i>y</i> it is defined as 
    *  |<i>z</i>| = &#8730;(<i>x</i><sup>2</sup> + <i>y</i><sup>2</sup>).
    *  @return |<code>this</code>|
    */
   public double abs() {
      return abs(new double[] {z[0],z[1]});
   }
   
   /**
    *  Returns the sum of two complex numbers <i>x</i> and <i>y</i>. 
    *  For <i>x</i> = <i>x</i><sub>0</sub> + i<i>x</i><sub>1</sub>
    *  and <i>y</i> = <i>y</i><sub>0</sub> + i<i>y</i><sub>1</sub>, we have
    *  <p align="center">
    *    <i>x + y</i> =
    *     <i>x</i><sub>0</sub> + <i>y</i><sub>0</sub> + i
    *     (<i>x</i><sub>1</sub> + <i>y</i><sub>1</sub>)
    *  </p>
    *  @param x the first addend in the array representation
    *  @param y the second addend in the array representation
    *  @return the sum <i>x</i> + <i>y</i>
    *  @see #add(Complex)
    */
   public static double[] add( double[] x, double[] y ) {
      double[] result = new double[2];
      result[0] = x[0] + y[0];
      result[1] = x[1] + y[1];
      return result;
   }

   /**
    *  Returns the sum of this number and the complex number <i>z</i>. 
    *  For <i>x</i> = <i>x</i><sub>0</sub> + i<i>x</i><sub>1</sub>
    *  and <i>y</i> = <i>y</i><sub>0</sub> + i<i>y</i><sub>1</sub>, we have
    *  <p align="center">
    *    <i>x + y</i> =
    *     <i>x</i><sub>0</sub> + <i>y</i><sub>0</sub> + i
    *     (<i>x</i><sub>1</sub> + <i>y</i><sub>1</sub>)
    *  </p>
    *  @param z the addend
    *  @return the sum <code>this</code> + <i>z</i>
    *  @see #plus(Complex)
    *  @see #add(double[],double[])
    */
   public Complex add(Complex z) {
      return new Complex(this.z[0] + z.z[0], this.z[1] + z.z[1]);
   }

   /**
    *  Returns the argument of the complex number <i>z</i>. 
    *  If <i>z</i> = <i>x</i> + i<i>y</i>, we have arg(<i>z</i>) = arctan(<i>y</i>/<i>x</i>).
    *  @param z a complex number
    *  @return the argument of <i>z</i>
    *  @see #arg(Complex)
    */
   public static double arg( double[] z ) {
      return Math.atan2( z[1], z[0] );
   }
   
   /**
    *  Returns the argument of the complex number <i>z</i>.
    *  If <i>z</i> = <i>x</i> + i<i>y</i>, we have arg(<i>z</i>) = arctan(<i>y</i>/<i>x</i>).
    *  @param z a complex number
    *  @return the argument of <i>z</i>
    *  @see #arg()
    *  @see #arg(double[])
    */
   public static double arg(Complex z) {
      return Math.atan2( z.z[1], z.z[0] );
   }
   
   /**
    *  Returns the argument of this complex number <i>z</i>.
    *  If <i>z</i> = <i>x</i> + i<i>y</i>, we have arg(<i>z</i>) = arctan(<i>y</i>/<i>x</i>).
    *  @return the argument of <code>this</code>
    *  @see #arg(Complex)
    *  @see #arg(double[])
    */
   public double arg() {
      return arg(this);
   }
   
   /** Returns the cosine of a complex number <i>z</i>.*/
   public static double[] cos( double[] z ) {
      double[] result = new double[2];
      
      result[0] = Math.cos(z[0]) * Math.cosh(z[1]);  // since JDK 1.5:
      //result[0] = Math.cos(z[0]) * ( Math.exp(z[1]) + Math.exp(-z[1]) ) / 2;
      result[1] = Math.sin(z[0]) * Math.sinh(z[1]); // since JDK 1.5
      //result[1] = Math.sin(z[0]) * ( Math.exp(z[1]) - Math.exp(-z[1]) ) / 2;
      return result;
   }

   /** Returns the cosine of this complex number.*/
   public static Complex cos(Complex z) {
      return new Complex(cos(new double[]{z.z[0],z.z[1]}));
      /*
      double[] result = new double[2];
      
      result[0] = Math.cos(z[0]) * Math.cosh(z[1]);  // since JDK 1.5:
      //result[0] = Math.cos(z.z[0]) * ( Math.exp(z.z[1]) + Math.exp(-z.z[1]) ) / 2;
      result[1] = Math.sin(z[0]) * Math.sinh(z[1]); // since JDK 1.5
      //result[1] = Math.sin(z.z[0]) * ( Math.exp(z.z[1]) - Math.exp(-z.z[1]) ) / 2;
      return new Complex(result);
      */
   }

   /** divides a real number <i>x</i> by a complex number <i>y</i>. 
    *  This method is implemented avoiding overflows.
    *  @param x the dividend
    *  @param y the divisor
    *  @see #divide(double, Complex)
    *  @see #divide(double[], double[])
    */
   public static double[] divide( double x, double[] y ) {
      double[] w = new double[2];
      double h;

      if ( Math.abs(y[0]) == 0 && Math.abs(y[1]) == 0 ) {
         if ( x > 0 ) 
            w[0] = Double.POSITIVE_INFINITY;
         else if ( x < 0 )
            w[0] = Double.NEGATIVE_INFINITY;
         else 
            w[0] = 1;
         return w;
      }
      
      if ( Math.abs(y[0]) >= Math.abs(y[1]) ) {
         h = y[1]/y[0];
         w[0] = x / ( y[0] + y[1] * h );
         w[1] = - x * h / ( y[0] + y[1] * h );
      } else {
         h = y[0]/y[1];
         w[0] = x * h / ( y[0] * h + y[1] );
         w[1] = - x / ( y[0] * h + y[1] );
      }
      
      return w;
   }

   /** divides a real number <i>x</i> by a complex number <i>y</i>. 
    *  This method is implemented avoiding overflows.
    *  @param x the dividend
    *  @param y the divisor
    *  @see #divide(double, double[])
    */
   public static Complex divide( double x, Complex y ) {
      return new Complex(divide(x, new double[]{y.z[0],y.z[1]}));
   }

   /** divides two complex numbers <i>x</i> and <i>y</i>. This method is implemented avoiding overflows.
    *  @param x dividend
    *  @param y divisor
    *  @return <i>x/y</i>
    *  @see #divide(double, double[])
    */
   public static double[] divide( double[] x, double[] y ) {
      double[] w = new double[2];
      double h;

      if ( Math.abs(y[0]) == 0 && Math.abs(y[1]) == 0 ) {
         if ( x[0] > 0 ) 
            w[0] = Double.POSITIVE_INFINITY;
         else if ( x[0] < 0 )
            w[0] = Double.NEGATIVE_INFINITY;
         else 
            w[0] = 1;
         if ( x[1] > 0 ) 
            w[1] = Double.POSITIVE_INFINITY;
         else if ( x[1] < 0 )
            w[1] = Double.NEGATIVE_INFINITY;
         else 
            w[1] = 0; // if Im x == 0, x/0 is real!
         return w;
      }
      
      if ( Math.abs(y[0]) >= Math.abs(y[1]) ) {
         h = y[1]/y[0];
         w[0] = ( x[0] + x[1] * h ) / ( y[0] + y[1] * h );
         w[1] = ( x[1] - x[0] * h ) / ( y[0] + y[1] * h );
      } else {
         h = y[0]/y[1];
         w[0] = ( x[0] * h + x[1] ) / ( y[0] * h + y[1] );
         w[1] = ( x[1] * h - x[0] ) / ( y[0] * h + y[1] );
      }
      
      return w;
   }

   /** divides this complex numbers by <i>z</i>. This method is implemented avoiding overflows.
    *  @param z divisor
    *  @return <code>this</code>/<i>z</i>
    *  @see #divide(double[], double[])
    */
   public Complex divide( Complex z ) {
      return new Complex(divide(new double[]{this.z[0],this.z[1]}, new double[]{z.z[0],z.z[1]}));
   }

   /**
    *  The exponential function of a complex number <i>z</i>.
    *  The following formula holds for <i>z</i> = <i>x</i> + i<i>y</i>:
    *  <p style="text-align:center">
    *     exp(<i>z</i>)
    *     =
    *     e<sup><i>x</i></sup>
    *     (cos<i>y</i> + i sin <i>y</i>).   
    *  </p>
    *  @param z a complex number
    *  @return exp(<i>z</i>)
    *  @see #ln(double[])
    */
   public static double[] exp( double[] z) {
      if ( z[0] > 709 ) return multiply( Double.POSITIVE_INFINITY, ONE_);

      double[] w = { Math.exp(z[0]) * Math.cos(z[1]), Math.exp(z[0]) * Math.sin(z[1]) };
      return w;
   }

   /**
    *  The exponential function of the complex number <i>z</i>.
    *  The following formula holds for <i>z</i> = <i>x</i> + i<i>y</i>:
    *  <p style="text-align:center">
    *     exp(<i>z</i>)
    *     =
    *     e<sup><i>x</i></sup>
    *     (cos<i>y</i> + i sin <i>y</i>),
    *  </p>
    *  where <i>z</i> is this complex number.
    *  @return exp(<i>z</i>) where <i>z</i> is this complex number
    *  @see #ln(Complex)
    */
   public static Complex exp(Complex z) {
      return new Complex(exp(new double[]{z.z[0],z.z[1]}));
      /*
      if ( z[0] > 709 ) return new Complex(multiply( Double.POSITIVE_INFINITY, ONE_ ));

      double[] w = { Math.exp(z[0]) * Math.cos(z[1]), Math.exp(z[0]) * Math.sin(z[1]) };
      return new Complex(w);
      */
   }

   /**
    *  The Euler gamma function &#915;(<i>z</i>) of a complex number <i>z</i>.
    *  For Re <i>z</i> &gt; 0, it is computed according to the method of Lanczos.
    *  Otherwise, the following formula of Weierstrass is applied, 
    *  which holds for any <i>z</i> but converges more slowly.
   <p align="center">
   <table align="center" border="0">
     <tr>
       <td> 
         &#915;(<i>z</i>)
       </td><td align="center"> 
         &nbsp; = &nbsp;
       </td><td>
         <table border="0">
           <tr><td align="center">1</td></tr> 
           <tr><td height="1"><hr/></td></tr> 
           <tr><td align="center"><i>z</i> e<sup><i>&#947;z</i></sup></td></tr> 
         </table>
       </td><td>
         <table border="0"> 
           <tr><td align="center" class="small">&#8734;</td></tr> 
           <tr><td align="center" style="font-size:xx-large;">&#928;</td></tr> 
           <tr><td align="center" class="small"><i>n</i>=1</td></tr> 
         </table> 
       </td><td align="center" style="font-size:xx-large;">[
       </td><td align="center" style="font-size:xx-large;">(
       </td><td>
         1 +
       </td><td>
         <table border="0">
           <tr><td align="center"><i>z</i></td></tr> 
           <tr><td height="1"><hr/></td></tr> 
           <tr><td align="center"><i>n</i></td></tr> 
         </table>
       </td><td align="center" style="font-size:xx-large;">)<sup><span style="font-size:small;">-1</span></sup>
       </td><td> 
         e<sup><i>z/n</i></sup>   
       </td><td align="center" style="font-size:xx-large;">]
       </td>
     </tr>
   </table>
   </p>
    *  It is approximated up to a relative error of 10<sup>6</sup> times 
    *  the given {@link #ACCURACY accuracy}.
    *  Here &#947; denotes the {@link Numbers#GAMMA Euler-Mascheroni constant}.
    *  @param z a complex number
    *  @return &#915;(<i>z</i>)
    *  @see #gamma(Complex)
    *  @see #lnGamma(double[])
    *  @see Numbers#gamma(double)
    */
   public static double[] gamma(double[] z) {
      if ( z[0] < 0 ) { // Weierstrass form:
         double[] w = divide(ONE_, multiply( z, power( Math.E, multiply(GAMMA, z) ) ) );
         int nMax = (int) ( 1e-6 / ACCURACY );
         double[] z_n;
         for ( int n = 1; n <= nMax; n++ ) {
            z_n = multiply(1.0/n, z);
            w = multiply( w, add( ONE_, z_n ) );
            w = divide( w, power( Math.E, z_n ) );
         }
         w = divide(1.0, w);
         return w;
      }

      double[] x = {z[0], z[1]};
      double[] c = { 76.18009173,-86.50532033,24.01409822,-1.231739516,.00120858003,-5.36382e-6 };

      boolean reflec;

      if ( x[0] >= 1. ) {
         reflec = false;
         x[0] = x[0] - 1.;
      } else {
         reflec = true;
         x[0] = 1. - x[0];
      }
      
      double[] xh  = {x[0] + .5, x[1]};
      double[] xgh = {x[0] + 5.5, x[1]};
      double[] s = ONE_;
      double[] anum = {x[0], x[1]};
      for (int i = 0; i < c.length; ++i) {
         anum = add( anum, ONE_);
         s = add( s, divide(c[i], anum) );;
      }
      s = multiply( 2.506628275, s );
      //g = Math.pow(xgh, xh) * s / Math.exp(xgh);
      double[] g = multiply( power(xgh, xh), s );
      g = divide(g, power(Math.E, xgh) );
      if (reflec) {
         //result =  PI x / (g * sin(PI x));
         if ( Math.abs(x[1]) > 709 ) {  // sin( 710 i ) = Infinity !! 
            return ZERO_;
         }
         double[] result = multiply(Math.PI, x);
         result = divide( result, multiply( g, sin( multiply(Math.PI, x) ) ) );
         return result;
      } else {
         return g;
      }
   }

   /**
    *  The Euler gamma function &#915;(<i>z</i>) of a complex number <i>z</i>.
    *  For Re <i>z</i> &gt; 0, it is computed according to the method of Lanczos.
    *  Otherwise, the following formula of Weierstrass is applied, 
    *  which holds for any <i>z</i> but converges more slowly.
   <p align="center">
   <table align="center" border="0">
     <tr>
       <td> 
         &#915;(<i>z</i>)
       </td><td align="center"> 
         &nbsp; = &nbsp;
       </td><td>
         <table border="0">
           <tr><td align="center">1</td></tr> 
           <tr><td height="1"><hr/></td></tr> 
           <tr><td align="center"><i>z</i> e<sup><i>&#947;z</i></sup></td></tr> 
         </table>
       </td><td>
         <table border="0"> 
           <tr><td align="center" class="small">&#8734;</td></tr> 
           <tr><td align="center" style="font-size:xx-large;">&#928;</td></tr> 
           <tr><td align="center" class="small"><i>n</i>=1</td></tr> 
         </table> 
       </td><td align="center" style="font-size:xx-large;">[
       </td><td align="center" style="font-size:xx-large;">(
       </td><td>
         1 +
       </td><td>
         <table border="0">
           <tr><td align="center"><i>z</i></td></tr> 
           <tr><td height="1"><hr/></td></tr> 
           <tr><td align="center"><i>n</i></td></tr> 
         </table>
       </td><td align="center" style="font-size:xx-large;">)<sup><span style="font-size:small;">-1</span></sup>
       </td><td> 
         e<sup><i>z/n</i></sup>   
       </td><td align="center" style="font-size:xx-large;">]
       </td>
     </tr>
   </table>
   </p>
    *  It is approximated up to a relative error of 10<sup>6</sup> times 
    *  the given {@link #ACCURACY accuracy}.
    *  Here &#947; denotes the {@link Numbers#GAMMA Euler-Mascheroni constant}.
    *  @param z a complex number
    *  @return &#915;(<i>z</i>)
    *  @see #exp(double[])
    *  @see Numbers#gamma(double)
    */
   public static Complex gamma(Complex z) {
      return new Complex(gamma(new double[]{z.z[0],z.z[1]}));
   }
   
   /** Returns the natural logarithm of the cosine of a complex number <i>z</i>.*/
   public static double[] lnCos( double[] z ) {
      double[] result = new double[2];
      
      if ( Math.abs(z[1]) <= 709 ) {
         //result[0] = Math.cos(z[0]) * Math.cosh(z[1]);  // since JDK 1.5:
         result[0] = Math.cos(z[0]) * ( Math.exp(z[1]) + Math.exp(-z[1]) ) / 2;
         //result[1] = Math.sin(z[0]) * Math.sinh(z[1]); // since JDK 1.5
         result[1] = Math.sin(z[0]) * ( Math.exp(z[1]) - Math.exp(-z[1]) ) / 2;
         result = ln(result);
      } else { // approximately cosh y = sinh y = e^|y| / 2:
         // ln |cos z| = ln |y| - ln 2 for z = x + iy
         result[0] = Math.abs(z[1]) - Math.log(2);
         // arg |sin z| = arctan( sgn y cot x ) for z = x + iy:
         if ( z[1] < 0 ) {
            result[1] = Math.atan( - 1 / Math.tan(z[0]) );
         } else {
            result[1] = Math.atan( 1 / Math.tan(z[0]) );
         }
      }
      return result;
   }

   /**
    *  Logarithm of this complex number <i>z</i>. It is defined by
    *  ln <i>z</i> = ln |<i>z</i>| + i arg(<i>z</i>).
    *  @return ln <i>z</i> where <i>z</i> is this complex number
    *  @see #abs(Complex)
    *  @see #arg(Complex)
    *  @see #exp(Complex)
    *  @see #ln(double[])
    */
   public static Complex ln(Complex z) {
      return new Complex(ln(new double[]{z.z[0],z.z[1]}));
   }
   
   /**
    *  Logarithm of a complex number <i>z</i>. It is defined by
    *  ln <i>z</i> = ln |<i>z</i>| + i arg(<i>z</i>).
    *  @param z complex number
    *  @return ln <i>z</i>
    *  @see #abs(double[])
    *  @see #arg(double[])
    *  @see #exp(double[])
    *  @see #ln(Complex)
    */
   public static double[] ln( double[] z) {
      double[] result = { Math.log( abs(z) ), arg(z) };
      return result;
   }
   
   /**
    *  Logarithm of the Euler gamma function of a complex number <i>z</i>.
    *  For Re <i>z</i> &gt; 0, it is computed according to the method of Lanczos.
    *  Otherwise, the following formula is applied, which holds for any <i>z</i>
    *  but converges more slowly.
   <p align="center">
   <table align="center" border="0">
     <tr>
       <td> 
         ln &#915;(<i>z</i>)
       </td><td align="center"> 
         &nbsp; = &nbsp;     
       </td><td> 
         - ln <i>z</i> - <i>&#947;z</i>   
       </td><td>
         +    
       </td><td>
         <table border="0"> 
           <tr><td align="center" class="small">&#8734;</td></tr> 
           <tr><td align="center" style="font-size:xx-large;">&#931;</td></tr> 
           <tr><td align="center" class="small"><i>n</i>=1</td></tr> 
         </table> 
       </td><td align="center" style="font-size:xx-large;">[
       </td><td>
         <table border="0">
           <tr><td align="center"><i>z</i></td></tr> 
           <tr><td height="1"><hr/></td></tr> 
           <tr><td align="center"><i>n</i></td></tr> 
         </table>
       </td><td>
         - ln
       </td><td align="center" style="font-size:xx-large;">(
       </td><td>
         1 +
       </td><td>
         <table border="0">
           <tr><td align="center"><i>z</i></td></tr> 
           <tr><td height="1"><hr/></td></tr> 
           <tr><td align="center"><i>n</i></td></tr> 
         </table>
       </td><td align="center" style="font-size:xx-large;">)
       </td><td align="center" style="font-size:xx-large;">]
       </td>
     </tr>
   </table>
   </p>
    *  Here &#947; denotes the {@link Numbers#GAMMA Euler-Mascheroni constant}.
    *  @param z a complex number
    *  @return ln &#915;(<i>z</i>)
    *  @see #lnGamma(Complex)
    *  @see #gamma(double[])
    */
   public static double[] lnGamma( double[] z) {
      if ( z[0] < 0 ) {
         double[] w = add( ln(z), multiply(GAMMA, z) );
         w = multiply(-1.0, w);
         int nMax = (int) ( Math.abs(z[0]) / ACCURACY );
         if ( nMax > 10000 ) nMax = 10000;
         double[] z_n;
         for ( int n = 1; n <= nMax; n++ ) {
            z_n = multiply(1.0/n, z);
            w = add( w, z_n );
            w = subtract( w, ln( add( ONE_, z_n ) ) );
         }
         return w;
      }
      
      double[] x = {z[0], z[1]};
      double[] c = { 76.18009173,-86.50532033,24.01409822,-1.231739516,.00120858003,-5.36382e-6 };

      boolean reflec;

      if ( x[0] >= 1. ) {
         reflec = false;
         x[0] = x[0] - 1.;
      } else {
         reflec = true;
         x[0] = 1. - x[0];
      }
      
      double[] xh  = {x[0] + .5, x[1]};
      double[] xgh = {x[0] + 5.5, x[1]};
      double[] s = ONE_;
      double[] anum = {x[0], x[1]};
      for (int i = 0; i < c.length; ++i) {
         anum = add( anum, ONE_);
         s = add( s, divide(c[i], anum) );;
      }
      s = multiply( 2.506628275, s );
      //g = xh * Math.log(xgh) + Math.log(s) - xgh;
      double[] g = multiply(xh, ln(xgh));
      g = add(g, ln(s));
      g = subtract(g, xgh);
      if (reflec) {
         //result = Math.log(xx * Math.PI) - g - Math.log( Math.sin(xx * Math.PI) );
         double[] result = ln( multiply(Math.PI, x) );
         result = subtract( result, g);
         result = subtract( result, lnSin( multiply(Math.PI, x) ) );
         return result;
      } else {
         return g;
      }
   }

   /**
    *  Logarithm of the Euler gamma function of a complex number <i>z</i>.
    *  For Re <i>z</i> &gt; 0, it is computed according to the method of Lanczos.
    *  Otherwise, the following formula is applied, which holds for any <i>z</i>
    *  but converges more slowly.
   <p align="center">
   <table align="center" border="0">
     <tr>
       <td> 
         ln &#915;(<i>z</i>)
       </td><td align="center"> 
         &nbsp; = &nbsp;     
       </td><td> 
         - ln <i>z</i> - <i>&#947;z</i>   
       </td><td>
         +    
       </td><td>
         <table border="0"> 
           <tr><td align="center" class="small">&#8734;</td></tr> 
           <tr><td align="center" style="font-size:xx-large;">&#931;</td></tr> 
           <tr><td align="center" class="small"><i>n</i>=1</td></tr> 
         </table> 
       </td><td align="center" style="font-size:xx-large;">[
       </td><td>
         <table border="0">
           <tr><td align="center"><i>z</i></td></tr> 
           <tr><td height="1"><hr/></td></tr> 
           <tr><td align="center"><i>n</i></td></tr> 
         </table>
       </td><td>
         - ln
       </td><td align="center" style="font-size:xx-large;">(
       </td><td>
         1 +
       </td><td>
         <table border="0">
           <tr><td align="center"><i>z</i></td></tr> 
           <tr><td height="1"><hr/></td></tr> 
           <tr><td align="center"><i>n</i></td></tr> 
         </table>
       </td><td align="center" style="font-size:xx-large;">)
       </td><td align="center" style="font-size:xx-large;">]
       </td>
     </tr>
   </table>
   </p>
    *  Here &#947; denotes the {@link Numbers#GAMMA Euler-Mascheroni constant}.
    *  @param z a complex number
    *  @return ln &#915;(<i>z</i>)
    *  @see #gamma(Complex)
    *  @see #lnGamma(double[])
    */
   public static Complex lnGamma(Complex z) {
      return new Complex(lnGamma(new double[]{z.z[0],z.z[1]}));
   }
   
   /** Returns the natural logarithm of the sine of a complex number <i>z</i>.
    *  @param z a complex number in the array representation
    *  @return lnSin <i>z</i>
    */
   public static Complex lnSin(Complex z) {
      return new Complex(lnSin(new double[]{z.z[0], z.z[1]}));
   }
   
   /** Returns the natural logarithm of the sine of a complex number <i>z</i>.
    *  @param z a complex number in the array representation
    *  @return lnSin <i>z</i>
    */
   public static double[] lnSin( double[] z ) {
      double[] result = new double[2];
      
      if ( Math.abs(z[1]) <= 709 ) {
         result[0] = Math.sin(z[0]) * Math.cosh(z[1]);  // since JDK 1.5:
         //result[0] = Math.sin(z[0]) * ( Math.exp(z[1]) + Math.exp(-z[1]) ) / 2;
         result[1] = Math.cos(z[0]) * Math.sinh(z[1]); // since JDK 1.5
         //result[1] = Math.cos(z[0]) * ( Math.exp(z[1]) - Math.exp(-z[1]) ) / 2;
         result = ln(result);
      } else { // approximately cosh y = sinh y = e^|y| / 2:
         // ln |sin z| = ln |y| - ln 2 for z = x + iy
         result[0] = Math.abs(z[1]) - Math.log(2);
         // arg |sin z| = arctan( sgn y cot x ) for z = x + iy:
         if ( z[1] < 0 ) {
            result[1] = Math.atan( - 1 / Math.tan(z[0]) );
         } else {
            result[1] = Math.atan( 1 / Math.tan(z[0]) );
         }
      }
      return result;
   }

   /**
    *  subtracts <i>z</i> from this complex number. 
    *  We have <code>this</code>.subtract(<i>z</i>) = <code>this</code> - <i>z</i> 
    *  = Re (<code>this</code> - <i>y</i>) + Im (<code>this</code> - <i>z</i>).
    *  @param z a complex number
    *  @return the difference <code>this</code> - <i>z</i>
    *  @see #subtract(Complex)
    */
   public Complex minus(Complex z) {
      return new Complex(this.z[0] - z.z[0], this.z[1] - z.z[1]);
   }
   
   /** The product of a real number <i>x</i> and a complex number <i>z</i>.
    *  @param x a real number
    *  @param z a complex number in the array representation
    *  @return the product <i>xz</i>
    */
   public static double[] multiply( double x, double[] z ) {
      double[] w = new double[2];
      
      w[0] = x * z[0];
      w[1] = x * z[1];
      
      return w;
   }

   /** The product of a real number <i>x</i> with this complex number.
    *  @param x a real number
    *  @return the product <i>xz</i> where <i>z</i> is this complex number
    */
   public Complex multiply(double x) {
      return new Complex(x * z[0], x * z[1]);
   }

   /** The product of two complex numbers.
    *  For <i>x</i> = <i>x</i><sub>0</sub> + i<i>x</i><sub>1</sub>
    *  and <i>y</i> = <i>y</i><sub>0</sub> + i<i>y</i><sub>1</sub>, we have
    *  <p align="center">
    *    <i>xy</i> =
    *     <i>x</i><sub>0</sub><i>y</i><sub>0</sub> - <i>x</i><sub>1</sub><i>y</i><sub>1</sub>
    *     + i
    *     (<i>x</i><sub>1</sub><i>y</i><sub>0</sub> + <i>x</i><sub>0</sub><i>y</i><sub>1</sub>)
    *  </p>
    *  @param x the first factor in the array representation
    *  @param y the second factor in the array representation
    *  @return the product <i>xy</i>
    */
   public static Complex multiply( Complex x, Complex y ) {
      return new Complex(multiply(new double[]{x.z[0],x.z[1]},new double[]{y.z[0],y.z[1]}));
   }
   
   /** The product of two complex numbers.
    *  For <i>x</i> = <i>x</i><sub>0</sub> + i<i>x</i><sub>1</sub>
    *  and <i>y</i> = <i>y</i><sub>0</sub> + i<i>y</i><sub>1</sub>, we have
    *  <p align="center">
    *    <i>xy</i> =
    *     <i>x</i><sub>0</sub><i>y</i><sub>0</sub> - <i>x</i><sub>1</sub><i>y</i><sub>1</sub>
    *     + i
    *     (<i>x</i><sub>1</sub><i>y</i><sub>0</sub> + <i>x</i><sub>0</sub><i>y</i><sub>1</sub>)
    *  </p>
    *  @param x the first factor in the array representation
    *  @param y the second factor in the array representation
    *  @return the product <i>xy</i>
    */
   public static double[] multiply( double[] x, double[] y ) {
      double[] w = new double[2];
      
      w[0] = x[0] * y[0] - x[1] * y[1];
      w[1] = x[1] * y[0] + x[0] * y[1];
      
      return w;
   }

   /** Returns the product of this complex number and the complex number <i>z</i>.
    *  For <i>x</i> = <i>x</i><sub>0</sub> + i<i>x</i><sub>1</sub>
    *  and <i>y</i> = <i>y</i><sub>0</sub> + i<i>y</i><sub>1</sub>, we have
    *  <p align="center">
    *    <i>xy</i> =
    *     <i>x</i><sub>0</sub><i>y</i><sub>0</sub> - <i>x</i><sub>1</sub><i>y</i><sub>1</sub>
    *     + i
    *     (<i>x</i><sub>1</sub><i>y</i><sub>0</sub> + <i>x</i><sub>0</sub><i>y</i><sub>1</sub>)
    *  </p>
    *  @param z a complex number
    *  @return the product <code>this</code>&#x2219;<i>z</i>
    */
   public Complex multiply(Complex z) {
      return new Complex(multiply(new double[]{this.z[0],this.z[1]}, new double[]{z.z[0],z.z[1]}));
   }
   
   /**
    *  Returns the sum of this number and the complex number <i>z</i>. 
    *  For <i>x</i> = <i>x</i><sub>0</sub> + i<i>x</i><sub>1</sub>
    *  and <i>y</i> = <i>y</i><sub>0</sub> + i<i>y</i><sub>1</sub>, we have
    *  <p align="center">
    *    <i>x + y</i> =
    *     <i>x</i><sub>0</sub> + <i>y</i><sub>0</sub> + i
    *     (<i>x</i><sub>1</sub> + <i>y</i><sub>1</sub>)
    *  </p>
    *  @param z the addend
    *  @return the sum <code>this</code> + <i>z</i>
    *  @see #add(Complex)
    */
   public Complex plus(Complex z) {
      return new Complex(this.z[0] + z.z[0], this.z[1] + z.z[1]);
   }

   /** Returns <i>x<sup>s</sup></i> for a real number <i>x</i> and a complex number <i>s</i>.
    *  For <i>s</i> = <i>s</i><sub>0</sub> + i<i>s</i><sub>1</sub>, we have
    *  <p align="center">
    *    <i>x<sup>s</sup></i> 
    *    = <i>x</i><sup><i>s</i><sub>0</sub></sup> 
    *    [ cos( <i>s</i><sub>1</sub> ln <i>x</i> ) 
    *    + i sin( <i>s</i><sub>1</sub> ln <i>x</i> ) ].
    *  </p>
    *  <p>
    *    if <i>x</i> &gt; 0, and
    *  </p>
    *  <p align="center">
    *    <i>x<sup>s</sup></i> 
    *    = |<i>x</i>|<sup><i>s</i><sub>0</sub></sup> 
    *    [ cos( <i>s</i><sub>1</sub> ln |<i>x</i>| + <i>s</i><sub>0</sub>&#960;) 
    *    + i sin( <i>s</i><sub>1</sub> ln |<i>x</i>| + <i>s</i><sub>0</sub>&#960;) ].
    *  </p>
    *  @param x a real number as the base
    *  @param s a complex number as the exponent
    *  @return <i>z<sup>s</sup></i>
    *  @see #pow(Complex)
    *  @see #power(double,double[])
    */
   public static Complex power( double x, Complex s ) {
      return new Complex(power(x,new double[]{s.z[0],s.z[1]}));
   }

   /** Returns <i>x<sup>s</sup></i> for a real number <i>x</i> and a complex number <i>s</i>.
    *  For <i>s</i> = <i>s</i><sub>0</sub> + i<i>s</i><sub>1</sub>, we have
    *  <p align="center">
    *    <i>x<sup>s</sup></i> 
    *    = <i>x</i><sup><i>s</i><sub>0</sub></sup> 
    *    [ cos( <i>s</i><sub>1</sub> ln <i>x</i> ) 
    *    + i sin( <i>s</i><sub>1</sub> ln <i>x</i> ) ].
    *  </p>
    *  <p>
    *    if <i>x</i> &gt; 0, and
    *  </p>
    *  <p align="center">
    *    <i>x<sup>s</sup></i> 
    *    = |<i>x</i>|<sup><i>s</i><sub>0</sub></sup> 
    *    [ cos( <i>s</i><sub>1</sub> ln |<i>x</i>| + <i>s</i><sub>0</sub>&#960;) 
    *    + i sin( <i>s</i><sub>1</sub> ln |<i>x</i>| + <i>s</i><sub>0</sub>&#960;) ].
    *  </p>
    *  @param x a real number as the base
    *  @param s a complex number in the array representation as the exponent
    *  @return <i>z<sup>s</sup></i>
    *  @see #power(double,double[])
    *  @see #pow(Complex)
    */
   public static double[] power( double x, double[] s ) {
      double absX = Math.abs(x);
      double[] w = new double[2];
      
      if ( abs(s) < ACCURACY ) { // s=0?
         w = ONE_;
         return w;
      } else if ( absX < ACCURACY ) {  // x=0?
         return w; // w=0
      }

      w[0] = Math.pow( x, s[0] );
      w[1] = w[0];
      if ( x > 0 ) {
         w[0] *= Math.cos( s[1] * Math.log( absX ) );
         w[1] *= Math.sin( s[1] * Math.log( absX ) );
      } else {
         w[0] *= Math.cos( s[1] * Math.log( absX ) + s[0]*Math.PI );
         w[1] *= Math.sin( s[1] * Math.log( absX ) + s[0]*Math.PI );
      }
      
      return w;
   }

   /** 
    *  Returns <i>z<sup>s</sup></i> where <i>z</i> is this complex number, 
    *  and <i>s</i> is a complex number.
    *  For <i>z</i> = <i>r</i> e<sup>i<i>&#966;</i></sup> 
    *  (that is, <i>r</i> = |<i>z</i>| and <i>&#966;</i> = arg <i>z</i>),
    *  and <i>s</i> = <i>x</i> + i<i>y</i>, we have
    *  <p align="center">
    *    <i>z<sup>s</sup></i> 
    *    &nbsp; = &nbsp; 
    *    <i>r<sup>x</sup></i> e<sup>-<i>y&#966;</i></sup> 
    *    [ cos( <i>x&#966;</i> + <i>y</i> ln <i>r</i> ) 
    *    + i sin( <i>x&#966;</i> + <i>y</i> ln <i>r</i> ) ].
    *  </p>
    *  @param s the exponent
    *  @return <i>z<sup>s</sup></i> where <i>z</i> is this complex number
    *  @see #power(double[],double[])
    */
   public Complex pow( Complex s ) {
      double r = abs();

      if ( s.abs() < ACCURACY ) { // s=0?
         return ONE;
      } else if ( r < ACCURACY ) {  // z=0?
         return ZERO;
      }
            
      double phi = arg();
      double phase = s.z[0] * phi + s.z[1] * Math.log(r);
      double[] w = new double[2];
      w[0] = Math.pow( r, s.z[0] ) * Math.exp( - s.z[1] * phi );
      w[1] = w[0];
      
      w[0] *= Math.cos( phase );
      w[1] *= Math.sin( phase );
      
      return new Complex(w);
   }

   /** 
    *  Returns <i>z<sup>s</sup></i> for two complex numbers <i>z</i>, <i>s</i>.
    *  For <i>z</i> = <i>r</i> e<sup>i<i>&#966;</i></sup> 
    *  (that is, <i>r</i> = |<i>z</i>| and <i>&#966;</i> = arg <i>z</i>),
    *  and <i>s</i> = <i>x</i> + i<i>y</i>, we have
    *  <p align="center">
    *    <i>z<sup>s</sup></i> 
    *    &nbsp; = &nbsp; 
    *    <i>r<sup>x</sup></i> e<sup>-<i>y&#966;</i></sup> 
    *    [ cos( <i>x&#966;</i> + <i>y</i> ln <i>r</i> ) 
    *    + i sin( <i>x&#966;</i> + <i>y</i> ln <i>r</i> ) ].
    *  </p>
    *  @param z the base
    *  @param s the exponent
    *  @return <i>z<sup>s</sup></i>
    *  @see #power(double,double[])
    */
   public static double[] power( double[] z, double[] s ) {
      double r = abs(z);

      if ( abs(s) < ACCURACY ) { // s=0?
         return ONE_;
      } else if ( r < ACCURACY ) {  // z=0?
         return ZERO_; // w=0
      }
            
      double phi = arg(z);
      double phase = s[0] * phi + s[1] * Math.log(r);
      double[] w = new double[2];
      w[0] = Math.pow( r, s[0] ) * Math.exp( - s[1] * phi );
      w[1] = w[0];
      
      w[0] *= Math.cos( phase );
      w[1] *= Math.sin( phase );
      
      return w;
   }

   /** Returns the reciprocal of a complex number <i>y</i>. 
    *  This method is implemented avoiding overflows.
    *  @param y a complex number
    *  @return 1/<i>y</i>
    *  @see #divide(double, double[])
    *  @see #reciprocal()
    */
   public static double[] reciprocal( double[] y ) {
      double[] w = new double[2];
      double h;

      if ( Math.abs(y[0]) == 0 && Math.abs(y[1]) == 0 ) {
         w[0] = Double.POSITIVE_INFINITY;
         return w;
      }
      
      if ( Math.abs(y[0]) >= Math.abs(y[1]) ) {
         h = y[1]/y[0];
         w[0] = 1 / ( y[0] + y[1] * h );
         w[1] = - h / ( y[0] + y[1] * h );
      } else {
         h = y[0]/y[1];
         w[0] = h / ( y[0] * h + y[1] );
         w[1] = - 1 / ( y[0] * h + y[1] );
      }      
      return w;
   }

   /** Returns the reciprocal of this number. 
    *  This method is implemented avoiding overflows.
    *  @return 1/<code>this</code>
    *  @see #reciprocal(double[])
    */
   public Complex reciprocal() {
      double[] y = z;
      double[] w = new double[2];
      double h;

      if ( Math.abs(y[0]) == 0 && Math.abs(y[1]) == 0 ) {
         w[0] = Double.POSITIVE_INFINITY;
         return new Complex(w);
      }
      
      if ( Math.abs(y[0]) >= Math.abs(y[1]) ) {
         h = y[1]/y[0];
         w[0] = 1 / ( y[0] + y[1] * h );
         w[1] = - h / ( y[0] + y[1] * h );
      } else {
         h = y[0]/y[1];
         w[0] = h / ( y[0] * h + y[1] );
         w[1] = - 1 / ( y[0] * h + y[1] );
      }
      return new Complex(w);
   }

   /** Returns the sine of a complex number <i>z</i>.
    *  @param z a complex number in the array representation
    *  @return sin <i>z</i>
    */
   public static double[] sin( double[] z ) {
      return new double[]{
         Math.sin(z[0]) * Math.cosh(z[1]),
         Math.cos(z[0]) * Math.sinh(z[1])
      };
   }

   /** Returns the sine of a complex number <i>z</i>.
    *  @param z a complex number
    *  @return sin <i>z</i>
    */
   public static Complex sin(Complex z) {
      return new Complex(
         Math.sin(z.z[0]) * Math.cosh(z.z[1]),
         Math.cos(z.z[0]) * Math.sinh(z.z[1])
      );
   }
   
   /** Returns the sine of this complex number.
    *  @return sin <i>z</i>
    */
   public Complex sin() {
      return new Complex(
         Math.sin(z[0]) * Math.cosh(z[1]),
         Math.cos(z[0]) * Math.sinh(z[1])
      );
   }
   
   /** Returns the square root of a complex number <i>z</i>.
    *  @param z a complex number in the array representation
    *  @return the suare root of <i>z</i>
    */
   public static double[] sqrt( double[] z ) {
      double[] y = {0.,0.};
      double w = 0.;
      double h;

      if ( Math.abs(z[0]) != 0 || Math.abs(z[1]) != 0 ) {      
         if ( Math.abs(z[0]) >= Math.abs(y[1]) ) {
            h = z[1]/z[0];
            w = Math.sqrt( ( 1 + Math.sqrt( 1 + h*h ) ) / 2 );
            w = Math.sqrt(z[0]) * w;
         } else {
            h = Math.abs(z[0]/z[1]);
            w = Math.sqrt( ( h + Math.sqrt( 1 + h*h ) ) / 2 );
            w = Math.sqrt(z[1]) * w;
         }
         
         if ( z[0] >= 0.) {
            y[0] = w;
            y[1] = z[1] / (2*w);
         } else if ( z[0] < 0 && z[1] >= 0 ) {
            y[0] = Math.abs( z[1] ) / (2*w);
            y[1] = w;
         } else if ( z[0] < 0 && z[1] < 0 ) {
            y[0] = Math.abs( z[1] ) / (2*w);
            y[1] = -w;
         }
      }
      
      return y;
   }
   
   /** Returns the square root of a complex number <i>z</i>.
    *  @param z a complex number
    *  @return the suare root of <i>z</i>
    */
   public static Complex sqrt(Complex z) {
      return new Complex(sqrt(new double[]{z.z[0],z.z[1]}));
   }
   
   /**
    *  subtracts two complex numbers <i>x</i> and <i>y</i>. 
    *  We have subtract(<i>x</i>, <i>y</i>) = <i>x</i> - <i>y</i> 
    *  = Re (<i>x</i> - <i>y</i>) + Im (<i>x</i> - <i>y</i>).
    *  @param x a complex number in the array representation
    *  @param y a complex number in the array representation
    *  @return the difference <i>x</i> - <i>y</i>
    */
   public static double[] subtract( double[] x, double[] y ) {
      double[] result = new double[2];
      result[0] = x[0] - y[0];
      result[1] = x[1] - y[1];
      return result;
   }

   /**
    *  subtracts <i>z</i> from this complex number. 
    *  We have <code>this</code>.subtract(<i>z</i>) = <code>this</code> - <i>z</i> 
    *  = Re (<code>this</code> - <i>y</i>) + Im (<code>this</code> - <i>z</i>).
    *  @param z a complex number
    *  @return the difference <code>this</code> - <i>z</i>
    *  @see #minus(Complex)
    *  @see #subtract(double[],double[])
    */
   public Complex subtract(Complex z) {
      return new Complex(this.z[0] - z.z[0], this.z[1] - z.z[1]);
   }
   
   /** Returns a string representation of this complex number in a "readable" standard format. 
    *  @see #toString(double[],java.text.DecimalFormat)
    */
   public String toString() {
      return toString(new double[] {z[0],z[1]});
   }
   
   /** Returns a string representation of the complex number <i>z</i>
    *  in a "readable" standard format. 
    *  @see #toString(double[])
    */
   public static String toString(Complex z) {
      return toString(new double[] {z.z[0],z.z[1]}, new java.text.DecimalFormat("#,###.########"));
   }
   
   /** displays a complex number to a "readable" standard format. 
    *  @param z the complex number to be formatted
    *  @see #toString(double[],java.text.DecimalFormat)
    */
   public static String toString( double[] z ) {
      java.text.DecimalFormat digit = new java.text.DecimalFormat("#,###.########");
      return toString(z, digit);
   }
   
   /** Returns a string representation of this complex number in a "readable" standard format. 
    *  @param z the complex number to be formatted
    *  @param digit the decimal format in which <i>z</i> is to be displayed
    *  @see #toString(double[],java.text.DecimalFormat)
    */
   public static String toString(Complex z, java.text.DecimalFormat digit) {
      return toString(new double[] {z.z[0],z.z[1]}, digit);
   }
   
   /** displays a complex number to the "readable" format <code>digit</code>. 
    *  If the real or the imaginary part are too large or too small,
    *  scientific notation is used.
    *  @param z the complex number to be formatted
    *  @param digit the decimal format in which <i>z</i> is to be displayed
    *  @see #toString(double[])
    */
   public static String toString( double[] z, java.text.DecimalFormat digit ) {
      java.text.DecimalFormat scientific = new java.text.DecimalFormat("0.########E0");
      double upLimit = 1e9, lowLimit = 1e-9;
      boolean formatCondition = true;
      String output = "";
      
      if ( Double.toString(z[0]).equals("NaN") || Double.toString(z[1]).equals("NaN") ) {
         output += "NaN";
      } else if ( Math.abs(z[0]) < ACCURACY && Math.abs(z[1]) < ACCURACY ) { //z[0] == 0 && z[1] == 0
         output += "0";
      } else if ( Math.abs(z[0]) >= ACCURACY && Math.abs(z[1]) < ACCURACY ) {
         formatCondition = ( Math.abs(z[0]) < upLimit && Math.abs(z[0]) > lowLimit );
         output += formatCondition ? digit.format(z[0]) : scientific.format(z[0]);
      } else if ( Math.abs(z[0]) >= ACCURACY && z[1] > 0 ) {
         formatCondition = ( Math.abs(z[0]) < upLimit && Math.abs(z[0]) > lowLimit );
         output += formatCondition ? digit.format(z[0]) : scientific.format(z[0]);
         output += " + "; 
         if ( Math.abs(z[1] - 1) >= ACCURACY ) {
            formatCondition = ( Math.abs(z[1]) < upLimit && Math.abs(z[1]) > lowLimit );
            output += formatCondition ? digit.format(Math.abs(z[1])) : scientific.format(z[1]);
            output += " ";
         }
         output += "i";
      } else if ( Math.abs(z[0]) >= ACCURACY && z[1] < 0 ) {
         formatCondition = ( Math.abs(z[0]) < upLimit && Math.abs(z[0]) > lowLimit );
         output += formatCondition ? digit.format(z[0]) : scientific.format(z[0]);
         output += " - ";
         if ( Math.abs(z[1] + 1) >= ACCURACY ) { //z[1] != -1
            formatCondition = ( Math.abs(z[1]) < upLimit && Math.abs(z[1]) > lowLimit );
            output += formatCondition ? digit.format(Math.abs(z[1])) : scientific.format(Math.abs(z[1]));
            output += " ";
         }
         output += "i";
      } else { // case z[0] == 0 && z[1] != 0:
         if ( Math.abs(z[1] + 1) < ACCURACY ) {
            output += "- ";
         } else if ( Math.abs(z[1] - 1) >= ACCURACY ) {
            formatCondition = ( Math.abs(z[1]) < upLimit && Math.abs(z[1]) > lowLimit );
            output += formatCondition ? digit.format(z[1]) : scientific.format(z[1]);
            output += " ";
         }
         output += "i";
      }
      
      return output;
   }


   /** for test purposes ...*/
   /*
   public static void main ( String args[] ) {
      // Eingabefeld:
      javax.swing.JTextField feld1 = new javax.swing.JTextField("1");
      javax.swing.JTextField feld2 = new javax.swing.JTextField("1");
      javax.swing.JTextField feld3 = new javax.swing.JTextField("23");
      javax.swing.JTextField feld4 = new javax.swing.JTextField("-21");
    
      Object[] msg = {"Re z:", feld1, "Im z:", feld2, "Re w:", feld3, "Im w:", feld4};
      javax.swing.JOptionPane optionPane = new javax.swing.JOptionPane( msg );
    
      optionPane.createDialog(null,"Eingabe").setVisible(true);

      //double[] z = { Double.parseDouble( feld1.getText() ), Double.parseDouble( feld2.getText() ) }; 
      //double[] w = { Double.parseDouble( feld3.getText() ), Double.parseDouble( feld4.getText() ) };       
      //double[] result = {0.,.0};
      
      Complex z = new Complex(Double.parseDouble( feld1.getText() ), Double.parseDouble( feld2.getText() ) );
      Complex w = new Complex(Double.parseDouble( feld3.getText() ), Double.parseDouble( feld4.getText() ) );
      Complex result;
      java.text.DecimalFormat digit = new java.text.DecimalFormat( "#,###.##########" );
      
      String ausgabe = "";           // Ausgabestring
        
      long start = System.currentTimeMillis();
      
      result = multiply( z, w );
      
      long zeit = System.currentTimeMillis();

      ausgabe += "\n multiply(" + toString(z, digit);
      ausgabe += ", " + toString(w, digit);
      ausgabe += ") = ";
      ausgabe += toString( result, digit );
      ausgabe += "  (" + digit.format(zeit - start) + " ms)";
      
      start = System.currentTimeMillis();
      
      //double[] v = lnSin(z);
      Complex v = lnSin(z);
      ausgabe += "\n ln sin(" + toString(z, digit) + ") = " + toString(v,digit); //digit.format( abs(z) );
      //ausgabe += ", arg = " + ( z[1] % Math.PI ) + " \u03C0";
      ausgabe += ", arg = " + ( z.z[1] % Math.PI ) + " \u03C0";
      ausgabe += "\n exp(" + toString(v,digit) + ") = ";
      v = exp(v);
      ausgabe += toString(v,digit);
      

      //double[] 
      v = gamma(z);
      zeit = System.currentTimeMillis();
      ausgabe += "\n \u0393(" + toString(z, digit) + ") = " + toString(v,digit); //digit.format( abs(z) );
      ausgabe += "  (" + digit.format(zeit - start) + " ms)";
      //System.out.println("### v = (" + v[0] + ", " + v[1] + ")");
      
      v = lnGamma(z);
      zeit = System.currentTimeMillis();
      ausgabe += "\n ln \u0393(" + toString(z, digit) + ") = " + toString(v,digit); //digit.format( abs(z) );
      ausgabe += "  (" + digit.format(zeit - start) + " ms)";

      // Ausgabe auf dem Bildschirm:
      javax.swing.JOptionPane.showMessageDialog( null, ausgabe, "Ergebnis", javax.swing.JOptionPane.PLAIN_MESSAGE );
        
      System.exit( 0 );
   }
   // */
}