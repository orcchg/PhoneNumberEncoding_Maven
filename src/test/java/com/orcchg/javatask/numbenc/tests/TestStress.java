package com.orcchg.javatask.numbenc.tests;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.orcchg.javatask.numbenc.struct.Automaton;
import com.orcchg.javatask.numbenc.struct.LookupTable;
import com.orcchg.javatask.numbenc.struct.Solver;
import com.orcchg.javatask.numbenc.utils.Util;

public class TestStress {
  private static Solver mSolver;
  private static List<String> mNumbers, mDictionary, mDictionaryModified;
  private static Random mRng;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    mSolver = new Solver();
    mNumbers = new ArrayList<String>(100);
    mDictionary = new ArrayList<String>(100);
    mDictionaryModified = new ArrayList<String>(100);
    mRng = new Random();
    mRng.setSeed(239);
    
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();

    try (InputStream fin = classloader.getResourceAsStream("dict.txt");
         BufferedReader reader = new BufferedReader(new InputStreamReader(fin))) {
      String line = null;
      while ((line = reader.readLine()) != null) {
        mDictionary.add(line);
        mDictionaryModified.add(Util.convertUmlautCharsToPlaceholders(line));
        char label = line.charAt(0);
        Automaton automaton = mSolver.getAutomaton(label);
        if (automaton == null) {
          automaton = mSolver.addEmptyAutomaton(label);
        }
        automaton.addWord(line);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    try (InputStream fin = classloader.getResourceAsStream("in.txt");
         BufferedReader reader = new BufferedReader(new InputStreamReader(fin))) {
      String line = null;
      while ((line = reader.readLine()) != null) {
        mNumbers.add(line);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  @AfterClass
  public static void tearDownAfterClass() throws Exception {}

  @Before
  public void setUp() throws Exception {}

  @After
  public void tearDown() throws Exception {}
  
  @Test
  public void testBruteForcePrerequisites() {
    List<String> combinations = Util.generateAllCombinations("4824", LookupTable.map);
    assertTrue("Not all combinations were generated!", combinations.size() == 1575);
    for (String combination : combinations) {
      if (!Util.hasAdjacentDigits(combination)) {
        List<String> matching_combination = matchWords(combination, mDictionaryModified);
        if (!matching_combination.isEmpty()) {
          for (String match : matching_combination) {
//            assertTrue("Matching malfunctions!",
//                match.equals("fort") || match.equals("Tor4") || match.equals("Torf"));
          }
        }
      }
    }
    
    List<String> matches = matchWords("|dOrtMixer", mDictionaryModified);
    for (String match : matches) {
      assertTrue("Matching malfunctions!",
          match.contains("|d") && match.contains("Ort") && match.contains("Mixer"));
    }
  }
  
  @Test
  public void testSampleInput() {
    for (String number : mNumbers) {
      String digital_number = Util.remainDigitsOnly(number);
      List<String> solution = mSolver.solve(digital_number);
      List<String> solution_no_spaces = new ArrayList<>(solution.size());
      for (String answer : solution) {
        solution_no_spaces.add(Util.removeWhitespaces(Util.removeFormatPrefix(answer)));
      }
      List<String> brute_force_solution = bruteForceProcessNumber(digital_number);
      
      assertTrue("Solutions must have equal sizes!", solution_no_spaces.size() == brute_force_solution.size());
      List<String> brute_force_without_placeholders = new ArrayList<>(brute_force_solution.size());
      for (String bf_answer : brute_force_solution) {
        brute_force_without_placeholders.add(Util.convertPlaceholdersToUmlautChars(bf_answer));
      }
      brute_force_without_placeholders.removeAll(solution_no_spaces);
      assertTrue("Solutions must be completely equal!", brute_force_without_placeholders.isEmpty());
    }
  }
  
  /* Private methods */
  // --------------------------------------------------------------------------
  private List<String> bruteForceProcessNumber(final String digital_number) {
    List<String> answer = new ArrayList<>(10000);
    List<String> combinations = Util.generateAllCombinations(digital_number, LookupTable.map);
    for (String combination : combinations) {
      if (!Util.hasAdjacentDigits(combination)) {
        List<String> matching_combination = matchWords(combination, mDictionaryModified);
        if (!matching_combination.isEmpty()) {
          answer.addAll(matching_combination);
        }
      }
    }
    return cullingWrong(answer);
  }
  
  private List<String> matchWords(String combination, final List<String> dictionary) {
    List<String> answer = new ArrayList<>(1000);
    if (combination.isEmpty()) {
      return answer;
    }
    
    boolean starts_with_digit = Character.isDigit(combination.charAt(0));
    if (combination.length() == 1 && starts_with_digit) {
      answer.add(combination);
      return answer;
    }
    
    storeMatchingWords(dictionary, combination, answer);
    
    if (answer.isEmpty() && starts_with_digit) {
      storeMatchingWords(dictionary, combination.substring(1), answer);
      for (int i = 0; i < answer.size(); ++i) {
        answer.set(i, combination.charAt(0) + answer.get(i));
      }
    }

    return answer;
  }
  
  private void storeMatchingWords(final List<String> dictionary, String combination, List<String> answer) {
    for (String word : dictionary) {
      boolean starts = combination.startsWith(word);
      if (starts) {
        String suffix = combination.substring(word.length());
        if (!suffix.isEmpty()) {
          List<String> subanswer = matchWords(suffix, dictionary);
          for (String subword : subanswer) {
            answer.add(word + subword);
          }
        } else {
          answer.add(word);
        }
      }
    }
  }
  
  private List<String> cullingWrong(final List<String> answer_with_mistakes) {
    if (answer_with_mistakes.isEmpty()) {
      return answer_with_mistakes;
    }
    
    List<String> answer = new ArrayList<>(answer_with_mistakes.size());
    for (String word : answer_with_mistakes) {
      char first = word.charAt(0);
      if (word.length() > 1 && !Character.isDigit(first)) {
        answer.add(word);
      }
    }
    
    if (answer.isEmpty()) {
      return answer_with_mistakes;  // leave answers starting from digit, coz there are no others
    }
    
    return answer;
  }
}
