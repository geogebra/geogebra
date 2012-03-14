% ----------------------------------------------------------------------
% $Id: ibalpkapur.tst 469 2009-11-28 13:58:18Z arthurcnorman $
% ----------------------------------------------------------------------
% Copyright (c) 2007-2009 A. Dolzmann and T. Sturm
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

load redlog$
rlset boolean$
on rlpcprint$
off time$
off gc$
off rlverbose$

lisp;

procedure gen3knf(n,m);
   % generates a formula in 3knf form with [n] variables and [m] clauses.
   begin scalar varl,clausl,a,b,c;
      for i:=1:n do varl := gensym() . varl;
      for i:=1:m do <<
         a := rl_mk2('equal,nth(varl,random n + 1),random 2);
         b := rl_mk2('equal,nth(varl,random n + 1),random 2);
         c := rl_mk2('equal,nth(varl,random n + 1),random 2);
         clausl := rl_mkn('or,{a,b,c}) . clausl
      >>;
      return rl_mkn('and,clausl)
   end;

operator gen3knf;

procedure gensat(n,m);
   % generates a random formula with [n] variables and [m] operators
   begin scalar varl;
      for i:=1:n do varl := gensym() . varl;
      return gensat1(m,varl)
   end;

operator gensat;

procedure gensat1(m,varlist);
   % generates a random formula with [m] operators containing all vars in
   % [varlist]
   if m = 0 then
      rl_mk2('equal,nth(varlist,random length varlist + 1),1)
   else
      begin scalar leftm,rightm,oper,leftcl,rightcl;
         leftm := random m;
         rightm := (m - 1) - leftm;
         oper := random 6;
         if oper = 0 then
            return rl_mk1('not,gensat1(m - 1,varlist))
         else if oper = 1 then
            oper := 'and
         else if oper = 2 then
            oper := 'or
         else if oper = 3 then
            oper := 'impl
         else if oper = 4 then
            oper := 'repl
         else if oper = 5 then
            oper := 'equiv;
         leftcl := gensat1(leftm,varlist);
         rightcl := gensat1(rightm,varlist);
         return rl_mk2(oper,leftcl,rightcl)
      end;

procedure testumode(n,m);
   % Test Polynomgeneration modes. [n] is a positive integer meaning the ammount
   % of variables in the test cases. [m] is a positive integer meaning the 
   % number of operators per formula.
   % returns nil.
   begin scalar checkcases,starttime,endtime,ammo;
      ammo := 100;
      checkcases := for i:=1:ammo collect gensat(n,m);

      off ibalp_kapurgb;
      ioto_prin2t "Vergleich der Umwandlungsvarianten";
      ioto_prin2t {"Anzahl der Variablen: ",n};
      ioto_prin2t {"Anzahl der Operatoren: ",m};
      ioto_prin2t "----------------------------------";
      
      starttime := time();
      for each j in checkcases do ibalp_kapur(j,'sat,'knf);
      endtime := time();
      ioto_prin2t {"3KNF: ",(endtime-starttime)/ammo,"ms"};

      starttime := time();
      for each j in checkcases do ibalp_kapur(j,'sat,'kapurknf);
      endtime := time();
      ioto_prin2t {"Kombiniert: ",(endtime-starttime)/ammo,"ms"};

      starttime := time();
      for each j in checkcases do ibalp_kapur(j,'sat,'kapur);
      endtime := time();
      ioto_prin2t {"Kapur: ",(endtime-starttime)/ammo,"ms"};

      starttime := time();
      for each j in checkcases do ibalp_kapur(j,'sat,'direct);
      endtime := time();
      ioto_prin2t {"Direkt: ",(endtime-starttime)/ammo,"ms"};
      return nil
   end;

operator testumode;

procedure testinternal(n,m);
   % Test Internal settings. [n] is a positive integer meaning the ammount
   % of variables in the test cases. [m] is a positive integer meaning the 
   % number of clauses in 3KNF per formula.
   % returns nil.
   begin scalar checkcases,starttime,endtime,ammo;
      ammo := 100;
      
      checkcases := for i:=1:ammo collect gen3knf(n,m);
      ioto_prin2t "Vergleich der internen Einstellungen";
      ioto_prin2t {"Anzahl der Variablen: ",n};
      ioto_prin2t {"Anzahl der Klauseln: ",m};
      ioto_prin2t "------------------------------------";
      
      off ibalp_kapurgb;
      vdpsortmode!* := 'lex;
      starttime := time();
      for each j in checkcases do ibalp_kapur(j,'sat,'kapur);
      endtime := time();
      ioto_prin2t {"Buchb (lex): ",(endtime-starttime)/ammo,"ms"};

      vdpsortmode!* := 'gradlex;
      starttime := time();
      for each j in checkcases do ibalp_kapur(j,'sat,'kapur);
      endtime := time();
      ioto_prin2t {"Buchb (gradlex): ",(endtime-starttime)/ammo,"ms"};

      on ibalp_kapurgb;
      starttime := time();
      for each j in checkcases do ibalp_kapur(j,'sat,'kapur);
      endtime := time();
      ioto_prin2t {"KapurGB (gradlex): ",(endtime-starttime)/ammo,"ms"};
      
      starttime := time();
      for each j in checkcases do rl_qe(rl_ex(j,nil),nil);
      endtime := time();
      ioto_prin2t {"QE rlex: ",(endtime-starttime)/ammo,"ms"};

      return nil
   end;

operator testinternal;

end;  % of file
