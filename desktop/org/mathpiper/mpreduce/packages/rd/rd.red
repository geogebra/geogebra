% ----------------------------------------------------------------------
% $Id: rd.red 477 2009-11-28 14:09:32Z arthurcnorman $
% ----------------------------------------------------------------------
% Copyright (c) 2009 Thomas Sturm
% ----------------------------------------------------------------------
% Redistribution and use in source and binary forms, with or without
% modification, are permitted provided that the following conditions
% are met:
%
%    * Redistributions of source code must retain the relevant
%      copyright notice, this list of conditions and the following
%      disclaimer.
%    * Redistributions in binary form must reproduce the above
%      copyright notice, this list of conditions and the following
%      disclaimer in the documentation and/or other materials provided
%      with the distribution.
%
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
% "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
% LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
% A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
% OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
% SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
% LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
% DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
% THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
% (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
% OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
% 

module rd;

load!-package 'remake;

fluid '(here!* packagemap!*);

switch rd_force;

copyd('olderfaslp_orig,'olderfaslp);

procedure olderfaslp(x,y);
   !*rd_force or olderfaslp_orig(x,y);

procedure rd_init(here);
   <<
      here!* := here;
      read!-package_map concat2(here!*,"/packages/package.map");
   >>;

procedure module2!-to!-file(u,v);
   % Converts the module u in package directory v to a fully rooted file
   % name.
   if v then
      concat2(mkfil v,concat2("/",concat2(mkfil u,".red")))
   else
      concat2(mkfil u,".red");

procedure m2f(u,v);
   module2!-to!-file(u,get_path v);

procedure read!-package_map(mapfile);
   begin scalar chan;
      chan := rds open(mapfile,'input);
      packagemap!* := read();
      close rds chan;
      return packagemap!*
   end;

procedure get_path(y);
   begin scalar w;
      w := atsoc(y,packagemap!*);
      return if w then concat2(here!*,concat2("/packages/",cadr w));
   end;

procedure get_submodules(y);
   begin scalar w;
      w := get(y,'package);
      if w then return w;
      w := file!-transform(m2f(y,y),function get_submodules1);
      if w then
 	 eval w
      else
 	 put(y,'package,{y});
      return get(y,'package)
   end;

procedure get_submodules1();
   begin scalar w;
      repeat <<
	 w := xread t
      >> until eqcar(w,'create!-package);
      return w
   end;

procedure makep(y);
   begin scalar rmk,packl,sy,z;
      packl := get_submodules y;
      rmk := nil; while packl and not rmk do <<
   	 sy := car packl;
   	 packl := cdr packl;
   	 z := m2f(sy,y);
	 if 'psl memq lispsystem!* then
 	    sy := concat2("$fasl/", concat2(mkfil sy,".b"));
   	 if olderfaslp(sy,z) then rmk := t
      >>;
      return rmk
   end;

procedure rlint!-msg(y);
   <<
      terpri();
      prin2 "MAKESHMSG ";
      prin2 "Syntax-checking ";
      prin2 y;
      !#if (memq 'csl lispsystem!*)
      	 prin2t " for csl ...";
      !#else
      	 prin2t " for psl ...";
      !#endif
   >>;

procedure rlint(y);
   begin scalar z;
      off msg;
      on cref;
      for each sy in get_submodules y do <<
   	 z := m2f(sy,y);
   	 in_list1(z,t)
      >>;
      off cref;
   end;

procedure make(y);
   <<
      !#if (memq 'csl lispsystem!*)
      <<
   	 on backtrace;  % In case something goes wrong.
      	 !*savedef := nil;
	 !*native_code := nil
      >>;
      !#else
      <<
	 load!-package 'compiler;
	 errorset('(load compat),nil,nil)  % PSL compiler support.
      >>;
      !#endif
      !*argnochk := t;
      package!-remake2(y,get_path y)
   >>;

procedure up!-to!-date!-exit(y);
   <<
      terpri();
      prin2 "MAKESHVMSG ";
      prin2 y;
      prin2t " is up to date";
      bye
   >>;

endmodule;

end;  % of file
