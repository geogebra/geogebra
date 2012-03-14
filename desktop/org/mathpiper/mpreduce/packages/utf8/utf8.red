% ----------------------------------------------------------------------
% $Id: utf8.red 1400 2011-09-14 07:48:06Z thomas-sturm $
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

lisp <<
   fluid '(utf8_rcsid!* utf8_copyright!*);
   utf8_rcsid!* := "$Id: utf8.red 1400 2011-09-14 07:48:06Z thomas-sturm $";
   utf8_copyright!* := "(c) 2009 T. Sturm"
>>;


module utf8;

create!-package('(utf8),nil);

fluid '(lispsystem!* overflowed!* posn!* testing!-width!* !*nat);

!#if (memq 'psl lispsystem!*)
   fluid '(maxchannels writefunction out!*);
!#endif

switch utf8;
switch utf82d;
switch utf82dround;
switch utf8exp;
switch utf8expall;
switch utf8diffquot;
switch utf8pad;

put('utf82dround,'simpfg,'((t (utf82droundon)) (nil (utf82droundoff))));

procedure utf82droundon();
   <<
      put('utf8_mat!-top!-l,'utf8,'(1 226 142 155));
      put('utf8_mat!-mid!-l,'utf8,'(1 226 142 156));
      put('utf8_mat!-low!-l,'utf8,'(1 226 142 157));
      put('utf8_mat!-top!-r,'utf8,'(1 226 142 158));
      put('utf8_mat!-mid!-r,'utf8,'(1 226 142 159));
      put('utf8_mat!-low!-r,'utf8,'(1 226 142 160))
   >>;

procedure utf82droundoff();
   <<
      put('utf8_mat!-top!-l,'utf8,'(1 226 142 161));
      put('utf8_mat!-mid!-l,'utf8,'(1 226 142 162));
      put('utf8_mat!-low!-l,'utf8,'(1 226 142 163));
      put('utf8_mat!-top!-r,'utf8,'(1 226 142 164));
      put('utf8_mat!-mid!-r,'utf8,'(1 226 142 165));
      put('utf8_mat!-low!-r,'utf8,'(1 226 142 166))
   >>;

on1 'utf8;
on1 'utf82d;
on1 'utf82dround;
off1 'utf8exp;
on1 'utf8expall;
on1 'utf8diffquot;
on1 'utf8pad;

copyd('prin2!*_orig,'prin2!*);
copyd('scprint_orig,'scprint);
copyd('exptpri_orig,'exptpri);

procedure prin2!*(u);
   if not !*utf8 then
      prin2!*_orig u
   else
      utf8_prin2!* u;

procedure utf8_prin2!*(u);
   if outputhandler!* then apply2(outputhandler!*,'prin2!*,u)
   else begin integer m,n,p; scalar x,y;
      if x := get(u,'oldnam) then u := x;
      if overflowed!* then return 'overflowed
      else if !*fort then return fprin2!* u
      else if !*nat then <<
	 if u = 'pi then u := symbol '!.pi
	 else if u = 'infinity then u := symbol 'infinity>>;
      % Suggested by Wolfram Koepf:
      if fixp u and n>50 and !*rounded then return rd!:prin i2rd!* u;
      n := if x := get(u,'utf8) then
 	 car x
      else if (x := utf8_indexsplit u) and (y := get(car x,'utf8)) then
	 car y + cadr x
      else
 	 lengthc u;
      m := posn!* #+ n;
      p := linelength nil - spare!*;
      return if m<=p
	 or (not testing!-width!*
	    % The next line controls whether to add a newline before a long id.
	    % At present it causes one in front of a number too.
	    and <<not fixp u and terpri!* t; (m := posn!* #+ n)<=p>>)
      then add_prin_char(u,m)
	 % Identifier longer than one line.
      else if testing!-width!*
      then <<overflowed!* := t;'overflowed>>
      else prin2lint(u,posn!* #+ 1,p #- 1)
   end;

procedure scprint(u,n);
   <<
      if not !*utf8 then
      	 scprint_orig(u,n)
      else
      	 utf8_scprint(u,n);
      if !*utf8pad then
      	 utf8_dots(cdaar lastcar u - posn!*)
   >>;

procedure utf8_scprint(u,n);
   begin scalar m,w,x,padded;
      posn!* := 0;
      for each v in u do <<
	 if cdar v=n then <<
	    if not((m:= caaar v-posn!*)<0) then
	       if !*utf8pad and not padded then <<
		  utf8_dots m;
		  padded := t
	       >> else
	    	  spaces m;
	    if w := get(cdr v,'utf8) then
	       utf8_tyo w
	    else if w := utf8_indexsplit cdr v then <<
	       if x := get(car w,'utf8) then
		  utf8_tyo x
	       else
	       	  utf8_prin2 car w;
	       utf8_tyo cdr w
	    >> else
	       utf8_prin2 cdr v;
	    posn!* := cdaar v
 	 >>
      >>
   end;

procedure utf8_dots(n);
   for i := 1:n do prin2 " ";

!#if (memq 'psl lispsystem!*)
   procedure utf8_tyo(itml);
      <<
	 setf(wgetv(lineposition,out!*),wgetv(lineposition,out!*)+car itml);
      	 for each itm in cdr itml do
      	    utf8_channelwritechar(out!*,lisp2char itm)
      >>;

   procedure utf8_channelwritechar(channel,char);
      <<
      	 if not wleq(0,channel) and wleq(channel,maxchannels) then
      	    noniochannelerror(channel,"ChannelWriteChar");
      	 idapply(wgetv(writefunction,channel),{channel,char})
      >>;
!#else
   procedure utf8_tyo(itml);
      for each itm in cdr itml do
      	 tyo itm;
!#endif

procedure utf8_prin2(itm);
   prin2 itm;

procedure utf8_indexsplit(u);
   begin integer idxlen; scalar l,d;
      if numberp u or digit u then
	 return nil;
      l := reversip explode u;
      while digit car l do <<
	 idxlen := idxlen + 1;
	 d := append(utf8_subscript car l,d);
      	 l := cdr l
      >>;
      return intern compress reversip l . (idxlen . d)
   end;

procedure utf8_subscript(d);
   cdr atsoc(d,'((!1 . (226 130 129)) (!2 . (226 130 130)) (!3 . (226 130 131))
      (!4 . (226 130 132)) (!5 . (226 130 133)) (!6 . (226 130 134))
      (!7 . (226 130 135)) (!8 . (226 130 136)) (!9 . (226 130 137))
      (!0 . (226 130 128))));

procedure exptpri(x,y);
   if not !*utf8 then
      exptpri_orig(x,y)
   else
      utf8_exptpri(x,y);

procedure utf8_exptpri(x,p);
   begin scalar q,expo,w;
      if not !*nat then
      	 return 'failed;
      if null !*utf8exp or not numberp caddr x then
      	 return exptpri_orig(x,p);
      expo := explode caddr x;
      if null !*utf8expall and utf8_supmixp expo then
	 return exptpri_orig(x,p);
      q := pairp cadr x and (w := get(caadr x,'infix)) and
 	 w <= get('expt,'infix);
      if q then prin2!* "(";
      maprin cadr x;
      if q then prin2!* ")";
      x := compress append('(u t f 8 !_ e x p),expo);
      put(x,'utf8,length expo . for each d in expo join copy utf8_supscript d);
      utf8_prin2!* x
   end;

procedure utf8_supmixp(exp);
   intersection(exp,'(!1 !2 !3)) and intersection(exp,'(!4 !5 !6 !7 !8 !9 !0));

procedure utf8_supscript(d);
   cdr atsoc(d,'((!1 . (194 185)) (!2 . (194 178)) (!3 . (194 179))
      (!4 . (226 129 180)) (!5 . (226 129 181)) (!6 . (226 129 182))
      (!7 . (226 129 183)) (!8 . (226 129 184)) (!9 . (226 129 185))
      (!0 . (226 129 176))));

procedure utf8_priabs(u);
   if not !*nat then
      'failed
   else if not !*utf8 then
      'failed
   else <<
      prin2!* "|";
      maprin cadr u;
      prin2!* "|"
   >>;

procedure utf8_pripartial(u);
   if not !*nat then
      'failed
   else if not !*utf8 then
      'failed
   else <<
      utf8_prin2!* car u;
      maprin cadr u
   >>;

procedure utf8_pridiff(u);
   if not !*nat then
      'failed
   else if not !*utf8 then
      'failed
   else if !*utf8diffquot then <<
      maprin {'quotient,
	 if eqn(cadddr u,1) then
 	    {'partial,cadr u}
 	 else
 	    {'powpartial,cadr u,cadddr u},
	 if eqn(cadddr u,1) then
 	    {'partial,caddr u}
 	 else
 	    {'expt,{'partial,caddr u},cadddr u}}
   >> else <<
      if eqn(cadddr u,1) then
	 utf8_prin2!* 'partial
      else
      	 maprin {'expt,'partial,cadddr u};
      utf8_prin2!* caddr u;
      utf8_prin2!* "(";
      maprin cadr u;
      utf8_prin2!* ")";
   >>;

procedure utf8_pripowpartial(u);
   <<
      maprin {'expt,'partial,caddr u};
      maprin cadr u
   >>;

procedure utf8_priint(u);
   if not !*nat then
      'failed
   else if not !*utf8 then
      'failed
   else if !*utf82d then
      intprint u
   else <<
      utf8_prin2!* car u;
      if cdddr u then <<
	 utf8_prin2!* "[";
	 maprin cadddr u;
	 utf8_prin2!* ",";
	 maprin car cddddr u;
	 utf8_prin2!* "]"
      >>;
      utf8_prin2!* " ";
      maprin cadr u;
      utf8_prin2!* " d";
      utf8_prin2!* caddr u
   >>;

procedure intprint u;
   % Hijacked from mathpr/xprint.red.
   if not !*nat or !*fort then 'failed
   else begin
      scalar m;
      prin2!* symbol 'int!-mid;
      m := posn!* - 1;
      pline!* := (((m . posn!*) . (ycoord!* + 1)) .
                      symbol 'int!-top) . pline!*;
      pline!* := (((m . posn!*) . (ycoord!* - 1)) .
                      symbol 'int!-low) . pline!*;
      if ycoord!*+1>ymax!* then ymax!* := ycoord!*+1;
      if ymin!*>ycoord!*-1 then ymin!* := ycoord!*-1;
      prin2!* " ";
      maprin cadr u;
      prin2!* " ";
      prin2!* symbol 'd;
      maprin caddr u
   end;

procedure symbol(s);
   if !*utf8 and !*utf82d then
      get(s,'utf8_2d!-symbol!-character) or
      get(s,'utf8_symbol!-character) or get(s,'symbol!-character)
   else if !*utf8 then
      get(s,'utf8_symbol!-character) or get(s,'symbol!-character)
   else
      get(s,'symbol!-character);

put('ex,'utf8,'(1 226 136 131));
put('all,'utf8,'(1 226 136 128));
put('not,'utf8,'(2 194 172 32));
put('and,'utf8,'(1 226 136 167));
put('or,'utf8,'(1 226 136 168));
put('repl,'utf8,'(1 226 134 144));
put('impl,'utf8,'(1 226 134 146));
put('equiv,'utf8,'(1 226 134 148));
%put('repl,'utf8,'(2 226 159 181 32));
%put('impl,'utf8,'(2 226 159 182 32));
%put('equiv,'utf8,'(2 226 159 183 32));
put('bex,'utf8,'(1 226 168 134));
put('ball,'utf8,'(1 226 168 133));

put('reals,'utf8,'(1 226 132 157));
put('ofsf,'utf8,'(1 226 132 157));
put('integers,'utf8,'(1 226 132 164));
put('pasf,'utf8,'(1 226 132 164));
put('boolean,'utf8,'(1 240 157 148 185));
put('ibalp,'utf8,'(1 240 157 148 185));

put('!>!=,'utf8,'(1 226 137 165));
put('!<!=,'utf8,'(1 226 137 164));
put('!<!>,'utf8,'(1 226 137 160));

put('cong,'utf8,'(1 226 137 161));
put('ncong,'utf8,'(1 226 137 161 226 128 139 204 184));
%put('ncong,'utf8,'(1 226 137 162);
%put('ncong,'utf8,'(1 226 137 161 226 131 146));

put('infinity,'utf8,'(1 226 136 158));
put('infty,'utf8,'(1 226 136 158));

put('!*,'utf8,'(1 226 139 133));

put('bar,'utf8_symbol!-character,'utf8_bar);
put('utf8_bar,'utf8,'(1 226 128 149));

put('alpha,'utf8,'(1 206 177));
put('beta,'utf8,'(1 206 178));
put('gamma,'utf8,'(1 206 179));
put('delta,'utf8,'(1 206 180));
put('epsilon,'utf8,'(1 206 181));
put('zeta,'utf8,'(1 206 182));
put('eta,'utf8,'(1 206 183));
put('theta,'utf8,'(1 206 184));
put('iota,'utf8,'(1 206 185));
put('kappa,'utf8,'(1 206 186));
put('lambda,'utf8,'(1 206 187));
put('mu,'utf8,'(1 206 188));
put('nu,'utf8,'(1 206 189));
put('xi,'utf8,'(1 206 190));
put('omikron,'utf8,'(1 206 191));
put('pi,'utf8,'(1 207 128));
put('rho,'utf8,'(1 207 129));
put('sigma,'utf8,'(1 207 131));
put('tau,'utf8,'(1 207 132));
put('ypsilon,'utf8,'(1 207 133));
put('phi,'utf8,'(1 207 134));
put('chi,'utf8,'(1 207 135));
put('psi,'utf8,'(1 207 136));
put('omega,'utf8,'(1 207 137));

put('int,'utf8,'(1 226 136 171));
put('int!-top,'utf8_2d!-symbol!-character,'utf8_int!-top);
put('utf8_int!-top,'utf8,'(1 226 140 160));
put('int!-mid,'utf8_2d!-symbol!-character,'utf8_int!-mid);
put('utf8_int!-mid,'utf8,'(1 226 142 174));
put('int!-low,'utf8_2d!-symbol!-character,'utf8_int!-low);
put('utf8_int!-low,'utf8,'(1 226 140 161));
put('int,'prifn,'utf8_priint);
put('abs,'prifn,'utf8_priabs);

put('partial,'utf8,'(1 226 136 130));
put('partial,'prifn,'utf8_pripartial);
put('powpartial,'prifn,'utf8_pripowpartial);  % Hack but how else ...?

put('diff,'prifn,'utf8_pridiff);

put('!*!*!*,'utf8,'(1 226 136 153));

put('mat!-top!-l,'utf8_2d!-symbol!-character,'utf8_mat!-top!-l);
put('mat!-mid!-l,'utf8_2d!-symbol!-character,'utf8_mat!-mid!-l);
put('mat!-low!-l,'utf8_2d!-symbol!-character,'utf8_mat!-low!-l);
put('mat!-top!-r,'utf8_2d!-symbol!-character,'utf8_mat!-top!-r);
put('mat!-mid!-r,'utf8_2d!-symbol!-character,'utf8_mat!-mid!-r);
put('mat!-low!-r,'utf8_2d!-symbol!-character,'utf8_mat!-low!-r);

endmodule;  % utf8

end;  % of file
