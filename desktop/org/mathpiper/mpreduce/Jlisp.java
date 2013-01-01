package org.mathpiper.mpreduce;

// Jlisp
//
// Standard Lisp system coded in Java. Actually this goes
// way beyond the Standard Lisp Report and includes a large fraction
// of that which is present in the CSL Lisp system.
//
// The purpose of this implementation is to support
// REDUCE. Early versions of jlisp were amazingly slow but
// performance is gradually improving!

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


//import geogebra.common.main.App;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.math.BigInteger;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.mathpiper.mpreduce.datatypes.Cons;
import org.mathpiper.mpreduce.datatypes.LispEqualHash;
import org.mathpiper.mpreduce.datatypes.LispHash;
import org.mathpiper.mpreduce.datatypes.LispString;
import org.mathpiper.mpreduce.datatypes.LispVector;
import org.mathpiper.mpreduce.exceptions.EOFException;
import org.mathpiper.mpreduce.exceptions.LispException;
import org.mathpiper.mpreduce.exceptions.ProgEvent;
import org.mathpiper.mpreduce.exceptions.ResourceException;
import org.mathpiper.mpreduce.functions.builtin.Fns;
import org.mathpiper.mpreduce.functions.functionwithenvironment.Bytecode;
import org.mathpiper.mpreduce.functions.lisp.LispFunction;
import org.mathpiper.mpreduce.functions.lisp.Undefined;
import org.mathpiper.mpreduce.io.Fasl;
import org.mathpiper.mpreduce.io.streams.DoubleWriter;
import org.mathpiper.mpreduce.io.streams.LispOutputStream;
import org.mathpiper.mpreduce.io.streams.LispStream;
import org.mathpiper.mpreduce.io.streams.LispStringReader;
import org.mathpiper.mpreduce.io.streams.WriterToLisp;
import org.mathpiper.mpreduce.numbers.LispInteger;
import org.mathpiper.mpreduce.numbers.LispSmallInteger;
import org.mathpiper.mpreduce.packagedatastore.PDS;
import org.mathpiper.mpreduce.packagedatastore.PDSInputStream;
import org.mathpiper.mpreduce.packagedatastore.PDSOutputStream;
import org.mathpiper.mpreduce.special.Specfn;
import org.mathpiper.mpreduce.special.SpecialFunction;
import org.mathpiper.mpreduce.symbols.Gensym;
import org.mathpiper.mpreduce.symbols.Symbol;
//import org.mathpiper.mpreduce.javacompiler.Fns4;

public class Jlisp extends Environment
{
        private static String version = ".016";

	// Within this file I will often reference lispIO and lispErr
	// directly. Elsewhere they should ONLY be accessed via the Lisp
	// variables that point towards them. The direct access here is in
	// cases where the Lisp world may not have been fully set up.
	public static LispStream lispIO, lispErr;
	public static boolean interactivep = false;
	public static boolean debugFlag = false;
	public static boolean headline = true;
	public static boolean backtrace = true;
	public static LispObject errorCode;
	public static int verbosFlag = 1;
	public static boolean trapExceptions = true;
	private static Writer transcript = null;

	public static boolean interruptEvaluation = false;

	public static void print(String s) throws ResourceException
	{
		((LispStream)(lit[Lit.std_output].car/*value*/)).print(s);
	}

	public static void println(String s) throws ResourceException
	{
		((LispStream)(lit[Lit.std_output].car/*value*/)).println(s);
	}

	public static void print(LispObject s) throws ResourceException
	{
		if (s==null) print("<null>"); else s.print();
	}

	public static void println(LispObject s) throws ResourceException
	{
		if (s==null) print("<null>"); else s.print();
		((LispStream)(lit[Lit.std_output].car/*value*/)).println();
	}

	public static void println() throws ResourceException
	{
		((LispStream)(lit[Lit.std_output].car/*value*/)).println();
	}

	public static void errprint(String s) throws ResourceException
	{
		((LispStream)(lit[Lit.err_output].car/*value*/)).print(s);
	}

	public static void errprintln(String s) throws ResourceException
	{
		((LispStream)(lit[Lit.err_output].car/*value*/)).println(s);
	}

	public static void errprintln() throws ResourceException
	{
		((LispStream)(lit[Lit.err_output].car/*value*/)).println();
	}

	public static void traceprint(String s) throws ResourceException
	{
		((LispStream)(lit[Lit.tr_output].car/*value*/)).print(s);
	}

	public static void traceprintln(String s) throws ResourceException
	{
		((LispStream)(lit[Lit.tr_output].car/*value*/)).println(s);
	}

	public static void traceprintln() throws ResourceException
	{
		((LispStream)(lit[Lit.tr_output].car/*value*/)).println();
	}

	public static LispObject error(String s) throws LispException
	{
		if (headline)
		{   errprintln();
			errprintln("++++ " + s);
		}
                	     ResourceException.errors_now++;
 	  	     if (ResourceException.errors_limit > 0 &&
 	  	         ResourceException.errors_now > ResourceException.errors_limit)
                         {   if (headline) errprintln("++++ Error count resource exceeded");
                            throw new ResourceException("error count");
                         }

		     checkExit(s);



		throw new LispException(s);
	}

