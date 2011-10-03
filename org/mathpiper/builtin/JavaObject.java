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
package org.mathpiper.builtin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.Utility;
import org.mathpiper.lisp.cons.ConsPointer;

public class JavaObject extends BuiltinContainer {

    private Object javaObject;

    public JavaObject(Object javaObject) {
        this.javaObject = javaObject;
    }



    public String typeName() {
        return javaObject.getClass().getName();
    }//end method.


    public Object getObject() {
        return javaObject;
    }//end method.


    public static List lispListToJavaList(Environment aEnvironment, int aStackTop,ConsPointer lispList) throws Exception {
        LispError.check(aEnvironment, aStackTop, Utility.isList(lispList), LispError.NOT_A_LIST, "INTERNAL");
        
        lispList.goNext(aStackTop, aEnvironment);

        ArrayList javaList = new ArrayList();

        while (lispList.getCons() != null) {

            Object item = lispList.car();
            //item = narrow(item);
            javaList.add(item);

            lispList.goNext(aStackTop, aEnvironment);

        }//end while.

        return javaList;
    }//end method.


    public static double[] lispListToJavaDoubleArray(Environment aEnvironment, int aStackTop, ConsPointer lispListPointer) throws Exception {
        LispError.check(aEnvironment, aStackTop, Utility.isList(lispListPointer), LispError.NOT_A_LIST, "INTERNAL");

        lispListPointer.goNext(aStackTop, aEnvironment); //Remove List designator.

        double[] values = new double[Utility.listLength(aEnvironment, aStackTop, lispListPointer)];

        int index = 0;
        while (lispListPointer.getCons() != null) {

            Object item = lispListPointer.car();

            LispError.check(aEnvironment, aStackTop, item instanceof String, LispError.INVALID_ARGUMENT, "INTERNAL");
            String itemString = (String) item;

            try {
                values[index++] = Double.parseDouble(itemString);
            } catch (NumberFormatException nfe) {
                LispError.raiseError("Can not convert into a double." , "INTERNAL", aStackTop, aEnvironment);
            }//end try/catch.

            lispListPointer.goNext(aStackTop, aEnvironment);

        }//end while.

        return values;
        
    }//end method.

}//end class.

