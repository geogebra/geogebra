BUCKET_DIR=https://apps-builds.s3-eu-central-1.amazonaws.com/geogebra/branches/$1/latest
rm -rf content
mkdir content
cd content
wget -q $BUCKET_DIR/geogebra-bundle.zip
wget -q $BUCKET_DIR/graphing.html
wget -q $BUCKET_DIR/classic.html
wget -q $BUCKET_DIR/evaluator.html
wget -q $BUCKET_DIR/notes.html
wget -q $BUCKET_DIR/resetTest.html
wget -q $BUCKET_DIR/GeoGebraLiveTest.html
wget -q $BUCKET_DIR/geogebra-live.js
wget -q $BUCKET_DIR/changes.csv
unzip geogebra-bundle.zip
mv GeoGebra/HTML5/5.0/web3d .
mv GeoGebra/HTML5/5.0/css .