	public static LispObject error(String s, LispObject a) throws LispException
	{
		if (headline)
		{   errprintln();
			errprint("++++ " + s + ": ");
			a.errPrint();
			errprintln();
		}

                     ResourceException.errors_now++;
 	  	     if (ResourceException.errors_limit > 0 &&
 	  	         ResourceException.errors_now > ResourceException.errors_limit)
                         {   if (headline) errprintln("++++ Error count resource exceeded");
                            throw new ResourceException("error count");
                        }

		

		checkExit(s);



		throw new LispException(s);
	}


	// The main parts of this file relate to system startup options

	public static PDS [] images = new PDS[10];
	public static int outputImagePos;
	public static int imageCount;

	static String [] imageFile = new String[10];

	public static void main(String [] args)
	{
		startup(args,
		        new InputStreamReader(System.in),
		        new PrintWriter(System.out),
		        true);
	}

	static Reader in;
	public static PrintWriter out;

	public static boolean standAlone;

	public static Vector openOutputFiles = null;

	public static boolean restarting = false;
	static String  restartModule = null;
	static String  restartFn = null;
	static String  restartArg = null;

	static boolean finishingUp = false;

	public static void startup(String [] args,
	                           Reader Xin, PrintWriter Xout,
	                           boolean standAloneFlag)
	{
		in = Xin;
		out = Xout;
		lispIO = null;
		standAlone = standAloneFlag;
		Thread t = null;
		if (standAlone)
		{   final int screenRefreshInterval = 2500;
			t = new FlushOutputThread();
			t.start();
		}
		// I am pretty keen that all output files should be closed (and in the
		// process they should be flushed) so that data is never lost. So I keep
		// a record (in this Vector) of ones that are liable to need closing, and
		// then in a "finally" clause I zoom through cleaning up.
		openOutputFiles = new Vector(10, 5);
		try
		{   startup1(args);
		}
                catch (Exception e)
 	  	{e.printStackTrace();}
		finally
		{
			lispIO = null;
			finishingUp = true;
			if (t != null){
				t.interrupt();     // so it can exit
			}
			int i;
			// In general I close in the opposite order from that in which I opened files.
			// The code here is such that if closing one file happened to have a side
			// effect of closing another along the way that would not be a calamity.
			while ((i=openOutputFiles.size()) != 0)
			{   ((LispStream)openOutputFiles.get(i-1)).close();
			}
		}
		// If I was run as an application not an applet (via any route!) I am
		// permitted to exit.
		// if (!CWin.isApplet) System.exit(0);
	}

