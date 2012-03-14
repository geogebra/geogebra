%
% This script is normally run as
%     reduce DIR/testall.red -D@srcdir=DIR -Dwhich=XXX
% where XXX is the name of a module that is to be tested. If XXX is left
% empty then the script will test all known modules.
%
% Maybe most neatly used via
%      make testall
% OR   make test1 which=MODULENAME
%

symbolic;

load!-module 'remake;

get_configuration_data();

if boundp 'which and which and not (which = "") then <<
   mods := compress explodec which;
   if member(mods, reduce_test_cases) then test_a_package list mods
   else error(0, list("unknown module to test", module)) >>
else test_a_package reduce_test_cases;

end;

