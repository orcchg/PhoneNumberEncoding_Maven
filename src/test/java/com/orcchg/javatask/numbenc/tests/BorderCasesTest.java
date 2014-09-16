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
import com.orcchg.javatask.numbenc.utils.Util;

public class BorderCasesTest {
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
    mSmallDictionary.add("an");
    mSmallDictionary.add("Bo\"");
    mSmallDictionary.add("da");
    mSmallDictionary.add("Fee");
    mSmallDictionary.add("Tor");
    mSmallDictionary.add("O\"mlaut");
    mSmallDictionary.add("Zeit");
    mSmallDictionary.add("Geist");
    mSmallDictionary.add("Feueur");
    mSmallDictionary.add("u\"nd");
    mSmallDictionary.add("Wasser");
    
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
    List<String> answer = mSmallSolver.solve("--0//-");
    assertTrue("No acceptable automata - nothing should be printed!", answer.isEmpty());
  }
}
