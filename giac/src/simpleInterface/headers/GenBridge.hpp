#include "ContextBridge.hpp"
#include <string>

using namespace std;
namespace giac {
	class gen;	
}

class GenBridge {
public:
	giac::gen* g;
	GenBridge(string expression, ContextBridge& context);
	~GenBridge();
	void eval(int level, ContextBridge& context);
	string print(ContextBridge& context);
};