	static void startup1(String [] args) throws Exception
	{
		long startTime = System.currentTimeMillis();
		String [] inputFile = new String [10];
		int inputCount = 0;
		imageCount = 0;
		outputImagePos = -1;
		boolean coldStart = false;
		String mainOutput = null;
		String logFile = null;
		boolean verbose = false;
		boolean copyrightRequest = false;
		String [] errs = new String [10];
		int errCount = 0;
		String [] defineSymbol = new String [10];
		int defineCount = 0;
		String [] undefineSymbol = new String [10];
		int undefineCount = 0;
		boolean noRestart = false;
		boolean batchSwitch = false;
		// I may need to display diagnostics before I have finshed setting up
		// streams etc in their proper final form, so I arrange a provisional
		// setting that directs early messages to the terminal.
		lispIO = lispErr = new LispOutputStream();
		lit[Lit.std_output] = lit[Lit.tr_output] =
		                              lit[Lit.err_output] = lit[Lit.std_input] =
		                                                            lit[Lit.terminal_io] = lit[Lit.debug_io] =
		                                                                                           lit[Lit.query_io] = Symbol.intern("temp-stream");
		standardStreams();

		// The options that I accept here are intended to match (as far as I can
		// reasonably make them) the ones used with the "CSL" Lisp implementation.
		// I scan the command line to decode them. Note that until this has
		// been completed I can not do proper Lisp output because I will not have
		// seen redirection requests.
		int i;
		for (i=0; i<args.length; i++)
		{   String arg = args[i];
			String arg1;
			if (arg.length() >= 2 && arg.charAt(0) == '-')
			{   char key = Character.toLowerCase(arg.charAt(1));
				switch (key)
				{
				case '-': // redirect all output
					break;
				case 'b': // flips batchp() result
					batchSwitch = true;
					continue;
				case 'c': // display copyright notice
					copyrightRequest = true;
					continue;
				case 'd': // define symbol
					break;
				case 'e': // "experiment" control
					continue;
				case 'f': // serve on a socket
					break;
				case 'g': // enhance debugging
					debugFlag = true;
					continue;
				case 'i': // specify (input) image or library
					break;
				case 'k': // indicate amount of memory to use
					break;
				case 'l': // transcript of output to a log file
					break;
				case 'm': // (memory trace control)
					continue;
				case 'n': // ignore restart function in image
					noRestart = true;
					continue;
				case 'o': // output image
					break;
				case 'p': // profile option
					continue;
				case 'q': // quiet mode
					verbose = false;
					continue;
				case 'r': // initial random seed
					break;
				case 's': // view machine code from any compilation
					continue;
				case 't': // inspect time-stamp on a module
					continue;
				case 'u': // undefined symbol
					break;
				case 'v': // verbose mode
					verbose = true;
					continue;
				case 'w': // run in windowed mode
					continue;
				case 'x': // less trapping of possibly internal errors
					System.out.println("JVM exit on error set.");
					trapExceptions = false;
					continue;
				case 'y': // ignore restart-function in saved image
					continue;
				case 'z': // cold start mode
					coldStart = true;
					continue;
				default:
					if (errCount < errs.length)
						errs[errCount++] =
						        "Invalid option \"" + arg + "\"";
					continue;
				}
				// In many cases an option takes an argument. I permit either -Ixx or -I xx
				// and separate off xxx here.
				if (arg.length() > 2) arg1 = arg.substring(2);
				else if (i+1<args.length) arg1 = args[++i];
				else
					{   if (errCount < errs.length)
						errs[errCount++] =
						        "Option \"" + arg +
						        "\" invalid as final option";
					continue;
				}
				// Now arg is the initial key and arg1 is the follow-up.
				switch (key)
				{
				case '-': // redirect all output
					mainOutput = arg1;
					break;
				case 'd': // define a symbol
					if (defineCount < defineSymbol.length)
						defineSymbol[defineCount++] = arg1;
					break;
				case 'f': // serve on a socket
					break;
				case 'i': // specify (input) image or library
					if (imageCount < imageFile.length)
						imageFile[imageCount++] = arg1;
					break;
				case 'k': // indicate amount of memory to use
					break;
				case 'l': // transcript of output to a log file
					logFile = arg1;
					break;
				case 'o': // output image
					// If the user specifies an output image then I will make it available
					// as an input image too, in the place in the search-list that it
					// appears on the command-line: eg
					//    -i file1.img -o file2.img -i file3.img
					// will scan in order file1/file2/file3 when loading files, and will
					// write to file2.
					if (imageCount < imageFile.length)
					{   outputImagePos = imageCount;
						imageFile[imageCount++] = arg1;
					}
					break;
				case 'r': // initial random seed
					break;
				case 'u': // undefined symbol
					if (undefineCount < undefineSymbol.length)
						undefineSymbol[undefineCount++] = arg1;
					break;
				}
			}
			else inputFile[inputCount++] = arg;
		}
		// Now I have finished decoding the command line. The first parts I
		// process are those relating to the intended destination for
		// output.


		// The directive "-l file" arranges that a copy of all output goes to
		// the named file (if it can be opened) in addition to the usual
		// destination (which may have been adjusted using "-- file").
		if (logFile != null)
			{   try
			{   transcript = new BufferedWriter(
				                         new FileWriter(LispStream.nameConvert(logFile)));
			}
			catch (IOException e)
			{   transcript = null;
				if (errCount < errs.length)
					errs[errCount++] =
					        "File \"" + logFile +
					        "\" unavailable as a log file";
			}
		}

		// If the user had specified "-- file" then the main output is to that
		// file. Otherwise the main output is to the initial "standard" stream.
		// If the file named after "--" can not be opened for writing then the
		// directive is ignored and output again goes to the "standard" place.

		if (mainOutput == null)
		{   if (transcript == null) lispIO = new LispOutputStream();
			else lispIO = new DoubleWriter(transcript);
		}
		else
			{   try
			{   if (transcript == null) lispIO = new LispOutputStream(mainOutput);
				else lispIO = new DoubleWriter(mainOutput, transcript);
			}
			catch (IOException e)
			{    errs[errCount++] =
				        "File \"" + mainOutput + "\" could not be written to";
				if (transcript == null) lispIO = new LispOutputStream();
				else lispIO = new DoubleWriter(transcript);
			}
		}
		if (transcript != null)
			lispIO.println("Transcript sent to file " + logFile);
		lispErr = lispIO; // lispErr sent to spool file if lispIO is...
		// now I have Java variables that refer to the output streams I need
		// to establish.

		// Now I am in a position to display any errors relating to
		// command line options.
		for (i=0; i<errCount; i++)
			lispErr.println(errs[i]);

		LispSmallInteger.preAllocate();  // some small integers treated specially

		// For use while I am re-loading images and also to assist the
		// custom Lisp bytecoded stuff I build a table of all the functions
		// that I have built into this Lisp.
		//
		//App.debug("Loading builtins");
		builtinFunctions = new HashMap();
		builtinSpecials  = new HashMap();
		for (i=0; i<fns1.builtins.length; i++)
		{   ((LispFunction)fns1.builtins[i][1]).name =
			        (String)fns1.builtins[i][0];
			builtinFunctions.put(fns1.builtins[i][0], fns1.builtins[i][1]);
		}
		//App.debug("fns1 loaded");
		for (i=0; i<fns2.builtins.length; i++)
		{   ((LispFunction)fns2.builtins[i][1]).name =
			        (String)fns2.builtins[i][0];
			builtinFunctions.put(fns2.builtins[i][0], fns2.builtins[i][1]);
		}
		//App.debug("fns2 loaded");
		for (i=0; i<fns3.builtins.length; i++)
		{   ((LispFunction)fns3.builtins[i][1]).name =
			        (String)fns3.builtins[i][0];
			builtinFunctions.put(fns3.builtins[i][0], fns3.builtins[i][1]);
		}
		//App.debug("fns3 loaded");
		for (i=0; i<mpreduceFunctions.builtins.length; i++)
		{   ((LispFunction)mpreduceFunctions.builtins[i][1]).name =
			        (String)mpreduceFunctions.builtins[i][0];
			builtinFunctions.put(mpreduceFunctions.builtins[i][0], mpreduceFunctions.builtins[i][1]);
		}
		//App.debug("mpreduceFunctions loaded");

		/*for (i=0; i<fns4.builtins.length; i++)
		{   ((LispFunction)fns4.builtins[i][1]).name =
			        (String)fns4.builtins[i][0];
			builtinFunctions.put(fns4.builtins[i][0], fns4.builtins[i][1]);
		}*/
		for (i=0; i<specfn.specials.length; i++)
		{   ((SpecialFunction)specfn.specials[i][1]).name =
			        (String)specfn.specials[i][0];
			builtinSpecials.put(specfn.specials[i][0], specfn.specials[i][1]);
		}
		//App.debug("specfn loaded");

		Bytecode.setupBuiltins();
		//App.debug("setupBuiltins done");


		// I open all the image files that the user had mentioned...
		if (imageCount == 0)
			{   if (verbose)
				lispErr.println(
				        "Image file defaulting to in-store data");
			imageFile[0] = "-";
			imageCount = 1;
		}
		for (i=0; i<imageCount; i++)
		{   images[i] = null;
			try
				{   if (imageFile[0].equals("-"))
				{
					// I get the ClassLoader for LispStream as a randomish convenient
					// class that is part of my code. Then I can access my image as
					// a resource, searching for it wherever I loaded my classes from.
					// This may well be the .jar file I am using...
					ClassLoader cl = lispIO.getClass().getClassLoader();
					InputStream is = cl.getResourceAsStream("default.img");

                                        if(is == null)
                                        {
                                            is = cl.getResourceAsStream("reduce.img");
                                        }
                                        
                                        if(is == null)
                                        {
                                            is = cl.getResourceAsStream("minireduce.img");
                                        }

					if (is != null) images[i] = new PDS(is);
				}
				else images[i] = new PDS(imageFile[i], i==outputImagePos);
			}
			catch (IOException e)
			{
			}
		}

		//App.debug("Image to be loaded has been defined");
		
		// The next stage is either to create an initial Lisp heap or to
		// re-load one that had been saved from a previous session. Things are
		// made MUCH more complicated here because a running Lisp can (under program
		// control) get itself restarted either in cold or warm-start mode.

		boolean loaded;

		for (;;)  // loop here is for the oddly named RESTART-CSL function
		{
			loaded = false;
			// The next section is a sort of admission of confusion. When I restart the
			// whole of the old word ought to get discarded: Java garbage collection
			// ought to reap it. However that seems not to happen anything like as well
			// as I intended, with BAD effects on total storage use in restarted systems
			// (most of the old as well as most of the new heap remains!). This could
			// well be MY fault with some valid Lisp root not being restored, but
			// right now I can not find it and it COULD also be a consequence of
			// a conservative GC strategy in the Java world. Anyway to reduce the pain as
			// much as possible I will destroy a lot of connectivity in the old heap
			// now so that even if bits of it are still referred to that will only lead
			// to a small memory loss not a huge one.
			if (restarting)
			{   for (i=0; i< LispReader.chars.length; i++)  LispReader.chars[i] = null;
				for (i=0; i<LispReader.oblist.length; i++)
				{   nil = LispReader.oblist[i];
					// Do a radical clean-up of all existing symbols
					if (nil != null)
					{   nil.car/*value*/ = null;
						nil.cdr/*plist*/ = null;
						nil.fn = null;
						nil.special = null;
					}
					LispReader.oblist[i] = null;
				}
				LispReader.oblistCount = 0;
				((LispHash)lit[Lit.hashtab]).hash.clear();
				for (i=0; i<lit.length; i++)    lit[i] = null;
				for (i=0; i<LispReader.spine.length; i++)  LispReader.spine[i] = null;
				lispIO.tidyup(null);
				lispErr.tidyup(null);
				nil = null;
				lispTrue = null;
				modulus = 1;
				bigModulus = BigInteger.valueOf(modulus);
				Specfn.progData = null;
				Specfn.progEvent = Specfn.NONE;
				errorCode = null;
			}
			if (!coldStart)
			{   GZIPInputStream image = null;
				PDSInputStream ii = null;
				// I will re-load from the first checkpoint file in the list that has
				// a HeapImage stored in it.
				for (i=0; i<imageCount; i++)
					{   try
					{   ii = new PDSInputStream(images[i], "HeapImage");
					}
					catch (IOException e)
					{
					}
					if (ii != null) break;
				}
				try
					{   if (ii == null)
						throw new IOException("No valid checkpoint file found");
					image = new GZIPInputStream(
					                new BufferedInputStream(ii, 32768));
					Symbol.symbolCount =
					        Cons.consCount =
					                LispString.stringCount = 0;
					LispReader.restore(image);
					loaded = true;
				}
				catch (Exception e)
				{   lispErr.println("Failed to load image \"" +
					                    imageFile[0] + "<HeapImage>\"");
					// The next two lines are for debugging at least
					lispErr.println(e.getMessage());
					e.printStackTrace(new PrintWriter(new WriterToLisp(lispErr)));
					loaded = false;
				}
				finally
					{   if (image != null)
						{   try
						{   image.close();
						}
						catch (IOException e)
						{   lispErr.println("Failed to load image");
							loaded = false;
						}
					}
				}
				if (restarting && !loaded)
				{   lispIO.println("+++ No image file when restarting");
					return;
				}
			}
			//App.debug("Image (if any) has been loaded");
			
			// If no image file was available I will fall back to a cold start. This is
			// probably not what is wanted in the long run but will be useful while
			// testing.
			if (!loaded)
			{   initSymbols();
				DateFormat df = DateFormat.getInstance();
				df.setTimeZone(TimeZone.getDefault());
				lit[Lit.birthday] = new LispString(df.format(new Date()));
			}
			else
			{   // System.out.println("Bodge here...");
				//initfns(fns4.builtins);
			}
			lispIO.tidyup(nil);
			lispErr.tidyup(nil);

			// Having set up an image I optionally display a banner.
			if (verbose)
			{   lispIO.println("Jlisp 0.93a ... " +
				                   ((LispString)lit[Lit.birthday]).string);
				if (loaded)
				{   lispIO.println("Sym    = " + Symbol.symbolCount);
					lispIO.println("Cons   = " + Cons.consCount);
					lispIO.println("String = " + LispString.stringCount);
				}
				if (copyrightRequest)
				{
					lispIO.println("Copyright \u00a9 (C) Codemist Ltd, 1998-2000");
				}
			}

			// If the user specifed -Dxxx, -Dxxx=yyy or -Uxxx on the command
			// line I process that here. I will perform all the "undefine"
			// operations before any of the "define" ones, but otherwise
			// proceed left to right
			for (i=0; i<undefineCount; i++)
			{   Symbol s = Symbol.intern(undefineSymbol[i]);
				s.car/*value*/ = lit[Lit.undefined];
				s = null;
			}
			for (i=0; i<defineCount; i++)
			{   String name = defineSymbol[i];
				LispObject value;
				int eqPos = name.indexOf('=');
				// Just -Dname without an "=" sets the name to T
				if (eqPos == -1) value = lispTrue;
				else
				{   String v = name.substring(eqPos+1);
					name = name.substring(0, eqPos);
					int lv = v.length();
					// If the value specified was enclosed in double quotes I strip those
					// off. Thus -Dname=xxx and -Dname="xxx" both set name to a string "xxx".
					// Note that -Dname= will set name to the empty string "" which is non-nil
					// so is OK for "true".
					if (lv != 0 &&
					        v.charAt(0) == '\"' &&
					        v.charAt(lv-1) == '\"')
						v = v.substring(1, lv-1);
					value = new LispString(v);
				}
				Symbol s = Symbol.intern(name);
				s.car/*value*/ = value;
				s = null;
				value = null;
			}

			for (i=0; i<128; i++) // To speed up readch()
			{   LispReader.chars[i] = Symbol.intern(String.valueOf((char)i));
			}

			// If no input files had been specified I will read from the standard
			// input - often the keyboard. Otherwise I will process each file that
			// is given. This seems a bulky bit of code because of Java's
			// insistence on exception processing. I do not work too hard on that!
			if (inputCount == 0)
			{   interactivep = !batchSwitch;
				if (!restarting)
					lispIO.setReader("<stdin>", in, standAlone, true);
				standardStreams();
				//System.out.printf("set up standard streams%n");
				try
				{   readEvalPrintLoop(noRestart);
					throw new ProgEvent(ProgEvent.STOP, nil, "EOF");
				}
				catch (ProgEvent e)
				{
					checkExit(e.getMessage());


					switch (e.type)
					{
					case ProgEvent.STOP:
						restarting = false;
						break;
					case ProgEvent.PRESERVE:
						Cons www1 = (Cons)e.details;
                        preserve(www1.car, www1.cdr);
						restarting = false;
						break;
					case ProgEvent.PRESERVERESTART:
                        Cons www2 = (Cons)e.details;
                        preserve(www2.car, www2.cdr);
                        e.details = lispTrue;
                        lispErr.println("PRESERVERESTART GOT CALLED");
                        // Drop through to restart.
					case ProgEvent.RESTART:
						println();
						println("Restart Lisp...");
						// the RESTART event has (details/extra) as Lisp items carried
						// with it.
						//    If details=nil it asks for a cold start
						//    If details=t   it asks for a normal start using the default
						//                   restart-action from the image
						//    if details=f   it does a warm restart but then calls function f
						//                   (this is any atomic f not nil or t)
						//    if details=(m f) it does a warm start, then loads module m and
						//                   finally calls function f
						// In the two latter cases (ie details other than nil/t) if extra is provided
						// it is passed on as an argument to the user-specified restart function f.
						//
						// This elaborate behaviour is as grew up piecemeal in CSL and it is expected
						// that this function is only used when setting up scripts to rebuild major
						// bits of software so MAYBE the fact that it is a bit obscure is not too
						// much of a problem.
						restartFn = null;
						restartModule = null;
						restartArg = null;
						if (e.details == nil) coldStart = true;
						else try
							{   coldStart = false;
								if (e.details != lispTrue)
									{   if (e.details.atom)
									{   restartFn = Fns.explodeToString(e.details);
									}
									else
									{   restartModule =
										        Fns.explodeToString(e.details.car);
										LispObject w1 = e.details.cdr;
										if (!w1.atom) w1 = w1.car;
										restartFn = Fns.explodeToString(w1);
									}
									if (e.extras != null)
										restartArg = Fns.explodeToString(e.extras);
								}
							}
							catch (Exception e1)
							{   System.out.println("Unexpected exception " + e1);
							}
						restarting = true;
						continue;
					default:
						errprintln();
						errprintln("Stopping because of " + e.message);
						restarting = false;
						break;
					}
				}
				if (restarting) continue;
				else break;
			}
			else
			{   interactivep = batchSwitch;
				if (restarting) inputCount = 1;
				for (i=0; i<inputCount; i++)
					{   try
						{   if (!restarting)
							lispIO.setReader(
							        inputFile[i],
							        new BufferedReader(
							                new FileReader(inputFile[i])),
							        false, true);
						standardStreams();
						try
						{   readEvalPrintLoop(noRestart);
						}
						catch (ProgEvent e)
						{
							checkExit(e.getMessage());

							switch (e.type)
							{
							case ProgEvent.STOP:
								restarting = false;
								i = inputCount;
								break;
							case ProgEvent.PRESERVE:
								Cons w = (Cons)e.details;
								preserve(w.car, w.cdr);
								i = inputCount;
								restarting = false;
								break;
							case ProgEvent.PRESERVERESTART:
	                            Cons www2 = (Cons)e.details;
	                            preserve(www2.car, www2.cdr);
	                            e.details = lispTrue;
	                            lispErr.println("Preserverestart was called");
	                            // Drop through to restart.
							case ProgEvent.RESTART:
								println();
								println("Restart Lisp...");
								restartFn = null;
								restartModule = null;
								restartArg = null;
								if (e.details == nil) coldStart = true;
								else try
									{   coldStart = false;
										if (e.details != lispTrue)
											{   if (e.details.atom)
											{   restartFn = Fns.explodeToString(e.details);
											}
											else
											{   restartModule =
												        Fns.explodeToString(e.details.car);
												LispObject w1 = e.details.cdr;
												if (!w1.atom) w1 = w1.car;
												restartFn = Fns.explodeToString(w1);
											}
											if (e.extras != null)
												restartArg = Fns.explodeToString(e.extras);
										}
									}
									catch (Exception e1)
									{   System.out.println("Unexpected exception " + e);
									}
								i = inputCount;
								restarting = true;
								break;
							default:
								errprintln();
								errprintln(
								        "Stopping because of " + e.message);
								i = inputCount;
								restarting = false;
								break;
							}
						}
						finally
						{   if (!restarting) lispIO.reader.close();
						}
					}
					catch (IOException e)
					{

						checkExit(e.getMessage());

						errprintln("Failed to read from \"" +
						           inputFile[i] + "\"");
					}
				}
			}
			if (restarting) continue;
			else break; // loop to do with RESTART-CSL calls
		}
		if (verbose)
		{   long endTime = System.currentTimeMillis();
			long elapsed = endTime - startTime;
			long secs = elapsed / 1000;
			long millis = elapsed % 1000;
			long tenths = millis / 100;
			long hunds = (millis % 100) / 10;
			lispIO.println("End of Lisp run after " +
			               secs + "." + tenths + hunds + " seconds");
		}

		lispIO.close();
		
		//App.debug("end of startup1");

	}

