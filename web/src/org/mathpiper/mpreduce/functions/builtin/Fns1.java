package org.mathpiper.mpreduce.functions.builtin;

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

// Fns1.java

// Each built-in function is created wrapped in a class
// that is derived from BuiltinFunction.




import java.io.PrintStream;
import java.math.BigInteger;
import java.util.Date;
import java.util.Iterator;

import org.mathpiper.mpreduce.Environment;
import org.mathpiper.mpreduce.Jlisp;
import org.mathpiper.mpreduce.LispEqualObject;
import org.mathpiper.mpreduce.LispObject;
import org.mathpiper.mpreduce.LispReader;
import org.mathpiper.mpreduce.Lit;
import org.mathpiper.mpreduce.datatypes.Cons;
import org.mathpiper.mpreduce.datatypes.LispHash;
import org.mathpiper.mpreduce.datatypes.LispString;
import org.mathpiper.mpreduce.datatypes.LispVector;
import org.mathpiper.mpreduce.exceptions.LispException;
import org.mathpiper.mpreduce.exceptions.ProgEvent;
import org.mathpiper.mpreduce.exceptions.ResourceException;
import org.mathpiper.mpreduce.functions.functionwithenvironment.Bytecode;
import org.mathpiper.mpreduce.functions.lisp.Interpreted;
import org.mathpiper.mpreduce.functions.lisp.LispFunction;
import org.mathpiper.mpreduce.functions.lisp.Macro;
import org.mathpiper.mpreduce.functions.lisp.Undefined;
import org.mathpiper.mpreduce.io.streams.LispCounter;
import org.mathpiper.mpreduce.io.streams.LispExploder;
import org.mathpiper.mpreduce.io.streams.LispStream;
import org.mathpiper.mpreduce.io.streams.ListReader;
import org.mathpiper.mpreduce.io.streams.WriterToLisp;
import org.mathpiper.mpreduce.numbers.LispFloat;
import org.mathpiper.mpreduce.numbers.LispInteger;
import org.mathpiper.mpreduce.numbers.LispNumber;
import org.mathpiper.mpreduce.numbers.LispSmallInteger;
import org.mathpiper.mpreduce.packagedatastore.PDS;
import org.mathpiper.mpreduce.special.Specfn;
import org.mathpiper.mpreduce.symbols.Gensym;
import org.mathpiper.mpreduce.symbols.Symbol;

import com.ibm.icu.text.SimpleDateFormat;

