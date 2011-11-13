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

import org.mathpiper.lisp.printers.MathPiperPrinter;

import org.mathpiper.lisp.Utility;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.cons.ConsTraverser;
import org.mathpiper.lisp.cons.AtomCons;
import org.mathpiper.lisp.tokenizers.MathPiperTokenizer;
import org.mathpiper.io.MathPiperInputStream;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.cons.SublistCons;
import org.mathpiper.lisp.Operator;
import org.mathpiper.lisp.collections.OperatorMap;

public class MathPiperParser extends Parser
{

    public OperatorMap iPrefixOperators;
    public OperatorMap iInfixOperators;
    public OperatorMap iPostfixOperators;
    public OperatorMap iBodiedOperators;
    //private Environment iEnvironment;
    
    boolean iError;
    boolean iEndOfFile;
    String iLookAhead;
    public ConsPointer iSExpressionResult = new ConsPointer();

    public MathPiperParser(MathPiperTokenizer aTokenizer,
            MathPiperInputStream aInput,
            Environment aEnvironment,
            OperatorMap aPrefixOperators,
            OperatorMap aInfixOperators,
            OperatorMap aPostfixOperators,
            OperatorMap aBodiedOperators)
    {
        super(aTokenizer, aInput, aEnvironment);
        iPrefixOperators = aPrefixOperators;
        iInfixOperators = aInfixOperators;
        iPostfixOperators = aPostfixOperators;
        iBodiedOperators = aBodiedOperators;
        iEnvironment = aEnvironment;

        iError = false;
        iEndOfFile = false;
        iLookAhead = null;
    }

    @Override
    public void parse(int aStackTop, ConsPointer aResult) throws Exception
    {
        parse(aStackTop);
        aResult.setCons(iSExpressionResult.getCons());
    }

    public void parse(int aStackTop) throws Exception
    {
        readToken(aStackTop);
        if (iEndOfFile)
        {
            iSExpressionResult.setCons(iEnvironment.iEndOfFileAtom.copy( iEnvironment, true));
            return;
        }

        readExpression(iEnvironment,aStackTop, MathPiperPrinter.KMaxPrecedence);  // least precedence

        if (iLookAhead != iEnvironment.iEndStatementAtom.car())
        {
            fail(aStackTop);
        }
        if (iError)
        {
            while (iLookAhead.length() > 0 && iLookAhead != iEnvironment.iEndStatementAtom.car())
            {
                readToken(aStackTop);
            }
        }

        if (iError)
        {
            iSExpressionResult.setCons(null);
        }
        LispError.check(iEnvironment, aStackTop, !iError, LispError.INVALID_EXPRESSION, "INTERNAL");
    }

    void readToken(int aStackTop) throws Exception
    {
        // Get token.
        iLookAhead = iTokenizer.nextToken(iEnvironment, aStackTop, iInput,
                iEnvironment.getTokenHash());
        if (iLookAhead.length() == 0)
        {
            iEndOfFile = true;
        }
    }

    void matchToken(int aStackTop, String aToken) throws Exception
    {
        if (!aToken.equals(iLookAhead))
        {
            fail(aStackTop);
        }
        readToken(aStackTop);
    }

