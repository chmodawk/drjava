/*BEGIN_COPYRIGHT_BLOCK*

PLT Utilities BSD License

Copyright (c) 2007-2008 JavaPLT group at Rice University
All rights reserved.

Developed by:   Java Programming Languages Team
                Rice University
                http://www.cs.rice.edu/~javaplt/

Redistribution and use in source and binary forms, with or without modification, are permitted 
provided that the following conditions are met:

    - Redistributions of source code must retain the above copyright notice, this list of conditions 
      and the following disclaimer.
    - Redistributions in binary form must reproduce the above copyright notice, this list of 
      conditions and the following disclaimer in the documentation and/or other materials provided 
      with the distribution.
    - Neither the name of the JavaPLT group, Rice University, nor the names of the library's 
      contributors may be used to endorse or promote products derived from this software without 
      specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS AND 
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*END_COPYRIGHT_BLOCK*/

package edu.rice.cs.plt.text;

import java.io.StringReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.iter.SizedIterable;
import edu.rice.cs.plt.recur.RecurUtil;
import edu.rice.cs.plt.collect.OneToOneMap;
import edu.rice.cs.plt.collect.OneToOneHashMap;
import edu.rice.cs.plt.lambda.Lambda;
import edu.rice.cs.plt.lambda.Thunk;
import edu.rice.cs.plt.lambda.LazyThunk;

public final class TextUtil {
  
  public static final String NEWLINE = System.getProperty("line.separator", "\n");
  
  /** Prevents instance creation */
  private TextUtil() {}
  
  /**
   * Convert the given object to a string.  This method invokes {@link RecurUtil#safeToString(Object)}
   * to provide simple, safe handling of {@code null} values, arrays, and self-referential data structures
   * (with cooperation from the {@code toString()} method of the relevant class).
   */
  public static String toString(Object o) {
    return RecurUtil.safeToString(o);
  }
  
  /**
   * Break a string into a list of lines.  {@code "\n"}, {@code "\r"}, and {@code "\r\n"}
   * are considered line delimiters.  The empty string is taken to contain 0 lines.  An optional final
   * trailing newline will be ignored.
   */
  public static SizedIterable<String> getLines(String s) {
    SizedIterable<String> result = IterUtil.<String>empty();
    BufferedReader r = new BufferedReader(new StringReader(s));
    try {
      String line = r.readLine();
      while (line != null) {
        result = IterUtil.compose(result, line);
        line = r.readLine();
      }
    }
    catch (IOException e) {
      // Should not happen with a StringReader, but if it does, just ignore it
    }
    finally { 
      try { r.close(); }
      catch (IOException e) { /* ignore */ }
    }
    return result;
  }
  
