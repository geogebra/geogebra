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
package org.mathpiper.builtin.functions.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import org.mathpiper.builtin.BuiltinFunction;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.Utility;
import org.mathpiper.lisp.cons.Cons;
import org.mathpiper.lisp.cons.SublistCons;

/**
 *
 *
 */
public class GlobalVariablesGet extends BuiltinFunction {

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception {

        java.util.Set<String> variablesSet = ((Map) aEnvironment.getGlobalState().getMap()).keySet();
        
        java.util.List variablesList = new ArrayList(variablesSet);

        Collections.sort(variablesList, new NameComparator() );

        Cons head = Utility.iterableToList(aEnvironment, aStackTop, variablesList);

        getTopOfStackPointer(aEnvironment, aStackTop).setCons(SublistCons.getInstance(aEnvironment, head));

    }//end method.



    private class NameComparator implements Comparator<String>{

        public int compare(String s1, String s2) {
            return s1.compareToIgnoreCase(s2);
        }//end method.
    }//end class.

}//end class.



/*
%mathpiper_docs,name="GlobalVariablesGet",categories="User Functions;Variables"
*CMD GlobalVariablesGet --- return a list which contains the names of all the global variables

*CALL
GlobalVariablesGet()


*DESC
Return a list which contains the names of all the global variables.

*E.G.
In> GlobalVariablesGet()
Result> {\$CacheOfConstantsN1,%,I,\$numericMode2}

%/mathpiper_docs
 */
