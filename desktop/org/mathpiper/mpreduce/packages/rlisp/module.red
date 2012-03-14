% module module; % Support for module and package use.

% Author: Anthony C. Hearn.

% Copyright (c) 1990 The RAND Corporation.  All rights reserved.

% Redistribution and use in source and binary forms, with or without
% modification, are permitted provided that the following conditions are met:
%
%    * Redistributions of source code must retain the relevant copyright
%      notice, this list of conditions and the following disclaimer.
%    * Redistributions in binary form must reproduce the above copyright
%      notice, this list of conditions and the following disclaimer in the
%      documentation and/or other materials provided with the distribution.
%
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
% AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
% THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
% PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNERS OR
% CONTRIBUTORS
% BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
% CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
% SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
% INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
% CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
% ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
% POSSIBILITY OF SUCH DAMAGE.
%


%
% WARNING. This code is loaded quite early in the process of
% bootstrapping. As a result it has to be written such that it
% will work properly with the cut-down bootstrap version of the
% RLISP parser. Various constructions such as <<...>> are not
% available....
%

fluid '(!*backtrace !*mode !*faslp);

global '(exportslist!* importslist!* loaded!-packages!* loaded!-modules!*
         mode!-list!*);

!*mode := 'symbolic;   % initial value.

remprop('exports,'stat);

remprop('imports,'stat);

remprop('module,'stat);

symbolic procedure exports u;
   begin exportslist!* := union(u,exportslist!*) end;

symbolic procedure imports u;
   begin importslist!* := union(u,importslist!*) end;

symbolic procedure module u;
   % Sets up a module definition.
   begin
      mode!-list!* := !*mode . mode!-list!*;
      !*mode := 'symbolic
   end;

symbolic procedure endmodule;
   begin
      if null mode!-list!*
        then rederr  "ENDMODULE called outside module";
      exportslist!* := nil;
      importslist!* := nil;
      !*mode := car mode!-list!*;
      mode!-list!* := cdr mode!-list!*
   end;

deflist('((exports rlis) (imports rlis) (module rlis)),'stat);

put('endmodule,'stat,'endstat);

flag('(endmodule),'go);

flag('(module endmodule),'eval);

put('xmodule,'newnam,'module);   % Hook for module extensions.

% Support for package loading.

put('load,'stat,'rlis);

put('load,'formfn,'formload);


symbolic procedure formload(u,vars,mode);
   if eq(mode, 'symbolic) then
      list('progn,
% Adapted to maintain loaded!-modules!*
           list('setq, 'loaded!-modules!*,
              list('union, 'loaded!-modules!*, mkquote cdr u)),
           list('evload, mkquote cdr u))
   else list('load!_package, mkquote cdr u);

symbolic procedure load!-package u;
   begin scalar x,y;
      if stringp u
        then return load!-package intern intern compress explode u
   % intern intern is needed for, e.g., "../huhu".
       else if null idp u then rederr list(u,"is not a package name")
       else if memq(u,loaded!-packages!*)
%       then progn(lprim list("Package",u,"already loaded"), return u)
        then return u
       else if or(atom(x:= errorset(list('evload,list('quote,list u)),
                               nil,!*backtrace)),
                  cdr x)
        then rederr
           list("error in loading package",u,"or package not found");
      loaded!-packages!* := u . loaded!-packages!*;
      loaded!-modules!* := union(loaded!-modules!*, list u);
      x := get(u,'package);
      if x then x := cdr x;
   a: if null x then return install!-patches u
       else if null atom get(car x,'package) then load!-package car x
       else if or(atom(y := errorset(list('evload,
                                         list('quote,list car x)),
                                    nil,!*backtrace)),
                  cdr y)
        then rederr list("module",car x,"of package",u,
                         "cannot be loaded");
      loaded!-modules!* := union(loaded!-modules!*, list car x);
      x := cdr x;
      go to a
   end;

% Now a more user-friendly version.

remprop('load!_package,'stat);

remprop('packages!_to!_load,'stat);

symbolic procedure load!_package u;
   begin scalar x;
      x := u;
   a: if null x then return nil;
      load!-package car x;
      x := cdr x;
      go to a
   end;

symbolic procedure packages!_to!_load u;
   %% FJW: Load other packages at package load time only, i.e. do not
   %% load during building (hence not to be flagged eval).
   if null !*faslp then load!_package u;

put('load!_package,'stat,'rlis);

put('packages!_to!_load,'stat,'rlis);

flag('(load!-package load!_package),'eval);


% Support for patching REDUCE sources.

symbolic procedure patchstat;
   % Read a patch for a given package.
   begin scalar !*mode,u,v,x,y,z,z2;
      x := scan();   % Package name.
      scan();        % Remove semicolon.
 a:   !*mode := 'symbolic;
      y := xread nil;
      if eqcar(y,'symbolic) then y := cadr y
       else if flagpcar(y,'modefn)
        then progn(!*mode := car y, y := cadr y);
      if eq(y,'endpatch)
       then progn(u := name!-for!-patched!-version(x, z),
                  z2 :=  list('de,u,nil,'progn . reversip z) . z2,
                  z2 := list('patches!-load!-check,mkquote x,mkquote u) . z2,
                  return ('patch . reversip z2))
       else if eqcar(y,'procedure)
        then progn(
               v := cadr y,
               u := name!-for!-patched!-version(v, y),
               z := list('instate!-patches,mkquote v,mkquote u,mkquote x) . z,
               z2  := convertmode(('procedure . u . cddr y),nil,
                                  'symbolic,!*mode)
                     . z2)
       else z := convertmode(y,nil,'symbolic,!*mode) . z;
      go to a;
   end;

symbolic procedure name!-for!-patched!-version(name, extra);
   % hashtagged!-name (in CSL) constructs a name that starts with NAME but
   % then continues with a hash value based on EXTRA. The improbability of
   % hash collisions then makes it reasonable to use an interned symbol.
   if member('psl, lispsystem!*) then gensym()
    else hashtagged!-name(name,extra);

symbolic procedure instate!-patches(new,old,pkg);
  begin scalar x;
     x := getd old;
     if x then putd(new,car x,cdr x)
      else rerror('module,1,list(new,"has a badly set-up patch"));
     return nil
  end;

symbolic procedure install!-patches u;
   % Written this way for bootstrapping.
   if eq(u,'patches) then nil
    else if (u := get(u,'patchfn))
     then begin scalar !*usermode,!*redefmsg; eval list u end;

symbolic procedure patches!-load!-check(u,v);
   begin
      put(u,'patchfn,v);
      if memq(u,loaded!-packages!*) then install!-patches u
   end;

put('patch,'stat,'patchstat);

symbolic procedure formpatch(u,vars,mode);
   'progn . cdr u;

put('patch,'formfn,'formpatch);

% endmodule;

end;
