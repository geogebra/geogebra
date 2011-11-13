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

import org.mathpiper.lisp.Environment;

/** 
 * Similar to ConsPointer, but implements an array of pointers to CONS.
 *  
 */
public class ConsPointerArray {

    int iSize;
    ConsPointer iArray[];


    public ConsPointerArray(Environment aEnvironment, int aSize, Cons aInitialItem) {
        iArray = new ConsPointer[aSize];
        iSize = aSize;
        int i;
        for (i = 0; i < aSize; i++) {
            iArray[i] = new ConsPointer();
            iArray[i].setCons(aInitialItem);
        }
    }


    public int size() {
        return iSize;
    }


    public ConsPointer getElement(int aItem) {
        return iArray[aItem];
    }


    public ConsPointer[] getElements(int first, int last) throws IndexOutOfBoundsException {
        if (first < last && first > 0 && last > 0 && first < iSize - 1 && last < iSize - 1) {
            ConsPointer[] arguments = new ConsPointer[last - first];
            int i = 0;
            for (int x = first; x < last; x++) {
                arguments[i++] = iArray[x];
            }
            return arguments;
        } else {
            throw new IndexOutOfBoundsException("Stack index is out of bounds.");
        }
    }


    public void setElement(int aItem, Cons aCons) {
        iArray[aItem].setCons(aCons);
    }

}
