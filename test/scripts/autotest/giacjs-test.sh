#!/bin/sh

# Runs all giac tests in Javascript. It uses Mike's Javascript testing suite.
# Command-line headless run of the tests are provided by PhantomJS.
# See also giacjs-test.js.

# Here we use a recent version (>=1.9). Unfortunately we do not get the results
# in the simplest form from PhantomJS, so we need to refine the <TABLE>
# DOM object by using another external tool, xmlstarlet.
# Output: giacjs-test.sql which is a list of SQLite3 insert commands.
# They are also inserted into the ../../sqlite3db database at the end.

# Call this script in ./junit-tests before running ../../warnings to make sure
# that the giac Javascript entries will be put into the database.

# @author Zoltan Kovacs <zoltan@geogebra.org>

test -r autotest.conf && . ./autotest.conf

# 0. You may fine tune the output here
PHANTOMJS=phantomjs-1.9
TESTJS=giacjs-test.js
TIMEOUT_SEC=60
# URL=http://www.geogebra.org/web/giac/CASUnitTests.html #?_start=1&_end=10
URL="$WEBTESTPROTOCOL://$WEBTESTSERVER:$WEBTESTPORT/$WEBTESTWARDIR/CASUnitTests.html?_start=1" # &_end=10
MYNAME=giacjs-test
SQLFILE=giacjs-test.sql

# 1. Testing prerequisities
XML=`which xml`
if [ "$XML" = "" ]; then
 XML=`which xmlstarlet`
 fi
if [ "$XML" = "" ]; then
 echo "No XMLStarlet executable found on path"
 exit 11
 fi

SQLITE3=`which sqlite3`
if [ "$SQLITE3" = "" ]; then
 echo "No sqlite3 executable found on path"
 exit 12
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
REVISION=`./myrevision`

# 3. Resetting output
rm -f $SQLFILE

# 4. Running PhantomJS
$PHANTOMJS $TESTJS $URL $TIMEOUT_SEC

# 5. Processing HTML output from PhantomJS

# We need to remove <BR>s since xmlstarlet will complain about the HTML.
# Maybe there is a way to convince xmlstarlet that <BR> is a proper HTML tag without </BR>.
cat $FATALRAW | sed s/"<br>"/". "/g > $FATAL
cat $OUTRAW | sed s/"<br>"/". "/g > $OUT

# classname, name, message, type, revision, error
# -----------------------------------------------
# giacjs-test, 8: Factor(x^2-1), ..., FATAL, revision, 1 for fatal (0 otherwise)

create_sql() {
 FILE=$1
 TYPE=$2
 ERROR=$3
 N=`cat $FILE | $XML sel -t -v 'count(/table/tbody/tr)'`
 for i in `seq 1 $N`; do
  CLASSNAME=$MYNAME
  # Apostrophes will be doubled for SQLite3 compliance.
  NAME=`cat $FILE | $XML sel -t -v /table/tbody/tr[$i]/td[1] | sed s/\'/\'\'/g`
  GOT=`cat $FILE | $XML sel -t -v /table/tbody/tr[$i]/td[2] | sed s/\'/\'\'/g`
  EXPECTED=`cat $FILE | $XML sel -t -v /table/tbody/tr[$i]/td[3] | sed s/\'/\'\'/g`
  REMARK=`cat $FILE | $XML sel -t -v /table/tbody/tr[$i]/td[4] | sed s/\'/\'\'/g`
  MESSAGE="Expected: $EXPECTED got: $GOT. $REMARK"

  echo "insert into names (id) values '$MYNAME';" >> $SQLFILE
  echo "insert into tests (classname, name, message, type, revision, error) values " >> $SQLFILE
  echo " ('$CLASSNAME', '$NAME', '$MESSAGE', '$TYPE', $REVISION, $ERROR);" >> $SQLFILE
  done
 }

# 6. Create SQL inserts
create_sql $FATAL FATAL 1
create_sql $OUT ERROR 0

# 7. Adding inserts into database
cat $SQLFILE | $SQLITE ../../sqlite3db
