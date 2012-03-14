%
% This script is normally run via
%     make recompile which=modulename
% or  make bootstraprecompile which=modulename
%

symbolic;

load!-module 'remake;

get_configuration_data();

fluid '(!*forcecompile);
!*forcecompile := t; % Ignore date-stamps and force compilation to happen

if boundp 'which and which and not (which = "") then <<
   mods := compress explodec which;
   if member(mods, reduce_base_modules) or
      member(mods, reduce_extra_modules) then build_reduce_modules list mods
   else error(0, list("unknown module to recompile", mods)) >>
else <<
   terpri();
   printc "Must specify which module should be recompiled, eg";
   printc "  make recompile which=modulename";
   stop 8 >>;

end;
