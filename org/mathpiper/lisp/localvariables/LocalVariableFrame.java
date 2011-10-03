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
package org.mathpiper.lisp.localvariables;

public class LocalVariableFrame {

    public LocalVariableFrame iNext;
    public LocalVariable iFirst;
    LocalVariable iLast;
    private String functionName;


    public LocalVariableFrame(LocalVariableFrame aNext, LocalVariable aFirst, String functionName) {
        iNext = aNext;
        iFirst = aFirst;
        iLast = aFirst;
        this.functionName = functionName;
    }


    public void add(LocalVariable aNew) {
        aNew.iNext = iFirst;
        iFirst = aNew;
    }


    public void delete() {
        LocalVariable t = iFirst;
        LocalVariable next;
        while (t != iLast) {
            next = t.iNext;
            t = next;
        }
    }//end method.


    public String getFunctionName() {
        return functionName;
    }

}//end class

