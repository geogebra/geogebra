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
package org.mathpiper.lisp.parsers;

import org.mathpiper.lisp.cons.SublistCons;
import org.mathpiper.lisp.cons.AtomCons;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.tokenizers.MathPiperTokenizer;
import org.mathpiper.io.MathPiperInputStream;
import org.mathpiper.lisp.*;

public class Parser {

    public MathPiperTokenizer iTokenizer;
    public MathPiperInputStream iInput;
    public Environment iEnvironment;
    public boolean iListed;


    public Parser(MathPiperTokenizer aTokenizer, MathPiperInputStream aInput,
            Environment aEnvironment) {
        iTokenizer = aTokenizer;
        iInput = aInput;
        iEnvironment = aEnvironment;
        iListed = false;
    }


    public void parse(int aStackTop, ConsPointer aResult) throws Exception {
        aResult.setCons(null);

        String token;
        // Get token.
        token = iTokenizer.nextToken(iEnvironment, aStackTop, iInput, iEnvironment.getTokenHash());
        if (token.length() == 0) //TODO FIXME either token == null or token.length() == 0?
        {
            aResult.setCons(AtomCons.getInstance(iEnvironment, aStackTop, "EndOfFile"));
            return;
        }
        parseAtom(iEnvironment, aStackTop, aResult, token);
    }


    void parseList(Environment aEnvironment, int aStackTop, ConsPointer aResult) throws Exception {
        String token;

        ConsPointer iter = aResult;
        if (iListed) {
            aResult.setCons(AtomCons.getInstance(iEnvironment, aStackTop, "List"));
            iter = (aResult.cdr()); //TODO FIXME
        }
        for (;;) {
            //Get token.
            token = iTokenizer.nextToken(iEnvironment, aStackTop, iInput, iEnvironment.getTokenHash());
            // if token is empty string, error!
            LispError.check(iEnvironment, aStackTop, token.length() > 0, LispError.INVALID_TOKEN, "INTERNAL"); //TODO FIXME
            // if token is ")" return result.
            if (token == iEnvironment.getTokenHash().lookUp(")")) {
                return;
            }
            // else parse simple atom with parse, and append it to the
            // results list.

            parseAtom(aEnvironment, aStackTop, iter, token);
            iter = (iter.cdr()); //TODO FIXME
        }
    }


    void parseAtom(Environment aEnvironment, int aStackTop, ConsPointer aResult, String aToken) throws Exception {
        // if token is empty string, return null pointer (no expression)
        if (aToken.length() == 0) //TODO FIXME either token == null or token.length() == 0?
        {
            return;
        }
        // else if token is "(" read in a whole array of objects until ")",
        //   and make a sublist
        if (aToken == iEnvironment.getTokenHash().lookUp("(")) {
            ConsPointer subList = new ConsPointer();
            parseList(aEnvironment, aStackTop, subList);
            aResult.setCons(SublistCons.getInstance(aEnvironment, subList.getCons()));
            return;
        }
        // else make a simple atom, and return it.
        aResult.setCons(AtomCons.getInstance(iEnvironment, aStackTop, aToken));
    }

}
