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
package org.mathpiper.ui.text.consoles;

//import org.mathpiper.lisp.UtilityFunctions;

import java.io.*;
import org.mathpiper.Version;
import org.mathpiper.interpreters.EvaluationResponse;
import org.mathpiper.interpreters.Interpreter;
import org.mathpiper.interpreters.Interpreters;


/**
 * Provides a command line console which can be used to interact with a mathpiper instance.
 */
public class Console {

    private Interpreter interpreter;
    private boolean suppressOutput = false;


    public Console() {
        //MathPiper needs an output stream to send "side effect" output to.
        //StandardFileOutputStream stdoutput = new StandardFileOutputStream(System.out);
        interpreter = Interpreters.getSynchronousInterpreter();
    }


    void addDirectory(String directory) {
        interpreter.addScriptsDirectory(directory);
    }


    String readLine(InputStreamReader aStream) {
        StringBuffer line = new StringBuffer();
        try {
            int c = aStream.read();
            while (c != '\n') {
                line.append((char) c);
                c = aStream.read();
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return line.toString();
    }


    String evaluate(String input) {
        //return (String) interpreter.evaluate(input);
        EvaluationResponse response = interpreter.evaluate(input, true);

        String responseString = "";

        if (suppressOutput == false) {
            responseString = "Result> " + response.getResult() + "\n";
        } else {
            responseString = "Result> " + "OUTPUT SUPPRESSED\n";
            this.suppressOutput = false;
        }


        if (!response.getSideEffects().equalsIgnoreCase("")) {
            responseString = responseString + "Side Effects>\n" + response.getSideEffects() + "\n";
        }

        if (!response.getExceptionMessage().equalsIgnoreCase("")) {
            responseString = responseString + response.getExceptionMessage() + " Source file name: " + response.getSourceFileName() + ", Near line number: " + response.getLineNumber() + "\n";
        }
        else if (response.getException() != null)
        {
            response.getException().printStackTrace();
        }


        return responseString;
    }//end evaluate.


    /**
     * A Read Evaluate Print Loop for implementing text consoles.
     *
     * @param in console input.
     * @param out console output.
     */
    public void repl(InputStream inputStream, PrintStream out) {
        out.println("\nMathPiper version '" + Version.version + "'.");
        out.println("See http://mathpiper.org for more information and documentation on MathPiper.");
        out.println("Place a backslash at the end of a line to enter multiline input.");
        out.println("To exit MathPiper, enter \"Exit()\" or \"exit\" or \"quit\" or Ctrl-c.\n");
        /*TODO fixme
        System.out.println("Type ?? for help. Or type ?function for help on a function.\n");
        System.out.println("Type 'restart' to restart MathPiper.\n");
         */
        //out.println("To see example commands, keep typing Example()\n");

        //piper.Evaluate("BubbleSort(N(PSolve(x^3-3*x^2+2*x,x)), \"<\");");

        boolean quitting = false;
        String oneOrMoreLineInput = "";
        String input;
        while (!quitting) {
            out.print("In> ");
            input = readLine(new InputStreamReader(inputStream));
            input = input.trim();

            if (input.endsWith("\\")) {
                oneOrMoreLineInput += input.substring(0, input.length() - 1);
                continue;
            } else {
                oneOrMoreLineInput += input;
            }

            oneOrMoreLineInput = oneOrMoreLineInput.trim();

            if(oneOrMoreLineInput.endsWith(";;"))
            {
                this.suppressOutput = true;
            }
            String responseString = evaluate(oneOrMoreLineInput);

            oneOrMoreLineInput = "";

            out.println(responseString);

            if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {

                quitting = true;
            }
        }
    }//end repl.


    /**
     * The normal entry point for running mathpiper from a command line.  It processes command line arguments,
     * sets mathpiper's standard output to System.out, then enters a REPL (Read, Evaluate, Print Loop).  Currently,
     * the console only supports the --rootdir and --archive command line options.
     *
     * @param argv
     */
    public static void main(String[] argv) {
        Console console = new Console();
        String defaultDirectory = null;
        String archive = null;



        int i = 0;
        while (i < argv.length) {
            if (argv[i].equals("--rootdir")) {
                i++;
                defaultDirectory = argv[i];
            }
            if (argv[i].equals("--archive")) {
                i++;
                archive = argv[i];
            } else {
                break;
            }
            i++;
        }

        //Change the default directory. tk.
        if (defaultDirectory != null) {
            console.addDirectory(defaultDirectory);
        }

        if (i < argv.length) {
            for (; i < argv.length; ++i) {
                String cmd = "LoadScript(\"".concat(argv[i]).concat("\");");
                System.out.println(console.evaluate(cmd));
            }
        } else {
            console.repl(System.in, System.out);
        }



    }//end main.
}

