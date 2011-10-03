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
package org.mathpiper.lisp.cons;

import java.util.Map;
import org.mathpiper.lisp.Environment;


/**
 * Class Cons is the base object class that can be put in
 *  linked lists. It either has a pointer to a string
 * or it is a holder for a sublist, ,
 *  or it is a Java object.  All of these values are obtainable
 * using car();
 *  It is a reference-counted object. ConsPointer handles the reference counting. ap.
 */
public abstract class Cons //Note:tk:was MathPiperObject.
{

    protected Map metadataMap;


    public Cons() throws Exception
    {
        metadataMap = null; //aEnvironment.iEmptyAtom;
    }//end constructor.


    public abstract ConsPointer cdr();

    public abstract Object car() throws Exception;

    public abstract int type();



    /**
     * If this is a number, return a BigNumber representation of it.
     */
    public Object getNumber(int aPrecision, Environment aEnvironment) throws Exception {
        return null;
    }

    public abstract Cons copy( Environment aEnvironment, boolean aRecursed) throws Exception;





    /**
     *  Return a pointer to extra info. This allows for annotating
     *  an object. Returns NULL by default.
     */
    public Map getMetadataMap()
    {
        return metadataMap;
    }//end method.



    public void setMetadataMap(Map metaDataMap)
    {
        this.metadataMap = metaDataMap;
    }//end method.



    public boolean isEqual(Cons aOther) throws Exception {
        // iCdr line handles the fact that either one is a string
        if (car() != aOther.car()) {
            return false;
        }

        //So, no strings.
        ConsPointer iter1 = (ConsPointer) car();
        ConsPointer iter2 = (ConsPointer) aOther.car();
        if (!(iter1 != null && iter2 != null)) {
            return false;
        }

        // check all elements in sublist
        while (iter1.getCons() != null && iter2.getCons() != null) {
            if (!iter1.getCons().isEqual(iter2.getCons())) {
                return false;
            }

            iter1 = iter1.cdr();
            iter2 = iter2.cdr();
        }
        //One list longer than the other?
        if (iter1.getCons() == null && iter2.getCons() == null) {
            return true;
        }
        return false;
    }//end method.



    
}//end class.
