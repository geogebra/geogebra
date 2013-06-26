#!/usr/bin/env python3
# -*- coding: utf-8 -*-

# This script creates the portable version for Linux (32bit & 64bit).
# @author Christian Schött <schoett@gmx.de>
# @author Zoltan Kovacs <zoltan@geogebra.org>

# argument 1: version of GeoGebra (eg. 3.2.44.0)
# argument 2: path of directory containing jre installation files (eg. jre-6u21-linux-x64.bin)
# argument 3: path of directory containing unpacked geogebra files
# argument 4: path of geogebra icon (eg. geogebra.png)
# argument 5: path of start script geogebra
# argument 6: path of destination directory

import os, shutil, sys, tarfile, tempfile
if len(sys.argv) != 7:
	print("Error: Six arguments are expected.")
	sys.exit(1)
if not os.path.exists(sys.argv[2]):
	print("Error: "+sys.argv[2]+" does not exist.")
	sys.exit(1)
if not os.path.exists(sys.argv[3]):
	print("Error: "+sys.argv[3]+" does not exist.")
	sys.exit(1)
if not os.path.exists(sys.argv[4]):
	print("Error: "+sys.argv[4]+" does not exist.")
	sys.exit(1)
if not os.path.exists(sys.argv[5]):
	print("Error: "+sys.argv[5]+" does not exist.")
	sys.exit(1)
if not os.path.exists(sys.argv[6]):
	print("Error: "+sys.argv[6]+" does not exist.")
	sys.exit(1)
geogebra_version = sys.argv[1]
java_path = os.path.abspath(sys.argv[2])
unpacked_path = os.path.abspath(sys.argv[3])
icon_path = os.path.abspath(sys.argv[4])
start_script_path = os.path.abspath(sys.argv[5])
destination_path = os.path.abspath(sys.argv[6])
if not os.path.isdir(java_path):
	print("Error: "+java_path+" is not a directory.")
	sys.exit(1)
if not os.access(java_path,os.X_OK):
	print("Error: "+java_path+" is not executable.")
	sys.exit(1)
if not os.path.isdir(unpacked_path):
	print("Error: "+unpacked_path+" is not a directory.")
	sys.exit(1)
if not os.path.isfile(icon_path):
	print("Error: "+icon_path+" is not a file.")
	sys.exit(1)
if os.path.splitext(icon_path)[1] != ".png":
	print("Error: "+icon_path+" has not the ending \".png\".")
	sys.exit(1)
if not os.access(icon_path,os.R_OK):
	print("Error: "+icon_path+" is not readable.")
	sys.exit(1)
if not os.path.isfile(start_script_path):
	print("Error: "+start_script_path+" is not a file.")
	sys.exit(1)
if not os.path.isdir(destination_path):
	print("Error: "+destination_path+" is not a directory.")
	sys.exit(1)
icon_filename = os.path.basename(icon_path)
temp_dir = tempfile.mkdtemp(dir=".")
try:
	shutil.copytree(unpacked_path, temp_dir+"/GeoGebra-Linux-Portable-"+geogebra_version+"/geogebra")
	os.chdir(temp_dir+"/GeoGebra-Linux-Portable-"+geogebra_version)
	shutil.copy(icon_path, ".")
	if not icon_filename == "geogebra.png":
		os.rename(icon_filename, "geogebra.png")
	os.chmod("geogebra.png",0o444)
	shutil.copy(start_script_path, "geogebra")
	os.chmod("geogebra/geogebra",0o755)

	for root, _, files in os.walk(java_path):
		for f in files:
			java_filename = os.path.join(root, f)
			if not ".bin" in java_filename:
				continue
			os.system("sh "+java_filename)
			content = os.listdir(os.getcwd())

			arch = "i686"
			if "linux-x64" in java_filename:
				arch = "x86_64"
			elif not "linux-i586" in java_filename:
				print("Error: Architecture can not be determined.")
				sys.exit(1)

			jre_dir = ""
			for name in content:
				if "jre" in name and not "-" in name:
					jre_dir = name
					break
			if jre_dir == "":
				print("Error: "+java_filename+" does not create expected folder.")
				sys.exit(1)
			os.rename(jre_dir, jre_dir+"-"+arch)

	with open("geogebra-portable", "w") as start_script_geogebra_portable_file:
		start_script_geogebra_portable_file_lines = ["#!/bin/bash\n",
		    "#---------------------------------------------\n",
		    "# Script to start GeoGebra-Portable\n",
		    "#---------------------------------------------\n",
		    "\n",
		    "#---------------------------------------------\n", 
		    "# Export name of this script\n",
		    "\n",
		    "export GG_SCRIPTNAME=$(basename $0)\n", 
		    "\n",
		    "#---------------------------------------------\n", 
		    "# Find out path of this script\n",
		    "\n", "GG_PATH=\"${BASH_SOURCE[0]}\"\n", 
		    "if [ -h \"${GG_PATH}\" ]; then\n", 
		    "\twhile [ -h \"${GG_PATH}\" ]; do\n", 
		    "\t\tGG_PATH=`readlink \"${GG_PATH}\"`\n", 
		    "\tdone\n", "fi\n", "pushd . > /dev/null\n", 
		    "cd `dirname ${GG_PATH}` > /dev/null\n", 
		    "GG_PATH=`pwd`\n", "popd > /dev/null\n", 
		    "\n", 
		    "#---------------------------------------------\n", 
		    "# Export Java Command\n", 
		    "\n",
		    "ARCH=`arch`\n",
		    "export JAVACMD=\"$GG_PATH/"+jre_dir+"-$ARCH/bin/java\"\n", 
		    "\n", 
		    "#---------------------------------------------\n", 
		    "# Export path of directory containing .$GG_SCRIPTNAME/geogebra.conf\n", 
		    "\n", "export GG_CONFIG_PATH=\"$GG_PATH\"\n", 
		    "\n", "#---------------------------------------------\n", 
		    "# Run\n", 
		    "\n", 
		    "exec \"$GG_PATH/geogebra/geogebra\" --settingsfile=\"$GG_PATH/geogebra.properties\" \"$@\"\n"]
		start_script_geogebra_portable_file.writelines(start_script_geogebra_portable_file_lines)
	os.chmod("geogebra-portable",0o755)
	with open("readme.txt", "w") as readme_file:
		readme_file_lines = ["To start GeoGebra-Portable, run geogebra-portable.\n",
		    "Use option \"--help\" for more information.\n"]
		readme_file.writelines(readme_file_lines)
	os.chdir("../")
	tar_bz2_file = tarfile.open(destination_path+"/GeoGebra-Linux-Portable-"+geogebra_version+".tar.bz2", "w:bz2")
	try:
		tar_bz2_file.add("GeoGebra-Linux-Portable-"+geogebra_version)
	finally:
		tar_bz2_file.close()
finally:
	os.chdir("../")
	shutil.rmtree(temp_dir)
sys.exit(0)
