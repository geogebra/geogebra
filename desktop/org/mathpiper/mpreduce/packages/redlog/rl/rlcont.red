% ----------------------------------------------------------------------
% $Id: rlcont.red 81 2009-02-06 18:22:31Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 1995-2009 Andreas Dolzmann and Thomas Sturm
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

lisp <<
   fluid '(rl_cont_rcsid!* rl_cont_copyright!*);
   rl_cont_rcsid!* :=
      "$Id: rlcont.red 81 2009-02-06 18:22:31Z thomas-sturm $";
   rl_cont_copyright!* := "Copyright (c) 1995-2009 A. Dolzmann and T. Sturm"
>>;

module rlcont;
% Reduce logic component context selection. Submodule of [redlog].

%put('rlset,'stat,'rl_setstat);
%put('rlset,'formfn,'rl_setform);

put('rlset,'psopfn,'rl_set!$);

put('b,'rl_calias,'ibalp);
put('!B,'rl_calias,'ibalp);  % for !*raise=nil
put('boolean,'rl_calias,'ibalp);

put('c,'rl_calias,'acfsf);
put('!C,'rl_calias,'acfsf);
put('complex,'rl_calias,'acfsf);

put('differential,'rl_calias,'dcfsf);

put('padics,'rl_calias,'dvfsf);

put('r,'rl_calias,'ofsf);
put('!R,'rl_calias,'ofsf);
put('reals,'rl_calias,'ofsf);

put('queues,'rl_calias,'qqe);

put('terms,'rl_calias,'talp);

put('z,'rl_calias,'pasf);
put('!Z,'rl_calias,'pasf);
put('integers,'rl_calias,'pasf);

% procedure rl_setstat();
%    begin scalar f,x,l;
%       f := cursym!*;
%       x := scan();
%       if x neq '!*lpar!* then <<
% 	 scan();
% 	 return f . {x}
%       >>;
%        while (x := scan()) neq '!*semicol!* do
% 	 if not (x eq '!*comma!*) then
% 	    l := x . l;
%       if not eqcar(l,'!*rpar!*) then
% 	 symerr("Too few right parentheses",nil);
%       return f . reversip cdr l
%    end;

% procedure rl_setform(l);
%    rl_set!$ cdr l;

procedure rl_set!$(argl);
   begin scalar w;
      if argl then <<
      	 w := reval car argl;
      	 if eqcar(w,'list) then <<
	    if cdr argl then rederr "too many arguments";
	    argl := cdr w
      	 >> else
	    argl := w . for each x in cdr argl collect reval x
      >>;
      return 'list . rl_set argl
   end;

procedure rl_set(argl);
   begin scalar cntxt,w;
      cntxt := if rl_cid!* then append(rl_usedcname!*,rl_argl!*) else nil;
      if null argl then return cntxt;
      if rl_cid!* then rl_exit();
      w := rl_enter(argl);
      if w then <<
	 if cntxt then rl_enter(cntxt);
	 rederr w
      >>;
      return cntxt;
   end;

procedure rl_exit();
   begin scalar w;
      w := for each pair in get(car rl_cid!*,'rl_cswitches) collect
	 car pair . rl_onp car pair;
      put(car rl_cid!*,'rl_cswitches,w);
      for each pair in rl_ocswitches!* do
	 rl_vonoff(car pair,cdr pair);
      if (w := get(car rl_cid!*,'rl_exit)) then
	 apply(w,nil);
   end;

procedure rl_enter(argl);
   begin scalar w,enter,cid,usedcname;
      usedcname := car argl;
      cid := get(usedcname,'rl_calias) or usedcname;
      argl := cdr argl;
      w := errorset({'load!-package,mkquote(cid)},nil,!*backtrace)
	 where !*msg=nil;
      if errorp w then
	 return {"switching to context",cid,"failed"};
      if not flagp(cid,'rl_package) then
	 return {cid,"is not an rl package"};
      enter := get(cid,'rl_enter);
      if null enter and argl then <<
	 lprim {"extra",ioto_cplu("argument",cdr argl),"ignored"};
	 argl := nil;
      >>;
      if enter then <<
      	 w := apply(enter,{argl});
      	 if not car w then
	    return cdr w
      	 else
	    argl := cdr w
      >>;
      rl_cid!* := {cid};
      rl_argl!* := argl;
      rl_usedcname!* := {usedcname};
      rl_ocswitches!* := nil;
      for each pair in get(car rl_cid!*,'rl_cswitches) do <<
	 rl_ocswitches!* := (car pair . rl_onp car pair) . rl_ocswitches!*;
	 rl_vonoff(car pair,cdr pair)
      >>;
      rl_ocswitches!* := reversip rl_ocswitches!*;
      rl_updcache();
      rmsubs();
      return nil
   end;

procedure rl_onp(s);
   eval intern compress append(explode '!*,explode s);

procedure rl_vonoff(sw,v);
   % Verbose [onoff]. [sw] is a switch; [v] is Bool.
   if v neq rl_onp sw then <<
      lprim {"turned",if rl_onp sw then "off" else "on","switch",sw};
      onoff(sw,v)
   >>;

procedure rl_updcache();
   % Update cache.
   <<
      for each bbv in rl_bbl!* do
	 set(bbv,nil);
      for each x in get(car rl_cid!*,'rl_params) do
      	 set(car x,cdr x);
      for each sv in rl_servl!* do
	 set(sv,nil);
      for each x in get(car rl_cid!*,'rl_services) do
      	 set(car x,cdr x)
   >>;

procedure rl_serviadd(tag,name,value);
   rl_sbiadd(tag,'rl_services,name,value);

procedure rl_bbiadd(tag,name,value);
   rl_sbiadd(tag,'rl_params,name,value);

procedure rl_cswadd(tag,name,value);
   rl_sbiadd(tag,'rl_cswitches,name,value);

procedure rl_sbiadd(tag,prp,name,value);
   begin scalar w,al,old;
      if not flagp(tag,'rl_package) then
	 rederr {tag,"is not a context identifier"};
      al := get(tag,prp);
      w := atsoc(name,al);
      if null w then <<
	 al := (name . value) . al;
      	 put(tag,prp,al);
	 return nil
      >>;
      old := cdr w;
      cdr w := value;
      lprim {"Changed definition of",name};
      put(tag,prp,al);
      return old
   end;

procedure rl_copyc(new,old);
   % Copy context. [new] and [old] are context identifiers. Returns
   % any. Copies all relevant properties and flags from [old] to
   % [new]. The return value is the value of the last property if it
   % was set, else [nil].
   <<
      if flagp(old,'rl_package) then
	 flag({new},'rl_package);
      for each tag in '(simpfnname rl_cswitches rl_params rl_services rl_prepat
      	 rl_resimpat rl_lengthat rl_prepterm rl_simpterm rl_enter rl_exit) do
	    rl_cput(new,tag,old)
   >>;

procedure rl_cput(new,tag,old);
   % Conditional put. [new] and [old] are context identifiers; [tag]
   % is an identifier. Returns the value of the property [tag] if set
   % for [old], [nil] else. If the property [tag] is set for [old],
   % then it is copied to [new].
   (if w then put(new,tag,w)) where w=get(old,tag);

(if w then
   rl_deflang!* := {intern compress reversip cdr reversip cdr explode w})
      where w=getenv("RLDEFLANG");
if rl_deflang!* then rl_set rl_deflang!*;

endmodule;  % [rlcont]

end;  % of file