	static void standardStreams()
	{
		lit[Lit.std_output].car/*value*/  = lispIO;
		lit[Lit.tr_output].car/*value*/   = lispIO;
		lit[Lit.err_output].car/*value*/  = lispErr;
		lit[Lit.std_input].car/*value*/   = lispIO;
		lit[Lit.terminal_io].car/*value*/ = lispIO;
		lit[Lit.debug_io].car/*value*/    = lispIO;
		lit[Lit.query_io].car/*value*/    = lispIO;
	}

        //Checkpoint calls this function.
	public static void preserve(LispObject arg1, LispObject arg2) throws Exception
	{
		PDS imagePDS = images[outputImagePos];
		if (imagePDS == null)
		{   errprintln("no output image file available");
			return;
		}
		LispObject save1 = lit[Lit.restart];
		LispObject save2 = lit[Lit.banner];
		lit[Lit.restart] = arg1;
		lit[Lit.banner] = arg2;

		LispObject oldBirthday = lit[Lit.birthday];
		// I want the new image file to have a fresh date
		DateFormat df = DateFormat.getInstance();
		df.setTimeZone(TimeZone.getDefault());
		lit[Lit.birthday] =
		        new LispString(df.format(new Date()));

		GZIPOutputStream dump = null;
		try
		{   dump = new GZIPOutputStream(
			                   new BufferedOutputStream(
			                           new PDSOutputStream(imagePDS, "HeapImage"),
			                           32768));
			LispReader.preserve(dump);
			println();
			println("Image written");
		}
		catch (IOException e)
		{   errprintln("IO error on dump file: " + e.getMessage());
		}
		finally
			{   if (dump != null)
				try
				{   dump.close();
				}
				catch (IOException e)
				{
				}
			lit[Lit.birthday] = oldBirthday;
			lit[Lit.restart] = save1;
			lit[Lit.banner] = save2;
		}
	}



