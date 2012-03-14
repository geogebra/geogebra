module proc4;  % Support for REDUCE 4 procedures.

% Author:  Anthony C. Hearn, Eberhard Schruefer.

% Copyright (c) 1996 RAND.  All rights reserved.

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


fluid '(!*specification_reduce !*specification);

fluid '(!*spec);

switch spec;

put('spec,'simpfg,'((t (setq !*specification_reduce t) nil)));
%                      (de type_reduce (u v)
%                          (type_reduce1 (list v u)))))));

symbolic procedure n_formproc(u,vars);
   begin scalar body,name,truename,type,typelist,varlis,x,y,
                mode,constraint,pckg_orig;
        u := cdr u;
        name := car u;
        if cadr u then mode := cadr u else mode := 'generic;
        u := cddr u;
        type := if atom car u then car u
                 else caar u;
        pckg_orig := if atom car u then nil
                      else cdar u;
        if flagp(name,'lose) and (!*lose or null !*defn)
          then return progn(lprim list(name,
                            "not defined (LOSE flag)"),
                        nil)
         else if !*redeflg!* and getd name
          then lprim list(name,"redefined");
        varlis := cadr u;
        u := caddr u;
        x := if eqcar(u,'block) then cadr u else nil;
        y := append(varlis,x);
        typelist := for each j in varlis collect cdr j;
        varlis := for each j in varlis collect car j;
        constraint := mode;
        truename := name;
        if !*specification_reduce
           then if (name := get_disambop(name,typelist,mode)) then nil
                 else rederr {"no rank definition found for",name}
         else name := mkrankedname(name,typelist,if atom mode then nil %mode
                                            else caar mode);
        if null atom mode and cdr mode
           then <<mode := caar mode;
                  constraint := {mode, mkquote
                                {'lambda,varlis,{'cond,{caadr constraint,
                                                  mkquote name}}}}>>
         else constraint := {varlis,{t,mkquote {name,mode}}};
        body := n_form1(u,y);
        if not(mode eq 'generic) and !*specification
          then if type body eq 'generic then body := value body
% Should issue a warning that result type generic prevents type consistency
% check
                else if xtype1(type body,mode) then body := value body
                else if xtype1(mode,type body)
                        then <<lprim
      {"procedure definition is only valid if type of body",type body,
       "is retractable to",mode};
                               body := mkretract(type body,mode,value body)>>
                else rederr {"procedure type",mode,
                             "is unrelated to ceiling type",
                              type body,"of procedure body"}
        else if mode eq 'generic then body := value body
        else body := {'check_type,value body,mkquote mode};
        if !*nosmacros and type eq 'smacro then type := 'expr;

% ---

        if type eq 'expr then body := list('de,name,varlis,body)
         else if type eq 'fexpr then body := list('df,name,varlis,body)
         else if type eq 'macro then body := list('dm,name,varlis,body)
         else if type eq 'emb then return embfn(name,varlis,body)
         else body := {'putc,
                        mkquote name,
                        mkquote type,
                        mkquote {'lambda,varlis,body}};
        body := if !*specification_reduce % should check if we have a rank
                   then {mode,{'progn,body,
                                mkquote mkobject(truename,'variable)}}
                 else {mode,{'progn,
                       {'addrank0,mkquote truename,mkquote typelist,
                           mkquote constraint},
                       {'put,mkquote name,''pckg_orig,mkquote pckg_orig},
                        body,mkquote mkobject(truename,'variable)}};
        if !*defn and type memq '(fexpr macro smacro) then lispeval body;
        return body
   end;

symbolic procedure mkretract(atyp,ttyp,exp);
   {'retract,mkquote atyp,mkquote ttyp,exp};

symbolic procedure retract(atyp,ttyp,exp);
   if xtype1(ttyp,type exp) then exp
    else rederr "was not retractable";

put('procedure,'n_formfn,'n_formproc);

endmodule;

end;
