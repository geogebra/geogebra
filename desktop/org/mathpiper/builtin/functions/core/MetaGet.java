/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mathpiper.builtin.functions.core;


import java.util.Map;
import org.mathpiper.builtin.BuiltinFunction;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.cons.AtomCons;
import org.mathpiper.lisp.cons.Cons;
import org.mathpiper.lisp.cons.ConsPointer;


public class MetaGet extends BuiltinFunction {

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception {

        ConsPointer objectPointer = new ConsPointer();
        objectPointer.setCons(getArgumentPointer(aEnvironment, aStackTop, 1).getCons());


        ConsPointer keyPointer = new ConsPointer();
        keyPointer.setCons(getArgumentPointer(aEnvironment, aStackTop, 2).getCons());
        LispError.checkIsString(aEnvironment, aStackTop, keyPointer, 2, "MetaGet");


        Map metadataMap = objectPointer.getCons().getMetadataMap();

        if (metadataMap == null) {
            getTopOfStackPointer(aEnvironment, aStackTop).setCons(AtomCons.getInstance(aEnvironment, aStackTop, "Empty"));

            return;
        }//end if.


        Cons valueCons = (Cons) metadataMap.get((String) keyPointer.getCons().car());


        if (valueCons == null) {
            getTopOfStackPointer(aEnvironment, aStackTop).setCons(AtomCons.getInstance(aEnvironment, aStackTop, "Empty"));
        } else {
            getTopOfStackPointer(aEnvironment, aStackTop).setCons(valueCons);
        }



    }//end method.


}//end class.



/*
%mathpiper_docs,name="MetaGet",categories="User Functions;Built In"
*CMD MetaGet --- returns the metadata for a value or an unbound variable
*CORE
*CALL
MetaGet(value_or_unbound_variable, key_string)

*PARMS

{value_or_unbound_variable} -- a value or an unbound variable

{key_string} -- a string which is the key for the given value


*DESC

Returns the metadata for a value or an unbound variables.  The metadata is
held in an associative list.



*SEE MetaSet, MetaKeys, MetaValues, Unbind
%/mathpiper_docs
 */
