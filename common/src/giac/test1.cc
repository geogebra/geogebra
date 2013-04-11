#include <iostream>
#include <string>
using namespace std;

int main(){
  for (;;){
    string s;
    cout << "Enter name (0 stop) " << endl;
    cin >> s;
    cout << s << endl;
    if (s=="0")
      break;
  }
}
