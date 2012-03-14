# ----------------------------------------------------------------------
# $Id: mma.awk 1047 2010-12-27 09:33:12Z thomas-sturm $
# ----------------------------------------------------------------------
# Copyright (c) 2008-2010 Thomas Sturm
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
}

/^In\[2\]/ {
    f=0
    printf(";\n") > rf
}

/^Out\[1\]\/\/InputForm=/ {
    f=1
}

(f==1) {
    sub(/^Out\[1\]\/\/InputForm= /,"",$0);
    if (verb=="t" && !match($0,/^$/))
	print "+++ MATHEMATICA raw output:", $0
    for (i=1; i<=NF; i++) {
	oi = $i;
	gsub(/\&\&/," and ",$i);
	gsub(/\|\|/," or ",$i);
	gsub(/!=/,"<>",$i);
	gsub(/==/,"=",$i);
	printf("%s",$i) > rf
	if (match(oi,/[a-z0-9]+/) && i<NF && match($(i+1),/[a-z0-9]+/))
	    printf("*") > rf
    }
}

/^Out\[2]=/ && (time=="t") {
    sub(/^Out\[2\]= /,"",$0)
    print "+++ MATHEMATICA system time:", $0, "seconds"
}

END {
    printf("end;\n") > rf
}
