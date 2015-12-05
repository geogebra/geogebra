#include "giac.h"

using namespace std;
using namespace giac;

int main(int argc, char *argv[]){
  int verbosemode = 1;
  // -m will disable verbose mute (useful to work as a simple filter)
  if ((argc==2) && (strcmp(argv[1], "-m") == 0))
    verbosemode = 0;
  if (verbosemode == 1) {
    cout << "This is a minimalist command line version of Giac" << endl;
    cout << "Enter expressions to evaluate" << endl;
    cout << "Example: factor(x^4-1); simplify(sin(3x)/sin(x))" << endl;
    cout << "int(1/(x^4-1)); int(1/(x^4+1)^4,x,0,inf)" << endl;
    cout << "f(x):=sin(x^2); f'(2); f'(y)" << endl;
    cout << "Press CTRL-D to stop" << endl;
    }
  context ct;
  string line;
  gen g;
  int n = 1; // in giac this starts from 0
  while (getline(cin, line)) {
    cout << n << ">> " << line << endl;
    cout << n++ << "<< ";
    g=gen(line,&ct);
    try {
      cout << eval(g,1,&ct) << endl;
      } catch (runtime_error & err) {
      cout << "ERROR: " << err.what() << endl;
      }
    }
  }
