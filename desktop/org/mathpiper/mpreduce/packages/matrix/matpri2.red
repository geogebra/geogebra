MODULE MATPRI; % matrix prettyprinter

% Author: Takeyuki Takahashi, Toyohashi University of Technology.

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


GLOBAL '(!&COUNT!& !&M!-P!-FLAG!& !&NAME !&NAMEARRAY);

% General functions.

SYMBOLIC PROCEDURE TERPRI!* U;
   BEGIN INTEGER N;
      IF !&M!-P!-FLAG!& THEN <<!&COUNT!& := T; GO TO C>>;
      IF !*FORT THEN RETURN FTERPRI U
       ELSE IF NOT PLINE!* OR NOT !*NAT THEN GO TO B;
      N := YMAX!*;
      PLINE!* := REVERSE PLINE!*;
    A:
      SCPRINT(PLINE!*,N);
      TERPRI();
      IF N=YMIN!* THEN GO TO B;
      N := N - 1;
      GO TO A;
    B:
      IF U THEN TERPRI();
    C:
      PLINE!* := NIL;
      POSN!* := ORIG!*;
      YCOORD!* := YMAX!* := YMIN!* := 0
   END;

SYMBOLIC PROCEDURE PLUS!-L U; PLUS!-L1(0,U);

SYMBOLIC PROCEDURE PLUS!-L1(N,U);
   IF NULL U THEN N ELSE <<N := N + CAR U; PLUS!-L1(N,CDR U)>>;

SYMBOLIC PROCEDURE DELNTH(N,L);
   IF N=1 THEN CDR L ELSE CAR L . DELNTH(N - 1,CDR L);


% MATRIX Pretty printer.

SYMBOLIC PROCEDURE MAT!-P!-PRINT U;
   BEGIN INTEGER C!-LENG1,ICOLN,PP,ICOL,COLUMN!-LENG,M,N;
         SCALAR COLUMN!-S!-POINT,MAXLENG,ELEMENT!-LENG;
      U := CDR U;
      ICOLN := LENGTH CAR U;
      ICOL := LINELENGTH NIL - 8;
      !&M!-P!-FLAG!& := T;
      ELEMENT!-LENG := !&COUNT U;
      !&M!-P!-FLAG!& := NIL;
    A:
      MAXLENG := !&MAX!-ROW ELEMENT!-LENG;
      C!-LENG1 := PLUS!-L MAXLENG + 3*(ICOLN - 1);
      IF C!-LENG1=COLUMN!-LENG THEN GO TO DUMP;
      COLUMN!-LENG := C!-LENG1;
      IF COLUMN!-LENG>ICOL
        THEN <<ELEMENT!-LENG :=
                SUBST( - 1,MAXL MAXLENG,ELEMENT!-LENG);
               GO TO A>>;
      PRIN2!* !&NAME;
      PRIN2!* " := ";
      TERPRI!* NIL;
      N := 0;
      COLUMN!-S!-POINT :=
       FOR EACH Y IN MAXLENG COLLECT <<N := N + Y;
                                       N := N + 3;
                                       N + 3>>;
      COLUMN!-S!-POINT := APPEND(LIST 3,COLUMN!-S!-POINT);
      TERPRI();
      PRIN2 "|-";
      SPACES (COLUMN!-LENG + 4);
      PRIN2 "-|";
      TERPRI();
      M := 1;
      FOR EACH Y IN U DO
         <<N := 1;
           FOR EACH Z IN Y DO
              <<POSN!* := NTH(COLUMN!-S!-POINT,N);
                IF NTH(NTH(ELEMENT!-LENG,M),N)<0
                  THEN <<PRIN2!* "*";
                         PRIN2!* "(";
                         PRIN2!* M;
                         PRIN2!* ",";
                         PRIN2!* N;
                         PRIN2!* ")">>
                 ELSE MAPRIN Z;
                N := N + 1>>;
           PP := COLUMN!-LENG + 7;
           FOR I := YMIN!*:YMAX!* DO
              <<PLINE!* := APPEND(PLINE!*, LIST(((0 . 1) . I) . "|"));
                PLINE!* := APPEND(LIST(((PP . (PP + 1)) . I) . "|"),
                                  PLINE!*)>>;
           TERPRI!* NIL;
           M := M + 1;
           PRIN2 "| ";
           SPACES (COLUMN!-LENG + 4);
           PRIN2 " |";
           TERPRI()>>;
      PRIN2 "|-";
      SPACES (COLUMN!-LENG + 4);
      PRIN2 "-|";
      TERPRI();
      TERPRI();
      M := 1;
      FOR EACH Y IN U DO
         <<N := 1;
           FOR EACH Z IN Y DO
              <<IF NTH(NTH(ELEMENT!-LENG,M),N)<0
                       THEN <<PRIN2!* "*";
                              PRIN2!* "(";
                              PRIN2!* M;
                              PRIN2!* ",";
                              PRIN2!* N;
                              PRIN2!* ")";
                              PRIN2!* " ";
                              MAPRIN Z;
                              TERPRI!* T>>;
                N := N + 1>>;
           M := M + 1>>;
      RETURN NIL;
    DUMP:
      PRIN2T "Column length too long";
      MATPRI!*('MAT . U,LIST MKQUOTE !&NAME,'ONLY)
   END;

SYMBOLIC PROCEDURE !&COUNT U;
   BEGIN INTEGER N;
      RETURN FOREACH Y IN U COLLECT
                FOREACH Z IN Y COLLECT
                   <<!&COUNT!& := NIL;
                     MAPRIN Z;
                     N := POSN!*;
                     PLINE!* := NIL;
                     POSN!* := ORIG!*;
                     YCOORD!* := YMAX!* := YMIN!* := 0;
                     IF NULL !&COUNT!& THEN N ELSE MINUS N>>;
   END;

