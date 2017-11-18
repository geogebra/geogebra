var giac = require('bindings')('giac');

var readline = require('readline');
var rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout,
  terminal: false
});

const args = process.argv;

var mini = true;

if (args.length > 1 && args[2] != "-m") {
 mini = false;
 console.log("This is a minimalist command line version of NodeGiac");
 console.log("Enter expressions to evaluate");
 console.log("Example: factor(x^4-1); simplify(sin(3x)/sin(x))");
 console.log("int(1/(x^4-1)); int(1/(x^4+1)^4,x,0,inf)");
 console.log("f(x):=sin(x^2); f'(2); f'(y)");
 console.log("Press CTRL-D to stop");
 }

var n=1;

rl.on('line', function(line){
    // echo
    console.log(n + ">> " + line);
    var ans = giac.evaluate(line);
    if (mini) ans = ans.replace(/\n/g, "\\n");
    console.log(n + "<< " + ans);
    n++;
})

/*
console.log(giac.evaluate("expand((x+y)^3)"));
console.log(giac.evaluate("expand((x+y)^4)"));
console.log(giac.evaluate("2^50"));
console.log(giac.evaluate("[1]"));
console.log(giac.evaluate("caseval(\"init geogebra\")"));
console.log(giac.evaluate("[1]"));
console.log(giac.evaluate("evalf(7,15)"));
console.log(giac.evaluate("caseval(\"close geogebra\")"));
console.log(giac.evaluate("normal(sqrt(1+i))"));
*/

/* The expected output is:
 *
 * x^3+y^3+3*x*y^2+3*x^2*y
 * x^4+y^4+4*x*y^3+6*x^2*y^2+4*x^3*y
 * 1125899906842624
 * [1]
 * "geogebra mode on"
 * {1}
 * 7.00000000000000
 * "geogebra mode off"
 * (sqrt(2)*sqrt(sqrt(2)+1)+(1+i)*sqrt(sqrt(2)+1))/(sqrt(2)+2)
 *
 */