	public static OutputStream odump;
	public static InputStream  idump;



	public static HashMap builtinFunctions, builtinSpecials;





        //-------------------------

	// set up fixed definitions

	static void initfns(Object [][] builtins)
	{
		for (int i=0; i<builtins.length; i++)
		{   Object [] s = builtins[i];
			String name = (String)s[0];
			LispFunction fn = (LispFunction)s[1];
			fn.name = name;
			Symbol.intern(name, fn, null);
		}

	}

	static void initSymbols() throws ResourceException
	{
		//System.out.println("Beginning cold start: " + oblistCount);
		Fns.prompt = null;
		Gensym.gensymCounter = 0;

		// set up nil first since it is needed by Symbol.intern
		nil = Symbol.intern("nil");
		nil.cdr/*plist*/ = nil;
		nil.car/*value*/ = nil;
		nil.car = nil.cdr = nil;

		// next set up "undefined" and "t" which both have themselves as value
		lit[Lit.undefined]       = Symbol.intern("*undefined-value*");
		((Symbol)lit[Lit.undefined]).car/*value*/ = lit[Lit.undefined];
		lispTrue                 = Symbol.intern("t");
		lispTrue.car/*value*/           = lispTrue;

		// Now the remaining literals. It does not matter that undefined gets
		// looked up again here, since the version already created will be found.
		for (int i=0; i<Lit.names.length; i++)
		{   lit[i] = Symbol.intern(Lit.names[i]);
		}

		// The object list has a funny treatment to make it agree with CSL
		lit[Lit.starpackage].car/*value*/ =
		        new LispVector(new LispObject [] {nil, LispReader.obvector});

		((Symbol)lit[Lit.raise]).car/*value*/ = nil;
		((Symbol)lit[Lit.lower]).car/*value*/ = lispTrue;
		((Symbol)lit[Lit.redefmsg]).car/*value*/ = lispTrue;

		// The things put in lispsystem* must include various ones relied upon
		// by the REDUCE build scripts!
		((Symbol)lit[Lit.lispsystem]).car/*value*/ =
		        new Cons(new Cons(Symbol.intern("platform"), Symbol.intern("java")),
			        new Cons(new Cons(Symbol.intern("c-code"), LispInteger.valueOf(0)),
			                 new Cons(new Cons(Symbol.intern("name"),   new LispString("java")),
			                          new Cons(Symbol.intern("csl"),       // a lie, in some sense!
			                                   new Cons(Symbol.intern("jlisp"),
			                                            new Cons(Symbol.intern("embedded"),
			                                                     nil))))));

		Fns.fluid(nil);
		Fns.fluid(lispTrue);
		Fns.fluid(lit[Lit.lispsystem]);
		Fns.fluid(lit[Lit.raise]);
		Fns.fluid(lit[Lit.lower]);
		Fns.fluid(lit[Lit.starcomp]);
		Fns.fluid(lit[Lit.commonLisp]);
		Fns.fluid(lit[Lit.redefmsg]);

		initfns(fns1.builtins);
		initfns(fns2.builtins);
		initfns(fns3.builtins);
                initfns(mpreduceFunctions.builtins);
		//initfns(fns4.builtins);
		// initfns(fns5.builtins);
		// initfns(fns6.builtins);

		{   Object [][] specials = specfn.specials;
			for (int i=0; i<specials.length; i++)
			{   Object [] s = specials[i];
				String name = (String)s[0];
				SpecialFunction fn = (SpecialFunction)s[1];
				fn.name = name;
				Symbol.intern(name, null, fn);
			}
		}

		lit[Lit.restart] = nil;
		lit[Lit.hashtab] = new LispHash(new LispEqualHash(), 2);
		lit[Lit.banner]  = new LispString("Jlisp");

		modulus = 1;
		bigModulus = BigInteger.valueOf(modulus);
		//System.out.println("After cold start: " + oblistCount);
	}

