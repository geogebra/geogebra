package org.mathpiper.mpreduce.io;

//
// This file is part of the Jlisp implementation of Standard Lisp
// Copyright \u00a9 (C) Codemist Ltd, 1998-2011.
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
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.mathpiper.mpreduce.Environment;
import org.mathpiper.mpreduce.Jlisp;
import org.mathpiper.mpreduce.LispObject;
import org.mathpiper.mpreduce.LispReader;
import org.mathpiper.mpreduce.Lit;
import org.mathpiper.mpreduce.Spid;
import org.mathpiper.mpreduce.datatypes.LispString;
import org.mathpiper.mpreduce.datatypes.LispVector;
import org.mathpiper.mpreduce.exceptions.LispException;
import org.mathpiper.mpreduce.functions.builtin.Fns;
import org.mathpiper.mpreduce.functions.functionwithenvironment.ByteOpt;
import org.mathpiper.mpreduce.functions.functionwithenvironment.Bytecode;
import org.mathpiper.mpreduce.functions.lisp.CallAs;
import org.mathpiper.mpreduce.functions.lisp.LispFunction;
import org.mathpiper.mpreduce.io.streams.LispStream;
import org.mathpiper.mpreduce.io.streams.WriterToLisp;
import org.mathpiper.mpreduce.numbers.LispInteger;
import org.mathpiper.mpreduce.packagedatastore.PDSInputStream;
import org.mathpiper.mpreduce.packagedatastore.PDSOutputStream;
import org.mathpiper.mpreduce.symbols.Symbol;

public class Fasl
{
    public static OutputStream writer = null;
    public static InputStream  reader = null;

    public static LispObject [] recent = null;
    public static int recentp, recentn;

    static String moduleName = "";

    public static LispObject startModule(LispObject arg1) throws LispException
    {
        if (arg1 == Environment.nil) // terminate file
        {   if (writer != null)
            {   try
                {   writer.write(0);
                    writer.write(0);
                    writer.write(0); // repeated object count for NULL!
                    writer.write(LispObject.X_NULL);
                    writer.close();
                }
                catch (IOException e)
                {   writer = null;
                    Jlisp.errprintln(
                        "+++ IO error on FASL file: " +
                        e.getMessage());
                    return Environment.nil;
                }
                writer = null;
                recent = null;
                Jlisp.println("+++ FASLEND " + moduleName);
                moduleName = "";
                return Jlisp.lispTrue;
            }
            else return arg1;
        }
        if (Jlisp.outputImagePos < 0 ||
            Jlisp.images[Jlisp.outputImagePos] == null)
            return Jlisp.error("no output image available");
        String name;
        if (arg1 instanceof Symbol)
        {   ((Symbol)arg1).completeName();
            name = ((Symbol)arg1).pname;
        }
        else if (arg1 instanceof LispString) name = ((LispString)arg1).string;
        else return Jlisp.error("start-module needs a symbol or string");
        name = name + ".fasl";
        moduleName = name;
        try
        {   writer =
                new GZIPOutputStream(
                    new BufferedOutputStream(
                        new PDSOutputStream(
                            Jlisp.images[Jlisp.outputImagePos],
                            name),
                        32768));
        }
        catch (IOException e)
        {   Jlisp.errprintln(
                "+++ Trouble with file \"" + name +
                "\": " + e.getMessage());
            return Environment.nil;
        }
        recent = new LispObject [512];
        recentp = recentn = 0;
        return Jlisp.lispTrue;
    }

    public static void defineInModule(int n) throws IOException
    {
// here I expect n to be in the range -1 to 0x3ffff
        n++;   // work with an offset number so that valid range includes "-1"
        writer.write(0); writer.write(0); writer.write(1); // sharedSize!
        writer.write(LispObject.X_DEFINMOD);
        int n1 = n >> 7;
        if (n1 == 0) writer.write(n);
        else
        {   writer.write(n | 0x80);
            n = n1;
            n1 = n >> 7;
            if (n1 == 0) writer.write(n);
            else
            {   writer.write(n | 0x80);
                writer.write(n >> 7);
            }
        }
    }

    public static void faslWrite(LispObject arg1) throws Exception
    {
        LispReader.dumpTree(arg1, writer);
    }
    
    static String name;

