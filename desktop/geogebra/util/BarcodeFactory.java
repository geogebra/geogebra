package geogebra.util;

import geogebra.main.Application;

import com.google.zxing.FormatException;

public class BarcodeFactory {

	
	  /**
	   * Replaces the last digit with the correct UPC/EAN checksum digit
	   * 
	   * adapted from com.google.zxing.oned.UPCEANReader
	   * Apache 2.0 licence
	   *
	   * @param s String of digits to check
	   * @return String with the last digit corrected if necessary
	   * @throws FormatException if the string does not contain only digits
	   */
	  public static String addStandardUPCEANChecksum(String s) throws FormatException {
	    int length = s.length();
	    if (length == 0) {
	    	throw FormatException.getFormatInstance();
	    }
	    
	    // replace last digit with '0' so the checksum works
	    s = s.substring(0, s.length() - 1) + '0';

	    int sum = 0;
	    for (int i = length - 2; i >= 0; i -= 2) {
	      int digit = (int) s.charAt(i) - (int) '0';
	      if (digit < 0 || digit > 9) {
	        throw FormatException.getFormatInstance();
	      }
	      sum += digit;
	    }
	    sum *= 3;
	    for (int i = length - 1; i >= 0; i -= 2) {
	      int digit = (int) s.charAt(i) - (int) '0';
	      if (digit < 0 || digit > 9) {
	        throw FormatException.getFormatInstance();
	      }
	      sum += digit;
	    }
	    
	    sum = sum % 10;
	    char checkDigit = sum == 0 ? (char)('0'+sum) : (char)('0'+(10 - sum));
		
	    // replace last digit with '0' so the checksum works
	    s = s.substring(0, s.length() - 1) + checkDigit;
	    
	    return s;
	    
	    
	  }

}
