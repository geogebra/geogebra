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
files accessed, send me the file called "included_paths.json".

Author: Arnaud
"""

import os, sys, json, urllib, urllib2
from time import time, sleep
from datetime import datetime
from getpass import getuser

save_filename = "files.json"
jython_root = "../../jython"
send_url = "http://geogebra.idm.jku.at/ggbtrans/jcollect/add/"
send_timeout = 10
start_time = time()
user = getuser()
platform = sys.platform

def load_data():
    try:
        with open(save_filename, "rb") as f:
            data, pending = json.loads(f.read())
            data = dict((k, set(v)) for k, v in data.iteritems())
            return data, pending
    except IOError:
        return {}, []

def save_data(data, pending):
    with open(save_filename, "wb") as f:
        data = dict((k, list(v)) for k, v in data.iteritems())
        f.write(json.dumps([data, pending]))

def send_data(pending):
    post_data = urllib.urlencode([
        ('user', user),
        ('platform', platform),
        ('data', json.dumps(pending)),
    ])
    response = urllib2.urlopen(send_url, post_data)
    return json.loads(response.read())

protected_dirs = ['jni']

#    'antlr', 'core', 'compiler', 'modules', 'kenai',
#    'constantine', 'posix', 'util', 'jsr223', 'jni',
#    'objectweb']

def force_include(root):
    return any('/' + d in root for d in protected_dirs)

def update_data(data, pending):
    new_files = 0
    for root, dirs, files in os.walk(jython_root):
        force = force_include(root)
        norm_root = root[len(jython_root) + 1:].replace("\\", "/")
        for name in files:
            path = os.path.join(root, name)
            stats = os.stat(path)
            if force or stats.st_atime >= start_time:
                included_names = data.setdefault(root, set())
                if name not in included_names:
                    new_files += 1
                    included_names.add(name)
                    norm_path = "%s/%s" % (norm_root, name)
                    pending.append(norm_path)
    return new_files

def calc_size(data):
    size = 0
    for root, files in data.iteritems():
        for name in files:
            path = os.path.join(root, name)
            stats = os.stat(path)
            size += stats.st_size
    return size / 2.0**20

def list_all(data):
    for root, files in data.iteritems():
        print root
        for name in files:
            print "    +", name

def delete_unused(data):
    for root, dirs, files in os.walk(jython_root):
        files_to_keep = data.get(root, set())
        for name in set(files) - files_to_keep:
            path = os.path.join(root, name)
            print "-", path
            os.remove(path)

if __name__ == "__main__":
    data, pending = load_data()
    should_save = False
    start_time = time()
    print "="*60
    print "Please use Python Scripting in GeoGebra..."
    print "Press Ctrl-C to quit"
    print "Only quit after you've stopped using GeoGebra!"
    print "="*60
    print "Data will be sent with the following information:"
    print "User:", user
    print "Platform:", platform
    print "="*60
    try:
        while True:
            sleep(5)
            sys.stdout.write(".")
            sys.stdout.flush()
            new_files = update_data(data, pending)
            if new_files:
                print
                print new_files, "new local files.",
                print "New size :", calc_size(data)
                should_save = True
            if pending:
                try:
                    print "\nSending data...",
                    res = send_data(pending)
                    print "Done - %s new files on server" % res["count"]
                    pending = []
                    should_save = True
                except urllib2.HTTPError, e:
                    print "HTTP Error ", e.code
                except urllib2.URLError, e:
                    print "Unable to send:", e.reason
            if should_save:
                save_data(data, pending)
                should_save = False
    except KeyboardInterrupt:
        print "\n\nThank you. Bye!\n"
        
