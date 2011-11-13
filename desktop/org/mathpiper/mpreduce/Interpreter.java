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

package org.mathpiper.mpreduce;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 *
 *
 */
public class Interpreter {

    Jlisp jlisp;
    private static Interpreter JlispCASInstance = null;
    private StringBuffer responseBuffer;
    private Pattern inputPromptPattern;
    private PipedInputStream myInputStream;
    private PipedOutputStream myOutputStream;
    private String response;
    private String startMessage;
    private String prompt;
    private Thread reduceThread;
    private boolean evaluationHalted = false;


    public Interpreter() {



        jlisp = new Jlisp();

        try {

            myOutputStream = new PipedOutputStream();
            myInputStream = new PipedInputStream();

            final PipedOutputStream jLispOutputStream = new PipedOutputStream(myInputStream);
            final PipedInputStream jLispInputStream = new PipedInputStream(myOutputStream);

            //myOutputStream.connect(jLispInputStream);
            //myInputStream.connect(jLispOutputStream);



            final String[] args = new String[0];

            reduceThread = new Thread(new Runnable() {

                public void run() {
                    try {
                        jlisp.startup(args,
                                new InputStreamReader(new BufferedInputStream(jLispInputStream)),
                                new PrintWriter(new BufferedOutputStream(jLispOutputStream)),
                                true);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

            });

            reduceThread.setName("MPReduce");

            reduceThread.start();


            responseBuffer = new StringBuffer();
            inputPromptPattern = Pattern.compile("\n*[0-9]+\\:");


            startMessage = getResponse();


            //Initialize MPReduce.
            evaluate("symbolic procedure update!_prompt; begin setpchar \"f179eb\" end;;");
            inputPromptPattern = Pattern.compile("\n*(f179eb)+");
            getResponse();


            evaluate("off int; on errcont; off nat;");
            String switchSetResponce = getResponse();


        } catch (Throwable t) {
            t.printStackTrace();

        }



    }//end constructor.


    public String getStartMessage() {
        return startMessage;
    }//end method.


    public String getPrompt() {
        return prompt;
    }//end method.


    public static Interpreter getInstance() throws Throwable {
        if (JlispCASInstance == null) {
            JlispCASInstance = new Interpreter();
        }
        return JlispCASInstance;
    }//end method.


    public synchronized void evaluate(String send) throws Throwable {

        send = send.trim();

        if(((send.endsWith(";")) || (send.endsWith("$"))) != true)
        {
            send = send + ";\n"; 
        }

        while(send.endsWith(";;"))
        {
            send = send.substring(0,send.length()-1);
        }

        while(send.endsWith("$"))
        {
            send = send.substring(0,send.length()-1);
        }

        send = send + "\n";

        myOutputStream.write(send.getBytes());
        myOutputStream.flush();
        

    }//end evaluate.


    public void interruptEvaluation() {
        try {
            evaluate(""); //Needed to make sure the next evaluation after the interruption works okay.

            jlisp.interruptEvaluation = true;

            evaluationHalted = true;
        } catch (Throwable e) {
            //Each excpetion.
        }
    }


    public String getResponse() throws Throwable {
        boolean keepChecking = true;

        mainLoop:
        while (keepChecking) {
            int serialAvailable = myInputStream.available();
            if (serialAvailable == 0) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ie) {
                    System.out.println("Jlisp session interrupted.");
                }
                continue mainLoop;
            }//end while

            byte[] bytes = new byte[serialAvailable];

            myInputStream.read(bytes, 0, serialAvailable);
            responseBuffer.append(new String(bytes));
            response = responseBuffer.toString();

            //Check for an error response.
            if(response.indexOf("*****") != -1)
            {
                responseBuffer.delete(0, responseBuffer.length());
                //response = response.trim();
                keepChecking = false;
            }

            //System.out.println("SSSSS " + response);
            Matcher matcher = inputPromptPattern.matcher(response);
            if (matcher.find()) {
                //System.out.println("PPPPPP found end");

                responseBuffer.delete(0, responseBuffer.length());

                int promptIndex = matcher.start();

                prompt = response.substring(promptIndex, response.length()).trim();
                response = response.substring(0, promptIndex);

                //response = response.trim();



                keepChecking = false;

            }//end if.

        }//end while.


        //Obtain the exceptin message from the input stream.
        if (this.evaluationHalted == true) { 
            int serialAvailable;
            while ((serialAvailable = myInputStream.available()) != 0) {
                byte[] bytes = new byte[serialAvailable];
                myInputStream.read(bytes, 0, serialAvailable);
                response = response + new String(bytes);
            }
            evaluationHalted = false;
        }

        if(! response.endsWith("$"))
        {
            response = response + "$";
        }

        return response;

    }//end method


    public static void main(String[] args) {
        Interpreter mpreduce = new Interpreter();

        String result = "";

        try {

            mpreduce.evaluate("off nat;");
            result = mpreduce.getResponse();
            System.out.println(result + "\n");

            mpreduce.evaluate("x^2;");
            result = mpreduce.getResponse();
            System.out.println(result + "\n");

            //An example which shows how to interrupt an evaluation.
            mpreduce.evaluate("(X-Y)^100;");
            Thread.sleep(100);
            System.out.println("Interrupting mpreduce evaluation.");
            mpreduce.interruptEvaluation();
            result = mpreduce.getResponse();
            System.out.println(result+ "\n");


            mpreduce.evaluate("2 + 2;");
            result = mpreduce.getResponse();
            System.out.println(result+ "\n");


            mpreduce.evaluate("Factorize(100);");
            result = mpreduce.getResponse();
            System.out.println(result+ "\n");

        } catch (Throwable t) {
            t.printStackTrace();
        }



    }

}//end class.


