package org.mathpiper.mpreduce.functions.lisp;

//
// This file is part of the Jlisp implementation of Standard Lisp
// Copyright \u00a9 (C) Codemist Ltd, 1998-2011.
//

/**************************************************************************
 * Copyright (C) 1998-2011, Codemist Ltd.                A C Norman       *
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

import org.mathpiper.mpreduce.Environment;
import org.mathpiper.mpreduce.Jlisp;
import org.mathpiper.mpreduce.LispObject;
import org.mathpiper.mpreduce.LispReader;
import org.mathpiper.mpreduce.exceptions.ResourceException;
import org.mathpiper.mpreduce.io.Fasl;
import org.mathpiper.mpreduce.symbols.Symbol;

public class AutoLoad extends LispFunction
{

    public Symbol name;
    public LispObject data;
    
    public AutoLoad(Symbol name, LispObject data)
    {
        this.name = name;
	this.data = data;
    }
    
    public LispObject op0() throws Exception
    {
        name.completeName();
        name.fn = new Undefined(name.pname);
        Fasl.loadModule(data.car);
        return name.fn.op0();
    }

    public LispObject op1(LispObject a1) throws Exception
    {
        name.completeName();
        name.fn = new Undefined(name.pname);
        Fasl.loadModule(data.car);
        return name.fn.op1(a1);
    }

    public LispObject op2(LispObject a1, LispObject a2) throws Exception
    {
        name.completeName();
        name.fn = new Undefined(name.pname);
        Fasl.loadModule(data.car);
        return name.fn.op2(a1, a2);
    }

    public LispObject opn(LispObject [] args) throws Exception
    {
        name.completeName();
        name.fn = new Undefined(name.pname);
        Fasl.loadModule(data.car);
        return name.fn.opn(args);
    }

    public void print() throws ResourceException
    {
        name.completeName();
        Jlisp.print("#Autoload<" + name.pname + ">");
    }

    public void print(int n) throws ResourceException
    {
        name.completeName();
        Jlisp.print("#Autoload<" + name.pname + ">");
    }
    
    public void scan()
    {
        if (LispReader.objects.contains(this)) // seen before?
	{   if (!LispReader.repeatedObjects.containsKey(this))
	    {   LispReader.repeatedObjects.put(
	            this,
	            Environment.nil); // value is junk at this stage
	    }
	}
	else
	{   LispReader.objects.add(this);
	    LispReader.stack.push(name);
	    LispReader.stack.push(data);
	}
    }
    

    
}

// End of LispFunction.java


