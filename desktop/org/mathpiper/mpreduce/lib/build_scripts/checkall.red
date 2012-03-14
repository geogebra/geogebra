%
% This script is normally run as
%     r38 ../util/checkall.red -D@srcdir=DIR -Dwhich_module=XXX
% where XXX is the name of a module that is to be checked. If XXX is left
% empty then the script will check all known modules.
%

load!-module 'remake;

lisp check_a_package();

end;

