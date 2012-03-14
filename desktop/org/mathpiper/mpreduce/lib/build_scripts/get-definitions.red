% Get definitions for Reduce functions

lisp;
on echo, comp, backtrace;

load!-module 'compiler;

% The following line will be left over from the system build if you build
% bootstrapreduce.img on the system you are now using! If not you need
% to adjust and activate this.

% @srcdir := "/cygdrive/c/projects/reduce-algebra/trunk/csl/cslbase";

<< m := open("$srcdir/../../src/packages/package.map", 'input);
   oldi := rds m;
   off echo;
   packages := read();
   on echo;
   rds oldi;
   close m >>;

symbolic procedure record!-a!-def(name, modname, type, d);
  put(name, 'definition, union(get(name, 'definition),
     list list(modname, type, d)));

symbolic procedure record!-defs!-for!-name(name, modname);
  begin
    scalar d, c;
    if (d := get(name, 'smacro)) and
       (c := md5 d) neq get(name, 'smacro!-checksum) then <<
       record!-a!-def(name, modname, 'smacro, d);
       put(name, 'smacro!-checksum, c) >>;
    if (d := get(name, '!*savedef)) and
       (c := md5 d) neq get(name, 'expr!-checksum) then <<
       record!-a!-def(name, modname, 'expr, d);
       put(name, 'expr!-checksum, c) >>;
  end;

symbolic procedure record!-defs modname;
  for each name in oblist() do record!-defs!-for!-name(name, modname);

record!-defs 'core;

load!-source := t; % So that savedefs get loaded without any checksum checking.

for each modname in packages do if modulep car modname then <<
%  princ "+++ About to load "; print car modname;
   load!-source car modname;
   record!-defs car modname >>;

defined := nil;

for each name in oblist() do
   if get(name, 'definition) then defined := name . defined;

defined := sort(defined, 'orderp)$

% Here I illustrate what I have collected by displaying cases where
% there seem to be two (or more) potentially conflicting definitions.

<< terpri();
   for each name in defined do
      if length get(name, 'definition) > 1 then <<
         print name;
         for each d in get(name, 'definition) do <<
            princ "Defined as "; prin cadr d;
            princ " in package "; print car d >>;
         terpri() >> >>;

end;

