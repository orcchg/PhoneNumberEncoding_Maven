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
import com.orcchg.javatask.numbenc.struct.Solver;

public class TestBorderCases {
  private static Solver mSolver, mSmallSolver;
  private static List<String> mNumbers, mSmallDictionary;
  private static Random mRng;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    mSolver = new Solver();
    mNumbers = new ArrayList<String>(100);
    mRng = new Random();
    mRng.setSeed(239);
    
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();

    try (InputStream fin = classloader.getResourceAsStream("dict.txt");
         BufferedReader reader = new BufferedReader(new InputStreamReader(fin))) {
      String line = null;
      while ((line = reader.readLine()) != null) {
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
    
    mSmallSolver = new Solver();
    mSmallDictionary = new ArrayList<String>();
    mSmallDictionary.add("An");
    mSmallDictionary.add("A\"n");
    mSmallDictionary.add("Bo\"");
    mSmallDictionary.add("BO\"");
    mSmallDictionary.add("da");
    mSmallDictionary.add("dA\"");
    mSmallDictionary.add("Fee");
    mSmallDictionary.add("Tor");
    mSmallDictionary.add("O\"mlaut");
    mSmallDictionary.add("Zeit");
    mSmallDictionary.add("Geist");
    mSmallDictionary.add("Feueur");
    mSmallDictionary.add("FeUeur");
    mSmallDictionary.add("Feu\"eur");
    mSmallDictionary.add("FeU\"eur");
    mSmallDictionary.add("u\"nd");
    mSmallDictionary.add("U\"nd");
    mSmallDictionary.add("Wasser");
    mSmallDictionary.add("WAsser");
    mSmallDictionary.add("Wa\"sser");
    mSmallDictionary.add("WA\"sser");
    
    for (String line : mSmallDictionary) {
      char label = line.charAt(0);
      Automaton automaton = mSmallSolver.getAutomaton(label);
      if (automaton == null) {
        automaton = mSmallSolver.addEmptyAutomaton(label);
      }
      automaton.addWord(line);
    }
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {}

  @Before
  public void setUp() throws Exception {}

  @After
  public void tearDown() throws Exception {}

  @Test
  public void testOneDigit() {
    for (int i = 0; i < 10; ++i) {
      StringBuilder number = new StringBuilder();
      int total_dashes = mRng.nextInt() % 10 + 1;
      int total_slashes = mRng.nextInt() % 10 + 1;
      int max = Math.max(total_dashes, total_slashes) + 2;
      for (int j = 0; j < max / 2; ++j) {
        int roll_dice = mRng.nextInt() % 2;
        if (roll_dice == 0) {
          number.append("-");
        } else {
          number.append("/");
        }
      }
      number.append(i);
      for (int j = max / 2; j <= max; ++j) {
        int roll_dice = mRng.nextInt() % 2;
        if (roll_dice == 0) {
          number.append("-");
        } else {
          number.append("/");
        }
      }
      
      List<String> answer = mSolver.solve(number.toString());
      assertTrue("One-digit number must have unique representation!", answer.size() <= 1);
    }
  }

  @Test
  public void testNoSuchAutomata() {
    List<String> answer = mSmallSolver.solve("--0/0/0-");
    assertTrue("No acceptable automata - nothing should be printed!", answer.isEmpty());
    
    answer = mSmallSolver.solve("--0//-");
    assertTrue("Permit one-digit numbers in any case", answer.size() == 1);
  }
  
  @Test
  public void testEmptyString() {
    List<String> answer = mSmallSolver.solve("");
    assertTrue("Nothing to be done!", answer.isEmpty());
  }
  
  @Test
  public void testInvalidNumberFormat() {
    try {
      mSmallSolver.solve(" ");
    } catch (RuntimeException expected) {
      assertTrue("The program must not be robust against incorrect formats " +
                 "of the dictionary file or the phone number file.", true);
    }
  }
  
  @Test
  public void testCaseSensitive() {
    List<String> answer = mSmallSolver.solve("40-7/07-2/--713//-25-33--02");
    assertTrue("Take all cases into consideration.", answer.size() == 32);
  }
  
  @Test
  public void testHardcodedCases() {
    List<String> answer = mSolver.solve("107-");
    assertTrue("Size of answer is incorrect!", answer.size() == 2);
    for (String string : answer) {
      assertTrue("Hardcoded answer was not matched!",
          string.equals("107-: neu") || string.equals("107-: je 7"));
    }
    
    answer = mSolver.solve("112");
    assertTrue("Answer must be empty!", answer.isEmpty());
    
    answer = mSolver.solve("5624-82");
    assertTrue("Size of answer is incorrect!", answer.size() == 2);
    for (String string : answer) {
      assertTrue("Hardcoded answer was not matched!",
          string.equals("5624-82: mir Tor") || string.equals("5624-82: Mix Tor"));
    }
    
    answer = mSolver.solve("4824");
    assertTrue("Size of answer is incorrect!", answer.size() == 3);
    for (String string : answer) {
      assertTrue("Hardcoded answer was not matched!",
          string.equals("4824: Torf") ||
          string.equals("4824: fort") ||
          string.equals("4824: Tor 4"));
    }
    
    answer = mSolver.solve("0721/608-4067");
    assertTrue("Answer must be empty!", answer.isEmpty());
    
    answer = mSolver.solve("10/783--5");
    assertTrue("Size of answer is incorrect!", answer.size() == 3);
    for (String string : answer) {
      assertTrue("Hardcoded answer was not matched!",
          string.equals("10/783--5: neu o\"d 5") ||
          string.equals("10/783--5: je bo\"s 5") ||
          string.equals("10/783--5: je Bo\" da"));
    }
    
    answer = mSolver.solve("1078-913-5");
    assertTrue("Answer must be empty!", answer.isEmpty());
    
    answer = mSolver.solve("381482");
    assertTrue("Size of answer is incorrect!", answer.size() == 1);
    for (String string : answer) {
      assertTrue("Hardcoded answer was not matched!",
          string.equals("381482: so 1 Tor"));
    }
    
    answer = mSolver.solve("04824");
    assertTrue("Size of answer is incorrect!", answer.size() == 3);
    for (String string : answer) {
      assertTrue("Hardcoded answer was not matched!",
          string.equals("04824: 0 Torf") ||
          string.equals("04824: 0 fort") ||
          string.equals("04824: 0 Tor 4"));
    }
  }
}
