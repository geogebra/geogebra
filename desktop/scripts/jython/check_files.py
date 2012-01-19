#!/usr/bin/env python

"""
This scripts creates or updates a file called "included_paths.json"
which contains all the paths to files in desktop/jython which are
accessed when GeoGebra is running (this only happens when working
with Python scripting).

The directory desktop/jython must be populated first.  This can be
done with the "reset_jython.sh" script in this directory.

The script, when started, monitors desktop/jython for access to files
within it. Every file which is accessed is recorded.  The idea is to
start the script, start GeoGebra and test Python scripting.  If the
script's output doesn't show anything, it means that it hasn't
detected access to a new file.

Play with it for a while and when it seems that there aren't any new
files accessed, send me the file called"included_paths.json".

Author: Arnaud
"""

import os
from time import time, sleep
from datetime import datetime
import json

jython_root = "../../jython"

def load_data():
    global start_time, included_paths
    try:
        with open("included_paths.json", "rb") as f:
            start_time, serializable = json.loads(f.read())
            included_paths = dict((k, set(v)) for k, v in serializable.iteritems())
    except IOError:
        start_time = time()
        included_paths = {}

def save_data():
    with open("included_paths.json", "wb") as f:
        serializable = dict((k, list(v)) for k, v in included_paths.iteritems())
        f.write(json.dumps((start_time, serializable)))

protected_dirs = ['jni']

#    'antlr', 'core', 'compiler', 'modules', 'kenai',
#    'constantine', 'posix', 'util', 'jsr223', 'jni',
#    'objectweb']

def force_include(root):
    return any('/' + d in root for d in protected_dirs)

def update_included():
    new_files = 0
    for root, dirs, files in os.walk(jython_root):
        force = force_include(root)
        for name in files:
            path = os.path.join(root, name)
            stats = os.stat(path)
            if force or stats.st_atime >= start_time:
                included_names = included_paths.setdefault(root, set())
                if name not in included_names:
                    included_names.add(name)
                    print "+", path
                    new_files += 1
    return new_files

def calc_size():
    size = 0
    for root, files in included_paths.iteritems():
        for name in files:
            path = os.path.join(root, name)
            stats = os.stat(path)
            size += stats.st_size
    return size / 2.0**20

def list_all():
    for root, files in included_paths.iteritems():
        print root
        for name in files:
            print "    +", name

def delete_unused():
    for root, dirs, files in os.walk(jython_root):
        files_to_keep = included_paths.get(root, set())
        for name in set(files) - files_to_keep:
            path = os.path.join(root, name)
            print "-", path
            os.remove(path)

if __name__ == "__main__":
    load_data()
    start_time = time()
    print "Please use Python Scripting in GeoGebra..."
    print "Press Ctrl-C (or I think Ctrl-Z on Windows) to quit"
    print "Only quit after you've stopped using GeoGebra!"
    while True:
        sleep(5)
        print datetime.now().ctime()
        new_files = update_included()
        if new_files:
            print new_files, "new files.  New size :", calc_size()
            save_data()
