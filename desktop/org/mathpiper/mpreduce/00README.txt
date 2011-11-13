                                  Jlisp
                                  =====



This is in effect a clone of CSL coded in Java. The Makefile illustrates
how to build a version of reduce using it. This is set up to be done
in-place within the Reeuce tree. When you look at Makefile you will see
that the procedure is rather simple.

(a) Compile all the java code, as in "javac *.java"
(b) Run the resulting code "java Jlisp ..." to build a file that
    contains a checkpointed memory-image with all of Reduce built into
    it. This will be called "reduce.img"
(c) Collect all the Java class files plus reduce.img (renamed to as
    "default.img" into a .jar file, "reduce.jar".

That can then be run by going
      java -jar reduce.jar   ... options ...

An option "-w" instructs it to run as a non-windowed console application.
In general the options show be as for csl.

The file "reduce.jar" represents a complete Reduce with all current packages.
It is around 3.3 Mbytes in size.

The current version is known to have some bugs and glitches. In particular
there are some failures visible in the log file from building Reduce and so
some of the packages may be incomplete. The problems are probably because
Jlisp does not yet implement some feature of full CSL and so needs (probably
minor) updating.


              Arthur Norman
              February 2011
