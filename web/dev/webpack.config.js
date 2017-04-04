const path = require('path');

const config = {
	entry:path.resolve(__dirname,'node_modules/@material/drawer/temporary/index.js'),
	output: {
	    filename: 'bundle.js',
	    path: path.resolve(__dirname,'dist')
	  },
	module: {rules: [
                {
                    test: /\.(js|jsx)$/,
                    include:path.resolve(__dirname),
                    use: {
                      loader: 'babel-loader'
                    },
                  }]
	}
}

module.exports = config;