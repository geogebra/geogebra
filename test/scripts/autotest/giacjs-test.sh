#!/bin/sh

# Runs all giac tests in Javascript. It uses Mike's Javascript testing suite.
# Command-line headless run of the tests are provided by PhantomJS.
# See also giacjs-test.js.

# Here we use a recent version (>=1.9). Unfortunately we do not get the results
# in the simplest form from PhantomJS, so we need to refine the <TABLE>
# DOM object by using another external tool, xmlstarlet.
# Output: giacjs-test.txt.

# @author Zoltan Kovacs <zoltan@geogebra.org>

test -r autotest.conf && . ./autotest.conf

# 0. You may fine tune the output here
PHANTOMJS=phantomjs-2.0.0
TESTJS=giacjs-test.js
TIMEOUT_SEC=60
MYNAME=giacjs-test
OUTPUTFILE=giacjs-test.txt
LIMIT=100
GIACJS=../../../web/war/giacTests.js

# Set the number of tests here
JSONSTART=`cat $GIACJS | nl | grep JSONSTART | awk '{print $1}'`
JSONEND=`cat $GIACJS | nl | grep JSONEND | awk '{print $1}'`
NOTESTS=$((JSONEND-JSONSTART+1))
echo "Assuming $NOTESTS tests"

# 1. Testing prerequisities
XML=`which xml`
if [ "$XML" = "" ]; then
 XML=`which xmlstarlet`
 fi
if [ "$XML" = "" ]; then
 echo "No XMLStarlet executable found on path"
 exit 11
 fi

which $PHANTOMJS >/dev/null || {
 echo "$PHANTOMJS is missing"
 exit 13
 }

# 2. Setting extra variables
FATALRAW=fatal.html
FATAL=fatal-edited.html
OUTRAW=out.html
OUT=out-edited.html

# 3. Resetting output
rm -f $OUTPUTFILE

create_txt() {
 FILE=$1
 SIZE=`ls -l $FILE | awk '{print $5}'`
 if [ $SIZE = 1 ]; then
  return
  fi
 N=`cat $FILE | $XML sel -t -v 'count(/table/tbody/tr)'`
 for i in `seq 1 $N`; do
  CLASSNAME=$MYNAME
  # Apostrophes will be doubled for former SQLite3 compliance.
  NAME=`cat $FILE | $XML sel -t -v /table/tbody/tr[$i]/td[1] | sed s/\'/\'\'/g`
  # Sometimes ". NNNNms" is appended to the name which must be removed:
  NAME=`echo "$NAME" | sed s/"\. [0-9]\+ms"//`
  GOT=`cat $FILE | $XML sel -t -v /table/tbody/tr[$i]/td[2] | sed s/\'/\'\'/g`
  EXPECTED=`cat $FILE | $XML sel -t -v /table/tbody/tr[$i]/td[3] | sed s/\'/\'\'/g`
  REMARK=`cat $FILE | $XML sel -t -v /table/tbody/tr[$i]/td[4] | sed s/\'/\'\'/g`
  MESSAGE="Expected: $EXPECTED got: $GOT. $REMARK"

  echo "$NAME|$MESSAGE" >> $OUTPUTFILE
  done
 }

# 4. Running PhantomJS
START=1
while [ $START -lt $NOTESTS ]; do
 END=$((START+$LIMIT-1))
 if [ $END -gt $NOTESTS ]; then
  END=$NOTESTS
  fi
 echo "Processing tests $START..$END..."
 $PHANTOMJS --ignore-ssl-errors=true $TESTJS $START $END $TIMEOUT_SEC

 # 5. Processing HTML output from PhantomJS

 # We need to remove <BR>s since xmlstarlet will complain about the HTML.
 # Maybe there is a way to convince xmlstarlet that <BR> is a proper HTML tag without </BR>.
 cat $FATALRAW | sed s/"<br>"/". "/g > $FATAL
 cat $OUTRAW | sed s/"<br>"/". "/g > $OUT

 # 6. Create output
 create_txt $FATAL
 create_txt $OUT

 START=$(($END+1))

 done
