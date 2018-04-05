cssparser.js
======

cssparser.js is a parser that generates json matched with source css structure.

##Description

* License: MIT license - [http://www.opensource.org/licenses/mit-license.php](http://www.opensource.org/licenses/mit-license.php)
* Author : Chang W. Doh

##Demo

* [http://cwdoh.github.io/cssparser.js/demo/CSS_stringify.html](//cwdoh.github.io/cssparser.js/demo/CSS_stringify.html)

##Dependency

Just want to use cssparser.js? Nothing needed.

If want generating parser, install 'jison' before it.

* Jison - [http://jison.org](http://jison.org )


##Usage

###from Command-line


First of all, you should install cssparser.

	$ npm install cssparser
	
	or
	
	$ npm install cssparser -g

Then execute and you can generate JSON file from command-line.

	$ cssparser cssFile
	
	or 
	
	$ cssparser cssFile -o output_file


###from CommonJS Module

You can generate javascript object from your javascript module.

	// getting parser module
	var cssparser = require("cssparser");
	
	// create new instance of Parser
	var parser = new cssparser.Parser();
	
	// parse & getting json
	var json = parser.parse( cssText );


##Generating parser from source

###Getting jison & source

	$ npm install jison -g
	$ git clone https://github.com/cwdoh/cssparser.js.git

###Generating from source

	$ grunt
	
	or
	
	$ jison ./src/cssparser.y ./src/css.l


##JSON Structure

There are 3 types of JSON format.

* simple - most simple.
	* simply consist of just key & value.
* deep - more detailed then simple mode.
	* this includes more informations of selector, terms, expression, queries, …
* atomic - most detailed. 'atomic' JSON has all pieces of each key & values in CSS.
	* e.g. length has numeric value & its unit like "100px" -> { "value": 100, "unit": "px" }

###Type 'simple'


	stylesheet_object =
	
		+ charset [Object]

		+ imports [Array]
			+ [Object(Import)]
				+ type : "import" : [DOMString]// URI or string
				+ mediaquries [DOMString] : // if query exist

		+ namespaces [Array]
			+ namespace [DOMString] : // URI or string 
			+ prefix [DOMString] : // if prefix exist

		+ rulelist [Array]
			+ [Object(Media)]
				+ type [DOMString] : "media"
				+ mediaqueries [DOMString] : // query string
				+ children [Array] : // nested rulelist
					+ rulelist // …

			+ [Object(FontFace)]
				+ type [DOMString] : "fontface"
				+ declarations [Object] : // declarations

			+ [Object(Page)]
				+ type [DOMString] : "page"
				+ id [DOMString] : // identifier
				+ pseudo [DOMString] : // pseudo string
				+ declarations [Object] : // declarations

			+ [Object(Style)]
				+ type [DOMString] : "style"
				+ selector [DOMString] : // selector string
				+ declarations [Object] : // declarations

			+ [Object(Keyframes)]
				+ type [DOMString] : "keyframes"
				+ id [DOMString] : // identifier
				+ prefix [DOMString] : // vendor prefix e.g. -moz-, -webkit-, -o-, …
				+ keyframes [Array]
					+ [Object(keyframe)]
						+ type [DOMString] : "keyframe"
						+ offset [DOMString] : // offset string
						+ declarations [Object] : // declarations

###Type 'deep'

Not yet.

###Type 'atomic'

Not yet.

##Example

Example is tested with rulesets of [http://css3please.com](http://css3please.com)

	cssparser example/test.css --console -i 4

###Input

	@charset 'utf-8';
	
	@import 'custom.css';
	@import url("fineprint.css");
	@import url("fineprint.css") print;
	@import url("bluish.css") projection, tv;
	@import "common.css" screen, projection;
	@import url('landscape.css') screen and (orientation:landscape);
	
	@namespace "http://www.w3c.org";
	@namespace svg "http://www.w3c.org/svg";
	
	@media screen {
		* {
		   position: absolute;
		 }
	
		.box_shadow {
		  -webkit-box-shadow: 0px 0px 4px 0px #ffffff; /* Android 2.3+, iOS 4.0.2-4.2, Safari 3-4 */
				  box-shadow: 0px 0px 4px 0px #ffffff; /* Chrome 6+, Firefox 4+, IE 9+, iOS 5+, Opera 10.50+ */
		}
	}
	
	@-webkit-keyframes myanim {
	  0%   { opacity: 0.0; }
	  50%  { opacity: 0.5; }
	  100% { opacity: 1.0; }
	}
	
	.matrix {
	  
	-webkit-transform: matrix(1.186,-0.069,0.102,1.036,16.595,73.291);
	-moz-transform: matrix(1.186,-0.069,0.102,1.036,16.595px,73.291px);
	-ms-transform: matrix(1.186,-0.069,0.102,1.036,16.595,73.291);
	-o-transform: matrix(1.186,-0.069,0.102,1.036,16.595,73.291);
	transform: matrix(1.186,-0.069,0.102,1.036,16.595,73.291);
	
	}
	
	@font-face {
	  font-family: 'WebFont';
	  src: url('myfont.woff') format('woff'), /* Chrome 6+, Firefox 3.6+, IE 9+, Safari 5.1+ */
	       url('myfont.ttf') format('truetype'); /* Chrome 4+, Firefox 3.5, Opera 10+, Safari 3—5 */
	}

###JSON Output

	{
		"charset": "'utf-8'",
		"imports": [
			{
				"import": "'custom.css'"
			},
			{
				"import": "url(\"fineprint.css\")"
			},
			{
				"import": "url(\"fineprint.css\")",
				"mediaqueries": "print"
			},
			{
				"import": "url(\"bluish.css\")",
				"mediaqueries": "projection, tv"
			},
			{
				"import": "\"common.css\"",
				"mediaqueries": "screen, projection"
			},
			{
				"import": "url('landscape.css')",
				"mediaqueries": "screen and (orientation:landscape)"
			}
		],
		"namespaces": [
			{
				"namespace": "\"http://www.w3c.org\""
			},
			{
				"namespace": "\"http://www.w3c.org/svg\"",
				"prefix": "svg"
			}
		],
		"rulelist": [
			{
				"type": "media",
				"mediaqueries": "screen",
				"children": [
					{
						"type": "style",
						"selector": "*",
						"declarations": {
							"position": "absolute"
						}
					},
					{
						"type": "style",
						"selector": ".box_shadow",
						"declarations": {
							"-webkit-box-shadow": "0px 0px 4px 0px #ffffff",
							"box-shadow": "0px 0px 4px 0px #ffffff"
						}
					}
				]
			},
			{
				"type": "keyframes",
				"id": "myanim",
				"keyframes": [
					{
						"offset": "0%",
						"declarations": {
							"opacity": "0.0"
						}
					},
					{
						"offset": "50%",
						"declarations": {
							"opacity": "0.5"
						}
					},
					{
						"offset": "100%",
						"declarations": {
							"opacity": "1.0"
						}
					}
				],
				"prefix": "-webkit-"
			},
			{
				"type": "style",
				"selector": ".matrix",
				"declarations": {
					"-webkit-transform": "matrix(1.186,-0.069,0.102,1.036,16.595,73.291)",
					"-moz-transform": "matrix(1.186,-0.069,0.102,1.036,16.595px,73.291px)",
					"-ms-transform": "matrix(1.186,-0.069,0.102,1.036,16.595,73.291)",
					"-o-transform": "matrix(1.186,-0.069,0.102,1.036,16.595,73.291)",
					"transform": "matrix(1.186,-0.069,0.102,1.036,16.595,73.291)"
				}
			},
			{
				"type": "fontface",
				"declarations": {
					"font-family": "'WebFont'",
					"src": "url('myfont.woff') format('woff'),url('myfont.ttf') format('truetype')"
				}
			}
		]
	}

##Change log

* 0.2.0 - May 20th, 2013
	* Initial release of cssparser.js.
* 0.2.1 - May 21st, 2013
	* Update grunt, dependencies, cli options & output message
	* Add 'keyframe' type at child node of keyframes
* 0.2.2 - July 27th, 2013
	* Add ratio type expression with '/'. thanks to Mohsen Heydari.

##To do list

* Parsing & generating options like simple JSON expression or more detailed.
* Error recovery for input css.
* Utilities
	* Minify & optimize css.
	* Auto-generate Cross-browsing stylesheets.