	public static void readEvalPrintLoop(boolean noRestart) throws ProgEvent, ResourceException
	{
		// If the user had set a restart-function when an image was preserved
		// then I will run that now unless the command-line had gone "-n" (for
		// "ignore restart function". That option is only intended for allowing
		// experts to recover when an image is a bit mangled!
		LispObject r = lit[Lit.restart];
		LispObject a = null;
		//@
		//println("restart mode in read eval print loop " + restartFn + " " + restartModule + " " + restartArg);
                println("MPReduce version " + Jlisp.version);
		if (restarting && restartFn != null)
		{   r = Symbol.intern(restartFn);
			if (restartArg != null)
			{   LispObject save = lit[Lit.std_input].car/*value*/;
				try
				{   lit[Lit.std_input].car/*value*/ =
					        new LispStringReader(restartArg);
					a = LispReader.read();
					((LispStream)lit[Lit.std_input].car/*value*/).close();
				}
				catch (Exception e)
				{   a = null;
					System.out.println("Unexpected exception " + e);
				}
				finally
				{   lit[Lit.std_input].car/*value*/ = save;
				}
			}
			if (restartModule != null)
				{   try
				{   Fasl.loadModule(new LispString(restartModule));
				}
				catch (Exception ex)
				{   System.out.println("Unexpected exception " + ex);
				}
			}
			restartFn = null;
			restartArg = null;
			restartModule = null;
		}
		if (noRestart ||
		        (r instanceof Symbol &&
		         ((Symbol)r).fn instanceof Undefined) ||
		        (r instanceof Undefined) ||
		        (!r.atom && r.car != lit[Lit.lambda]) ||
		        !(r instanceof Symbol || r instanceof Cons ||
		          r instanceof LispFunction))
		{}    // cases when the restart object looks wrong
		else
			{   try
				{   if (a == null)
					{   if (r instanceof Symbol)
						((Symbol)r).fn.op0();
					else if (r instanceof LispFunction)
						((LispFunction)r).op0();
					else Fns.apply0(r);
				}
				else
					{   if (r instanceof Symbol)
						((Symbol)r).fn.op1(a);
					else if (r instanceof LispFunction)
						((LispFunction)r).op1(a);
					else Fns.apply1(r, a);
				}
			}

			catch (Exception e)
			{
				if(trapExceptions == true)
				{
					if(e instanceof ProgEvent)
					{
						throw ((ProgEvent) e);
					}
					else
					{

						// ignore all other exceptions
						System.err.println("Stopping because of error: " +
						                   e.getMessage());
					}
				}
				else
				{
					checkExit(e.getMessage());
				}
			}//end try/catch.

			return;
		}
		// Otherwise I will run a simple READ-EVAL-PRINT loop
		for (;;)
			{   try
			{   r = LispReader.read();
			}
			catch (EOFException e)
			{
				break;
			}
			catch (Exception e)
			{   errprintln(
				        "Error while reading: " + e.getMessage());
				e.printStackTrace(new PrintWriter(new WriterToLisp(
				                                          ((LispStream)Jlisp.lit[Lit.err_output].car/*value*/))));
				break;
			}
			try
			{   LispObject v = r.eval();
				if (Specfn.progEvent != Specfn.NONE)
				{   Specfn.progEvent = Specfn.NONE;
					error("GO or RETURN out of context");
				}
				println();
				print("Value: ");
				v.print(LispObject.printEscape);
				println();
			}
			catch (Exception e)
				{   if (e instanceof LispException)
				{
					if (e instanceof ProgEvent)
					{   ProgEvent ep = (ProgEvent)e;
						switch (ep.type)
						{
						case ProgEvent.PRESERVERESTART:
							lispErr.println("Preserverestart was called");
						case ProgEvent.STOP:
						case ProgEvent.PRESERVE:
						case ProgEvent.RESTART:
							throw ep;
						default:
							break;
						}
					}
					LispException e1 = (LispException)e;
					errprintln();
					errprint("+++++ Error: " + e1.message);
					if (e1.details != null)
					{   errprint(": ");
						e1.details.errPrint();
					}
					errprintln();
				}
				else
				{   errprintln();
					errprintln("+++++ Error: " +
					           e.getMessage());
				}
				e.printStackTrace(new PrintWriter(new WriterToLisp(
				                                          ((LispStream)Jlisp.lit[Lit.err_output].car/*value*/))));
			}
		}
		return;
	}//end method.
	
	
	
	

