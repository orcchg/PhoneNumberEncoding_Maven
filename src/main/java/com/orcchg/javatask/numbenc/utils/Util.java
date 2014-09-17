package com.orcchg.javatask.numbenc.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.orcchg.javatask.numbenc.struct.LookupTable;

public class Util {
  public static boolean isQuote(char character) {
    return character == '"';
  }
  
  public static int getCharShift(char character) {
    return character - 'A';
  }
  
  public static String remainDigitsOnly(final String number) {
    String digital_number = number.replaceAll("-|/", "");
    return digital_number;
  }
  
  public static String remainLettersOnly(final String word) {
    String alpha_word = word.replaceAll("\"| ", "");
    return alpha_word;
  }
  
  public static String removeFirstChar(final String string) {
    return string.substring(1);
  }
  
  @SuppressWarnings("unchecked")
  public static List<String> removeDuplicates(List<String> list) {
    Set<String> set = new TreeSet<>();
    set.addAll(list);
    List<Object> result = Arrays.asList(set.toArray());
    return (List<String>)(List<?>) result;
  }
  
  public static boolean hasAdjacentDigits(final String input_string) {
    // remove spaces
    String string = input_string.replaceAll(" ", "");
    if (string.length() == 2 &&
        Character.isDigit(string.charAt(0)) &&
        Character.isDigit(string.charAt(1))) {
      return true;
    }
    
    for (int i = 1; i + 1 < string.length(); /* no-op */) {
      if (Character.isDigit(string.charAt(i))) {
        if (Character.isDigit(string.charAt(i - 1)) ||
            Character.isDigit(string.charAt(i + 1))) {
          return true;
        } else {
          i += 3;
          continue;
        }
      } else {
        i += 2;
        continue;
      }
    }
    return false;
  }
  
  public static char convertToUmlaut(char character) {
    char converted = 0;
    switch (character) {
      case 'a':
        converted = '{';
        break;
      case 'A':
        converted = '[';
        break;
      case 'o':
        converted = '|';
        break;
      case 'O':
        converted = '\\';
        break;
      case 'u':
        converted = '}';
        break;
      case 'U':
        converted = ']';
        break;
      default:
        throw new IllegalArgumentException("Unable to convert [" + character + "] to umlaut.");
    }
    return converted;
  }
  
  public static boolean isUmlaut(char character) {
    if (character == '{' || character == '|' || character == '}' ||
        character == '[' || character == '\\' || character == ']') {
      return true;
    }
    return false;
  }
  
  public static char convertFromUmlaut(char character) {
    char converted = 0;
    switch (character) {
      case '{':
        converted = 'a';
        break;
      case '[':
        converted = 'A';
        break;
      case '|':
        converted = 'o';
        break;
      case '\\':
        converted = 'O';
        break;
      case '}':
        converted = 'u';
        break;
      case ']':
        converted = 'U';
        break;
      default:
        throw new IllegalArgumentException("Character [" + character + "] has no mapping on set of umlauts.");
    }
    return converted;
  }
  
  public static String convertUmlautCharsToPlaceholders(final String word) {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i + 1 < word.length(); /* no-op */) {
      char label = word.charAt(i);
      if (word.charAt(i + 1) == '"') {
        char placeholder = convertToUmlaut(label);
        result.append(placeholder);
        i += 2;
      } else {
        result.append(label);
        ++i;
      }
    }
    char last = word.charAt(word.length() - 1);
    if (last != '"') {
      result.append(last);
    }
    return result.toString();
  }
  
  public static String convertPlaceholdersToUmlautChars(final String word) {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < word.length(); ++i) {
      char label = word.charAt(i);
      if (isUmlaut(label)) {
        char umlaut = convertFromUmlaut(label);
        result.append(umlaut).append('"');
      } else {
        result.append(label);
      }
    }
    return result.toString();
  }
  
  public static void printList(final String prefix, final List<String> list) {
    System.out.print(prefix + " ");
    for (String word : list) {
      System.out.print(word + " ");
    }
    System.out.println("");
  }
  
  public static List<String> generateAllCombinations(final String digital_number, char[][] table) {
    List<String> answer = new ArrayList<>(1000);  // Omega(3^n)
    if (digital_number.isEmpty()) {
      return answer;
    }
    
    char digit = digital_number.charAt(0);
    int value = Character.getNumericValue(digit);
    List<StringBuilder> combinations = new ArrayList<>();
    combinations.add(new StringBuilder().append(digit));
    
    char[] letters = table[value];
    for (char letter : letters) {
      combinations.add(new StringBuilder().append(letter));
    }
    
    String digital_suffix = digital_number.substring(1);
    List<String> subanswer = null;
    if (!digital_suffix.isEmpty()) {
      subanswer = generateAllCombinations(digital_suffix, table);
      for (StringBuilder preword : combinations) {
        for (String subword : subanswer) {
          answer.add(preword.toString() + subword);
        }
      }
    } else {
      for (StringBuilder preword : combinations) {
        answer.add(preword.toString());
      }
    }
    
    return answer;
  }
  
  /* Private methods */
  // --------------------------------------------------------------------------
}
