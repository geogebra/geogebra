#!/bin/sh/
#
# GeoGebra - Dynamic Mathematics for Everyone
# http://www.geogebra.org
#
# This file is part of GeoGebra.
#
# This program is free software; you can redistribute it and/or modify it 
# under the terms of the GNU General Public License as published by 
# the Free Software Foundation.
#
# Script for checking duplicate command names
#
# @author: Zbynek Konecny
#
cd ../geogebra/properties
for i in `ls command_*`; do echo "**Checking $i"; grep -v Syntax $i |sed "s/.*=\(.*\)/\1/" | sort > f$i; cat f$i | uniq | diff - f$i | grep '>' | sed "s/./$i/" |awk '{print "grep -F \"=" $2 "\"", $1}' | sh; done
