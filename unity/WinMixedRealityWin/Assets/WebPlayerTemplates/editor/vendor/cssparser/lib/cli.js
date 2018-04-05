#!/usr/bin/env node

(function () {
	var colors	   = require('colors');
	var cssparser  = require('./cssparser.js');
	var nomnom     = require('nomnom');
	var fs         = require('fs');
	var path       = require('path');
	
	var version = require('../package.json').version;
	
	var opts = require("nomnom")
	  .script('cssparser')
	  .option('file', {
	    flag: true,
	    position: 0,
	    help: 'CSS document file'
	  })
	  .option('outfile', {
	    abbr: 'o',
	    metavar: 'FILE',
	    help: 'Filename or base name of the generated JSON'
	  })
	  .option('indent', {
	    abbr: 'i',
	    default: 4,
	    help: 'indentation(string or number)'
	  })
	  .option('type', {
	    abbr: 't',
	    default: 'simple',
	    choices: [ 'simple', 'deep', 'atomic' ],
	    metavar: 'TYPE',
	    help: 'The type of JSON to generate (simple, deep, atomic)'
	  })
	  .option('console', {
	    abbr: 'c',
	    flag: true,
	    help: 'Display JSON to console only. this option will ignore output-file options.'
	  })
	  .option('version', {
	    abbr: 'V',
	    flag: true,
	    help: 'print version and exit',
	    callback: function() {
	       return version;
	    }
	  })
	  .parse();
	  
	function toJSON( raw, type, indent ) {
		var parser = new cssparser.Parser();
		
		return JSON.stringify( parser.parse( raw ), null, indent );
	}
	
    if (opts.file) {
		try {
	        var raw = fs.readFileSync(path.normalize(opts.file), 'utf8');
		}
		catch (e) {
			console.error( e.toString().red );
			return;
		}

		var name = path.basename((opts.outfile||opts.file)).replace(/\..*$/g,'');
		var type = opts.type;
		var indent = opts.indent;

		try {
			var json = toJSON(raw, type, indent);
		}
		catch (e) {
			console.error( e.toString().red );
			return;
		}

		if ( opts.console ) {
			console.log( json );
		}
		else {
			fs.writeFileSync(opts.outfile||(name + '.json'), json );
		}
    }
})();