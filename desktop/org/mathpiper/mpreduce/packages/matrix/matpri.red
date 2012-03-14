module matpri;   % Matrix printing routines.

% Author: Anthony C. Hearn.
% Modified by Arthur C. Norman.

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


fluid '(!*nat obrkp!* orig!* pline!* posn!* ycoord!* ymax!* ymin!*);

symbolic procedure setmatpri(u,v);
   matpri1(cdr v,u);

put('mat,'setprifn,'setmatpri);

symbolic procedure matpri u;
   matpri1(cdr u,nil);

symbolic procedure matpri1(u,x);
   % Prints a matrix canonical form U with name X.
   % Tries to do fancy display if nat flag is on.
   begin scalar m,n,r,l,w,e,ll,ok,name,nw,widths,firstflag,toprow,lbar,
                rbar,realorig;
      if !*fort
        then <<m := 1;
               if null x then x := "MAT";
               for each y in u do
                  <<n := 1;
                    for each z in y do
                       <<assgnpri(z,list list(x,m,n),'only);
                         n := n+1>>;
                    m := m+1>>;
               return nil>>;
      terpri!* t;
      if x and !*nat then <<
         name := layout!-formula(x, 0, nil);
         if name then <<
           nw := cdar name + 4;
           ok := !*nat >>>>
       else <<nw := 0; ok := !*nat>>;
      ll := linelength nil - spare!* - orig!* - nw;
      m := length car u;
      widths := mkvect(1 + m);
      for i := 1:m do putv(widths, i, 1);
      % Collect sizes for all elements to see if it will fit in
      % displayed matrix form.
      % We need to compute things wrt a zero orig for the following
      % code to work properly.
      realorig := orig!*;
      orig!* := 0;
      if ok then for each y in u do
       <<n := 1;
         l := nil;
         w := 0;
         if ok then for each z in y do if ok then <<
            e := layout!-formula(z, 0, nil);
              if null e then ok := nil
              else begin
                scalar col;
                col := max(getv(widths, n), cdar e);
% this allows for 2 blanks between cols, and also 2 extra chars, one
% for the left-bar and one for the right-bar.
                if (w := w + col + 2) > ll then ok := nil
                else <<
                  l := e . l;
                  putv(widths, n, col) >> end;
            n := n+1>>;
         r := (reverse l) . r >>;
         if ok then <<
         % Matrix will fit in displayed representation.
         % Compute format with respect to 0 posn.
         firstflag := toprow := t;
         r := for each py on reverse r collect begin
            scalar y, ymin, ymax, pos, pl, k, w;
            ymin := ymax := 0;
            pos := 1;    % Since "[" is of length 1.
            k := 1;
            pl := nil;
            y := car py;
            for each z in y do <<
               w := getv(widths, k);
               pl := append(update!-pline(pos+(w-cdar z)/2,0,caar z),
                            pl);      % Centre item in its field
               pos := pos + w + 2;    % 2 blanks between cols
               k := k + 1;
               ymin := min(ymin, cadr z);
               ymax := max(ymax, cddr z) >>;
            k := nil;
            if firstflag then firstflag := nil
             else ymax := ymax + 1;   % One blank line between rows
            for h := ymax step -1 until ymin do <<
               if toprow then <<
                  lbar := symbol 'mat!-top!-l;
                  rbar := symbol 'mat!-top!-r;
                  toprow := nil >>
                else if h = ymin and null cdr py then <<
                  lbar := symbol 'mat!-low!-l;
                  rbar := symbol 'mat!-low!-r >>
%               else lbar := rbar := symbol 'vbar;
                else <<lbar := symbol 'mat!-mid!-l;
                       rbar := symbol 'mat!-mid!-r>>;
               pl := ((((pos - 2) . (pos - 1)) . h) . rbar) . pl;
               k := (((0 . 1) . h) . lbar) . k >>;
            return (append(pl, k) . pos) . (ymin . ymax) end;
         orig!* := realorig;
         w := 0;
         for each y in r do w := w + (cddr y - cadr y + 1);
               % Total height.
         n := w/2;  % Height of mid-point.
         u := nil;
         for each y in r do <<
            u := append(update!-pline(0, n - cddr y, caar y), u);
            n := n - (cddr y - cadr y + 1) >>;
         if x then <<maprin x; oprin 'setq >>;
         pline!* := append(update!-pline(posn!*,ycoord!*,u),
                           pline!*);
         ymax!* := max(ycoord!* + w/2, ymax!*);
         ymin!* := min(ycoord!* + w/2 - w, ymin!*);
         terpri!*(not !*nat)>>
      else <<if x then <<maprin x; oprin 'setq>>; matpri2 u>>
   end;

symbolic procedure matpri2 u;
   begin scalar y;
      prin2!* 'mat;
      prin2!* "(";
      obrkp!* := nil;
      y := orig!*;
      orig!* := if posn!*<18 then posn!* else orig!*+3;
      while u do
         <<prin2!* "(";
           orig!* := orig!*+1;
           inprint('!*comma!*,0,car u);
           prin2!* ")";
           if cdr u
             then <<oprin '!*comma!*; orig!* := orig!*-1;
                    terpri!* !*nat>>;
           u := cdr u>>;
      obrkp!* := t;
      orig!* := y;
      prin2!* ")";
      if null !*nat then prin2!* "$";
      terpri!* t
   end;

endmodule;

end;
