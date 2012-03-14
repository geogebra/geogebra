module io; % Functions for handling input and output of files.

% Author: Anthony C. Hearn.

% Copyright (c) 1995 RAND. All rights reserved.

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


fluid '(!*echo !*int !*reduce4 semic!*);

global '(contl!* curline!* ifl!* ipl!* linelist!* ofl!* opl!* techo!*);

symbolic procedure file!-transform(u,v);
   % Performs a transformation on the file u.  V is name of function
   % used for the transformation.
   begin scalar echo,ichan,oldichan,val;
      echo := !*echo;
      !*echo := nil;
      ichan := open(u,'input);
      oldichan := rds ichan;
      val := errorset!*(list v,t);
      !*echo := echo;
      close ichan;
      rds oldichan;
      if not errorp val then return car val
   end;

symbolic procedure infile u;
   % Loads the single file u into REDUCE without echoing.
   begin scalar !*int;
   return file!-transform(u,function begin1)
   end;

symbolic procedure in u; in_non_empty_list u;  % REDUCE 3 hook.

symbolic procedure in_tex u;
   in_non_empty_list1(u,
      '(!\ e n d !{ r e d u c e !}));

symbolic procedure in_non_empty_list u; in_non_empty_list1(u, nil);

symbolic procedure in_non_empty_list1(u, prefixchars);
   begin scalar echop;
      echop := null(semic!* eq '!$); % Record echo character from input.
      if null ifl!* then techo!* := !*echo;   % Terminal echo status.
      if !*reduce4 then u := value u;
      for each fl in u do in_list1a(fl,echop,prefixchars);
      if ipl!* then ifl!* := car ipl!* else ifl!* := nil;
      if ifl!* then curline!* := caddr ifl!*;
      if !*reduce4 then return mkobject(nil,'noval)
   end;

symbolic procedure mkfil!* u;
   % Converts file descriptor U into valid system filename.
   % Allows for u to have an algebraic scalar value.
   begin scalar x;
      return if stringp u then u
              else if not idp u then typerr(u,"file name")
              else if flagp(u,'share) and stringp (x := eval u)
               then x
              else string!-downcase u
   end;

symbolic procedure in_list1(fl,echop);
   in_list1a(fl,echop,nil);

symbolic procedure in_list1a(fl,echop,prefixchars);
   begin scalar chan,echo,ochan,w,w1;
      echo := !*echo;   % Save current echo status.
      if !*reduce4 then if type fl neq 'string then typerr(fl,'string)
                         else fl := value fl;
      fl := mkfil!* fl;
% Now fl is a string and if ifl!* is nil then input had been coming from
% the "terminal", otherwise car ifl!* is the name of the file currently
% being read from.
%
% Now I wish to arrange that if fl is of the form "$/xxx" (or $\xxx") that
% the "$" is replaced by the directory portion of car ifl!*. Or by "." if
% ifl!*=nil or it does not contain a "/" or "\". 
      w := explode2 fl;
      if eqcar(w, '!$) and
         (eqcar(cdr w, '!/) or eqcar(cdr w, '!\)) then <<
        if null ifl!* then fl := compress('!" . !. . append(cdr w, '(!")))
        else <<
          w1 := reverse explode2 car ifl!*;
          while w1 and not (eqcar(w1, '!/) or eqcar(w1, '!\)) do w1 := cdr w1;
          if null w1 then w1 := '(!/ !.);
          w := '!" . append(reverse w1, append(cddr w, '(!")));
          fl := compress w >> >>;
      chan := open(fl,'input);
      ochan := rds chan;
      if assoc(fl,linelist!*) then nil;
      curline!* := 1;
      ifl!* := list(fl,chan,1);
      ipl!* := ifl!* . ipl!*;  % Add to input file stack.
      !*echo := echop;
      begin1a(prefixchars);
      rds ochan;
      close chan;
      !*echo := echo;   % Restore echo status.
      if null ipl!* and contl!* then return nil
       else if null ipl!* or null(fl eq caar ipl!*)
        then rederr list("FILE STACK CONFUSION",fl,ipl!*)
       else ipl!* := cdr ipl!*
   end;

symbolic procedure out u; out_non_empty_list u;  % REDUCE 3 hook.

symbolic procedure out_non_empty_list u;
   % U is a list of one file.
   begin integer n; scalar chan,fl,x;
      n := linelength nil;
      if !*reduce4 then u := value u;
      if null u then return nil;
      u := car u;
      if !*reduce4 then if type u neq 'string then typerr(u,'string)
                         else u := value u;
      if u eq 't then return <<wrs(ofl!* := nil); nil>>;
      fl := mkfil u;
      if not (x := assoc(fl,opl!*))
        then <<chan := open(fl,'output);
               if chan
                 then <<ofl!*:= fl . chan; opl!*:= ofl!* . opl!*>>>>
       else ofl!* := x;
      wrs cdr ofl!*;
      linelength n;
      if !*reduce4 then return mkobject(nil,'noval)
   end;

symbolic procedure shut u; shut_non_empty_list u;  % REDUCE 3 hook.

symbolic procedure shut_non_empty_list u;
   % U is a list of names of files to be shut.
   begin scalar fl1;
      if !*reduce4 then u := value u;
      for each fl in u do
       <<if !*reduce4
          then if type fl neq 'string then typerr(fl,'string)
                else fl := value fl;
         if fl1 := assoc((fl := mkfil fl),opl!*)
           then <<opl!* := delete(fl1,opl!*);
                  if fl1=ofl!* then <<ofl!* := nil; wrs nil>>;
                  close cdr fl1>>
         else if not (fl1 := assoc(fl,ipl!*))
          then rerror(rlisp,26,list(fl,"not open"))
         else if fl1 neq ifl!*
          then <<close cadr fl1; ipl!* := delete(fl1,ipl!*)>>
         else rerror(rlisp,27,
                     list("Cannot shut current input file",car fl1))>>;
      if !*reduce4 then return mkobject(nil,'noval)
   end;

deflist ('((in rlis) (in_tex rlis) (out rlis) (shut rlis)),'stat);  % REDUCE 3 only.

flag ('(in in_tex out shut),'eval);

flag ('(in in_tex out shut),'ignore);

endmodule;

end;
