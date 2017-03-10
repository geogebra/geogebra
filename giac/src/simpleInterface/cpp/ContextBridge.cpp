#include "ContextBridge.hpp"
#include "giac.h"

ContextBridge::ContextBridge() {
	c = new giac::context();
}