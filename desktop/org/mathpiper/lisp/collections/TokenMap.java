/* {{{ License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */ //}}}

// :indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:folding=explicit:collapseFolds=0:
package org.mathpiper.lisp.collections;

public class TokenMap extends MathPiperMap
{

   // java.util.Hashtable iMap = new java.util.Hashtable();


    /**
     * If the string is not in the table yet then insert it.
     * @param aString
     * @return the string.
     */
    public Object lookUp(String aString)
    {
        if (!iMap.containsKey(aString))
        {
            iMap.put(aString, aString);
        }

        return iMap.get(aString);
    }

   /**
     * If the string is not in the table yet then place double quotes
    * arount it and insert it.
     * @param aString
     * @return the string.
     */
    public String lookUpStringify(String aString)
    {
        aString = "\"" + aString + "\"";
        if (!iMap.containsKey(aString))
        {
            iMap.put(aString, aString);
        }
        
        return (String) iMap.get(aString);
        
    }

   /**
     * If the string is not in the table yet then remove its 
    * enclosing double quotes and insert it.
     * @param aString
     * @return the string.
     */
    public String lookUpUnStringify(String aString)
    {
        aString = aString.substring(1, aString.length() - 1);
        if (!iMap.containsKey(aString))
        {
            iMap.put(aString, aString);
        }

        return (String) iMap.get(aString);
        
    }

    // GarbageCollect
    public void garbageCollect()
    {
        //TODO FIXME
    }
}
