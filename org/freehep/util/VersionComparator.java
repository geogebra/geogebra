package org.freehep.util;

import java.util.Comparator;

/**
 *
 * @author Tony Johnson
 * @version $Id: VersionComparator.java,v 1.3 2008-05-04 12:22:38 murkle Exp $
 */
public class VersionComparator implements Comparator 
{
    private static String[] special = { "alpha" , "beta", "rc" };
    private static String pattern = "\\.+";
    /**
     * Compares two version numbers of the form 1.2.3.4
     * @return >0 if v1>v2, <0 if v1<v2 or 0 if v1=v2
     */
    public int versionNumberCompare(String v1, String v2) throws NumberFormatException 
    {
       String[] t1 = replaceSpecials(v1).split(pattern);
       String[] t2 = replaceSpecials(v2).split(pattern);
       int maxLength = Math.max(t1.length, t2.length);
       int i=0;
       for (; i<maxLength; i++) 
       {
          int i1 = i < t1.length ? Integer.parseInt(t1[i]) : 0;
          int i2 = i < t2.length ? Integer.parseInt(t2[i]) : 0;

          if (i1 == i2) continue;
          return i1-i2;
       }
       return 0;
    }
    private String replaceSpecials(String in)
    {
        for (int i=0; i<special.length; i++)
        {
           int j = -special.length + i;  
           in = in.replaceAll(special[i], "."+j+".");
        }
        return in;
    }
    
    public int compare(Object obj, Object obj1)
    {
       return versionNumberCompare(obj.toString(),obj1.toString());
    }
}