package geogebra.html5.util;

import geogebra.common.util.ScientificFormatAdapter;

import java.text.DecimalFormat;




/**
 * This code formats numbers in Scientific Notation. The input Number object is returned
 * as a ScientificFormated string. There are two output styles: Pure and Standard scientific
 * notation. Pure formatted numbers have precisely the number of digits specified by the
 * significant digits (sigDig) parameter and always specify a Base 10 Exponential(E).
 * Standard formated numbers have the number of digits specified by the significant
 * digits (sigDig) parameter but will not have a Base 10 Exponential(E) if the number of digits
 * in the mantissa <= maxWidth.
 *
 * @author Paul Spence
 * @author Mark Donszelmann
 * @version $Id: ScientificFormat.java,v 1.4 2009-06-22 02:18:22 hohenwarter Exp $
 */

public class ScientificFormat extends Format implements ScientificFormatAdapter
{
   /**
    * The number of significant digits the number is formatted to is recorded by sigDigit.
    * The maximum width allowed for the returned String is recorded by MaxWidth
    */
   private int sigDigit = 5;
   private int maxWidth = 8;
   private boolean sciNote = false;
   private DecimalFormat decimalFormat;
   
   public ScientificFormat()
   {
      
   }
   
   /**
    * Sets the significant digits, maximum allowable width and number formatting style
    * (SciNote == true for Pure formatting).
    */
   public ScientificFormat(int sigDigit, int maxWidth, boolean SciNote)
   {
      setSigDigits(sigDigit);
      setMaxWidth(maxWidth);
      setScientificNotationStyle(SciNote);
   }
 
   /**
    * Returns the number of significant digits
    */
   public int getSigDigits()
   {
      return sigDigit;
   }
   
   /**
    * Returns the maximum allowable width of formatted number excluding any exponentials
    */
   public int getMaxWidth()
   {
      return maxWidth;
   }
   
   /**
    * Returns the formatting style: True means Pure scientific formatting, False means standard.
    */
   public boolean getScientificNotationStyle()
   {
      return sciNote;
   }
   
   /**
    * Sets the number of significant digits for the formatted number
    */
   public void setSigDigits(int SigDigit)
   {
      if (SigDigit < 1) throw new IllegalArgumentException("sigDigit");
      sigDigit = SigDigit;
      decimalFormat = null;
   }
   
   /**
    * Sets the maximum allowable length of the formattted number mantissa before exponential notation
    * is used.
    */
   public void setMaxWidth(int mWidth)
   {
      if (mWidth < 3) throw new IllegalArgumentException("maxWidth");
      maxWidth = mWidth;
   }
   /**
    * Sets the format style used.
    * There are two output styles: Pure and Standard scientific
    * notation. Pure formatted numbers have precisely the number of digits specified by the
    * significant digits (sigDig) parameter and always specify a Base 10 Exponential(E).
    * Standard formated numbers have the number of digits specified by the significant
    * digits (sigDig) parameter but will not have a Base 10 Exponential(E) if the number of digits
    * in the mantissa <= maxWidth.
    */
   public void setScientificNotationStyle(boolean sciNote)
   {
      this.sciNote = sciNote;
   }
   
   
   //simplify method for taking log base 10 of x
   private final static double k = 1/Math.log(10);
   private static double Log10(double x)
   {
      if (x==0) return 0;
      else return Math.log(x)*k;
   }
   
   private int resolveErrorSigDigit(double x, double dx)
   {
      //dx should never be negative
      dx = Math.abs(dx);
      //make x +ve cause negative doesn't effect sigdigits
      x=Math.abs(x);
      
      //these circumstances errorsigdit does equal sigdigit, excluding infinity and Nan which are handled by format
      if(dx == 0 || Double.isInfinite(dx) || Double.isNaN(dx) || dx >= x) return sigDigit;
      
      //fail cases for log, method fails to handle
      if(x==0||Double.isInfinite(x) || Double.isNaN(x))return sigDigit;
      
      //otherwise solve for cases when dx<x
      int log =(int)Math.round(Log10(dx/x));//always will return negative number
      int errorsigdigit = sigDigit+log;
      if(errorsigdigit <1) return 1;
      return errorsigdigit;
   }
   private static DecimalFormat getDecimalFormat(int sigDig)
   {
      StringBuffer buffer = new StringBuffer("0.");
      for (int i=1; i<sigDig; i++) buffer.append('0');
      buffer.append("E0");
      return new DecimalFormat(buffer.toString());
   }
   /**
    * Format the number using scientific notation
    */
   public String format(double d)
   {
      return format(d,sigDigit);
   }
   private String format(double d, int sigDig)
   {
      // Delegate the hard part to decimalFormat
      if (decimalFormat == null) decimalFormat = getDecimalFormat(sigDigit);
      DecimalFormat format = (sigDig == sigDigit) ? decimalFormat : getDecimalFormat(sigDig);
      
      String preliminaryResult = format.format(d);
      if (sciNote) return preliminaryResult;
      
      int ePos = preliminaryResult.indexOf('E');      
      int exponent = Integer.parseInt(preliminaryResult.substring(ePos+1))+1;
      if (exponent>maxWidth) return preliminaryResult;
      if (exponent<-maxWidth+sigDig+1) return preliminaryResult;
      
      // We need to fix up the result
      
      int sign = preliminaryResult.charAt(0)=='-' ? 1 : 0;
      StringBuffer result = new StringBuffer(preliminaryResult.substring(sign,sign+1)+preliminaryResult.substring(sign+2,ePos));
      
      if (exponent >= sigDig)
      {
         for (int i=sigDig; i<exponent; i++) result.append('0');
      }
      else if (exponent < 0)
      {
         result.insert(0,".");
         for (int i=exponent; i<0; i++) result.insert(1,'0');
      }
      else 
      {
         result.insert(exponent,'.');
      }
      if (sign > 0) result.insert(0,'-');
      return result.toString();
   }
//   /**
//    * Format a number plus error using scientific notation
//    */
//   public String formatError(double d,double dx)
//   {
//      return format(dx, resolveErrorSigDigit(d, dx));
//   }

}