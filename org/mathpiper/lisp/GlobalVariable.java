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
package org.mathpiper.lisp;

import org.mathpiper.lisp.cons.ConsPointer;

/**
 * Value of a Lisp global variable.
 * The only special feature of this class is the attribute
 * <b>iEvalBeforeReturn</b>, which defaults to <b>LispFalse</b>. If this
 * attribute is set to <b>LispTrue</b>, the value in <b>iValue</b> needs to be
 * evaluated to get the value of the Lisp variable.
 * See: LispEnvironment::GetVariable()
 */
public class GlobalVariable {

    ConsPointer iValue;
    boolean iEvalBeforeReturn;
    private Environment iEnvironment;


    public GlobalVariable(Environment aEnvironment, GlobalVariable aOther) {
        iEnvironment = aEnvironment;
        iValue = new ConsPointer();
        iValue = aOther.iValue;
        iEvalBeforeReturn = aOther.iEvalBeforeReturn;
    }


    public GlobalVariable(Environment aEnvironment, ConsPointer aValue) {
        iEnvironment = aEnvironment;
        iValue = new ConsPointer();
        iValue.setCons(aValue.getCons());
        iEvalBeforeReturn = false;
    }


    public void setEvalBeforeReturn(boolean aEval) {
        iEvalBeforeReturn = aEval;
    }


    @Override
    public String toString() {
        return (String) iValue.getCons().toString();
    }


    public boolean isIEvalBeforeReturn() {
        return iEvalBeforeReturn;
    }


    public ConsPointer getValue() {
        return iValue;
    }

}
