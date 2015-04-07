#include <iostream>
#include <string>
using namespace std;

namespace xxx {
  string yyy(const string & s){
    return s.substr(0,s.size()-1);
  }
};

int main(){
  string s;
  cin >> s;
  cout << xxx::yyy(s) << endl;
}