public class Fns1
{
    public Object [][] builtins =
    {
        //{"userjava",                    new UserJavaFn()},
        {"acons",                       new AconsFn()},
        {"append",                      new AppendFn()},
        {"apply",                       new ApplyFn()},
        {"apply0",                      new Apply0Fn()},
        {"apply1",                      new Apply1Fn()},
        {"apply2",                      new Apply2Fn()},
        {"apply3",                      new Apply3Fn()},
        {"assoc",                       new AssocFn()},
        {"assoc**",                     new AssocStarStarFn()},
        {"atom",                        new AtomFn()},
        {"atsoc",                       new AtsocFn()},
        {"batchp",                      new BatchpFn()},
        {"binary_close_input",          new Binary_close_inputFn()},
        {"binary_close_output",         new Binary_close_outputFn()},
        {"binary_open_input",           new Binary_open_inputFn()},
        {"binary_open_output",          new Binary_open_outputFn()},
        {"binary_prin1",                new Binary_prin1Fn()},
        {"binary_prin2",                new Binary_prin2Fn()},
        {"binary_prin3",                new Binary_prin3Fn()},
        {"binary_prinbyte",             new Binary_prinbyteFn()},
        {"binary_princ",                new Binary_princFn()},
        {"binary_prinfloat",            new Binary_prinfloatFn()},
        {"binary_read2",                new Binary_read2Fn()},
        {"binary_read3",                new Binary_read3Fn()},
        {"binary_read4",                new Binary_read4Fn()},
        {"binary_readbyte",             new Binary_readbyteFn()},
        {"binary_readfloat",            new Binary_readfloatFn()},
        {"binary_select_input",         new Binary_select_inputFn()},
        {"binary_terpri",               new Binary_terpriFn()},
        {"binopen",                     new BinopenFn()},
        {"boundp",                      new BoundpFn()},
        {"bps-getv",                    new Bps_getvFn()},
        {"bps-putv",                    new Bps_putvFn()},
        {"bps-upbv",                    new Bps_upbvFn()},
        {"bpsp",                        new BpspFn()},
        {"break-loop",                  new Break_loopFn()},
        {"byte-getv",                   new Byte_getvFn()},
        {"bytecounts",                  new BytecountsFn()},
        {"c_out",                       new C_outFn()},
        {"caaaar",                      new CaaaarFn()},
        {"caaadr",                      new CaaadrFn()},
        {"caaar",                       new CaaarFn()},
        {"caadar",                      new CaadarFn()},
        {"caaddr",                      new CaaddrFn()},
        {"caadr",                       new CaadrFn()},
        {"caar",                        new CaarFn()},
        {"cadaar",                      new CadaarFn()},
        {"cadadr",                      new CadadrFn()},
        {"cadar",                       new CadarFn()},
        {"caddar",                      new CaddarFn()},
        {"cadddr",                      new CadddrFn()},
        {"caddr",                       new CaddrFn()},
        {"cadr",                        new CadrFn()},
        {"car",                         new CarFn()},
        {"car*",                        new CarStarFn()},
        {"carcheck",                    new CarcheckFn()},
        {"catch",                       new CatchFn()},
        {"cbrt",                        new CbrtFn()},
        {"cdaaar",                      new CdaaarFn()},
        {"cdaadr",                      new CdaadrFn()},
        {"cdaar",                       new CdaarFn()},
        {"cdadar",                      new CdadarFn()},
        {"cdaddr",                      new CdaddrFn()},
        {"cdadr",                       new CdadrFn()},
        {"cdar",                        new CdarFn()},
        {"cddaar",                      new CddaarFn()},
        {"cddadr",                      new CddadrFn()},
        {"cddar",                       new CddarFn()},
        {"cdddar",                      new CdddarFn()},
        {"cddddr",                      new CddddrFn()},
        {"cdddr",                       new CdddrFn()},
        {"cddr",                        new CddrFn()},
        {"cdr",                         new CdrFn()},
        {"char-code",                   new Char_codeFn()},
        {"char-downcase",               new Char_downcaseFn()},
        {"char-upcase",                 new Char_upcaseFn()},
        {"chdir",                       new ChdirFn()},
        {"checkpoint",                  new CheckpointFn()},
        {"cl-equal",                    new Cl_equalFn()},
        {"close",                       new CloseFn()},
        {"close-library",               new Close_libraryFn()},
        {"clrhash",                     new ClrhashFn()},
        {"code-char",                   new Code_charFn()},
        {"codep",                       new CodepFn()},
        {"compress",                    new CompressFn()},
        {"cons",                        new ConsFn()},
        {"consp",                       new ConspFn()},
        {"constantp",                   new ConstantpFn()},
        {"contained",                   new ContainedFn()},
        {"convert-to-evector",          new Convert_to_evectorFn()},
        {"copy",                        new CopyFn()},
        {"copy-module",                 new Copy_moduleFn()},
        {"create-directory",            new Create_directoryFn()},
        {"date",                        new DateFn()},
        {"dated-name",                  new Dated_nameFn()},
        {"datelessp",                   new DatelesspFn()},
        {"datestamp",                   new DatestampFn()},
        {"timeofday",                   new TimeofdayFn()},
        {"define-in-module",            new Define_in_moduleFn()},
        {"deflist",                     new DeflistFn()},
        {"deleq",                       new DeleqFn()},
        {"delete",                      new DeleteFn()},
        {"delete-file",                 new Delete_fileFn()},
        {"library-members",             new Library_membersFn()},
        {"delete-module",               new Delete_moduleFn()},
        {"demo-mode",                   new Demo_modeFn()},
        {"digit",                       new DigitFn()},
        {"directoryp",                  new DirectorypFn()},
        {"dm",                          new DmFn()},
        {"do",                          new DoFn()},
        {"do*",                         new DoStarFn()},
        {"dolist",                      new DolistFn()},
        {"dotimes",                     new DotimesFn()},
        {"double-execute",              new Double_executeFn()},
        {"egetv",                       new EgetvFn()},
        {"eject",                       new EjectFn()},
        {"enable-backtrace",            new Enable_backtraceFn()},
        {"enable-errorset",             new Enable_errorsetFn()},
        {"endp",                        new EndpFn()},
        {"eputv",                       new EputvFn()},
        {"eq",                          new EqFn()},
        {"eqcar",                       new EqcarFn()},
        {"equalcar",                    new EqualcarFn()},
        {"eql",                         new EqlFn()},
        {"eqlhash",                     new EqlhashFn()},
        {"equal",                       new EqualFn()},
        {"iequal",                      new EqualFn()},
        {"equalp",                      new EqualpFn()},
        {"error",                       new ErrorFn()},
        {"error1",                      new Error1Fn()},
        {"errorset",                    new ErrorsetFn()},
        {"eupbv",                       new EupbvFn()},
        {"eval",                        new EvalFn()},
        {"eval-when",                   new Eval_whenFn()},
        {"evectorp",                    new EvectorpFn()},
        {"evlis",                       new EvlisFn()},
        {"expand",                      new ExpandFn()},
        {"explode",                     new ExplodeFn()},
        {"explodetostring",             new ExplodetostringFn()},
        {"explode2",                    new Explode2Fn()},
        {"explode2lc",                  new Explode2lcFn()},
        {"explode2lcn",                 new Explode2lcnFn()},
        {"explode2n",                   new Explode2nFn()},
        {"explode2uc",                  new Explode2ucFn()},
        {"explode2ucn",                 new Explode2ucnFn()},
        {"explodebinary",               new ExplodebinaryFn()},
        {"explodec",                    new ExplodecFn()},
        {"explodecn",                   new ExplodecnFn()},
        {"explodehex",                  new ExplodehexFn()},
        {"exploden",                    new ExplodenFn()},
        {"explodeoctal",                new ExplodeoctalFn()},
        {"fetch-url",                   new Fetch_urlFn()},
        {"fgetv32",                     new Fgetv32Fn()},
        {"fgetv64",                     new Fgetv64Fn()},
        {"file-readablep",              new File_readablepFn()},
        {"file-writeablep",             new File_writeablepFn()},
        {"filedate",                    new FiledateFn()},
        {"filep",                       new FilepFn()},
        {"flag",                        new FlagFn()},
        {"flagp",                       new FlagpFn()},
        {"flagp**",                     new FlagpStarStarFn()},
        {"flagpcar",                    new FlagpcarFn()},
        {"fluid",                       new FluidFn()},
        {"fluidp",                      new FluidpFn()},
        {"flush",                       new FlushFn()},
        {"format",                      new FormatFn()},
        {"fp-evaluate",                 new Fp_evaluateFn()},
        {"fputv32",                     new Fputv32Fn()},
        {"fputv64",                     new Fputv64Fn()},
        {"funcall",                     new FuncallFn()},
        {"funcall*",                    new FuncallFn()},
        {"gctime",                      new GctimeFn()},
        {"gensym",                      new GensymFn()},
        {"gensym1",                     new Gensym1Fn()},
        {"gensym2",                     new Gensym2Fn()},
        {"gensymp",                     new GensympFn()},
        {"get",                         new GetFn()},
        {"get*",                        new GetStarFn()},
        {"get-current-directory",       new Get_current_directoryFn()},
        {"get-lisp-directory",          new Get_lisp_directoryFn()},
        {"getd",                        new GetdFn()},
        {"getenv",                      new GetenvFn()},
        {"gethash",                     new GethashFn()},
        {"getv",                        new GetvFn()},
        {"getv16",                      new Getv16Fn()},
        {"getv32",                      new Getv32Fn()},
        {"getv8",                       new Getv8Fn()},
        {"global",                      new GlobalFn()},
        {"globalp",                     new GlobalpFn()},
        {"hash-table-p",                new Hash_table_pFn()},
        {"hashcontents",                new HashcontentsFn()},
        {"hashtagged-name",             new Hashtagged_nameFn()},
        {"help",                        new HelpFn()},
        {"idp",                         new IdpFn()},
        {"indirect",                    new IndirectFn()},
        {"inorm",                       new InormFn()},
        {"input-libraries",             new Input_librariesFn()},
        {"intern",                      new InternFn()},
        {"intersection",                new IntersectionFn()},
        {"is-console",                  new Is_consoleFn()},
        {"last",                        new LastFn()},
        {"lastcar",                     new LastcarFn()},
        {"lastpair",                    new LastpairFn()},
        {"length",                      new LengthFn()},
        {"lengthc",                     new LengthcFn()},
        {"library-name",                new Library_nameFn()},
        {"linelength",                  new LinelengthFn()},
        {"list",                        new ListFn()},
        {"list*",                       new ListStarFn()},
        {"list-directory",              new List_directoryFn()},
        {"list-modules",                new List_modulesFn()},
        {"list-to-string",              new List_to_stringFn()},
        {"list-to-symbol",              new List_to_symbolFn()},
        {"list-to-vector",              new List_to_vectorFn()},
        {"list2",                       new List2Fn()},
        {"list2*",                      new List2StarFn()},
        {"list3",                       new List3Fn()},
        {"resource-exceeded",           new ResourceExceededFn()},
        {"resource-limit",              new ResourceLimitFn()}

    };

/*
static Class c = null;
static Method m0 = null, m1 = null, m2 = null, mn = null;


class UserJavaFn extends BuiltinFunction
{
// To use this, prepare a new class
//
//   public class UserJava
//   {   public static LispObject op1(LispObject a)
//       {   return ... }
//   }
// with PUBLIC STATIC methods op0, op1, op2 and opn (not all need be
// provided). Compile it and put it where the system class loader can
// find it. Maybe merge it into the mai .jar file? Then
//    (userjava <arg>)
// will call those methods for you, or if the class was not provided it
// will just return a complaint!
//
    void ensureClassLoaded() throws Exception
    {
        if (c == null)
        {   ClassLoader l = ClassLoader.getSystemClassLoader();
            c = l.loadClass("UserJava");
            Class lo = Class.forName("LispObject");
            Class lov = (new LispObject [0]).getClass();
            m0 = m1 = m2 = mn = null;
            try
            {   m0 = c.getMethod("op0", 
                                new Class [] {});
            }
            catch (NoSuchMethodException nsm) {}
            try
            {   m1 = c.getMethod("op1", 
                                new Class [] {lo});
            }
            catch (NoSuchMethodException nsm) {}
            try
            {   m2 = c.getMethod("op2", 
                                new Class [] {lo, lo});
            }
            catch (NoSuchMethodException nsm) {}
            try
            {   mn = c.getMethod("opn", 
                                new Class [] {lov});
            }
            catch (NoSuchMethodException nsm) {}
        }
    }

    public LispObject op0() throws Exception
    {
        ensureClassLoaded();
        if (m0 == null) return Jlisp.error("no 0-arg method in UserJava");
        return (LispObject)m0.invoke(null);
    }

    public LispObject op1(LispObject a) throws Exception
    {
        ensureClassLoaded();
        if (m1 == null) return Jlisp.error("no 1-arg method in UserJava");
        return (LispObject)m1.invoke(this, a);
    }

    public LispObject op2(LispObject a, LispObject b) throws Exception
    {
        ensureClassLoaded();
        if (m2 == null) return Jlisp.error("no 2-arg method in UserJava");
        return (LispObject)m2.invoke(this, a, b);
    }

    public LispObject opn(LispObject [] a) throws Exception
    {
        ensureClassLoaded();
        if (mn == null) return Jlisp.error("no n-arg method in UserJava");
        return (LispObject)mn.invoke(this, (Object)a);
    }
}
*/

class AconsFn extends BuiltinFunction
{
    public LispObject opn(LispObject [] args) throws Exception
    {
        if (args.length != 3) 
            return error("acons called with " + args.length +
                         " args when 3 were expected");
        return new Cons(new Cons(args[0], args[1]), args[2]);
    }
}

class AppendFn extends BuiltinFunction
{
    public LispObject op0()
    { return Environment.nil; }
    public LispObject op1(LispObject arg1)
    { return arg1; }
    public LispObject op2(LispObject arg1, LispObject arg2) throws ResourceException
    {
        LispObject r = Environment.nil;
        while (!arg1.atom)
        {   LispObject a = arg1;
            r = new Cons(a.car, r);
            arg1 = a.cdr;
        }
        while (!r.atom)
        {   LispObject a = r;
            r = a.cdr;
            a.cdr = arg2;
            arg2 = a;
        }
        return arg2;
    }
    public LispObject opn(LispObject [] args) throws ResourceException
    {
        int n = args.length;
        LispObject r = args[--n];
        for (int i=n-1; i>=0; i--)
        {   r = op2(args[i], r);
        }
        return r;
    }
}

class ApplyFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return applySub(arg1, 0, Environment.nil);
    }
    public LispObject op2(LispObject arg1, LispObject arg2) throws Exception
    {
        return applySub(arg1, 0, arg2);
    }
    public LispObject opn(LispObject [] aa) throws Exception
    {
        int n = aa.length;
        for (int i=1; i<n-1; i++) Fns.args[i-1] = aa[i];
        return applySub(aa[0], n-2, aa[n-1]);
    }
    LispObject applySub(LispObject fn, int n, LispObject a) throws Exception
    {
        while (!a.atom)
        {   Fns.args[n++] = a.car;
            a = a.cdr;
        }
        if (!fn.atom) return Fns.applyInner(fn, n);
        LispFunction f;
        if (fn instanceof Symbol) f = ((Symbol)fn).fn;
        else if (fn instanceof LispFunction) f = (LispFunction)fn;
        else return Jlisp.error("not a function", fn);
        switch (n)
        {
    case 0: return f.op0();
    case 1: return f.op1(Fns.args[0]);
    case 2: return f.op2(Fns.args[0], Fns.args[1]);
    default:
            LispObject [] v = new LispObject [n];
            for (int i=0; i<n; i++) v[i] = Fns.args[i];
            return f.opn(v);
        }
    }
}

