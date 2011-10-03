/* {{{ License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */ //}}}

// :indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:folding=explicit:collapseFolds=0:
package org.mathpiper.lisp.tokenizers;

class CommonLispTokenizer extends MathPiperTokenizer
{
/*
class CommonLispTokenizer extends LispTokenizer
{
    String NextToken(LispInput aInput, LispHashTable aHashTable)
  {
    char c;
    int firstpos;

REDO://TODO FIXME
    
    while(true)
    {
    firstpos = aInput.Position();

    // End of stream: return empty string
    if (aInput.EndOfStream())
    {
        return aHashTable.LookUpCounted(&aInput.StartPtr()[firstpos],aInput.Position()-firstpos);
    }
      //goto FINISH;

    c = aInput.Next();
    //printf("%c",c);

    //Parse brackets
    if (c == '(')      {}
    else if (c == ')') {}
    else if (c == '{') {}
    else if (c == '}') {}
    else if (c == '[') {}
    else if (c == ']') {}
    else if (c == ',') {}
    else if (c == '\'') {}
    else if (c == '%') {}
  //    else if (c == '\'') {}
    else if (c == '.' && !IsDigit(aInput.Peek()) )
    {
      while (aInput.Peek() == '.')
      {
        aInput.Next();
      }
    }
    // parse comments
    else if (c == '/' && aInput.Peek() == '*')
    {
      aInput.Next(); //consume *
FALSEALARM://TODO FIXME
      while (aInput.Next() != '*' && !aInput.EndOfStream());
      Check(!aInput.EndOfStream(),KLispErrCommentToEndOfFile);
      if (aInput.Peek() == '/')
      {
        aInput.Next();  // consume /
        goto REDO;
      }
      goto FALSEALARM;
    }
    else if (c == ';')
    {
      while (aInput.Next() != '\n' && !aInput.EndOfStream());
      goto REDO;
    }
    // parse literal strings
    else if (c == '\"')
    {
      LispString aResult;
      aResult.Resize(0);
      aResult.Append(c);
      while (aInput.Peek() != '\"')
      {
        if (aInput.Peek() == '\\')
        {
          aInput.Next();
          Check(!aInput.EndOfStream(),KLispErrParsingInput);
        }
        aResult.Append(aInput.Next());
        Check(!aInput.EndOfStream(),KLispErrParsingInput);
      }
      aResult.Append(aInput.Next()); // consume the close quote
      aResult.Append('\0');
      return aHashTable.LookUp(aResult.String());
    }
    //parse atoms
    else if (IsAlpha(c) || IsSymbolic(c))
    {
      while (IsAlNum( aInput.Peek()) || IsSymbolic( aInput.Peek()))
      {
        aInput.Next();
      }
    }
    else if (c == '_')
    {
      while (aInput.Peek() == '_')
      {
        aInput.Next();
      }
    }
    else if (IsDigit(c) || c == '.')
    {
      while (IsDigit( aInput.Peek())) aInput.Next();
      if (aInput.Peek() == '.')
      {
        aInput.Next();
        while (IsDigit( aInput.Peek())) aInput.Next();
      }
      if (NumericSupportForMantissa())
      {
        if (aInput.Peek() == 'e' || aInput.Peek() == 'E')
        {
          aInput.Next();
          if (aInput.Peek() == '-' || aInput.Peek() == '+')
            aInput.Next();
          while (IsDigit( aInput.Peek())) aInput.Next();
        }
      }
    }
    // Treat the char as a space.
    else
    {
      goto REDO;
    }

//FINISH://TODO FIXME
    return aHashTable.LookUpCounted(&aInput.StartPtr()[firstpos],aInput.Position()-firstpos);
  }
  
   */
}

