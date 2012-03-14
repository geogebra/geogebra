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


% fluid '(!*backtrace !*mode !*redefmsg !*usermode);
fluid '(!*backtrace !*mode);

global '(exportslist!* importslist!* loaded!-packages!* mode!-list!*);

!*mode := 'symbolic;   % initial value.

% Note: !*redefmsg and !*usermode are only currently used by PSL.

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

% Support for package creation and loading.

symbolic procedure create!-package(u,v);
   % Make module list u into a package with path v.  Dummy for now.
   car u;

create!-package('(module),'(rlisp));

put('load,'stat,'rlis);

put('load,'formfn,'formload);

symbolic procedure formload(u,vars,mode);
   list((if eq(mode,'symbolic) then 'evload else 'load!_package),
	mkquote cdr u);

symbolic procedure load!-package u;
   begin scalar x;
      if null idp u then rederr list(u,"is not a package name")
       else if memq(u,loaded!-packages!*)
%       then progn(lprim list("Package",u,"already loaded"), return u)
        then return u
       else if or(atom(x:= errorset(list('evload,list('quote,list u)),
                               nil,!*backtrace)),
                  cdr x)
        then rederr
           list("error in loading package",u,"or package not found");
      if (x := get(u,'patchfn))
        then begin scalar !*usermode,!*redefmsg; eval list x end;
      loaded!-packages!* := u . loaded!-packages!*
   end;

% Now a more friendly user version.

remprop('load!_package,'stat);

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
   load!_package u;

put('load!_package,'stat,'rlis);

put('packages!_to!_load,'stat,'rlis);

flag('(load!-package load!_package),'eval);


% Support for patching REDUCE 3.5 sources.

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
       then progn(u := dated!-gensym x,
                  z2 :=  list('de,u,nil,'progn . reversip z) . z2,
                  z2 := list('put,mkquote x,mkquote 'patchfn,mkquote u)
                           . z2,
                  return ('patch . reversip z2))
       else if eqcar(y,'procedure)
        then progn(u := dated!-gensym v,
               v := cadr y,
               z := list('copyd,mkquote v,mkquote u) . z,
               z2  := convertmode(('procedure . u . cddr y),nil,
                                  'symbolic,!*mode)
                     . z2)
       else z := convertmode(y,nil,'symbolic,!*mode) . z;
      go to a;
   end;

put('patch,'stat,'patchstat);

symbolic procedure formpatch(u,vars,mode);
   'progn . cdr u;

put('patch,'formfn,'formpatch);

% endmodule;

end;
