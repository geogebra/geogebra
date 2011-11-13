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

import java.util.Collections;

/** MathPiperMap allows you to associate arbitrary
 * information with a string in the above hash table. You can
 * specify what type of information to link to the string, and
 * this class then stores that information for a string. It is
 * in a sense a way to extend the string object without modifying
 * the string class itself. This class does not own the strings it
 * points to, but instead relies on the fact that the strings
 * are maintained in a hash table (like LispHashTable above).
 */
public class MathPiperMap
{
    //java.util.Hashtable iMap = new java.util.Hashtable();
    java.util.Map iMap = Collections.synchronizedMap(new java.util.HashMap());

    /**
     * Find the data associated to \a aString.
     * If \a aString is not stored in the hash table, this function
     * returns #NULL.
     * 
     * @param aString
     * @return the object which is associated with the key or null if there is
     * no object associated with the key.
     */
    public Object lookUp(String aString)
    {
        //if (iMap.containsKey(aString))
        //	return iMap.get(aString);
        //return null;
        return iMap.get(aString);
    }

    /**
     * Add an association to the hash table.
     *  If aString is already stored in the hash table, its
     * association is changed to aData. Otherwise, a new
     * association is added.
     * 
     * @param aData
     * @param aString
     */
    public void setAssociation(Object aData, String aString)
    {
        //if (iMap.containsKey(aString))
        //	iMap.remove(aString);

        iMap.put(aString, aData);
    }


    /**
     * Delete an association from the hash table.
     * 
     * @param aString
     */
    public void release(String aString)
    {
        //if (iMap.containsKey(aString))
        //iMap.remove(aString);
        iMap.remove(aString);
    }


    public java.util.Map getMap()
    {
        return iMap;
    }
}