class Apply0Fn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1 instanceof Symbol)
        {   return ((Symbol)arg1).fn.op0();
        }
        else if (arg1 instanceof LispFunction)
        {   return ((LispFunction)arg1).op0();
        }
        else return Fns.apply0(arg1);
    }
}

class Apply1Fn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2) throws Exception
    {
        if (arg1 instanceof Symbol)
        {   return ((Symbol)arg1).fn.op1(arg2);
        }
        else if (arg1 instanceof LispFunction)
        {   return ((LispFunction)arg1).op1(arg2);
        }
        return Fns.apply1(arg1, arg2);
    }
}

class Apply2Fn extends BuiltinFunction
{
    public LispObject opn(LispObject [] args) throws Exception
    {
        if (args.length != 3) 
            return error("apply2 called with " + args.length +
                         " args when 3 were expected");
        LispObject arg1 = args[0];
        if (arg1 instanceof Symbol)
        {   return ((Symbol)arg1).fn.op2(args[1], args[2]);
        }
        else if (arg1 instanceof LispFunction)
        {   return ((LispFunction)arg1).op2(args[1], args[2]);
        }
        else return Fns.apply2(arg1, args[1], args[2]);
    }
}

class Apply3Fn extends BuiltinFunction
{
    public LispObject opn(LispObject [] args) throws Exception
    {
        if (args.length != 4) 
            return error("apply3 called with " + args.length +
                         " args when 4 were expected");
        LispObject arg1 = args[0];
        LispObject [] n = new LispObject [3];
        n[0] = args[1]; n[1] = args[2]; n[2] = args[3];
        if (arg1 instanceof Symbol)
        {   return ((Symbol)arg1).fn.opn(n);
        }
        else if (arg1 instanceof LispFunction)
        {   return ((LispFunction)arg1).opn(n);
        }
        else return Fns.apply3(arg1, args[1], args[2], args[3]);
    }
}

class AssocFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2)
    {
        while (!arg2.atom)
        {   LispObject q = arg2.car;
            arg2 = arg2.cdr;
            if (q.atom) continue;
            if (arg1.lispequals(q.car)) return q;
        }
        return Environment.nil;
    }
}

class AssocStarStarFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2)
    {
        while (!arg2.atom)
        {   LispObject q = arg2.car;
            arg2 = arg2.cdr;
            if (q.atom) continue;
            if (arg1.lispequals(q.car)) return q;
        }
        return Environment.nil;
    }
}

// like ML   "fun atom (a :: b) = false | atom x = true;"

class AtomFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1)
    {   return arg1.atom ? Jlisp.lispTrue :
                           Environment.nil;
    }
}

class AtsocFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2) throws Exception
    {
        while (!arg2.atom)
        {   LispObject p = arg2;
            arg2 = p.cdr;
            if (p.car.atom) continue;
            LispObject q = p.car;
            if (arg1 instanceof LispNumber &&            // @@@
                arg1.lispequals(q.car)) return p.car;    // @@@
            else if (arg1 == q.car) return p.car;
        }
        return Environment.nil;
    }
}

class BatchpFn extends BuiltinFunction
{
    public LispObject op0() throws Exception
    {
        if (Jlisp.interactivep) return Environment.nil;
        else return Jlisp.lispTrue;
    }
}

class Binary_close_inputFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Binary_close_outputFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Binary_open_inputFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Binary_open_outputFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Binary_prin1Fn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Binary_prin2Fn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Binary_prin3Fn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Binary_prinbyteFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Binary_princFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Binary_prinfloatFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Binary_read2Fn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Binary_read3Fn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Binary_read4Fn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Binary_readbyteFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Binary_readfloatFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Binary_select_inputFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Binary_terpriFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class BinopenFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class BoundpFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1 instanceof Symbol &&
            ((Symbol)arg1).car/*value*/ != Jlisp.lit[Lit.undefined])
            return Jlisp.lispTrue;
        else return Environment.nil;
    }
}

class Bps_getvFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2) throws Exception
    {
        int n = arg2.intValue();
        int b = ((Bytecode)arg1).bytecodes[n] & 0xff;
        return LispInteger.valueOf(b);
    }
}

class Bps_putvFn extends BuiltinFunction
{
    public LispObject opn(LispObject [] args) throws Exception
    {
        if (args.length != 3) 
            return error("bps-putv called with " + args.length +
                         " args when 3 were expected");
        int n = args[1].intValue();
        int b = args[2].intValue();
        ((Bytecode)args[0]).bytecodes[n] = (byte)b;
        return args[2];
    }
}

class Bps_upbvFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        int n = ((Bytecode)arg1).bytecodes.length;
        return LispInteger.valueOf(n-1);
    }
}

class BpspFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1)
    {
        if (arg1 instanceof Bytecode) return Jlisp.lispTrue;
        else return Environment.nil;
    }
}

class Break_loopFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Byte_getvFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2) throws Exception
    {
        String s = ((LispString)arg1).string;
        int n = arg2.intValue();
        return LispInteger.valueOf((int)s.charAt(n));
    }
}

class BytecountsFn extends BuiltinFunction
{
    public LispObject op0() throws Exception
    {
        return Environment.nil;
    }
}

class C_outFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

// like ML   "fun car (a :: b) = a;"

class CarFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1.atom) return error("Attempt to take car of an atom");
        else return arg1.car;
    }
}

// like ML   "fun cdr (a :: b) = b;"

class CdrFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1.atom) return error("Attempt to take cdr of an atom");
        else return arg1.cdr;
    }
}

class CaaaarFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        return arg1;
    }
}

class CaaadrFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        return arg1;
    }
}

class CaaarFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        return arg1;
    }
}

class CaadarFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        return arg1;
    }
}

class CaaddrFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        return arg1;
    }
}

class CaadrFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        return arg1;
    }
}

class CaarFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        return arg1;
    }
}

class CadaarFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        return arg1;
    }
}

class CadadrFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        return arg1;
    }
}

class CadarFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        return arg1;
    }
}

class CaddarFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        return arg1;
    }
}

class CadddrFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        return arg1;
    }
}

class CaddrFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        return arg1;
    }
}

class CadrFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        return arg1;
    }
}

class CarStarFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1)
    {
        if (!arg1.atom) return arg1.car;
        return Environment.nil;

    }
}

class CarcheckFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class CatchFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class CbrtFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        double a = ((LispFloat)arg1).value;
        if (a == 0.0) return arg1;
        else if (a > 0.0) return new LispFloat(Math.pow(a, 1.0/3.0));
        else return new LispFloat(-Math.pow(-a, 1.0/3.0));
    }
}

class CdaaarFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        return arg1;
    }
}

class CdaadrFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        return arg1;
    }
}

class CdaarFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        return arg1;
    }
}

class CdadarFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        return arg1;
    }
}

class CdaddrFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        return arg1;
    }
}

class CdadrFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        return arg1;
    }
}

class CdarFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        return arg1;
    }
}

class CddaarFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        return arg1;
    }
}

class CddadrFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        return arg1;
    }
}

class CddarFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        return arg1;
    }
}

class CdddarFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.car;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        return arg1;
    }
}

class CddddrFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        return arg1;
    }
}

class CdddrFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        return arg1;
    }
}

class CddrFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        if (arg1.atom) return error("Attempt to take car/cdr of an atom");
        arg1 = arg1.cdr;
        return arg1;
    }
}

