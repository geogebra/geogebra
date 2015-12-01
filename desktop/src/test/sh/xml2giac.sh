#!/bin/sh

# Exports giac calls from a JUnit output file,
# e.g. TEST-org.geogebra.cas.GeoGebraCasIntegrationTest.xml,
# this takes place in desktop/build/test-results/ after
# running the JUnit tests.

if [ "$#" != 2 ]; then
 echo "Usage: $0 <input.xml> <output.txt>"
 exit 1
 fi
test -f $1 || exit 2
cat $1 | grep "giac input:" | awk '{
 i=index($0,"giac input:")
 q=substr($0,i+12)
 if (p!=q) print q
 p=q
 }' > $2