  /** Produce a string by concatenating {@code copies} instances of {@code s} */
  public static String repeat(String s, int copies) {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < copies; i++) { result.append(s); }
    return result.toString();
  }
  
  /** Produce a string by concatenating {@code copies} instances of {@code c} */
  public static String repeat(char c, int copies) {
    char[] result = new char[copies];
    Arrays.fill(result, c);
    return String.valueOf(result);
  }
  
  /** Create a string of (at least) the given length by filling in copies of {@code c} to the left of {@code s}. */
  public static String padLeft(String s, char c, int length) {
    StringBuilder result = new StringBuilder();
    int delta = length - s.length();
    for (int i = 0; i < delta; i++) { result.append(c); }
    result.append(s);
    return result.toString();
  }
  
  /** Create a string of (at least) the given length by filling in copies of {@code c} to the right of {@code s}. */
  public static String padRight(String s, char c, int length) {
    StringBuilder result = new StringBuilder();
    result.append(s);
    int delta = length - s.length();
    for (int i = 0; i < delta; i++) { result.append(c); }
    return result.toString();
  }
  
  /** Determine if the given character occurs in {@code s}.  Defined in terms of {@link String#indexOf(int)}. */
  public static boolean contains(String s, int character) { return s.indexOf(character) >= 0; }
  
  /**
   * Determine if the given string occurs in {@code s}.  Defined in terms of {@link String#indexOf(String)}.
   * This is also defined as {@link String#contains}, but is defined here for legacy support.
   */
  public static boolean contains(String s, String piece) { return s.indexOf(piece) >= 0; }
  
  /**
   * Determine if <em>any</em> of the given characters occurs in {@code s}.  Defined in terms of
   * {@link String#indexOf(int)}.
   */
  public static boolean containsAny(String s, int... characters) {
    for (int c: characters) { if (contains(s, c)) { return true; } }
    return false;
  }
  
  /**
   * Determine if <em>any</em> of the given strings occurs in {@code s}.  Defined in terms of
   * {@link String#indexOf(String)}.
   */
  public static boolean containsAny(String s, String... pieces) {
    for (String piece: pieces) { if (contains(s, piece)) { return true; } }
    return false;
  }
  
  /**
   * Determine if <em>all</em> of the given characters occur in {@code s}.  Defined in terms of
   * {@link String#indexOf(int)}.
   */
  public static boolean containsAll(String s, int... characters) {
    for (int c: characters) { if (!contains(s, c)) { return false; } }
    return true;
  }
  
  /**
   * Determine if <em>all</em> of the given strings occur in {@code s}.  Defined in terms of
   * {@link String#indexOf(String)}.
   */
  public static boolean containsAll(String s, String... pieces) {
    for (String piece: pieces) { if (!contains(s, piece)) { return false; } }
    return true;
  }
  
  /**
   * Determine if the given string occurs in {@code s}, ignoring differences in case.  Unlike 
   * {@link String#equalsIgnoreCase}, this test only compares the lower-case conversion of {@code s} to the lower-case
   * conversion of {@code piece}.
   */
  public static boolean containsIgnoreCase(String s, String piece) {
    return s.toLowerCase().indexOf(piece.toLowerCase()) >= 0;
  }
  
  /**
   * Determine if <em>any</em> of the given strings occurs in {@code s}, ignoring differences in case.  Defined in 
   * terms of {@link #containsIgnoreCase}.
   */
  public static boolean containsAnyIgnoreCase(String s, String... pieces) {
    for (String piece: pieces) { if (contains(s, piece)) { return true; } }
    return false;
  }
  
  /**
   * Determine if <em>all</em> of the given strings occur in {@code s}, ignoring differences in case.  Defined in 
   * terms of {@link #containsIgnoreCase}.
   */
  public static boolean containsAllIgnoreCase(String s, String... pieces) {
    for (String piece: pieces) { if (!contains(s, piece)) { return false; } }
    return true;
  }
  
  /**
   * Find the first occurance of any of the given characters in {@code s}.  If none are present, the result is 
   * {@code -1}.  Defined in terms of {@link String#indexOf(int)}.
   */
  public static int indexOfFirst(String s, int... characters) {
    int result = -1;
    for (int c : characters) {
      int index = s.indexOf(c);
      if (index >= 0 && (result < 0 || index < result)) { result = index; }
    }
    return result;
  }
  
  /**
   * Find the first occurance of any of the given strings in {@code s}.  If none are present, the result is 
   * {@code -1}.  Defined in terms of {@link String#indexOf(String)}.
   */
  public static int indexOfFirst(String s, String... pieces) {
    int result = -1;
    for (String piece : pieces) {
      int index = s.indexOf(piece);
      if (index >= 0 && (result < 0 || index < result)) { result = index; }
    }
    return result;
  }
  
  /**
   * Extract the portion of {@code s} before the first occurance of the given delimiter.  {@code s} if the
   * delimiter is not found.
   */
  public static String prefix(String s, int delim) {
    int index = s.indexOf(delim);
    return (index == -1) ? s : s.substring(0, index);
  }
  
  /**
   * Extract the portion of {@code s} after the first occurance of the given delimiter.  {@code s} if the
   * delimiter is not found.
   */
  public static String removePrefix(String s, int delim) {
    int index = s.indexOf(delim);
    return (index == -1) ? s : s.substring(index+1);
  }
    
  /**
   * Extract the portion of {@code s} after the last occurance of the given delimiter.  {@code s} if the
   * delimiter is not found.
   */
  public static String suffix(String s, int delim) {
    int index = s.lastIndexOf(delim);
    return (index == -1) ? s : s.substring(index+1);
  }
  
  /**
   * Extract the portion of {@code s} before the last occurance of the given delimiter.  {@code s} if the
   * delimiter is not found.
   */
  public static String removeSuffix(String s, int delim) {
    int index = s.lastIndexOf(delim);
    return (index == -1) ? s : s.substring(0, index);
  }
    
  
  public static boolean isDecimalDigit(char c) { return c >= '0' && c <= '9'; }
  
  public static boolean isOctalDigit(char c) { return c >= '0' && c <= '7'; }
  
  public static boolean isHexDigit(char c) {
    return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
  }
  
  /** 
   * Abstract class for string translation algorithms.  Implementations are responsible for modifying
   * {@code _result} and {@code _changed}; each character in the original string will be passed
   * to {@code processChar()}, followed by a single invocation of {@code finish()}.
   */
  private static abstract class StringTranslator implements Lambda<String, String> {
    protected final StringBuilder _result;
    protected boolean _changed;
    
    StringTranslator() { _result = new StringBuilder(); _changed = false; }
    
    public final String value(String s) {
      int length = s.length();
      for (int i = 0; i < length; i++) { processChar(s.charAt(i)); }
      finish();
      return _changed ? _result.toString() : s;
    }
    
    protected abstract void processChar(char c);
    
    protected abstract void finish();
  }
  
  /** Shared code for Unicode escaping and unescaping algorithms */
  private static abstract class UnicodeTranslator extends StringTranslator {
    private static enum State { START, BACKSLASH, U, DIG1, DIG2, DIG3 };

    private State _state;
    private StringBuilder _buffer; // includes everything that follows a "\\u"
    
    UnicodeTranslator() { _state = State.START; _buffer = new StringBuilder(); }
    
    protected abstract void handleStandardChar(char c, boolean backslashed);
    protected abstract void handlePartialEscape(String escape);
    protected abstract void handleCompleteEscape(String escape);
    
    private void reset(char c) {
      handlePartialEscape(_buffer.toString());
      _buffer.delete(0, _buffer.length());
      _state = State.START;
      if (c == '\\') { _state = State.BACKSLASH; }
      else { handleStandardChar(c, false); }
    }
    
    protected final void processChar(char c) {
      switch (_state) {
        case START:
          if (c == '\\') { _state = State.BACKSLASH; }
          else { handleStandardChar(c, false); }
          break;
        case BACKSLASH:
          if (c == 'u') { _state = State.U; }
          else { handleStandardChar(c, true); _state = State.START; } // intentionally includes double-backslash
          break;
        case U:
          if (isHexDigit(c)) { _buffer.append(c); _state = State.DIG1; }
          else if (c != 'u') { reset(c); }
          break;
        case DIG1:
          if (isHexDigit(c)) { _buffer.append(c); _state = State.DIG2; }
          else { reset(c); }
          break;
        case DIG2:
          if (isHexDigit(c)) { _buffer.append(c); _state = State.DIG3; }
          else { reset(c); }
          break;
        case DIG3:
          if (isHexDigit(c)) {
            _buffer.append(c);
            handleCompleteEscape(_buffer.toString());
            _buffer.delete(0, _buffer.length());
            _state = State.START;
          }
          else { reset(c); }
          break;
      }
    }
    
    protected final void finish() {
      switch (_state) {
        case START: break;
        case BACKSLASH: handleStandardChar('\\', false); break;
        default: handlePartialEscape(_buffer.toString()); break;
      }
    }
  }

  /**
   * Convert all non-ASCII characters in the string to Unicode escapes, as specified by JLS 3.3.
   * As suggested by JLS, an additional {@code u} is added to existing escapes in the string;
   * instances of {@code \} that precede a non-ASCII character or a malformed Unicode escape will
   * be encoded as {@code &#92;u005c}.  The original string may be safely reconstructed with 
   * {@link #unicodeUnescapeOnce}; to safely interpret <em>all</em> unicode escapes, including 
   * those in the original string, use {@link #unicodeUnescape} (in either case, this method
   * guarantees an absense of {@code IllegalArgumentException}s).
   */
  public static String unicodeEscape(String s) {
    return new UnicodeTranslator() {
      protected void handleStandardChar(char c, boolean backslashed) {
        if (c > '\u007f') {
          if (backslashed) { _result.append("\\u005c"); } // encoded '\'
          _result.append("\\u");
          _result.append(padLeft(Integer.toHexString(c), '0', 4));
          _changed = true;
        }
        else {
          if (backslashed) { _result.append('\\'); }
          _result.append(c);
        }
      }
      protected void handlePartialEscape(String escape) {
        _result.append("\\u005cu"); // encoded '\' plus 'u'
        _result.append(escape);
        _changed = true;
      }
      protected void handleCompleteEscape(String escape) {
        _result.append("\\uu"); // add a 'u'
        _result.append(escape);
        _changed = true;
      }
    }.value(s);
  }
  
  /**
   * Convert all one-level Unicode escapes in the string to their equivalent characters, as specified by JLS 3.3.
   * Higher-level escapes (containing multiple 'u' characters) will have a single 'u' removed.
   * @throws IllegalArgumentException  If a backslash-u escape in the string is not followed by 4 hex digits
   */
  public static String unicodeUnescapeOnce(String s) {
    return new UnicodeTranslator() {
      protected void handleStandardChar(char c, boolean backslashed) {
        if (backslashed) { _result.append('\\'); }
        _result.append(c);
      }
      protected void handlePartialEscape(String escape) {
        throw new IllegalArgumentException("Expected a hexadecimal digit after '\\u" + escape + "'");
      }
      protected void handleCompleteEscape(String escape) {
        if (escape.charAt(0) == 'u') {
          _result.append('\\');
          _result.append(escape); // skip the initial 'u'
        }
        else { _result.append((char) Integer.parseInt(escape, 16)); }
        _changed = true;
      }
    }.value(s);
  }

  /**
   * Convert all Unicode escapes in the string into their equivalent Unicode characters, as specified
   * by JLS 3.3.
   * @throws IllegalArgumentException  If a backslash-u escape in the string is not followed by 4 hex digits
   */
  public static String unicodeUnescape(String s) {
    return new UnicodeTranslator() {
      protected void handleStandardChar(char c, boolean backslashed) {
        if (backslashed) { _result.append('\\'); }
        _result.append(c);
      }
      protected void handlePartialEscape(String escape) {
        throw new IllegalArgumentException("Expected a hexadecimal digit after '\\u" + escape + "'");
      }
      protected void handleCompleteEscape(String escape) {
        int firstDigit = escape.lastIndexOf('u') + 1;
        _result.append((char) Integer.parseInt(escape.substring(firstDigit), 16));
        _changed = true;
      }
    }.value(s);
  }
  
  /**
   * Convert the given string to a form compatible with the Java language specification for character and string literals
   * (see JLS 3.10.6).  The characters {@code \}, {@code "}, and {@code '} are replaced with escape sequences.  All 
   * control characters between {@code &#92;u0000} and {@code &#92;u001F}, along with {@code &#92;u007F}, are replaced 
   * with mnemonic escape sequences (such as {@code "\n"}), or octal escape sequences if no mnemonic exists.
   */
  public static String javaEscape(String s) {
    return new StringTranslator() {
      protected void processChar(char c) {
        switch (c) {
          case '\b': _result.append("\\b"); _changed = true; break;
          case '\t': _result.append("\\t"); _changed = true; break;
          case '\n': _result.append("\\n"); _changed = true; break;
          case '\f': _result.append("\\f"); _changed = true; break;
          case '\r': _result.append("\\r"); _changed = true; break;
          case '\"': _result.append("\\\""); _changed = true; break;
          case '\'': _result.append("\\\'"); _changed = true; break;
          case '\\': _result.append("\\\\"); _changed = true; break;
          default:
            if (c < ' ' || c == '\u007f') {
              // must use 3 digits so that unescaping doesn't consume too many chars ("\12" vs. "\0012")
              _result.append('\\');
              _result.append(padLeft(Integer.toOctalString(c), '0', 3));
              _changed = true;
            }
            else { _result.append(c); }
            break;
        }
      }
      protected void finish() {}
    }.value(s);
  }
  
  private static enum JState { START, BACKSLASH, DIG1, DIG2, DIG3 };

  /**
   * Convert a string potentially containing Java character escapes (as in {@link #javaEscape}) to its
   * unescaped equivalent.  Note that Unicode escapes are <em>not</em> interpreted (strings from Java source
   * code should first be processed by {@link #unicodeUnescape}).
   * @throws IllegalArgumentException  If the character {@code \} is followed by an invalid escape character
   *                                   or the end of the string.
   */
  public static String javaUnescape(String s) {
    return new StringTranslator() {
      
      private JState _state = JState.START;
      private final StringBuilder _buffer = new StringBuilder(); // contains octal digits
      
      private void reset(char c) {
        _result.append((char) Integer.parseInt(_buffer.toString(), 8));
        _buffer.delete(0, _buffer.length());
        _state = JState.START;
        if (c == '\\') { _state = JState.BACKSLASH; _changed = true; }
        else { _result.append(c); }
      }
      
      protected void processChar(char c) {
        switch (_state) {
          case START:
            if (c == '\\') { _state = JState.BACKSLASH; _changed = true; }
            else { _result.append(c); }
            break;
          case BACKSLASH:
            switch (c) {
              case 'b': _result.append('\b'); _state = JState.START; break;
              case 't': _result.append('\t'); _state = JState.START; break;
              case 'n': _result.append('\n'); _state = JState.START; break;
              case 'f': _result.append('\f'); _state = JState.START; break;
              case 'r': _result.append('\r'); _state = JState.START; break;
              case '\"': _result.append('\"'); _state = JState.START; break;
              case '\'': _result.append('\''); _state = JState.START; break;
              case '\\': _result.append('\\'); _state = JState.START; break;
              case '0':
              case '1':
              case '2':
              case '3':
                _buffer.append(c); _state = JState.DIG1; break;
              case '4':
              case '5':
              case '6':
              case '7':
                _buffer.append(c); _state = JState.DIG2; break;
              default:
                throw new IllegalArgumentException("'" + c + "' after '\\'");
            }
            break;
          case DIG1:
            if (isOctalDigit(c)) { _buffer.append(c); _state = JState.DIG2; }
            else { reset(c); }
            break;
          case DIG2:
            if (isOctalDigit(c)) { _buffer.append(c); _state = JState.DIG3; }
            else { reset(c); }
            break;
          case DIG3:
            reset(c);
            break;
        }
      }
      
      protected void finish() {
        switch (_state) {
          case START: break;
          case BACKSLASH: throw new IllegalArgumentException("Nothing after after '\\'");
          default: _result.append((char) Integer.parseInt(_buffer.toString(), 8)); break;
        }
      }
    }.value(s);
  }

  /**
   * Produce a regular expression that matches the given string.  Backslash escape sequences are
   * used for all characters that potentially clash with regular expression syntax.  For simplicity,
   * escapes are applied to all control characters ({@code &#92;u0000} to {@code &#92;u001F} and 
   * {@code &#92;u007F}) and to all non-alphanumeric, non-space ASCII characters (in the range
   * {@code &#92;u0020} to {@code &#92;u007E}), including those that have no special meaning in
   * the regular expression syntax (such as {@code @}, {@code "}, and {@code ~}).  Where a
   * mnemonic escape for control characters exists, it is used; otherwise, the hexadecimal {@code \xhh}
   * notation is used.
   */
  public static String regexEscape(String s) {
    return new StringTranslator() {
      protected void processChar(char c) {
        switch (c) {
          case '\t': _result.append("\\t"); _changed = true; break;
          case '\n': _result.append("\\n"); _changed = true; break;
          case '\r': _result.append("\\r"); _changed = true; break;
          case '\f': _result.append("\\f"); _changed = true; break;
          case '\u0007': _result.append("\\a"); _changed = true; break;
          case '\u001b': _result.append("\\e"); _changed = true; break;
          default:
            if (c < ' ' || c == '\u007f') {
              _result.append("\\x");
              _result.append(padLeft(Integer.toHexString(c), '0', 2));
              _changed = true;
            }
            else if ((c > ' ' && c < '0') || (c > '9' && c < 'A') ||
                     (c > 'Z' && c < 'a') || (c > 'z' && c < '\u007F')) {
              _result.append('\\');
              _result.append(c);
              _changed = true;
            }
            else { _result.append(c); }
            break;
        }
      }
      protected void finish() {}
    }.value(s);
  }

  /**
   * Convert the given string to a form containing SGML character entities.  All characters appearing in
   * {@code entities} will be translated to their corrresponding entity names; if {@code convertToAscii} is
   * {@code true}, all other non-ASCII characters will be converted to numeric references.
   */
  public static String sgmlEscape(String s, final Map<Character, String> entities, final boolean convertToAscii) {
    return new StringTranslator() {
      protected void processChar(char c) {
        String entity = entities.get(c);
        if (entity != null) {
          _result.append('&');
          _result.append(entity);
          _result.append(';');
          _changed = true;
        }
        else if (convertToAscii && c > '\u007F') {
          _result.append("&#");
          _result.append((int) c);
          _result.append(';');
          _changed = true;
        }
        else { _result.append(c); }
      }
      protected void finish() {}
    }.value(s);
  }

  private static enum SGMLState { START, AMP, NAME, NUM, HEX_DIGITS, DEC_DIGITS };
  
  /**
   * Interpret all SGML character entities in the given string according to the provided name-character mapping.
   * @throws  IllegalArgumentException  If the string contains a malformed or unrecognized character entity
   */
  public static String sgmlUnescape(String s, final Map<String, Character> entities) {
    return new StringTranslator() {

      private SGMLState _state = SGMLState.START;
      private final StringBuilder _buffer = new StringBuilder(); // contains name or digits
      
      private void reset() { _buffer.delete(0, _buffer.length()); _state = SGMLState.START; }
      
      protected void processChar(char c) {
        switch (_state) {
          case START:
            if (c == '&') { _state = SGMLState.AMP; _changed = true; }
            else { _result.append(c); }
            break;
          case AMP:
            if (c == '#') { _state = SGMLState.NUM; }
            else if (c == ';') { throw new IllegalArgumentException("Missing entity name"); }
            else { _state = SGMLState.NAME; _buffer.append(c); }
            break;
          case NAME:
            if (c == ';') {
              Character namedChar = entities.get(_buffer.toString());
              if (namedChar == null) {
                throw new IllegalArgumentException("Unrecognized entity name: '" + _buffer.toString() + "'");
              }
              else { _result.append((char) namedChar); reset(); }
            }
            else { _buffer.append(c); }
            break;
          case NUM:
            if (c == 'x') { _state = SGMLState.HEX_DIGITS; }
            else if (isDecimalDigit(c)) { _state = SGMLState.DEC_DIGITS; _buffer.append(c); }
            else { throw new IllegalArgumentException("Expected decimal digit: '" + c + "'"); }
            break;
          case HEX_DIGITS:
            if (c == ';') {
              if (_buffer.length() == 0) { throw new IllegalArgumentException("Expected hexadecimal digit: ';'"); }
              else { _result.append((char) Integer.parseInt(_buffer.toString(), 16)); reset(); }
            }
            else if (isHexDigit(c)) { _buffer.append(c); }
            else { throw new IllegalArgumentException("Expected hexadecimal digit: '" + c + "'"); }
            break;
          case DEC_DIGITS:
            if (c == ';') { _result.append((char) Integer.parseInt(_buffer.toString())); reset(); }
            else if (isDecimalDigit(c)) { _buffer.append(c); }
            else { throw new IllegalArgumentException("Expected decimal digit: '" + c + "'"); }
            break;
        }
      }
      
      protected void finish() {
        if (_state != SGMLState.START) { throw new IllegalArgumentException("Unfinished entity"); }
      }
    }.value(s);
  }

  /**
   * Convert the given string to an escaped form compatible with XML.  The standard XML named entities
   * ({@code "}, {@code &}, {@code '}, {@code <}, and {@code >}) will be replaced with named references
   * (such as {@code &quot;}), and all non-ASCII characters will be replaced with numeric references.
   */
  public static String xmlEscape(String s) { return sgmlEscape(s, XML_ENTITIES.value(), true); }

 /**
  * Convert the given string to an escaped form compatible with XML.  The standard XML named entities
  * ({@code "}, {@code &}, {@code '}, {@code <}, and {@code >}) will be replaced with named references
  * (such as {@code &quot;}); if {@code convertToAscii} is {@code true}, all non-ASCII characters 
  * will be replaced with numeric references.
  */
  public static String xmlEscape(String s, boolean convertToAscii) {
    return sgmlEscape(s, XML_ENTITIES.value(), convertToAscii);
  }

 /**
   * Interpret all XML character entities in the given string.
   * @throws  IllegalArgumentException  If the string contains a malformed or unrecognized character entity
  */
  public static String xmlUnescape(String s) { return sgmlUnescape(s, XML_ENTITIES.value().reverse()); }

  /**
   * Convert the given string to an escaped form compatible with HTML.  All named entities
   * supported by HTML 4.0 will be replaced with named references, and all other non-ASCII
   * characters will be replaced with numeric references.  The {@code '} character will also
   * be replaced with a numeric refererence.
   */
  public static String htmlEscape(String s) { return sgmlEscape(s, HTML_ENTITIES.value(), true); }

 /**
   * Interpret all HTML character entities in the given string.
   * @throws  IllegalArgumentException  If the string contains a malformed or unrecognized character entity
  */
  public static String htmlUnescape(String s) { return sgmlUnescape(s, HTML_ENTITIES.value().reverse()); }

  
  /** Entity names for XML; declared lazily to prevent creation when it is not used */
  private static final Thunk<OneToOneMap<Character, String>> XML_ENTITIES = 
    LazyThunk.make(new Thunk<OneToOneMap<Character, String>>() {
    public OneToOneMap<Character, String> value() {
      OneToOneMap<Character, String> result = new OneToOneHashMap<Character, String>();
      // Source: Wikipedia, "List of XML and HTML character entity references"
      result.put('"', "quot");
      result.put('&', "amp");
      result.put('\'', "apos");
      result.put('<', "lt");
      result.put('>', "gt");
      return result;
    }      
  });

  
  /** Entity names for HTML; declared lazily to prevent creation when it is not used */
  private static final Thunk<OneToOneMap<Character, String>> HTML_ENTITIES = 
    LazyThunk.make(new Thunk<OneToOneMap<Character, String>>() {
    public OneToOneMap<Character, String> value() {
      OneToOneMap<Character, String> result = new OneToOneHashMap<Character, String>();
      // Source: Wikipedia, "List of XML and HTML character entity references"
      result.put('\'', "#39"); // no entity defined, but it's safer to escape it
      result.put('"', "quot");
      result.put('&', "amp");
      result.put('<', "lt");
      result.put('>', "gt");
      
      result.put('\u00A0', "nbsp");
      result.put('\u00A1', "iexcl");
      result.put('\u00A2', "cent");
      result.put('\u00A3', "pound");
      result.put('\u00A4', "curren");
      result.put('\u00A5', "yen");
      result.put('\u00A6', "brvbar");
      result.put('\u00A7', "sect");
      result.put('\u00A8', "uml");
      result.put('\u00A9', "copy");
      result.put('\u00AA', "ordf");
      result.put('\u00AB', "laquo");
      result.put('\u00AC', "not");
      result.put('\u00AD', "shy");
      result.put('\u00AE', "reg");
      result.put('\u00AF', "macr");
      result.put('\u00B0', "deg");
      result.put('\u00B1', "plusmn");
      result.put('\u00B2', "sup2");
      result.put('\u00B3', "sup3");
      result.put('\u00B4', "acute");
      result.put('\u00B5', "micro");
      result.put('\u00B6', "para");
      result.put('\u00B7', "middot");
      result.put('\u00B8', "cedil");
      result.put('\u00B9', "sup1");
      result.put('\u00BA', "ordm");
      result.put('\u00BB', "raquo");
      result.put('\u00BC', "frac14");
      result.put('\u00BD', "frac12");
      result.put('\u00BE', "frac34");
      result.put('\u00BF', "iquest");
      result.put('\u00C0', "Agrave");
      result.put('\u00C1', "Aacute");
      result.put('\u00C2', "Acirc");
      result.put('\u00C3', "Atilde");
      result.put('\u00C4', "Auml");
      result.put('\u00C5', "Aring");
      result.put('\u00C6', "AElig");
      result.put('\u00C7', "Ccedil");
      result.put('\u00C8', "Egrave");
      result.put('\u00C9', "Eacute");
      result.put('\u00CA', "Ecirc");
      result.put('\u00CB', "Euml");
      result.put('\u00CC', "Igrave");
      result.put('\u00CD', "Iacute");
      result.put('\u00CE', "Icirc");
      result.put('\u00CF', "Iuml");
      result.put('\u00D0', "ETH");
      result.put('\u00D1', "Ntilde");
      result.put('\u00D2', "Ograve");
      result.put('\u00D3', "Oacute");
      result.put('\u00D4', "Ocirc");
      result.put('\u00D5', "Otilde");
      result.put('\u00D6', "Ouml");
      result.put('\u00D7', "times");
      result.put('\u00D8', "Oslash");
      result.put('\u00D9', "Ugrave");
      result.put('\u00DA', "Uacute");
      result.put('\u00DB', "Ucirc");
      result.put('\u00DC', "Uuml");
      result.put('\u00DD', "Yacute");
      result.put('\u00DE', "THORN");
      result.put('\u00DF', "szlig");
      result.put('\u00E0', "agrave");
      result.put('\u00E1', "aacute");
      result.put('\u00E2', "acirc");
      result.put('\u00E3', "atilde");
      result.put('\u00E4', "auml");
      result.put('\u00E5', "aring");
      result.put('\u00E6', "aelig");
      result.put('\u00E7', "ccedil");
      result.put('\u00E8', "egrave");
      result.put('\u00E9', "eacute");
      result.put('\u00EA', "ecirc");
      result.put('\u00EB', "euml");
      result.put('\u00EC', "igrave");
      result.put('\u00ED', "iacute");
      result.put('\u00EE', "icirc");
      result.put('\u00EF', "iuml");
      result.put('\u00F0', "eth");
      result.put('\u00F1', "ntilde");
      result.put('\u00F2', "ograve");
      result.put('\u00F3', "oacute");
      result.put('\u00F4', "ocirc");
      result.put('\u00F5', "otilde");
      result.put('\u00F6', "ouml");
      result.put('\u00F7', "divide");
      result.put('\u00F8', "oslash");
      result.put('\u00F9', "ugrave");
      result.put('\u00FA', "uacute");
      result.put('\u00FB', "ucirc");
      result.put('\u00FC', "uuml");
      result.put('\u00FD', "yacute");
      result.put('\u00FE', "thorn");
      result.put('\u00FF', "yuml");
      
      result.put('\u0152', "OElig");
      result.put('\u0153', "oelig");
      result.put('\u0160', "Scaron");
      result.put('\u0161', "scaron");
      result.put('\u0178', "Yuml");
      result.put('\u0192', "fnof");
      
      result.put('\u02C6', "circ");
      result.put('\u02DC', "tilde");
      
      result.put('\u0391', "Alpha");
      result.put('\u0392', "Beta");
      result.put('\u0393', "Gamma");
      result.put('\u0394', "Delta");
      result.put('\u0395', "Epsilon");
      result.put('\u0396', "Zeta");
      result.put('\u0397', "Eta");
      result.put('\u0398', "Theta");
      result.put('\u0399', "Iota");
      result.put('\u039A', "Kappa");
      result.put('\u039B', "Lambda");
      result.put('\u039C', "Mu");
      result.put('\u039D', "Nu");
      result.put('\u039E', "Xi");
      result.put('\u039F', "Omicron");
      result.put('\u03A0', "Pi");
      result.put('\u03A1', "Rho");
      result.put('\u03A3', "Sigma");
      result.put('\u03A4', "Tau");
      result.put('\u03A5', "Upsilon");
      result.put('\u03A6', "Phi");
      result.put('\u03A7', "Chi");
      result.put('\u03A8', "Psi");
      result.put('\u03A9', "Omega");
      
      result.put('\u03B1', "alpha");
      result.put('\u03B2', "beta");
      result.put('\u03B3', "gamma");
      result.put('\u03B4', "delta");
      result.put('\u03B5', "epsilon");
      result.put('\u03B6', "zeta");
      result.put('\u03B7', "eta");
      result.put('\u03B8', "theta");
      result.put('\u03B9', "iota");
      result.put('\u03BA', "kappa");
      result.put('\u03BB', "lambda");
      result.put('\u03BC', "mu");
      result.put('\u03BD', "nu");
      result.put('\u03BE', "xi");
      result.put('\u03BF', "omicron");
      result.put('\u03C0', "pi");
      result.put('\u03C1', "rho");
      result.put('\u03C2', "sigmaf");
      result.put('\u03C3', "sigma");
      result.put('\u03C4', "tau");
      result.put('\u03C5', "upsilon");
      result.put('\u03C6', "phi");
      result.put('\u03C7', "chi");
      result.put('\u03C8', "psi");
      result.put('\u03C9', "omega");
      
      result.put('\u03D1', "thetasym");
      result.put('\u03D2', "upsih");
      result.put('\u03D6', "piv");
      
      result.put('\u2002', "ensp");
      result.put('\u2003', "emsp");
      result.put('\u2009', "thinsp");
      result.put('\u200C', "zwnj");
      result.put('\u200D', "zwj");
      result.put('\u200E', "lrm");
      result.put('\u200F', "rlm");
      result.put('\u2013', "ndash");
      result.put('\u2014', "mdash");
      result.put('\u2018', "lsquo");
      result.put('\u2019', "rsquo");
      result.put('\u201A', "sbquo");
      result.put('\u201C', "ldquo");
      result.put('\u201D', "rdquo");
      result.put('\u201E', "bdquo");
      result.put('\u2020', "dagger");
      result.put('\u2021', "Dagger");
      result.put('\u2022', "bull");
      result.put('\u2026', "hellip");
      result.put('\u2030', "permil");
      result.put('\u2032', "prime");
      result.put('\u2033', "Prime");
      result.put('\u2039', "lsaquo");
      result.put('\u203A', "rsaquo");
      result.put('\u203E', "oline");
      result.put('\u2044', "frasl");
      result.put('\u20AC', "euro");
      
      result.put('\u2111', "image");
      result.put('\u2118', "weierp");
      result.put('\u211C', "real");
      result.put('\u2122', "trade");
      result.put('\u2135', "alefsym");
      result.put('\u2190', "larr");
      result.put('\u2191', "uarr");
      result.put('\u2192', "rarr");
      result.put('\u2193', "darr");
      result.put('\u2194', "harr");
      result.put('\u21B5', "crarr");
      result.put('\u21D0', "lArr");
      result.put('\u21D1', "uArr");
      result.put('\u21D2', "rArr");
      result.put('\u21D3', "dArr");
      result.put('\u21D4', "hArr");
      
      result.put('\u2200', "forall");
      result.put('\u2202', "part");
      result.put('\u2203', "exist");
      result.put('\u2205', "empty");
      result.put('\u2207', "nabla");
      result.put('\u2208', "isin");
      result.put('\u2209', "notin");
      result.put('\u220B', "ni");
      result.put('\u220F', "prod");
      result.put('\u2211', "sum");
      result.put('\u2212', "minus");
      result.put('\u2217', "lowast");
      result.put('\u221A', "radic");
      result.put('\u221D', "prop");
      result.put('\u221E', "infin");
      result.put('\u2220', "ang");
      result.put('\u2227', "and");
      result.put('\u2228', "or");
      result.put('\u2229', "cap");
      result.put('\u222A', "cup");
      result.put('\u222B', "int");
      result.put('\u2234', "there4");
      result.put('\u223C', "sim");
      result.put('\u2245', "cong");
      result.put('\u2248', "asymp");
      result.put('\u2260', "ne");
      result.put('\u2261', "equiv");
      result.put('\u2264', "le");
      result.put('\u2265', "ge");
      result.put('\u2282', "sub");
      result.put('\u2283', "sup");
      result.put('\u2284', "nsub");
      result.put('\u2286', "sube");
      result.put('\u2287', "supe");
      result.put('\u2295', "oplus");
      result.put('\u2297', "otimes");
      result.put('\u22A5', "perp");
      result.put('\u22C5', "sdot");
      
      result.put('\u2308', "lceil");
      result.put('\u2309', "rceil");
      result.put('\u230A', "lfloor");
      result.put('\u230B', "rfloor");
      result.put('\u2329', "lang");
      result.put('\u232A', "rang");
      
      result.put('\u25CA', "loz");
      
      result.put('\u2660', "spades");
      result.put('\u2663', "clubs");
      result.put('\u2665', "hearts");
      result.put('\u2666', "diams");
      return result;
    }
  });
  
}