    void readExpression(Environment aEnvironment,int aStackTop, int depth) throws Exception
    {
        readAtom(aEnvironment, aStackTop);

        for (;;)
        {
            //Handle special case: a[b]. a is matched with lowest precedence!!
            if (iLookAhead == iEnvironment.iProgOpenAtom.car())
            {
                // Match opening bracket
                matchToken(aStackTop, iLookAhead);
                // Read "index" argument
                readExpression(aEnvironment, aStackTop, MathPiperPrinter.KMaxPrecedence);
                // Match closing bracket
                if (iLookAhead != iEnvironment.iProgCloseAtom.car())
                {
                    LispError.raiseError("Expecting a ] close bracket for program block, but got " + iLookAhead + " instead.", "[INTERNAL]", aStackTop, aEnvironment);
                    return;
                }
                matchToken(aStackTop, iLookAhead);
                // Build into Ntn(...)
                String theOperator = (String) iEnvironment.iNthAtom.car();
                insertAtom(aStackTop, theOperator);
                combine(aEnvironment,aStackTop, 2);
            } else
            {
                Operator op = (Operator) iInfixOperators.lookUp(iLookAhead);
                if (op == null)
                {
                    //printf("op [%s]\n",iLookAhead.String());
                    if(iLookAhead.equals(""))
                    {

                       LispError.raiseError("Expression must end with a semi-colon (;)", "[INTERNAL]", aStackTop, aEnvironment);
                        return;
                    }
                    if (MathPiperTokenizer.isSymbolic(iLookAhead.charAt(0)))
                    {
                        int origlen = iLookAhead.length();
                        int len = origlen;
                        //printf("IsSymbolic, len=%d\n",len);

                        while (len > 1)
                        {
                            len--;
                            String lookUp =
                                    (String) iEnvironment.getTokenHash().lookUp(iLookAhead.substring(0, len));

                            //printf("trunc %s\n",lookUp.String());
                            op = (Operator) iInfixOperators.lookUp(lookUp);
                            //if (op) printf("FOUND\n");
                            if (op != null)
                            {
                                String toLookUp = iLookAhead.substring(len, origlen);
                                String lookUpRight =
                                       (String) iEnvironment.getTokenHash().lookUp(toLookUp);

                                //printf("right: %s (%d)\n",lookUpRight.String(),origlen-len);

                                if (iPrefixOperators.lookUp(lookUpRight) != null)
                                {
                                    //printf("ACCEPT %s\n",lookUp.String());
                                    iLookAhead = lookUp;
                                    MathPiperInputStream input = iInput;
                                    int newPos = input.position() - (origlen - len);
                                    input.setPosition(newPos);
                                    //printf("Pushhback %s\n",&input.startPtr()[input.position()]);
                                    break;
                                } else
                                {
                                    op = null;
                                }
                            }
                        }
                        if (op == null)
                        {
                            return;
                        }
                    } else
                    {
                        return;
                    }




                //              return;
                }
                if (depth < op.iPrecedence)
                {
                    return;
                }
                int upper = op.iPrecedence;
                if (op.iRightAssociative == 0)
                {
                    upper--;
                }
                getOtherSide(aEnvironment,aStackTop, 2, upper);
            }
        }
    }

    void readAtom(Environment aEnvironment, int aStackTop) throws Exception
    {
        Operator op;
        // parse prefix operators
        op = (Operator) iPrefixOperators.lookUp(iLookAhead);
        if (op != null)
        {
            String theOperator = iLookAhead;
            matchToken(aStackTop, iLookAhead);
            {
                readExpression(aEnvironment,aStackTop, op.iPrecedence);
                insertAtom(aStackTop, theOperator);
                combine(aEnvironment,aStackTop, 1);
            }
        } // Else parse brackets
        else if (iLookAhead == iEnvironment.iBracketOpenAtom.car())
        {
            matchToken(aStackTop, iLookAhead);
            readExpression(aEnvironment,aStackTop, MathPiperPrinter.KMaxPrecedence);  // least precedence
            matchToken( aStackTop, (String) iEnvironment.iBracketCloseAtom.car());
        } //parse lists
        else if (iLookAhead == iEnvironment.iListOpenAtom.car())
        {
            int nrargs = 0;
            matchToken(aStackTop, iLookAhead);
            while (iLookAhead != iEnvironment.iListCloseAtom.car())
            {
                readExpression(aEnvironment,aStackTop, MathPiperPrinter.KMaxPrecedence);  // least precedence
                nrargs++;

                if (iLookAhead == iEnvironment.iCommaAtom.car())
                {
                    matchToken(aStackTop, iLookAhead);
                } else if (iLookAhead != iEnvironment.iListCloseAtom.car())
                {
                    LispError.raiseError("Expecting a } close bracket for a list, but got " + iLookAhead + " instead.", "[INTERNAL]", aStackTop, aEnvironment);
                    return;
                }
            }
            matchToken(aStackTop, iLookAhead);
            String theOperator = (String) iEnvironment.iListAtom.car();
            insertAtom(aStackTop, theOperator);
            combine(aEnvironment, aStackTop, nrargs);

        } // parse prog bodies
        else if (iLookAhead == iEnvironment.iProgOpenAtom.car())
        {
            int nrargs = 0;

            matchToken(aStackTop, iLookAhead);
            while (iLookAhead != iEnvironment.iProgCloseAtom.car())
            {
                readExpression(aEnvironment,aStackTop, MathPiperPrinter.KMaxPrecedence);  // least precedence
                nrargs++;

                if (iLookAhead == iEnvironment.iEndStatementAtom.car())
                {
                    matchToken(aStackTop, iLookAhead);
                } else
                {
                    LispError.raiseError("Expecting ; end of statement in program block, but got " + iLookAhead + " instead.", "[INTERNAL]", aStackTop, aEnvironment);
                    return;
                }
            }
            matchToken(aStackTop, iLookAhead);
            String theOperator = (String) iEnvironment.iProgAtom.car();
            insertAtom(aStackTop, theOperator);

            combine(aEnvironment, aStackTop, nrargs);
        } // Else we have an atom.
        else
        {
            String theOperator = iLookAhead;
            matchToken(aStackTop, iLookAhead);

            int nrargs = -1;
            if (iLookAhead == iEnvironment.iBracketOpenAtom.car())
            {
                nrargs = 0;
                matchToken(aStackTop, iLookAhead);
                while (iLookAhead != iEnvironment.iBracketCloseAtom.car())
                {
                    readExpression(aEnvironment,aStackTop, MathPiperPrinter.KMaxPrecedence);  // least precedence
                    nrargs++;

                    if (iLookAhead == iEnvironment.iCommaAtom.car())
                    {
                        matchToken(aStackTop, iLookAhead);
                    } else if (iLookAhead != iEnvironment.iBracketCloseAtom.car())
                    {
                        LispError.raiseError("Expecting ) closing bracket for sub-expression, but got " + iLookAhead + " instead.", "[INTERNAL]", aStackTop, aEnvironment);
                        return;
                    }
                }
                matchToken(aStackTop, iLookAhead);

                op = (Operator) iBodiedOperators.lookUp(theOperator);
                if (op != null)
                {
                    readExpression(aEnvironment,aStackTop, op.iPrecedence); // MathPiperPrinter.KMaxPrecedence
                    nrargs++;
                }
            }
            insertAtom(aStackTop, theOperator);
            if (nrargs >= 0)
            {
                combine(aEnvironment, aStackTop, nrargs);
            }
        }

        // parse postfix operators

        while ((op = (Operator) iPostfixOperators.lookUp(iLookAhead)) != null)
        {
            insertAtom(aStackTop, iLookAhead);
            matchToken(aStackTop, iLookAhead);
            combine(aEnvironment,aStackTop, 1);
        }
    }

