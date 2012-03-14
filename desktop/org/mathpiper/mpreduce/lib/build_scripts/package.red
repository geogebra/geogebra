% This gets a single REDUCE package compiled and up to date

symbolic;

load!-module 'remake;

if not boundp 'target or null target then target := 'alg;

get_configuration_data t;

build_reduce_modules list target;

end;

