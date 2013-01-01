package org.mathpiper.mpreduce.datatypes;

//
// This file is part of the Jlisp implementation of Standard Lisp
// Copyright \u00a9 (C) Codemist Ltd, 1998-2000.
//

/**************************************************************************
 * Copyright (C) 1998-2011, Codemist Ltd.                A C Norman       *
 *                            also contributions from Vijay Chauhan, 2002 *
 *                                                                        *
 * Redistribution and use in source and binary forms, with or without     *
 * modification, are permitted provided that the following conditions are *
 * met:                                                                   *
 *                                                                        *
 *     * Redistributions of source code must retain the relevant          *
 *       copyright notice, this list of conditions and the following      *
 *       disclaimer.                                                      *
 *     * Redistributions in binary form must reproduce the above          *
 *       copyright notice, this list of conditions and the following      *
 *       disclaimer in the documentation and/or other materials provided  *
 *       with the distribution.                                           *
 *                                                                        *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS    *
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT      *
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS      *
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE         *
 * COPYRIGHT OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,   *
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,   *
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS  *
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND *
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR  *
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF     *
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH   *
 * DAMAGE.                                                                *
 *************************************************************************/


import java.io.IOException;
import java.io.ObjectInputStream;

import org.mathpiper.mpreduce.Environment;
import org.mathpiper.mpreduce.Jlisp;
import org.mathpiper.mpreduce.LispObject;
import org.mathpiper.mpreduce.LispReader;
import org.mathpiper.mpreduce.exceptions.ResourceException;

public class LispString extends LispObject
{

    public static int stringCount = 0;

    public String string;

    public LispString(String s)
    {
        this.string = s;
    }

    static StringBuffer sb = new StringBuffer();

    public void iprint() throws ResourceException
    {
        String s;
        if ((currentFlags & printEscape) != 0) s = escapedPrint(); 
        else s = string;
        if ((currentFlags & noLineBreak) == 0 &&
            currentOutput.column + s.length() > currentOutput.lineLength)
            currentOutput.println(); 
        currentOutput.print(s);
    }

    String escapedPrint()
    {
        sb.setLength(0);
        sb.append("\"");
        int n = string.indexOf('"');
        if (n == -1) sb.append(string);
        else
        {   int s = 0;
            while (n != -1)
            {   sb.append(string.substring(s, n+1));
                sb.append("\"");
                s = n+1;
                n = string.indexOf('"', s);
            }
            sb.append(string.substring(s, string.length()));
        }
        sb.append("\"");
        return sb.toString();
    }

    public void blankprint() throws ResourceException
    {
        String s;
        if ((currentFlags & printEscape) != 0) s = escapedPrint(); 
        else s = string;
        if ((currentFlags & noLineBreak) == 0 &&
            currentOutput.column + s.length() >= currentOutput.lineLength)
            currentOutput.println();
        else currentOutput.print(" ");
        currentOutput.print(s);
    }

    public boolean lispequals(Object b)
    {   if (!(b instanceof LispString)) return false;
        return string.equals(((LispString)b).string);
    }

    public boolean equals(Object b)
    {   if (!(b instanceof LispString)) return false;
        return string.equals(((LispString)b).string);
    }

    public int lisphashCode()
    {
        return string.hashCode();
    }
    
    public int hashCode()
    {
        return string.hashCode();
    }

    public void scan()
    {
        if (LispReader.objects.contains(string)) // seen before?
	{   if (!LispReader.repeatedObjects.containsKey(string))
	    {   LispReader.repeatedObjects.put(
	            string,
	            Environment.nil); // value is junk at this stage
	    }
	}
	else LispReader.objects.add(string);
    }
    
    public void dump() throws Exception
    {
        Object w = LispReader.repeatedObjects.get(string);
	if (w != null &&
	    w instanceof Integer) putSharedRef(w); // processed before
	else
	{   if (w != null) // will be used again sometime
	    {   LispReader.repeatedObjects.put(
	            string,
		    new Integer(LispReader.sharedIndex++));
		Jlisp.odump.write(X_STORE);
            }
// The next line turns the string into bytes using the platform's default
// encoding. I would LIKE to use a representation guaranteed to be available
// and to behave consistently everywhere... 
	    byte [] rep = string.getBytes("UTF8");
	    int length = rep.length;
	    putPrefix2(length, X_STRn, X_STR);
	    for (int i=0; i<length; i++)
	    {   Jlisp.odump.write(rep[i]);
            }
	}
    }

    private void readObject(ObjectInputStream stream)
                 throws ClassNotFoundException, IOException
    {
        stream.defaultReadObject();
        stringCount++;
    }


}


// end of LispString.java