    void getOtherSide(Environment aEnvironment, int aStackTop, int aNrArgsToCombine, int depth) throws Exception
    {
        String theOperator = iLookAhead;
        matchToken(aStackTop, iLookAhead);
        readExpression(aEnvironment, aStackTop,  depth);
        insertAtom(aStackTop, theOperator);
        combine(aEnvironment, aStackTop, aNrArgsToCombine);
    }

    void combine(Environment aEnvironment, int aStackTop, int aNrArgsToCombine) throws Exception
    {
        ConsPointer subList = new ConsPointer();
        subList.setCons(SublistCons.getInstance(aEnvironment,iSExpressionResult.getCons()));
        ConsTraverser consTraverser = new ConsTraverser(aEnvironment, iSExpressionResult);
        int i;
        for (i = 0; i < aNrArgsToCombine; i++)
        {
            if (consTraverser.getCons() == null)
            {
                fail(aStackTop);
                return;
            }
            consTraverser.goNext(aStackTop);
        }
        if (consTraverser.getCons() == null)
        {
            fail(aStackTop);
            return;
        }
        subList.cdr().setCons(consTraverser.cdr().getCons());
        consTraverser.cdr().setCons(null);

        Utility.reverseList(aEnvironment, ((ConsPointer) subList.car()).cdr(),
                ((ConsPointer) subList.car()).cdr());
        iSExpressionResult.setCons(subList.getCons());
    }

    void insertAtom(int aStackTop, String aString) throws Exception
    {
        ConsPointer ptr = new ConsPointer();
        ptr.setCons(AtomCons.getInstance(iEnvironment, aStackTop, aString));
        ptr.cdr().setCons(iSExpressionResult.getCons());
        iSExpressionResult.setCons(ptr.getCons());
    }

    void fail(int aStackTop) throws Exception // called when parsing fails, raising an exception
    {
        iError = true;
        if (iLookAhead != null)
        {
            LispError.raiseError("Error parsing expression, near token " + iLookAhead + ".", "[INTERNAL]", aStackTop, iEnvironment);
        }
        LispError.raiseError("Error parsing expression.", "[INTERNAL]", aStackTop, iEnvironment);
    }
};
