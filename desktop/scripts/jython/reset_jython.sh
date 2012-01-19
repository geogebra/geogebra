# This script expands jython.jar into desktop/jython/
#
# - desktop/jython needs to be added to the eclipse class path and
# desktop/lib/jython.jar needs to be removed
# 
# - then start the check_files.py script and fire up GeoGebra
#
# Author: Arnaud

# This points to the root of the desktop project.  Update it!
DESKTOP=/Users/arno/Documents/workspace/desktop

JYTHON_ROOT=$DESKTOP/jython
JYTHON_JAR=$DESKTOP/lib/jython.jar

# Create $JYTHON_ROOT if necessary
if [ ! -d $JYTHON_ROOT ]; then
    mkdir $JYTHON_ROOT
fi

# Go to $JYTHON_ROOT and empty it
cd $JYTHON_ROOT
rm -R *

# Unzip the contents of $JYTHON_ROOT into $JYTHON_JAR
jar xf $JYTHON_JAR
