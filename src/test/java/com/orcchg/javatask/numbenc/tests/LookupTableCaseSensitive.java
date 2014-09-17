package com.orcchg.javatask.numbenc.tests;

class LookupTableCaseSensitive {
  public static final int TABLE_SIZE = 58;
  
  public static char[][] map = new char[][]{
    {'e', 'E'},                                  // 0
    {'j', 'J', 'n', 'N', 'q', 'Q'},              // 1
    {'r', 'R', 'w', 'W', 'x', 'X'},              // 2
    {'d', 'D', 's', 'S', 'y', 'Y'},              // 3
    {'f', 'F', 't', 'T'},                        // 4
    {'a', 'A', '{', '[', 'm', 'M'},              // 5   { - a", [ - A"
    {'c', 'C', 'i', 'I', 'v', 'V'},              // 6
    {'b', 'B', 'k', 'K', 'u', 'U', '}', ']'},    // 7   } - u", ] - U"
    {'l', 'L', 'o', 'O', '|', '\\', 'p', 'P'},   // 8   | - o", \ - O"
    {'g', 'G', 'h', 'H', 'z', 'Z'},              // 9
  };
}
