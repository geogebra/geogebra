# Makefile for "Jlisp"
# Now using a reasonably standard "make" syntax.


all:	reduce.jar minireduce.jar jlisp.jar

.PHONY:	clean
clean:
	-rm -rf *.jar *.class *.img *.log *.bak *~

# The idea here is that the jar file contains EVERYTHING needed for
# Reduce, and so "java -jar reduce.jar" will launch the system with its
# default image file.

reduce.jar:	*.java jlisp_manifest reduce.img
	javac *.java
	cp reduce.img default.img
	-rm -f reduce.jar
	jar cmf jlisp_manifest reduce.jar *.class default.img
	rm default.img

# minireduce.jar is a cut-down system not containing all the Reduce modules.
# It will thus be smaller (albeit less capable of performing advanced
# operations).

minireduce.jar:	*.java jlisp_manifest minireduce.img
	javac *.java
	cp minireduce.img default.img
	-rm -f minireduce.jar
	jar cmf jlisp_manifest minireduce.jar *.class default.img
	rm default.img

# jlisp.jar is merely the Lisp system without any algebra. If may sometimes
# be easier to work with this smaller system while developing or interfacing?

jlisp.jar:	*.java jlisp_manifest jlisp.img
	javac *.java
	cp jlisp.img default.img
	-rm -f jlisp.jar
	jar cmf jlisp_manifest jlisp.jar *.class default.img
	rm default.img


JLISP   = java -jar jlisp.jar
MEM=300M
STK=8M
REDUCE	= java -Xmx$(MEM) -Xms$(MEM) -Xss$(STK) -jar reduce.jar


###########################################################################

C = ../cslbase

# I make the targets here .PHONY so that (eg) "make reduce.img" always
# rebuilds the image even though I have not got careful dependencies set up
# to make it clear why this is necessary.

.PHONY:	jlisp.img
jlisp.img:
	javac *.java
	-rm -f jlisp.img
	java Jlisp -w -v -z -o jlisp.img $(C)/buildcsl.lsp \
		-D@cslbase="$(C)" -- jlispimg.log



.PHONY:	reduce.img
reduce.img:
	javac *.java
	-rm -f reduce.img
	java Jlisp -w -v -z -o reduce.img $(C)/buildreduce.lsp \
		-D@srcdir="$(C)" -D@reduce="$(C)/../.." -- reduce.log


.PHONY:	minireduce.img
minireduce.img:
	javac *.java
	-rm -f minireduce.img
	java Jlisp -w -v -z -o minireduce.img $(C)/buildreduce.lsp \
		-Dminireduce -D@srcdir="$(C)" -D@reduce="$(C)/../.." -- reduce.log


.PHONY:	testall
testall:
	mkdir -p testlogs
	$(REDUCE) -w $(C)/testall.red -D@srcdir=$(C) -D@reduce="$(C)/../.." -- testlogs/testreduce.log
	$(REDUCE) -w $(C)/checkall.red -D@srcdir=$(C) -D@reduce="$(C)/../.." -- testlogs/checkall.log

.PHONY:	test1
# Use:    make test1 which=modulename
test1:
	mkdir -p testlogs
	$(REDUCE) -w -v $(C)/testall.red -D@srcdir=$(C) -D@reduce="$(C)/../.." -Dwhich=$(which) -- testlogs/test$(which).log

.PHONY:	debug1
# Use:    make debug1 which=modulename
#         as test1 but passes the "-g" flag that makes all backtraces noisy.
debug1:
	mkdir -p testlogs
	$(REDUCE) -w -v -g $(C)/testall.red -D@srcdir=$(C) -D@reduce="$(C)/../.." -Dwhich=$(which) -- testlogs/test$(which).log

.PHONY:	checkall
checkall:
	mkdir -p testlogs
	$(REDUCE) -w $(C)/checkall.red -D@srcdir=$(C) -D@reduce="$(C)/../.." -- testlogs/checkall.log

# end of Makefile