class Char_codeFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Char_downcaseFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        char ch;
        if (arg1 instanceof Symbol)
        {   ((Symbol)arg1).completeName();
            ch = ((Symbol)arg1).pname.charAt(0);
        }
        else if (arg1 instanceof LispInteger)
            ch = (char)arg1.intValue();
        else if (arg1 instanceof LispString)
            ch = ((LispString)arg1).string.charAt(0);
        else return error("bad arg for char-downcase");
        byte [] bch = new byte [] { (byte)Character.toLowerCase(ch) };
        return Symbol.intern(new String(bch));
    }
}

class Char_upcaseFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        char ch;
        if (arg1 instanceof Symbol)
        {   ((Symbol)arg1).completeName();
            ch = ((Symbol)arg1).pname.charAt(0);
        }
        else if (arg1 instanceof LispInteger)
            ch = (char)arg1.intValue();
        else if (arg1 instanceof LispString)
            ch = ((LispString)arg1).string.charAt(0);
        else return error("bad arg for char-upcase");
        byte [] bch = new byte [] { (byte)Character.toUpperCase(ch) };
        return Symbol.intern(new String(bch));
    }
}

class ChdirFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class CheckpointFn extends BuiltinFunction
{
    public LispObject op0() throws Exception
    {
        return op1(Environment.nil);
    }
    
    public LispObject op1(LispObject arg1) throws Exception
    {
        return op2(arg1, Environment.nil);
    }
    
    public LispObject op2(LispObject arg1, LispObject arg2) throws Exception
    {
        throw new Exception("PRESERVE not supported.");
    }
}

class Cl_equalFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class CloseFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1 instanceof LispStream)
        {   ((LispStream)arg1).close();
        }
        return Environment.nil;
    }
}

class Close_libraryFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class ClrhashFn extends BuiltinFunction
{
    public LispObject op0() throws Exception
    {
        ((LispHash)Jlisp.lit[Lit.hashtab]).hash.clear();
        return Environment.nil;
    }
    
    public LispObject op1(LispObject ht) throws Exception
    {
        ((LispHash)ht).hash.clear();
        return Environment.nil;
    }
}

class Code_charFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class CodepFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1 instanceof BuiltinFunction) return Jlisp.lispTrue;
        else return Environment.nil;
    }
}

class CompressFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        LispObject save = Jlisp.lit[Lit.std_input].car/*value*/;
        LispStream from = new ListReader(arg1);
        LispObject r = Environment.nil;
        try
        {   Jlisp.lit[Lit.std_input].car/*value*/ = from;
            r = LispReader.getInstance().read();
//-             int c = from.readChar();
            from.close();
//- // The next section is a pretty shameless hack to make REDUCE a bit
//- // more robust. If when I parse the input to COMPRESS I find something
//- // left over, I will take that as an indication that what the user
//- // intended was to have a symbol made up of all the characters in the
//- // input data (except that "!" gets treated as an escape (which is no
//- // longer needed, but which must be ignored)
//-             if (c != -1)
//-             {   StringBuffer s = new StringBuffer();
//-                 boolean escaped = false;
//-                 while (!arg1.atom)
//-                 {   LispObject k = arg1.car;
//-                     arg1 = arg1.cdr;
//-                     char ch;
//-                     if (k instanceof LispString)
//-                         ch = ((LispString)k).string.charAt(0);
//-                     else if (k instanceof LispInteger)
//-                         ch = (char)k.intValue();
//-                     else if (k instanceof Symbol)
//-                         ch = ((Symbol)k).pname.charAt(0);
//-                     else break;
//-                     if (!escaped && ch == '!')
//-                     {   escaped = true;
//-                         continue;
//-                     }
//-                     escaped = false;
//-                     s.append(ch);
//-                 }
//-                 return Symbol.intern(s.toString());
//-             }
        }
        catch (Exception e)
        {   Jlisp.errprintln(
                "Error in compress: " + e.getMessage());

            LispStream ee = // @@@
                        (LispStream)Jlisp.lit[Lit.err_output].car/*value*/;
            new PrintStream(new WriterToLisp(ee)).print(e.getMessage());
            r = Environment.nil;
        }
        finally
        {   Jlisp.lit[Lit.std_input].car/*value*/ = save;
        }
        return r;
    }
}

// like ML   "fun cons a b = a :: b;"

class ConsFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2) throws ResourceException
    {   return new Cons(arg1, arg2);
    }
}

class ConspFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1)
    {   return arg1.atom ? Environment.nil :
               Jlisp.lispTrue;
    }
}

class ConstantpFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1 instanceof Symbol || !arg1.atom)
            return Environment.nil;
        else return Jlisp.lispTrue;
    }
}

class ContainedFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Convert_to_evectorFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class CopyFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return arg1.copy();
    }
}

class Copy_moduleFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        throw new Exception("Copy_module not supported.");
    }
}

class Create_directoryFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class DateFn extends BuiltinFunction
{
    public LispObject op0()
    {
        Date now = new Date();
        String s = now.toString();
        return new LispString(s);
    }
    public LispObject op1(LispObject a1) throws Exception
    {
    	return error(name + ".op1 not yet implemented");
    }

}

class Dated_nameFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class DatelesspFn extends BuiltinFunction
{
    public LispObject op2(LispObject a1, LispObject a2) throws Exception
    {
        String s1, s2;
        s1 = ((LispString)a1).string;
        s2 = ((LispString)a2).string;
        long d1=0, d2=0;
        try
        {
            d1 = Date.parse(s1);
            d2 = Date.parse(s2);
        }
        catch(Exception ex)
        {
            error("badly formatted date");
        }

 
        boolean res = d1 < d2;
        return res ? Jlisp.lispTrue : Environment.nil;
    }
}

class DatestampFn extends BuiltinFunction
{
    public LispObject op0()
    {
        Date now = new Date();
        return LispInteger.valueOf(now.getTime());
    }
}

class TimeofdayFn extends BuiltinFunction
{
    public LispObject op0() throws Exception
    {
        Date now = new Date();
        long ms = now.getTime();
        return new Cons(LispInteger.valueOf(ms/1000),
                        LispInteger.valueOf(1000*(ms%1000)));
    }
}

class Define_in_moduleFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        throw new Exception("Define_in_module not supported.");
    }
}

class DeflistFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class DeleqFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2) throws Exception
    {
        LispObject w = Environment.nil;
        while (!arg2.atom)
        {   LispObject a2 = arg2;
            arg2 = a2.cdr;
            if (arg1 instanceof LispNumber &&    // @@@
                arg1.lispequals(a2.car)) break;  // @@@
            else if (a2.car == arg1) break;
            w = new Cons(a2.car, w);
        }
        while (!w.atom)
        {   LispObject cw = w;
            w = cw.cdr;
            cw.cdr = arg2;
            arg2 = cw;
        }
        return arg2;
    }
}

class DeleteFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2) throws ResourceException
    {
        LispObject w = Environment.nil;
        while (!arg2.atom)
        {   LispObject a2 = arg2;
            arg2 = a2.cdr;
            if (arg1.lispequals(a2.car)) break;
            w = new Cons(a2.car, w);
        }
        while (!w.atom)
        {   LispObject cw = w;
            w = cw.cdr;
            cw.cdr = arg2;
            arg2 = cw;
        }
        return arg2;
    }
}

class Delete_fileFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        String s;
        if (arg1 instanceof Symbol)
        {   ((Symbol)arg1).completeName();
            s = ((Symbol)arg1).pname;
        }
        else if (arg1 instanceof LispString) s = ((LispString)arg1).string;
        else return Environment.nil;
        return LispStream.fileDelete(s);
    }
}

class Library_membersFn extends BuiltinFunction
{
    public LispObject op0() throws Exception
    {
        throw new Exception("Function not supported.");
    }
}

class Delete_moduleFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        Jlisp.println("++++ delete-module not coded yet"); // @@@
        return Environment.nil;
    }
}

class Demo_modeFn extends BuiltinFunction
{
    public LispObject op0() throws Exception
    {
        return Environment.nil;
    }
}

class DigitFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (!(arg1 instanceof Symbol)) return Environment.nil;
        Symbol s = (Symbol)arg1;
        s.completeName();
        char ch = s.pname.charAt(0);
        if (Character.isDigit(ch)) return Jlisp.lispTrue;
        else return Environment.nil;
    }
}

class DirectorypFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class DmFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class DoFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class DoStarFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class DolistFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class DotimesFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Double_executeFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class EgetvFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class EjectFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Enable_backtraceFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Enable_errorsetFn extends BuiltinFunction
{
	public LispObject op2(LispObject arg1, LispObject arg2) throws Exception
    {
// Not actually doing anything yet
        //System.out.printf("enable-errorset called%n");
        return new Cons(LispInteger.valueOf(1),
                        LispInteger.valueOf(1));
    }
}

class EndpFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {   if (arg1 == Environment.nil) return Jlisp.lispTrue;
        else if (!arg1.atom) return Environment.nil;
        else return error("ill-formed list detected by ENDP");
    }
}

class EputvFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

// (eq a b) is true if a and b are the same thing

class EqFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2)
    {
        if (arg1 instanceof LispNumber)                                // @@@
            return arg1.lispequals(arg2) ? Jlisp.lispTrue : Environment.nil; // @@@
        else return arg1==arg2 ? Jlisp.lispTrue : Environment.nil;
    }
}

class EqcarFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2)
    {
        if (arg1.atom) return Environment.nil;
        arg1 = arg1.car;
        if (arg1 instanceof LispNumber)                                // @@@
            return arg1.lispequals(arg2) ? Jlisp.lispTrue : Environment.nil; // @@@
        else return arg1==arg2 ? Jlisp.lispTrue : Environment.nil;
    }
}

class EqualcarFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2)
    {
        if (!arg1.atom &&
            (arg1.car == arg2 ||
             arg1.car.lispequals(arg2))) return Jlisp.lispTrue;
        else return Environment.nil;
    }
}

class EqlFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class EqlhashFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class EqualFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2)
    {
        if (arg1 == arg2) return Jlisp.lispTrue;
        return (arg1.lispequals(arg2) ? 
                Jlisp.lispTrue :
                Environment.nil);
    }
}

class EqualpFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class ErrorFn extends BuiltinFunction
{
    public LispObject op1(LispObject a) throws Exception
    {
        return op2(LispInteger.valueOf(0), a);
    }

    public LispObject op2(LispObject arg1, LispObject arg2) throws Exception
    {
        if (Jlisp.headline)
        {   Jlisp.errprintln();
            Jlisp.errprint("+++++ Error ");
            arg1.errPrint();
            Jlisp.errprint(" ");
            arg2.errPrint();
            Jlisp.errprintln();
        }
        if (!arg1.atom) arg1 = LispInteger.valueOf(0);
        Jlisp.errorCode = arg1;
        return error("Error function called");
    }
}

class Error1Fn extends BuiltinFunction
{
    public LispObject op0() throws Exception
    {
        if (!Jlisp.debugFlag) Jlisp.headline = Jlisp.backtrace = false;
        return error("Error1 function called");
    }
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (!Jlisp.debugFlag) Jlisp.headline = Jlisp.backtrace = false;
        return error("Error1 function called");
    }
}

class ErrorsetFn extends BuiltinFunction
{
    public LispObject opn(LispObject [] args) throws Exception
    {
        if (args.length != 3) 
            return error("errorset called with " + args.length + 
                         " arguments when 3 expected");
        LispObject form = args[0];
        boolean savehead = Jlisp.headline;
        boolean saveback = Jlisp.backtrace;
        try
        {   Jlisp.headline = (args[1] != Environment.nil);
            Jlisp.backtrace = (args[2] != Environment.nil);
// "-g" forces all errors to be noisy!
            if (Jlisp.debugFlag)
            {   Jlisp.headline = true;
                Jlisp.backtrace = true;
            }
            Jlisp.errorCode = Jlisp.lispTrue; // gets reset by user error function
            try
            {   form = form.eval();
                if (Specfn.progEvent != Specfn.NONE)
                {   Specfn.progEvent = Specfn.NONE;
                    error("GO or RETURN out of context");
                }
            }
            catch (Exception e) 
            {
            	if(Jlisp.trapExceptions == false)
            	{
            		//System.out.println(e.getMessage());
            		e.printStackTrace();
            	}
            	
                if (e instanceof ProgEvent)
                {   ProgEvent ep = (ProgEvent)e;
                    switch (ep.type)
                    {
                case ProgEvent.STOP:
                case ProgEvent.PRESERVE:
                case ProgEvent.PRESERVERESTART:
                case ProgEvent.RESTART:
                case ProgEvent.THROW:
                        throw e;
                default:
                        break;
                    }
                }
                boolean head = Jlisp.headline;
                boolean back = Jlisp.backtrace;
                if (head || back)
                    Jlisp.errprintln();
                if (e instanceof LispException)
                {   LispException e1 = (LispException)e;
                    if (head)
                    {   Jlisp.errprint("+++++ Error: " + e1.getMessage());
                        if (e1.details != null)
                        {   Jlisp.errprint(": ");
                            e1.details.errPrint();
                        }
                        Jlisp.errprintln();
                    }
                }
                else
                {   if (head || back)
                        Jlisp.errprintln();
                    if (head)
                    {   String m = e.getMessage();
                        if (m == null) m = e.toString();
                        Jlisp.errprintln("+++++ Error: " + m);
                    }
                }
                if (back)
                {   LispStream ee = 
                        (LispStream)Jlisp.lit[Lit.err_output].car/*value*/;
                    new PrintStream(new WriterToLisp(ee)).print(e.getMessage());
                }
                if (e instanceof ResourceException) throw e;
// I will return the atom that was the first argument in a user call to
//    (error a b)
// if such is available. Otherwise I return T.
                form = Jlisp.errorCode;
                Jlisp.errorCode = Jlisp.lispTrue;
                if (form == null | !form.atom) form = Jlisp.lispTrue;
                return form;
            }
        }
        finally
        {   Jlisp.headline = savehead;
            Jlisp.backtrace = saveback;
        }
        return new Cons(form, Environment.nil);
    }
}




class ResourceExceededFn extends BuiltinFunction
{
 public LispObject op0() throws LispException
 {   throw new ResourceException("User indicated resource limit");
 }
}


/*
 * (resource!-limit form time space io errors)
 *   Evaluate the given form and if it succeeds return a
 *   list whose first item is its value. If it fails in the ordinary manner
 *   then its failure (error/throw/restart etc) gets passed back through
 *   here in a transparent manner. But if it runs out of resources this
 *   function catches that fact and returns an atomic value.
 *   Resource limits are not precise, and are specified by the
 *   subsequent arguments here:
 *      time:  an integer giving a time allowance in seconds
 *      space: an integer giving a measure of memory that may be used,
 *             expressed in units of "megaconses". This is space
 *             allocated - the fact that memory gets recycled does not
 *             get it discounted.
 *      io:    an integer limiting the number of kilobytes of IO that may
 *             be performed.(not enforced yet in Jlisp)
 *      errors:an integer limiting the number of times traditional
 *             Lisp errors can occur. Note that if errorset is used
 *             you could have very many errors raised.
 *   In each case specifying a negative limit means that that limit does
 *   not apply. But at least one limit must be specified.
 *   If calls to resource!-limit are nested the inner ones can only
 *   reduce the resources available to their form.
 *
 *   On success set *resource* limit to a list showing the resources used.
 *
 * For now this ignores the limits!
 */

