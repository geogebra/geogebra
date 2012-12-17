#!/usr/bin/env python3
# -*- coding: utf-8 -*-

# This script creates the generic version for Linux.
# @author Christian Schött <schoett@gmx.de> and Zoltan Kovacs <zoltan@geogebra.org>

# argument 1: version of GeoGebra (eg. 3.2.44.0)
# argument 2: path of directory containing unpacked geogebra files
# argument 3: path of start script geogebra
# argument 4: path of file license.txt
# argument 5: path of file geogebra.xml
# argument 6: path of file geogebra.desktop
# argument 7: path of file GeoGebra_hicolor_icons.tar.gz
# argument 8: path of file install.sh
# argument 9: path of file uninstall.sh
# argument 10: path of file install-sh
# argument 11: path of file README
# argument 12: path of destination directory

import os, shutil, sys, tarfile, tempfile
if len(sys.argv) != 13:
	print("Error: 12 arguments are expected.")
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
if not os.path.exists(sys.argv[7]):
	print("Error: "+sys.argv[7]+" does not exist.")
	sys.exit(1)
if not os.path.exists(sys.argv[8]):
	print("Error: "+sys.argv[8]+" does not exist.")
	sys.exit(1)
if not os.path.exists(sys.argv[9]):
	print("Error: "+sys.argv[9]+" does not exist.")
	sys.exit(1)
if not os.path.exists(sys.argv[10]):
	print("Error: "+sys.argv[10]+" does not exist.")
	sys.exit(1)
if not os.path.exists(sys.argv[11]):
	print("Error: "+sys.argv[11]+" does not exist.")
	sys.exit(1)
if not os.path.exists(sys.argv[12]):
	print("Error: "+sys.argv[12]+" does not exist.")
	sys.exit(1)
geogebra_version = sys.argv[1]
unpacked_path = os.path.abspath(sys.argv[2])
start_script_path = os.path.abspath(sys.argv[3])
license_txt_path = os.path.abspath(sys.argv[4])
geogebra_xml_path = os.path.abspath(sys.argv[5])
geogebra_desktop_path = os.path.abspath(sys.argv[6])
icons_tar_gz_file_path = os.path.abspath(sys.argv[7])
install_path = os.path.abspath(sys.argv[8])
uninstall_path = os.path.abspath(sys.argv[9])
install_sh_path = os.path.abspath(sys.argv[10])
readme_path = os.path.abspath(sys.argv[11])
destination_path = os.path.abspath(sys.argv[12])
if not os.path.isdir(unpacked_path):
	print("Error: "+unpacked_path+" is not a directory.")
	sys.exit(1)
if not os.path.isfile(start_script_path):
	print("Error: "+start_script_path+" is not a file.")
	sys.exit(1)
if not os.path.isfile(license_txt_path):
	print("Error: "+license_txt_path+" is not a file.")
	sys.exit(1)
if not os.path.isfile(geogebra_xml_path):
	print("Error: "+geogebra_xml_path+" is not a file.")
	sys.exit(1)
if not os.path.isfile(geogebra_desktop_path):
	print("Error: "+geogebra_desktop_path+" is not a file.")
	sys.exit(1)
if not os.path.isfile(icons_tar_gz_file_path):
	print("Error: "+icons_tar_gz_file_path+" is not a file.")
	sys.exit(1)
if not os.path.isfile(install_path):
	print("Error: "+install_path+" is not a file.")
	sys.exit(1)
if not os.path.isfile(uninstall_path):
	print("Error: "+uninstall_path+" is not a file.")
	sys.exit(1)
if not os.path.isfile(install_sh_path):
	print("Error: "+install_sh_path+" is not a file.")
	sys.exit(1)
if not os.path.isfile(readme_path):
	print("Error: "+readme_path+" is not a file.")
	sys.exit(1)
if not os.path.isdir(destination_path):
	print("Error: "+destination_path+" is not a directory.")
	sys.exit(1)
temp_dir = tempfile.mkdtemp()
try:
	os.chdir(temp_dir)
	os.mkdir("geogebra-"+geogebra_version)
	for element in os.listdir(unpacked_path):
		if os.path.isfile(unpacked_path+"/"+element) and os.path.splitext(unpacked_path+"/"+element)[1] == ".jar":
			shutil.copy(unpacked_path+"/"+element, "geogebra-"+geogebra_version)
	os.chdir("geogebra-"+geogebra_version)
	os.mkdir("icons")
	icons_tar_gz_file = tarfile.open(icons_tar_gz_file_path, "r:gz")
	try:
		for item in ["16x16", "22x22", "24x24", "32x32", "36x36", "48x48", "64x64", "72x72", "96x96", "128x128", "192x192", "256x256"]:
			icons_tar_gz_file.extract("hicolor/"+item+"/apps/geogebra.png", path="icons")
			icons_tar_gz_file.extract("hicolor/"+item+"/mimetypes/application-vnd.geogebra.file.png", path="icons")
			icons_tar_gz_file.extract("hicolor/"+item+"/mimetypes/application-vnd.geogebra.tool.png", path="icons")
		icons_tar_gz_file.extract("hicolor/scalable/apps/geogebra.svgz", path="icons")
		icons_tar_gz_file.extract("hicolor/scalable/mimetypes/application-vnd.geogebra.file.svgz", path="icons")
		icons_tar_gz_file.extract("hicolor/scalable/mimetypes/application-vnd.geogebra.tool.svgz", path="icons")
	finally:
		icons_tar_gz_file.close()
	shutil.copy(start_script_path, ".")
	os.chmod("geogebra",0o755)
	shutil.copy(license_txt_path, ".")
	shutil.copy(geogebra_xml_path, ".")
	shutil.copy(geogebra_desktop_path, ".")
	shutil.copy(uninstall_path, ".")
	shutil.copy(install_path, ".")
	shutil.copy(install_sh_path, ".")
	shutil.copy(readme_path, ".")
	os.chdir(temp_dir)
	geogebra_tar_gz_file = tarfile.open(destination_path+"/geogebra-"+geogebra_version+".tar.gz", "w:gz")
	try:
		geogebra_tar_gz_file.add("geogebra-"+geogebra_version)
	finally:
		geogebra_tar_gz_file.close()
finally:
	shutil.rmtree(temp_dir)
sys.exit(0)
