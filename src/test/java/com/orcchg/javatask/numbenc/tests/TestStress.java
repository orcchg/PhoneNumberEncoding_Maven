package com.orcchg.javatask.numbenc.tests;

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
  private static List<String> mNumbers, mDictionary;
  private static Random mRng;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    mSolver = new Solver();
    mNumbers = new ArrayList<String>(100);
    mDictionary = new ArrayList<String>(100);
    mRng = new Random();
    mRng.setSeed(239);
    
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();

    try (InputStream fin = classloader.getResourceAsStream("dict.txt");
         BufferedReader reader = new BufferedReader(new InputStreamReader(fin))) {
      String line = null;
      while ((line = reader.readLine()) != null) {
        mDictionary.add(line);
        System.out.print(line + " ");
        System.out.println(Util.convertUmlautCharsToPlaceholders(line));
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
  public void testStress() {
    List<String> combinations = generateAllCombinations("4824");
    for (String combination : combinations) {
      //System.out.println(combination);
    }
  }
  
  /* Private methods */
  // --------------------------------------------------------------------------
  // TODO: consider umlauts
  private List<String> generateAllCombinations(final String digital_number) {
    List<String> answer = new ArrayList<>(1000);  // Omega(3^n)
    if (digital_number.isEmpty()) {
      return answer;
    }
    
    char digit = digital_number.charAt(0);
    int value = Character.getNumericValue(digit);
    List<StringBuilder> combinations = new ArrayList<>();
    combinations.add(new StringBuilder().append(digit));
    
    char[] letters = LookupTable.map[value];
    for (char letter : letters) {
      combinations.add(new StringBuilder().append(letter));
    }
    
    String digital_suffix = digital_number.substring(1);
    List<String> subanswer = null;
    if (!digital_suffix.isEmpty()) {
      subanswer = generateAllCombinations(digital_suffix);
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
}
