#!/bin/sh

unzip -o -d ~/tmp ./$1.ggb

mv ~/tmp/geogebra_thumbnail.png ./$1.png

convert -format gif -resize 16x16 -background white -gravity center -extent 16x16 $1.png $1.gif

rm $1.png

mv $1.gif ../gui/images/