class ResourceLimitFn extends BuiltinFunction
{
    public LispObject op1(LispObject a1) throws Exception
    {
        return opn(new LispObject [] {a1});
    }
    public LispObject op2(LispObject a1, LispObject a2) throws Exception
    {
        return opn(new LispObject [] {a1, a2});
    }
    public LispObject opn(LispObject[] args) throws Exception {
        boolean ok = true;
        if (args.length > 5 || args.length < 1) {
            return error("resource-limit called with " + args.length
                    + " arguments when 1 to 5 expected");
        }
        int save_time_base = ResourceException.time_base,
                save_space_base = ResourceException.space_base,
                save_io_base = ResourceException.io_base,
                save_errors_base = ResourceException.errors_base;
        int save_time_limit = ResourceException.time_limit,
                save_space_limit = ResourceException.space_limit,
                save_io_limit = ResourceException.io_limit,
                save_errors_limit = ResourceException.errors_limit;
        LispObject form = args[0];
        LispObject time   = args[1];
        LispObject space  = args[2];
        LispObject io     = args.length > 3 ? args[3] : Environment.nil;
        LispObject errors = args.length > 4 ? args[4] : Environment.nil;
        int itime   = time instanceof LispInteger ? time.intValue() : -1;
        int ispace  = space instanceof LispInteger ? space.intValue() : -1;
        int iio     = io instanceof LispInteger ? io.intValue() : -1;
        int ierrors = errors instanceof LispInteger ? errors.intValue() : -1;
        LispObject r = Environment.nil;
        ResourceException.time_base = ResourceException.time_now;
        ResourceException.space_base = ResourceException.space_now;
        ResourceException.io_base = ResourceException.io_now;
        ResourceException.errors_base = ResourceException.errors_now;
        // If there is a limit already being imposed then this one can not extend
        // it, only shrink it.
        if (itime > 0) {
            int w = ResourceException.time_base + itime;
            if (ResourceException.time_limit > 0
                    && ResourceException.time_limit < w) {
                w = ResourceException.time_limit;
            }
            ResourceException.time_limit = w;
        }
        if (ispace > 0) {
            int w = ResourceException.space_base + (ispace < 4 ? 4 : ispace);
            if (ResourceException.space_limit > 0
                    && ResourceException.space_limit < w) {
                w = ResourceException.space_limit;
            }
            ResourceException.space_limit = w;
        }
        if (iio > 0) {
            int w = ResourceException.io_base + (iio < 2 ? 2 : iio);
            if (ResourceException.io_limit > 0
                    && ResourceException.io_limit < w) {
                w = ResourceException.io_limit;
            }
            ResourceException.io_limit = w;
        }
        if (ierrors > 0) {
            int w = ResourceException.errors_base + ierrors;
            if (ResourceException.errors_limit > 0
                    && ResourceException.errors_limit < w) {
                w = ResourceException.errors_limit;
            }
            ResourceException.errors_limit = w;
        }
        try {
            try
            {
            r = form.eval();
        } catch (ResourceException e) {
            ok = false;
        }
         itime =   ResourceException.time_now   - ResourceException.time_base;
         ispace =  ResourceException.space_now  - ResourceException.space_base;
         iio =     ResourceException.io_now     - ResourceException.io_base;
         ierrors = ResourceException.errors_now - ResourceException.errors_base;
        ((Symbol)(Jlisp.lit[Lit.resources])).car/*value*/ =
            new Cons(new LispSmallInteger(itime),
                new Cons(new LispSmallInteger(ispace),
                    new Cons(new LispSmallInteger(iio),
                        new Cons(new LispSmallInteger(ierrors), Environment.nil))));
        }finally{
 	  	         ResourceException.time_base    = save_time_base;
 	  	         ResourceException.space_base   = save_space_base;
 	  	         ResourceException.io_base      = save_io_base;
 	  	         ResourceException.errors_base  = save_errors_base;
 	  	         ResourceException.time_limit   = save_time_limit;
 	  	         ResourceException.space_limit  = save_space_limit;
 	  	         ResourceException.io_limit     = save_io_limit;
                         ResourceException.errors_limit = save_errors_limit;
        }
        if (ok) return new Cons(r, Environment.nil);
        else return Environment.nil;
    }
}

class EupbvFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class EvalFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        arg1 = arg1.eval();
        if (Specfn.progEvent != Specfn.NONE)
        {   Specfn.progEvent = Specfn.NONE;
            return error("GO or RETURN out of context");
        }
        return arg1;
    }
}

class Eval_whenFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class EvectorpFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class EvlisFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        LispObject r = Environment.nil;
        while (!arg1.atom)
        {   LispObject a1 = arg1;
            r = new Cons(a1.car.eval(), r);
            if (Specfn.progEvent != Specfn.NONE) return Environment.nil;
            arg1 = a1.cdr;
        }
        arg1 = Environment.nil;
        while (!r.atom)
        {   LispObject a1 = r;
            r = a1.cdr;
            a1.cdr = arg1;
            arg1 = a1;
        }
        return arg1;
    }
}

class ExpandFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class ExplodeFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        LispStream f = new LispExploder(true);
        LispObject save = Jlisp.lit[Lit.std_output].car/*value*/;
        try
        {   Jlisp.lit[Lit.std_output].car/*value*/ = f;
            arg1.print(LispObject.noLineBreak+LispObject.printEscape);
        }
        finally
        {   Jlisp.lit[Lit.std_output].car/*value*/ = save;
        }
        return Fns.reversip(f.exploded);
    }
}

class ExplodetostringFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return new LispString(Fns.explodeToString(arg1));
    }
}

class Explode2Fn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        LispStream f = new LispExploder(true);
        LispObject save = Jlisp.lit[Lit.std_output].car/*value*/;
        try
        {   Jlisp.lit[Lit.std_output].car/*value*/ = f;
            arg1.print(LispObject.noLineBreak);
        }
        finally
        {   Jlisp.lit[Lit.std_output].car/*value*/ = save;
        }
        return Fns.reversip(f.exploded);
    }
}

class Explode2lcFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        LispStream f = new LispExploder(true);
        LispObject save = Jlisp.lit[Lit.std_output].car/*value*/;
        try
        {   Jlisp.lit[Lit.std_output].car/*value*/ = f;
            arg1.print(LispObject.noLineBreak+LispObject.printLower);
        }
        finally
        {   Jlisp.lit[Lit.std_output].car/*value*/ = save;
        }
        return Fns.reversip(f.exploded);
    }
}

class Explode2lcnFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        LispStream f = new LispExploder(false);
        LispObject save = Jlisp.lit[Lit.std_output].car/*value*/;
        try
        {   Jlisp.lit[Lit.std_output].car/*value*/ = f;
            arg1.print(LispObject.noLineBreak+LispObject.printLower);
        }
        finally
        {   Jlisp.lit[Lit.std_output].car/*value*/ = save;
        }
        return Fns.reversip(f.exploded);
    }
}

class Explode2nFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        LispStream f = new LispExploder(false);
        LispObject save = Jlisp.lit[Lit.std_output].car/*value*/;
        try
        {   Jlisp.lit[Lit.std_output].car/*value*/ = f;
            arg1.print(LispObject.noLineBreak);
        }
        finally
        {   Jlisp.lit[Lit.std_output].car/*value*/ = save;
        }
        return Fns.reversip(f.exploded);
    }
}

class Explode2ucFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        LispStream f = new LispExploder(true);
        LispObject save = Jlisp.lit[Lit.std_output].car/*value*/;
        try
        {   Jlisp.lit[Lit.std_output].car/*value*/ = f;
            arg1.print(LispObject.noLineBreak+LispObject.printUpper);
        }
        finally
        {   Jlisp.lit[Lit.std_output].car/*value*/ = save;
        }
        return Fns.reversip(f.exploded);
    }
}

class Explode2ucnFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        LispStream f = new LispExploder(false);
        LispObject save = Jlisp.lit[Lit.std_output].car/*value*/;
        try
        {   Jlisp.lit[Lit.std_output].car/*value*/ = f;
            arg1.print(LispObject.noLineBreak+LispObject.printUpper);
        }
        finally
        {   Jlisp.lit[Lit.std_output].car/*value*/ = save;
        }
        return Fns.reversip(f.exploded);
    }
}

class ExplodebinaryFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        LispStream f = new LispExploder(true);
        LispObject save = Jlisp.lit[Lit.std_output].car/*value*/;
        try
        {   Jlisp.lit[Lit.std_output].car/*value*/ = f;
            arg1.print(LispObject.noLineBreak+
                       LispObject.printEscape+
                       LispObject.printBinary);
        }
        finally
        {   Jlisp.lit[Lit.std_output].car/*value*/ = save;
        }
        return Fns.reversip(f.exploded);
    }
}

class ExplodecFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        LispStream f = new LispExploder(true);
        LispObject save = Jlisp.lit[Lit.std_output].car/*value*/;
        try
        {   Jlisp.lit[Lit.std_output].car/*value*/ = f;
            arg1.print(LispObject.noLineBreak);
        }
        finally
        {   Jlisp.lit[Lit.std_output].car/*value*/ = save;
        }
        return Fns.reversip(f.exploded);
    }
}

class ExplodecnFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        LispStream f = new LispExploder(false);
        LispObject save = Jlisp.lit[Lit.std_output].car/*value*/;
        try
        {   Jlisp.lit[Lit.std_output].car/*value*/ = f;
            arg1.print(LispObject.noLineBreak);
        }
        finally
        {   Jlisp.lit[Lit.std_output].car/*value*/ = save;
        }
        return Fns.reversip(f.exploded);
    }
}

class ExplodehexFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        LispStream f = new LispExploder(true);
        LispObject save = Jlisp.lit[Lit.std_output].car/*value*/;
        try
        {   Jlisp.lit[Lit.std_output].car/*value*/ = f;
            arg1.print(LispObject.noLineBreak+
                       LispObject.printEscape+
                       LispObject.printHex);
        }
        finally
        {   Jlisp.lit[Lit.std_output].car/*value*/ = save;
        }
        return Fns.reversip(f.exploded);
    }
}

class ExplodenFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        LispStream f = new LispExploder(false);
        LispObject save = Jlisp.lit[Lit.std_output].car/*value*/;
        try
        {   Jlisp.lit[Lit.std_output].car/*value*/ = f;
            arg1.print(LispObject.noLineBreak+LispObject.printEscape);
        }
        finally
        {   Jlisp.lit[Lit.std_output].car/*value*/ = save;
        }
        return Fns.reversip(f.exploded);
    }
}

class ExplodeoctalFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        LispStream f = new LispExploder(true);
        LispObject save = Jlisp.lit[Lit.std_output].car/*value*/;
        try
        {   Jlisp.lit[Lit.std_output].car/*value*/ = f;
            arg1.print(LispObject.noLineBreak+
                       LispObject.printEscape+
                       LispObject.printOctal);
        }
        finally
        {   Jlisp.lit[Lit.std_output].car/*value*/ = save;
        }
        return Fns.reversip(f.exploded);
    }
}


class Fetch_urlFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Fgetv32Fn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Fgetv64Fn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class File_readablepFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class File_writeablepFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class FiledateFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        String s;
        if (arg1 instanceof Symbol)
        {   ((Symbol)arg1).completeName();
            s = ((Symbol)arg1).pname;
        }
        else if (arg1 instanceof LispString) s = ((LispString)arg1).string;
        else return Environment.nil;
        return LispStream.fileDate(s);
    }
}

class FilepFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
// use filedate(arg1) here.
        String s;
        if (arg1 instanceof Symbol)
        {   ((Symbol)arg1).completeName();
            s = ((Symbol)arg1).pname;
        }
        else if (arg1 instanceof LispString) s = ((LispString)arg1).string;
        else return Environment.nil;
        return LispStream.fileDate(s);
    }
}


class FlagFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2) throws Exception
    {
        while (!arg1.atom)
        {   LispObject p = arg1;
            Symbol name = (Symbol)p.car;
            arg1 = p.cdr;
            Fns.put(name, arg2, Jlisp.lispTrue);
        }
        return arg1;
    }
}

class FlagpFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2) throws Exception
    {
        LispObject res = Fns.get(arg1, arg2);
        if (res != Environment.nil) res = Jlisp.lispTrue;
        return res;
    }
}

class FlagpStarStarFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2) throws Exception
    {
        LispObject res = Fns.get(arg1, arg2);
        if (res != Environment.nil) res = Jlisp.lispTrue;
        return res;
    }
}

class FlagpcarFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2) throws Exception
    {
        if (arg1.atom) return Environment.nil;
        arg1 = arg1.car;
        LispObject res = Fns.get(arg1, arg2);
        if (res != Environment.nil) res = Jlisp.lispTrue;
        return res;
    }
}

class FluidFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class FluidpFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1)
    {
        return Fns.get(arg1, Jlisp.lit[Lit.special]);
    }
}

class FlushFn extends BuiltinFunction
{
    public LispObject op0() throws Exception
    {
        LispStream ee = 
            (LispStream)Jlisp.lit[Lit.std_output].car/*value*/;
        ee.flush();
        return Environment.nil;
    }
    public LispObject op1(LispObject arg1) throws Exception
    {
        LispStream ee = (LispStream)arg1;
        ee.flush();
        return Environment.nil;
    }
}

class FormatFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Fp_evaluateFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Fputv32Fn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Fputv64Fn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class FuncallFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1 instanceof Symbol)
        {   return ((Symbol)arg1).fn.op0();
        }
        else if (arg1 instanceof LispFunction)
        {   return ((LispFunction)arg1).op0();
        }
        else return Fns.apply0(arg1);
    }

    public LispObject op2(LispObject arg1, LispObject arg2) throws Exception
    {
        if (arg1 instanceof Symbol)
        {   return ((Symbol)arg1).fn.op1(arg2);
        }
        else if (arg1 instanceof LispFunction)
        {   return ((LispFunction)arg1).op1(arg2);
        }
        else return Fns.apply1(arg1, arg2);
    }

    public LispObject opn(LispObject [] aa) throws Exception
    {
        int n = aa.length;
        LispObject arg1 = aa[0];
        if (n == 3)
        {   if (arg1 instanceof Symbol)
            {   return ((Symbol)arg1).fn.op2(aa[1], aa[2]);
            }
            else if (arg1 instanceof LispFunction)
            {   return ((LispFunction)arg1).op2(aa[1], aa[2]);
            }
            else return error("function in funcall is invalid");
        }
        LispObject [] args = new LispObject [n-1];
        for (int i = 0;i<n-1;i++)
        {   args[i] = aa[i+1];
        }
        if (arg1 instanceof Symbol)
        {   return ((Symbol)arg1).fn.opn(args);
        }
        else if (arg1 instanceof LispFunction)
        {   return ((LispFunction)arg1).opn(args);
        }
        else return Fns.applyn(arg1, args);
    }
}

class GctimeFn extends BuiltinFunction
{
// It is not at all obvious that I have any way to record GC time in a Java
// implementation of Lisp, so I will always return 0.
    public LispObject op0()
    {
        return LispInteger.valueOf(0);
    }
}

class GensymFn extends BuiltinFunction
{
    public LispObject op0() throws Exception
    {
        return new Gensym("G");
    }
}

class Gensym1Fn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return new Gensym(((Symbol)arg1).pname);
    }
}

class Gensym2Fn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        ((Symbol)arg1).completeName();
        return new Gensym(((Symbol)arg1).pname);
    }
}

class GensympFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1 instanceof Gensym) return Jlisp.lispTrue;
        else return Environment.nil;
    }
}

class GetFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2) throws Exception
    {
        return Fns.get(arg1, arg2);
    }
}

class GetStarFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2) throws Exception
    {
        return Fns.get(arg1, arg2);
    }
}

class Get_current_directoryFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Get_lisp_directoryFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class GetdFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (!(arg1 instanceof Symbol)) return Environment.nil;
        Symbol name = (Symbol)arg1;
        if (name.special != null) 
            return new Cons(Jlisp.lit[Lit.fexpr], name.special);
        LispFunction fn = name.fn;
        if (fn instanceof Undefined) return Environment.nil;
        else if (fn instanceof Macro)
        {   LispObject body = ((Macro)fn).body;
            return new Cons(Jlisp.lit[Lit.macro], body);
        }
        else if (fn instanceof Interpreted)
        {   LispObject body = ((Interpreted)fn).body;
            return new Cons(Jlisp.lit[Lit.expr], body);
        }
        else return new Cons(Jlisp.lit[Lit.expr], fn);
    }
}

class GetenvFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        String s;
        if (arg1 instanceof Symbol)
        {   ((Symbol)arg1).completeName();
            s = ((Symbol)arg1).pname;
        }
        else if (arg1 instanceof LispString) s = ((LispString)arg1).string;
        else return Environment.nil;

        String s2 = null; //System.getProperty(s);
        if (s2 == null) return Environment.nil;
        else return new LispString(s2);
        
    }
}

class GethashFn extends BuiltinFunction
{
    public LispObject op1(LispObject key) throws ResourceException
    {
        LispObject r = (LispObject)
            ((LispHash)Jlisp.lit[Lit.hashtab]).hash.get(key);
        if (r == null) r = Environment.nil;
        else r = new Cons(key, r);  // as needed by REDUCE - apologies!
        return r;
    }
    public LispObject op2(LispObject key, LispObject table)
    {
        LispHash h = (LispHash)table;
        LispObject r = (LispObject)h.hash.get(key);
        if (r == null) r = Environment.nil;
        return r;
    }
    public LispObject opn(LispObject [] args) throws Exception
    {
        if (args.length != 3)
            return error("gethash called with " + args.length +
                "args when 1 to 3 expected");
        LispObject key = args[0];
        LispHash h = (LispHash)args[1];
        LispObject defaultValue = args[2];
        LispObject r = (LispObject)h.hash.get(key);
        if (r == null) r = defaultValue;
        return r;
    }
}

static LispObject lispZero = LispInteger.valueOf(0); // GC safe here!

class GetvFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2) throws Exception
    {
        if (!(arg1 instanceof LispVector))
            return Jlisp.error("Not a vector in getv", arg1);
        LispVector v = (LispVector)arg1;
        int i = arg2.intValue();
        arg1 = v.vec[i];
        if (arg1 == null) return lispZero; // for benefit of oblist()!
        else return arg1;
    }
}

