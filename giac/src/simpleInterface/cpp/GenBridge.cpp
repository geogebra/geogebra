#include "GenBridge.hpp"
#include "giac.h"

GenBridge::GenBridge(string expression, ContextBridge& context) {
	g = new giac::gen(expression, context.c);
}

GenBridge::~GenBridge() {
	delete g;
}

void GenBridge::eval(int level, ContextBridge& context) {
	g = new giac::gen(g->eval(level, context.c));
}

string GenBridge::print(ContextBridge& context) {
	return g->print(context.c);
}