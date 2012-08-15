package org.mathpiper.mpreduce.packagedatastore;

//

import java.io.IOException;

import org.mathpiper.mpreduce.io.streams.InputStream;

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



public class PDSInputStream extends InputStream
{

PDS pds;

long savedPosition;
int left;

public PDSInputStream(PDS pds, String member) throws IOException
{
    this.pds = pds;
    if (pds == null || pds.data == null)
        throw new IOException("PDS member " + member + " not found"); 
    Object on = pds.directory.get(member);
    if (on == null)
        throw new IOException("PDS member " + member + " not found");
    left = ((PDSEntry)on).len;
    savedPosition = pds.getFilePointer();
    pds.seek((long)((PDSEntry)on).loc);
}

public int available()
{
    return left;
}

public void close() throws IOException
{
    pds.seek(savedPosition);
}

public boolean markSupported()
{
    return false;
}

public int read() throws IOException
{
    if (left <= 0) return -1;
    else
    {   int c = pds.read();
        left--;
        return c;
    }    
}

public int read(byte [] b) throws IOException
{
    return read(b, 0, b.length);
}

public int read(byte [] b, int off, int len) throws IOException
{
    if (left <= 0) return -1;
    if (left < len) len = left;
    int n = pds.read(b, off, len);
    left -= n;
    return n;
}

}

// end of PDSInputStream.java