	private static void checkExit(String errorMessage)
	{
		if(trapExceptions == false)
		{
			System.out.println(errorMessage);

			try
			{
				if(transcript != null)
				{
					transcript.flush();
					transcript.close();
				}

				if(lispIO != null)
				{
                                        lispIO.print("f179eb");
					lispIO.flush();
					lispIO.close();
				}

				if(lispErr != null)
				{
					lispErr.flush();
					lispErr.close();
				}

			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			System.exit(1);


		}else
			try
			{
				if(transcript != null)
				{
					transcript.flush();
				}

				if(lispIO != null)
				{
					lispIO.flush();
				}

				if(lispErr != null)
				{
					lispErr.flush();
				}

			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		
	}//end method.


}

class FlushOutputThread extends Thread
{
	public void run()
	{
		for (;;)
			{   try
			{   sleep(2500);
			}
			catch (InterruptedException e)
			{}
			if (Jlisp.finishingUp) return;
			// The only stream that I flush regularly is the main output one, since
			// others should be directed to files (not the screen).
			if (Jlisp.lispIO != null) Jlisp.lispIO.flush();
			// Well maybe I will flush the one that is currently selected if that
			// is different...
			LispObject a = Jlisp.lit[Lit.std_output];
			if (a != null &&
			        a instanceof Symbol) a = a.car/*value*/;
			if (a != null &&
			        a != Jlisp.lispIO &&
			        a instanceof LispStream)
			{   ((LispStream)a).flush();
			}
		}
	}

}

// End of Jlisp.java

