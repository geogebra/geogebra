package org.mathpiper.mpreduce.io.streams;

/**************************************************************************
 * Copyright (C) 2011 Ted Kosan                                           *
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



public abstract class InputStream extends Object {


    public InputStream() {
    }


    public int available() throws IOException {
        return 0;
    }


    public void close() throws IOException {
    }


    public void mark(int readlimit) {
    }


    public boolean markSupported() {
        return false;
    }


    public abstract int read() throws IOException;

    public int read(byte b[]) throws IOException {
        return read(b, 0, b.length);
    }


    public int read(byte b[], int offset, int length) throws IOException {

        if (offset > b.length || offset < 0) {
            throw new ArrayIndexOutOfBoundsException("Illegal offset.");
        }
        if (length < 0 || length > b.length - offset) {
            throw new ArrayIndexOutOfBoundsException("Illegal length.");
        }
        for (int index = 0; index < length; index++) {
            int character;
            try {
                if ((character = read()) == -1) {
                    if(index == 0)
                    {
                        return -1;
                    }
                    else
                    {
                        return(index);
                    }
                }
            } catch (IOException e) {
                if (index != 0) {
                    return index;
                }
                throw e;
            }
            b[offset + index] = (byte) character;
        }
        return length;
    }


    public synchronized void reset() throws IOException {
        throw new IOException();
    }



}//end class.