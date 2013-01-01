package org.mathpiper.mpreduce.io.streams;

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



import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import org.mathpiper.mpreduce.Jlisp;
import org.mathpiper.mpreduce.exceptions.ResourceException;

public class LispDigester extends LispStream
{

    public LispDigester() throws ResourceException
    {
        super("<md5 digester>");
        try
        {   md = MessageDigest.getInstance("MD5", "SUN");
        }
        catch (NoSuchAlgorithmException e)
        {
            Jlisp.errprintln("No MD5 available: " + e.getMessage());
            md = null;
        }
        catch (NoSuchProviderException e)
        {
            Jlisp.errprintln("No provider: " + e.getMessage());
            md = null;
        }
    }

    public void flush()
    {
    }

    public void close()
    {
        md = null;
    }

    public void print(String s)
    {
        if (md == null) return;
        char [] v = s.toCharArray();
// It *MAY* be better to use getChars here and move data into a pre-allocated
// array of characters.
        for (int i=0; i<v.length; i++)
        {   char c = v[i];
// characters are in general 16-bits wide (even though all the charcters that
// I will normally use in the UK are only 7 bits) so I pass them to the
// message digest process as two bytes each.
            md.update((byte)(c >> 8));
            md.update((byte)c);
        }
    }

    public void println(String s)
    {
        print(s);
        if (md != null)
        {   md.update((byte)0);
            md.update((byte)'\n');
        }
    }

}

// end of LispDigester.java


