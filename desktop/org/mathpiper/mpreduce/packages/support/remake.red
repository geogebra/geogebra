module remake; % Update the fasl loading version and cross-reference of
               % a given file.

% Authors: Martin L. Griss and Anthony C. Hearn.
% Modified by ACN for the Sourceforge version...

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


fluid '(!*break
        !*cref
        !*crefchk
        !*faslp
        !*forcecompile
        !*int
        !*loadall
        !*usermode
        !*writingfaslfile
        lispsystem!*);

global '(!*argnochk nolist!* loaded!-modules!*);

symbolic procedure psl!-file!-write!-date u;
   % Returns write date of file u as an integer.
   (if null x then rederr list("file not found:",u)
    else cddr assoc('writetime,x))
   where x = filestatus(u,nil);

symbolic procedure olderfaslp(u,v);
   if 'psl memq lispsystem!*
     then null filep u
        or psl!-file!-write!-date u < psl!-file!-write!-date v
    else if null filedate v then rederr list("Missing file",v)
    else null modulep u or datelessp(modulep u,filedate v);

% Code for updating cross reference information.

nolist!* := append('(module endmodule),nolist!*);

% +++++ The cross-referencing capability probably no longer works.

symbolic procedure update!-cref x;
   % Updates cross-reference for x (module . path).
   begin scalar y,z;
      y := concat2("$rcref/",concat2(mkfil car x,".crf"));
      z := module2!-to!-file(car x,get(cdr x,'path));
      if olderfaslp(y,z) or !*forcecompile
        then <<terpri();
               terpri();
               if errorp errorset!*(list('upd!-cref1,mkquote car x,
                                                   mkquote z,mkquote y),
                                  t)
                 then lprie list("Error during cref of",x)>>
%                then errorprintf("***** Error during cref of %w%n",x)>>
   end;

symbolic procedure upd!-cref1(u,v,w);
   begin scalar !*break,!*cref,!*int,!*usermode,ochan,oldochan,oldll;
      lprim list("Cross referencing",u,"...");
%     prin2t bldmsg("*** Cross referencing %w ...",u);
      ochan := open(w,'output);
      oldochan := wrs ochan;
      oldll := linelength 75;
      crefon();   % this is entry point to cref routines
      !*cref := t;
      infile v;
      !*cref := nil;
      crefoff();
      close ochan;
      wrs oldochan;
      linelength oldll
   end;


% Support for packages directory.

symbolic procedure package!-remake x;
   (if y then package!-remake2(x,y) else package!-remake2(x,x))
   where y=get(x,'folder);

symbolic procedure package!-remake2(u,v);
   begin scalar y;
%     if !*crefchk then update!-cref2(u . v);
      update!-fasl2(u . v);
      evload list u;
      loaded!-modules!* := union(loaded!-modules!*, list u);
      y := get(u,'package);
      if y then y := cdr y;
      for each j in y do
         <<update!-fasl2(j . v);
%          if !*crefchk then update!-cref2(j . v)>>
         >>
   end;

symbolic procedure update!-fasl2 x;
   begin scalar y,z;
      if 'psl memq lispsystem!*
        then y := concat2("$fasl/", concat2(mkfil car x, ".b"))
       else y := car x;
      z := module2!-to!-file(car x,cdr x);
      if olderfaslp(y,z) or !*forcecompile
        then <<terpri();
               terpri();
               if errorp errorset!*(list('upd!-fasl1,mkquote car x,
                                                   mkquote z,
                                                   mkquote cdr x),
                                  t)
                 then <<if !*writingfaslfile then lispeval '(faslend);
                        lprie list("Error during mkfasl of",x)>>>>
   end;

symbolic procedure upd!-fasl1(u,v,w);
   % We rebind *fastfor here because it's the only case of "compiletime"
   % at the moment (!).
   begin scalar !*fastfor,!*lower,!*usermode,!*quiet!_faslout,!*break,x;
      !*faslp := t;
      !*quiet!_faslout := t;
      if not('psl memq lispsystem!*) then !*lower := t;
      if !*loadall and w neq u then <<
         evload list w;
         loaded!-modules := union(loaded!-modules!*, list w) >>;
      if x := get(u,'compiletime)
        then <<prin2 "*** Compile time: "; prin2t x; lispeval x>>;
      u := mkfil u;
      lprim list("Compiling",u,"...");
%     prin2t bldmsg("*** Compiling %w ...",u);
      terpri();
      if 'psl memq lispsystem!*
        then lispeval list('faslout, concat2("$fasl/",u))
       else lispeval list('faslout,u);
      infile v;
      lispeval '(faslend)
   end;

symbolic procedure module2!-to!-file(u,v);
   % Converts the module u in package directory v to a fully rooted file
   % name.
   if memq('vsl, lispsystem!*) then
     concat2("../packages/",concat2(mkfil v,
            concat2("/",concat2(mkfil u,".red"))))
   else
     concat2("$reduce/packages/",concat2(mkfil v,
            concat2("/",concat2(mkfil u,".red"))));

endmodule;

end;