GLOBAL '(!&MAX!-L);

SYMBOLIC PROCEDURE !&MAX!-ROW U;
   BEGIN SCALAR V;
    A:
      IF NULL CAR U THEN RETURN V;
      U := !&MAX!-ROW1 U;
      V := APPEND(V,LIST !&MAX!-L);
      GO TO A
   END;

SYMBOLIC PROCEDURE !&MAX!-ROW1 U;
   BEGIN
      !&MAX!-L := 1;
      RETURN FOR EACH Y IN U COLLECT
                <<!&MAX!-L := IF CAR Y<0 THEN 6
                               ELSE MAX(!&MAX!-L,CAR Y);
                  CDR Y>>
   END;

SYMBOLIC PROCEDURE MAXL U; MAXL1(CDR U,CAR U);

SYMBOLIC PROCEDURE MAXL1(U,V);
   IF NULL U THEN V
    ELSE IF CAR U>V THEN MAXL1(CDR U,CAR U)
    ELSE MAXL1(CDR U,V);

SYMBOLIC PROCEDURE MPRINT U;
   BEGIN SCALAR V;
    A:
      IF NULL U THEN RETURN NIL
       ELSE IF ATOM CAR U AND (V := GET(CAR U,'MATRIX))
        THEN <<!&NAME := CAR U;
               MAT!-P!-PRINT V;
               !&NAME := NIL>>
       ELSE IF STRINGP CAR U THEN VARPRI(CAR U,NIL,'ONLY)
       ELSE IF V := ARRAYP CAR U
        THEN <<!&NAMEARRAY := CAR U;
               PRINT!-ARRAY2(LIST V,NIL);
               !&NAMEARRAY := NIL;
               NIL>>
       ELSE <<!&NAME := CAR U;
              RAT!-P!-PRINT AEVAL CAR U;
              !&NAME := NIL>>;
    B:
      U := CDR U;
      GO TO A
   END;

RLISTAT '(MPRINT);

SYMBOLIC PROCEDURE PRINT!-ARRAY2(U,W);
   BEGIN INTEGER N; SCALAR V;
      V := CAR U;
      IF CAR V EQ '!&VECTOR
        THEN BEGIN
                N := CADR V;
                V := CDR V;
                IF W THEN W := CAR W;
                FOR I := 0:N DO
                   <<V := CDR V;
                     PRINT!-ARRAY2(V,LIST APPEND(W,LIST I))>>
             END
       ELSE IF V NEQ 0
        THEN <<!&NAME := APPEND(LIST !&NAMEARRAY,CAR W);
               RAT!-P!-PRINT V;
               !&NAME := NIL>>
   END;


% Rational function Pretty printer.

SYMBOLIC PROCEDURE RAT!-P!-PRINT U;
   BEGIN INTEGER OS,LN,ORGNUM,ORGDEN,LL,LENNUM,LENDEN;
         SCALAR NAME,UDEN,UNUM;
      IF NULL U THEN RETURN NIL;
      IF NUMBERP U
        THEN <<VARPRI(U,LIST MKQUOTE !&NAME,'ONLY);
               TERPRI();
               !&NAME := NIL;
               RETURN NIL>>;
      U := CADR U;
      !&M!-P!-FLAG!& := T;
      LENDEN := !&COUNT!-LENGTH (UDEN := CDR U./1);
      LENNUM := !&COUNT!-LENGTH (UNUM := CAR U./1);
      !&M!-P!-FLAG!& := NIL;
      LN := (LINELENGTH NIL - LENGTHC !&NAME) - 4;
      OS := ORIG!*;
      IF CDR U=1 OR LENDEN>LN OR LENNUM>LN THEN GO TO DUMP;
      IF !&NAME
        THEN <<INPRINT('SETQ,2,LIST !&NAME);
               OPRIN 'SETQ;
               NAME := PLINE!*;
               OS := POSN!*;
               !&NAME := NIL;
               PLINE!* := NIL>>;
      IF LENDEN>LENNUM
        THEN <<ORGNUM := (LENDEN - LENNUM)/2; LL := LENDEN>>
       ELSE <<ORGDEN := (LENNUM - LENDEN)/2; LL := LENNUM>>;
      POSN!* := ORGNUM + OS + 1;
      MAPRIN MK!*SQ UNUM;
      TERPRI!* NIL;
      IF NAME THEN PLINE!* := NAME ELSE PLINE!* := NIL;
      POSN!* := OS;
      FOR I := 1:LL + 2 DO PRIN2!* "-";
      TERPRI!* NIL;
      POSN!* := ORGDEN + OS + 1;
      MAPRIN MK!*SQ UDEN;
      TERPRI!* T;
      RETURN NIL;
    DUMP:
      VARPRI(MK!*SQ U,LIST MKQUOTE !&NAME,'ONLY);
      TERPRI();
      !&NAME := NIL
   END;

SYMBOLIC PROCEDURE !&COUNT!-LENGTH U;
   BEGIN INTEGER N;
      !&COUNT!& := NIL;
      MAPRIN MK!*SQ U;
      N := POSN!* - ORIG!*;
      IF !&COUNT!& THEN N := LINELENGTH NIL + 10;
      PLINE!* := NIL;
      POSN!* := ORIG!*;
      YCOORD!* := YMAX!* := YMIN!* := 0;
      RETURN N
   END;

ENDMODULE;

END;
