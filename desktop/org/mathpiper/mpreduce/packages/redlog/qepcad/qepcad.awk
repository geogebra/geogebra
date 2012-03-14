# ----------------------------------------------------------------------
# $Id: qepcad.awk 1047 2010-12-27 09:33:12Z thomas-sturm $
# ----------------------------------------------------------------------
# Copyright (c) 1995-2009 A. Dolzmann and T. Sturm, 2010 T. Sturm
# ----------------------------------------------------------------------
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions
# are met:
#
#    * Redistributions of source code must retain the relevant
#      copyright notice, this list of conditions and the following
#      disclaimer.
#    * Redistributions in binary form must reproduce the above
#      copyright notice, this list of conditions and the following
#      disclaimer in the documentation and/or other materials provided
#      with the distribution.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
# "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
# LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
# A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
# OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
# SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
# LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
# DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
# THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
# (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
# OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
#

BEGIN {
    time=tolower(time)
    verb=tolower(verb)
    slfqvb=tolower(slfqvb)
}

(slfqvb=="t") {
    print "+", $0
}

/^In other words/ {
    f=0
}

/^==============================/ {
    f=0
}

/The End/ {
    f=0
}

/There were/ {
    f=0
}

(f==1) {
    if (verb=="t" && !match($0,/^$/) && (slfqvb=="nil"))
	print "+++", name, "raw output:", $0
    for (i=1; i<=NF; i++) {
	oi = $i
	gsub(/\\\//," or ",$i)
	gsub(/\/\\/," and ",$i)
	gsub(/\[/,"(",$i)
	gsub(/\]/,")",$i)
	gsub(/\/=/,"<>",$i)
	printf("%s",$i) > rf
	if (match(oi,/[a-z0-9]+/) && i<NF && match($(i+1),/[a-z0-9]+/))
	    printf("*") > rf
    }
    printf("\n") > rf
}

/^System time/ && (time=="t") && (slfqvb=="nil") {
    print "+++", name, $0
}

/^An equivalent/ {
    f=1
}

/^\*\*/ && (slfqvb=="nil") {
    print ""
    print
}

/WARNING/ && (slfqvb=="nil") {
    print
}

/^Failure occurred in:/ && (slfqvb=="nil") {
    print ""
    print
}

/^Reason for the failure:/ && (slfqvb=="nil") {
    print
}

END {
    printf("; end;\n") > rf
}

{
    fflush(stdout)
}
