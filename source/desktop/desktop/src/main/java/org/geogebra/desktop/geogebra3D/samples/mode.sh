#!/bin/sh

unzip -o -d ~/tmp ./$1.ggb

mv ~/tmp/geogebra_thumbnail.png ./$1.png

convert -format gif -resize 32x32 -background white -gravity center -extent 32x32 $1.png $1_32.gif

rm $1.png

mv $1_32.gif ../gui/toolbar/images/
