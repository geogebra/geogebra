#include <node.h>
#include <v8.h>

#include "giac.h"

using namespace std;
using namespace giac;

using namespace v8;

void Method(const v8::FunctionCallbackInfo<Value>& args) {
  Isolate* isolate = Isolate::GetCurrent();
  HandleScope scope(isolate);
  context ct;
  std::string line_out;

  v8::String::Utf8Value param1(args[0]->ToString());
  std::string line_in = std::string(*param1);

  gen g;
  g=gen(line_in,&ct);
  try {
    line_out = giac::print(giac::eval(g,&ct),&ct);
    }
  catch (runtime_error & err) {
    line_out = err.what();
    }

  Handle<Value> line_out_v8 = String::NewFromUtf8( isolate, line_out.c_str() );
  args.GetReturnValue().Set(String::NewFromUtf8(isolate,line_out.c_str()));
}

void Init(Handle<Object> exports) {
  Isolate* isolate = Isolate::GetCurrent();
  exports->Set(String::NewFromUtf8(isolate, "giac"),
      FunctionTemplate::New(isolate, Method)->GetFunction());
}

NODE_MODULE(giac, Init)
