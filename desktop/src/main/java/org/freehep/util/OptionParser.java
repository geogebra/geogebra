package org.freehep.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

/**
 *
 * @author The FreeHEP team @ SLAC
 *
 */
public abstract class OptionParser
{
   public static Map parseOptions( String options )
   {
      Map hashValues = new HashMap();
      if (options != null)
      {
         StringTokenizer st = new StringTokenizer(options,",;");
         while ( st.hasMoreTokens() )
         {
            String tk = st.nextToken().toLowerCase(Locale.US).trim();
            int pos = tk.indexOf('=');
            if (pos < 0) hashValues.put(tk,"true");
            else hashValues.put(tk.substring(0,pos).trim(),tk.substring(pos+1).trim());
         }
      }
      return hashValues; 
   }
   
}
 
