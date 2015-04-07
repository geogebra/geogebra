#include "giac.h"

using namespace std;
using namespace giac;

int main(){
  //debug_infolevel=20;
  context ct;
  cout << "Enter expressions to evaluate" << endl;
  cout << "Example: factor(x^4-1); simplify(sin(3x)/sin(x))" << endl;
  cout << "int(1/(x^4-1)); int(1/(x^4+1)^4,x,0,inf)" << endl;
  cout << "f(x):=sin(x^2); f'(2); f'(y)" << endl;
  cout << "Enter 0 to stop" << endl;
  for (;;){
    gen g;
    cin >> g;
    if (is_zero(g))
      break;
    cout << g << "=" << caseval(g.print(&ct).c_str()) << endl;
    //cout << g << "=" << eval(g,1,&ct) << endl;
  }
}