    public static boolean openModule(LispObject arg1) throws LispException
    {
        name = "unknown";
        if (arg1 instanceof Symbol)
        {   ((Symbol)arg1).completeName();
            name = ((Symbol)arg1).pname;
        }
        else if (arg1 instanceof LispString) name = ((LispString)arg1).string;
        else Jlisp.error("symbol or string needed as module name");
        name = name + ".fasl";
        try 
        {   PDSInputStream ff = null;
            for (int i=0; i<Jlisp.imageCount; i++)
            {   try
                {   ff = new PDSInputStream(Jlisp.images[i], name);
                }
                catch (IOException e)
                {
                }
                if (ff != null) break;
            }
            if (ff == null) throw new IOException("module not found");
            reader =
                new GZIPInputStream(
                    new BufferedInputStream(ff, 32768));
        }
        catch (IOException e)
        {   Jlisp.errprintln(
                "+++ Trouble with file \"" + name +
                "\": " + e.getMessage());
            return true;  // failed
        }
        return false;
    }

    public static LispObject loadModule(LispObject arg1) throws Exception
    {
        InputStream readerSave = reader;
        LispObject [] recentSave = recent;
        int recentpSave = recentp;
        reader = null;
        boolean saveHeadline = Jlisp.headline,
                saveBacktrace = Jlisp.backtrace;
        if (openModule(arg1)) return Environment.nil;
        try
        {   if ((Jlisp.verbosFlag & 2) != 0)
                Jlisp.println("Fasl-loading \"" + name + "\"");
            Jlisp.headline = Jlisp.backtrace = true;
            recent = new LispObject [512];
            recentp = recentn = 0;
            LispObject lastSaveDef = Environment.nil;
            try
            {
                for (;;)
                {   LispObject w = faslRead();
                    if (w == null) break;
                    else if (w instanceof Spid)
                    {   Spid sw = (Spid)w;
                        if (sw.tag != Spid.DEFINMOD)
                        {   Jlisp.errprintln(
                                "bad data in loaded file (wrong Spid)");
                            break;
                        }
                        else
                        {   if (sw.data == -1) lastSaveDef = faslRead();
                            else 
                            {   readByteDef(sw.data, lastSaveDef);
                                lastSaveDef = Environment.nil;
                            }
                        }
                    }
                    try
                    {   w.eval();
                    }
// Note that I catch and ignore STOP etc requests from fasl files
// as well as "normal" Lisp errors. Mildly dubious?
                    catch (Exception e)
                    {   Jlisp.errprintln(
                            "+++ Ignoring error in loaded file");
                        if (Jlisp.backtrace)
                        {   e.printStackTrace(new PrintWriter(
                              new WriterToLisp(
                                (LispStream)Jlisp.lit[Lit.err_output].car
                                    /*value*/)));
                        }
                    }
                }
            }
            catch (IOException e)
            {   Jlisp.errprintln(
                    "+++ IO error on fasl file: " + e.getMessage());
            }
            finally
            {   try
                {   if (reader != null) reader.close();
                }
                catch (IOException e)
                {   // Ignore IO errors on closing an input file.
                }
            }
        }
        finally
        {   reader = readerSave;
            recent = recentSave;
            recentp = recentpSave;
            Jlisp.headline = saveHeadline;
            Jlisp.backtrace = saveBacktrace;
        }
        return Environment.nil;
    } 

    static void readByteDef(int nargs, LispObject savedef) throws Exception
    {
        Symbol name = (Symbol)faslRead();
        LispObject bps1 = faslRead();
        if (bps1 instanceof LispInteger)
        {   LispObject linkTo = faslRead();
            int nargs1 = bps1.intValue();
            LispFunction f = new CallAs(nargs, linkTo, nargs1);
            name.fn = f;
            return;
        }
        LispObject env1 = faslRead();
        LispVector env = (LispVector)env1;
        Bytecode bps = (Bytecode)bps1;
        if (nargs >= 0x100)
        {   Bytecode bps2 = new ByteOpt(nargs);
            bps2.bytecodes = bps.bytecodes;
            bps = bps2;
        }
        bps.env = env.vec;
        bps.nargs = nargs;
        name.fn = bps;
        if (savedef != Environment.nil)
            Fns.put(name, Jlisp.lit[Lit.savedef], savedef);
    }


    static LispObject faslRead() throws Exception
    {
        Jlisp.idump = reader;
        LispReader.preRestore();
        Jlisp.descendSymbols = false;
        LispObject r = LispReader.readObject();
        LispReader.postRestore();
        return r;
    }

}

// end of Fasl.java

