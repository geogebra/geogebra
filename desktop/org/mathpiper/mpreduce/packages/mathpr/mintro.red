
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

comment
I have put in support for the things that the Twente brigade had, and
indeed when used with care they have jolly good effects on readability.
I have also tinkered with what I had before quite a bit both to clean up
the code in a few places but mostly to fix the odd bug or so. I am
beginning to be quite cheerful about what it now looks like & so am
sending you this copy to play with. The main thing I do not know how
to decide is the default setting of !*nosplit which slows things down
(by doing much more look-ahead during printing) but can sometimes
improve the selection of points to split lines. I suspect very strongly
that behaviour where things are really too bit to split will always be
ugly - if you spot any particular disaster areas and can think of
good fixups....
Here it is, then: given I have the matrix printing & the dfprint option
in too I think this is tolerably compact.


    EXTRA FEATURES OF THE CLISP VERSION

By default use is made of some programmable characters so that integrals,
fractions and square roots are displayed neatly when 'on nat' is selected.
This use of special characters can be disabled by setting
        off clisp
which can be needed if the output from Reduce is to be sent to a printer
that can not handle the special characters. The function
        lisp spool "<filename>"
automatically switches off the clisp flag for this reason, so that the
transcript file only contains ordinary characters.

When they will fit matrices are displayed spread out on the page.
The forms sum(low,high,body), product(low,high,body) are displayed
as formal sums and products (but are not otherwise special at
all), as in
        sum(k=0,infinity,a(k)*x^k/fact k)
where fact now evaluates as the factorial (for positive integer
arguments, and infinity displays as an infinity sign (but does
not have any other special properties). The variable pi displays
as a greek pi character.

A set of facilities suggested by the Algebra group at Twente have been
included - these cause formal derivatives to be displayed as subscript
expressions, and allow other operators to show their arguments as
subscripts. After
        on dfprint
a derivative df(y(a,b),a) will be displayed as y with a suffix a,
and df(y(a,b),a,2,b) will display as
         y
          2a,b
After
         doindex p,q,r
all arguments of the operators p, q and r will be displayed as
subscripts. This effect can be undone by using
         offindex p,q,r
A related declaration
         donoarg p,q
causes all arguments in references to the operators p and q to
be hidden, and offnoargs p,q will cancel the effect. If arguments
have been supressed in this way the statement
        farg
will show what has been hidden.
        clfarg
will reset the record of information about hidden arguments.

A flag called nosplit (which is by default on) causes the Reduce
print code to try harder than has previously been the case to
avoid splitting terms across lines of output. having this switch
enabled slows down printing somewhat, and if this becomes
inconvenient the previous arrangements can be (approximately)
restored by
        off nosplit

;

end;
