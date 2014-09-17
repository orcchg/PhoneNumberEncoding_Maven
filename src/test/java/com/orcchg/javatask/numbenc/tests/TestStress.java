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
  public void testStress() {
    List<String> combinations = Util.generateAllCombinations("4824", LookupTable.map);
    assertTrue("Not all combinations were generated!", combinations.size() == 1575);
    for (String combination : combinations) {
      if (!Util.hasAdjacentDigits(combination)) {
        List<String> matching_combination = matchWords(combination, mDictionaryModified);
        if (!matching_combination.isEmpty()) {
          System.err.println("COMB: " + combination + "; " + matching_combination);
        }
      }
    }
    
    List<String> matches = matchWords("|dOrtMixer", mDictionaryModified);
    for (String match : matches) {
      System.out.println(match);
      assertTrue("Matching malfunctions!",
          match.contains("|d") && match.contains("Ort") && match.contains("Mixer"));
    }
  }
  
  /* Private methods */
  // --------------------------------------------------------------------------
  private List<String> matchWords(final String combination, final List<String> dictionary) {
    List<String> answer = new ArrayList<>(1000);
    if (combination.isEmpty()) {
      return answer;
    }
    
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
    return answer;
  }
}