class Getv16Fn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Getv32Fn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Getv8Fn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class GlobalFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class GlobalpFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1)
    {
        return Fns.get(arg1, Jlisp.lit[Lit.global]);
    }
}

class Hash_table_pFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1 instanceof LispHash) return Jlisp.lispTrue;
        else return Environment.nil;
    }
}

class HashcontentsFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws ResourceException
    {
        LispHash h = (LispHash)arg1;
        LispObject r = Environment.nil;
        if (h.flavour != 0)
        {   for (Iterator k = h.hash.keySet().iterator(); k.hasNext();)
            {   LispObject key = ((LispEqualObject)k.next()).value;
                Object value = h.hash.get(key);
                r = new Cons(
                        new Cons(key, (LispObject)value),
                        r);
            }
        }
        else
        {   for (Iterator k = h.hash.keySet().iterator(); k.hasNext();)
            {   Object key = k.next();
                Object value = h.hash.get(key);
                r = new Cons(
                        new Cons((LispObject)key, 
                                 (LispObject)value),
                        r);
            }
        }
        return r;
    }
}

class Hashtagged_nameFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class HelpFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class IdpFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1)
    {   return arg1 instanceof Symbol ? Jlisp.lispTrue :
               Environment.nil;
    }
}

class IndirectFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class InormFn extends BuiltinFunction
{
    public LispObject op2(LispObject la, LispObject lk) throws Exception
    {
        BigInteger a = la.bigIntValue();
        int k = lk.intValue();
        int r = 0;
        if (a.signum() == 0) return error("zero argument to inorm");
        while (!a.testBit(0))
        {   r++;
            a = a.shiftRight(1);
        }
        int n = a.bitLength(); // check later about negative cases!
        if (n <= k) 
            return new Cons(LispInteger.valueOf(a), LispInteger.valueOf(r)); 
        n = n - k; // number of bits to be lost
        boolean neg = a.signum() < 0;
        if (neg) a = a.negate();
        boolean toRound = a.testBit(n-1);
        a = a.shiftRight(n);
        if (toRound) a = a.add(BigInteger.ONE);
        while (!a.testBit(0))
        {   r++;
            a = a.shiftRight(1);
        }
        if (neg) a = a.negate();
        return new Cons(LispInteger.valueOf(a), LispInteger.valueOf(r+n));
    }
}

class Input_librariesFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class InternFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        if (arg1 instanceof LispString)
            return Symbol.intern(((LispString)arg1).string);
        else if (arg1 instanceof Symbol)
        {   ((Symbol)arg1).completeName();
            return Symbol.intern(((Symbol)arg1).pname);
        }
        else return error(
            "Argument to intern should be a symbol or a string");
    }
}

class IntersectionFn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2) throws Exception
    {
        LispObject r = Environment.nil;
        while (!arg1.atom)
        {   LispObject a1 = arg1;
            LispObject a2 = arg2;
            while (!a2.atom)
            {   LispObject a2a = a2;
                if (a2a.car.lispequals(a1.car)) break;
                a2 = a2a.cdr;
            }
            if (!a2.atom) r = new Cons(a1.car, r);
            arg1 = a1.cdr;
        }
        arg1 = Environment.nil;
        while (!r.atom)
        {   LispObject a1 = r;
            r = a1.cdr;
            a1.cdr = arg1;
            arg1 = a1;
        }
        return arg1;
    }
}

class Is_consoleFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class LastFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class LastcarFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class LastpairFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        LispObject r = arg1;
        while (!arg1.atom)
        {   r = arg1;
            arg1 = arg1.cdr;
        }
        return r;
    }
}

class LengthFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1)
    {
        int n = 0;
        while (!arg1.atom)
        {   n++;
            arg1 = arg1.cdr;
        }
        return LispInteger.valueOf(n);
    }
}

class LengthcFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws ResourceException
    {
        LispStream f = new LispCounter();
        LispObject save = Jlisp.lit[Lit.std_output].car/*value*/;
        try
        {   Jlisp.lit[Lit.std_output].car/*value*/ = f;
            arg1.print(LispObject.noLineBreak);
        }
        finally
        {   Jlisp.lit[Lit.std_output].car/*value*/ = save;
        }
        return LispInteger.valueOf(f.column);
    }
}

class LetStarFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class Library_nameFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class LinelengthFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        LispStream os = (LispStream)Jlisp.lit[Lit.std_output].car/*value*/;
        int prev = os.lineLength;
        if (arg1 instanceof LispInteger)
        {   int n = arg1.intValue();
            os.lineLength = n;
        }
        return LispInteger.valueOf(prev);
    }
}

class ListFn extends BuiltinFunction
{
    public LispObject op0() { return Environment.nil; }
    public LispObject op1(LispObject arg1) throws ResourceException
    {   return new Cons(arg1, Environment.nil); 
    }
    public LispObject op2(LispObject arg1, LispObject arg2) throws ResourceException
    {   return new Cons(arg1,
            new Cons(arg2, Environment.nil)); 
    }
    public LispObject opn(LispObject [] args) throws ResourceException
    {
        LispObject r = Environment.nil;
        for (int i=args.length; i!=0;)
        {   r = new Cons(args[--i], r);
        }
        return r;
    }
}

class ListStarFn extends BuiltinFunction
{
    public LispObject op0() { return Environment.nil; }
    public LispObject op1(LispObject arg1)
    {   return arg1; 
    }
    public LispObject op2(LispObject arg1, LispObject arg2) throws ResourceException
    {   return new Cons(arg1, arg2);
    }
    public LispObject opn(LispObject [] args) throws ResourceException
    {
        int i = args.length;
        LispObject r = args[--i];
        while (i != 0)
        {   r = new Cons(args[--i], r);
        }
        return r;
    }
}

class List_directoryFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        return error(name + " not yet implemented");
    }
}

class List_modulesFn extends BuiltinFunction
{
    public LispObject op0() throws Exception
    {
        PDS z = Jlisp.image;
            if (z != null) z.print();
        
        return Environment.nil;
    }
}

class List_to_stringFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        StringBuffer s = new StringBuffer();
        while (!arg1.atom)
        {   LispObject c = arg1;
            arg1 = c.cdr;
            LispObject ch = c.car;
            if (ch instanceof Symbol)
            {   ((Symbol)ch).completeName();
                s.append(((Symbol)ch).pname.charAt(0));
            }
            else if (ch instanceof LispString)
                s.append(((LispString)ch).string.charAt(0));
            else if (ch instanceof LispInteger)
                s.append((char)ch.intValue());
            else return error("Illegal item in list handed to list-to-string");
        }
        return new LispString(s.toString());
    }
}

class List_to_symbolFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        StringBuffer s = new StringBuffer();
        while (!arg1.atom)
        {   LispObject c = arg1;
            arg1 = c.cdr;
            LispObject ch = c.car;
            if (ch instanceof Symbol)
            {   ((Symbol)ch).completeName();
                s.append(((Symbol)ch).pname.charAt(0));
            }
            if (ch instanceof LispString)
                s.append(((LispString)ch).string.charAt(0));
            else if (ch instanceof LispInteger)
                s.append((char)ch.intValue());
            else return error("Illegal item in list handed to list-to-string");
        }
        return Symbol.intern(s.toString());
    }
}

class List_to_vectorFn extends BuiltinFunction
{
    public LispObject op1(LispObject arg1) throws Exception
    {
        LispObject w = arg1;
        int n = 0;
        while (!w.atom)
        {   n++;
            w = w.cdr;
        }
        LispVector r = new LispVector(n);
        n = 0;
        while (!arg1.atom)
        {   r.vec[n++] = arg1.car;
            arg1 = arg1.cdr;   
        }
        return r;
    }
}

class List2Fn extends BuiltinFunction
{
    public LispObject op2(LispObject arg1, LispObject arg2) throws ResourceException
    {
        return new Cons(arg1, new Cons(arg2, Environment.nil));
    }
}

class List2StarFn extends BuiltinFunction
{
    public LispObject opn(LispObject [] args) throws Exception
    {
        if (args.length != 3) 
            return error("list2* called with " + args.length +
                         " args when 3 were expected");
        else return new Cons(args[0], new Cons(args[1], args[2]));
    }
}

class List3Fn extends BuiltinFunction
{
    public LispObject opn(LispObject [] args) throws Exception
    {
        if (args.length != 3)
            return error("list3 called with " + args.length +
                         " args when 3 were expected");
        else return new Cons(args[0],
                             new Cons(args[1],
                                      new Cons(args[2], Environment.nil)));
    }
}


}

// end of Fns1.java

