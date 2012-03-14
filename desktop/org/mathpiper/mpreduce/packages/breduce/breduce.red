% ----------------------------------------------------------------------
% $Id: breduce.red 1275 2011-08-16 14:47:01Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 2008, 2011 Thomas Sturm
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
   fluid '(breduce_rcsid!* breduce_copyright!*);
   breduce_rcsid!* := "$Id: breduce.red 1275 2011-08-16 14:47:01Z thomas-sturm $";
   breduce_copyright!* := "(c) 2008, 2011 T. Sturm"
>>;

module breduce;
% Supporting code for the breduce batch system located in
% trunk/generic/breduce.

load!-package 'redlog;
load!-package 'tri;

fluid '(out!*);

flag('(breduce_unset breduce_instance
   breduce_every breduce_instance_abs breduce_every_abs),'reserved);

operator breduce_output;

procedure breduce_output(item,tab);
   <<
      item := if item eq 'breduce_unset then
	 "\textsc{sigint}"
      else if eqcar(item,'list) then
 	 cdr item
      else
 	 {item};
      breduce_tprin2t "<output>";
      breduce_prin2 item;
      if eqn(tab,1) then breduce_prin2 " & ";
      if eqn(tab,2) then breduce_prin2 "\\";
      breduce_tprin2t "</output>"
   >>;

operator breduce_setswitches;

procedure breduce_setswitches(sl,ii);
   begin scalar swval,swvals; integer l;
      sl := cdr sl;
      if null sl then return;
      l := length sl;
      swvals := breduce_number2swsetting(l,ii);
      for each sw in sl do <<
	 swval := car swvals;
      	 swvals := cdr swvals;
	 breduce_setswitch(sw,swval)
      >>
   end;

procedure breduce_number2swsetting(l,n);
   begin scalar sl;
      repeat <<
	 if remainder(n,2)=0 then
	    sl := cons(0,sl)
	 else
	    sl := cons(1,sl);
	 n := lisp(n/2)
      >> until n=0;
      for k := length sl + 1 : l do
	 sl := cons(0,sl);
      return sl
   end;

procedure breduce_setswitch(sw,onoff);
   if onoff=1 then on1 sw else off1 sw;

operator breduce_outputswitches;

procedure breduce_outputswitches(sl,ii);
   begin scalar swvals;
      sl := cdr sl;
      if null sl then return;
      swvals := breduce_number2swsetting(length sl,ii);
      breduce_tprin2t "<output>";
      for each swval in swvals do
	 breduce_prin2 {breduce_switch2ltx swval," & "};
      breduce_tprin2t "</output>"
   end;

procedure breduce_switch2ltx(sw);
   if sw = 1 then "$\bullet$" else " $\circ$ ";

operator breduce_processformula;

procedure breduce_processformula(sol);
   begin scalar success; integer w;
      if null sol then
	 return "\texttt{nil} (error)";
      sol := rl_simp sol;
      if rl_tvalp sol then
	 return {'list,sol};
      w := rl_qnum sol;
      success := if eqn(w,0) then "$\top$" else "$\bot$";
      return {'list,success," (",w,"q/",rl_atnum sol,"at)"}
   end;

operator breduce_tex;

procedure breduce_tex(sol);
   'list . '!$ . reversip('!$ . reverse mktag(reval sol,0,nil));

operator breduce_verbatim;

procedure breduce_verbatim(sol);
   {'list,"\verb@",aeval sol,"@"};

procedure breduce_flush();
!#if (memq 'psl lispsystem!*)
   <<
      flushbuffer out!*;
      channelflush out!*
   >>;
!#else
      flush();
!#endif

procedure breduce_prin2(l);
   breduce_prin21(l,nil,nil,nil);

procedure breduce_tprin2(l);
   breduce_prin21(l,t,nil,nil);

procedure breduce_prin2t(l);
   breduce_prin21(l,nil,t,nil);

procedure breduce_tprin2t(l);
   % Input/Output tools conditional terpri prin2 conditional terpri.
   % [l] is an atom or a list. Returns ANY. Equivalent to
   % [breduce_cterpri();breduce_prin2(l);breduce_cterpri()].
   breduce_prin21(l,t,t,nil);

procedure breduce_prin21(l,flg1,flg2,spc);
   <<
      if l and atom l then l := {l};
      if flg1 then breduce_cterpri();
      for each x in l do <<
 	 prin2 x;
 	 if spc then prin2 " "
      >>;
      breduce_flush();
      if flg2 then breduce_cterpri()
   >>;

procedure breduce_cterpri();
   if posn()>0 then terpri();

operator breduce_randomseed;

procedure breduce_randomseed(rnd);
   begin integer epoch,n,i;
      n := breduce_str2int getenv "n";
      if rnd eq 'breduce_instance then
	 return random_new_seed n;
      i := breduce_str2int getenv "i";
      if rnd eq 'breduce_every then
	 return random_new_seed(n+i);
      epoch := breduce_str2int getenv "epoch";
      if rnd eq 'breduce_instance_abs then
	 return random_new_seed(epoch+n);
      if rnd eq 'breduce_every_abs then
	 return random_new_seed(epoch+n+i);
      rederr {"unknown policy",rnd}
   end;

procedure breduce_str2int(s);
   compress reversip cdr reversip cdr explode s;

procedure breduce_datestamp();
   % Input/Output datestamp. No parameter. Returns an integer; the
   % number of seconds since the epoch.
!#if (memq 'psl lispsystem!*)
   <<
      date();
      sys2int wgetv(datebuffer,0)
   >>;
!#else
   datestamp();
!#endif

endmodule;

end;
