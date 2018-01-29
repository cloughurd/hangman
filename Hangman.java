//package hangman;

import java.io.*;
import java.util.*;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;

public class Hangman implements IEvilHangmanGame{
  private HashMap<Integer,HashSet<String>> fullDictionary = new HashMap<Integer,HashSet<String>>();
  private HashSet<String> workingDictionary = new HashSet<String>();
  private boolean[] guesses = new boolean[26];

  public static void main(String[] args){
    if(args.length < 3){
      System.out.println("Usage: java Hangman dicitionary wordLength guesses");
      return;
    }
    String filename = args[0];
    int wordLength = Integer.parseInt(args[1]);
    int numGuesses = Integer.parseInt(args[2]);
    if(wordLength < 2){
      System.out.println("Usage: java Hangman dicitionary wordLength guesses");
      return;
    }
    if(numGuesses < 1){
      System.out.println("Usage: java Hangman dicitionary wordLength guesses");
      return;
    }

    Hangman thisGame = new Hangman();
    thisGame.startGame(new File(filename), wordLength);
    thisGame.runGame(numGuesses, wordLength);
  }

  public void startGame(File dictionary, int wordLength){
    if(fullDictionary.size() == 0){
      readInDictionary(dictionary);
      if(fullDictionary.size() == 0)return;
    }
    workingDictionary = fullDictionary.get(wordLength);
  }
  private void readInDictionary(File dictionary){
    Scanner dictionaryReader = null;
    try{
      dictionaryReader = new Scanner(dictionary);
      while(dictionaryReader.hasNext()){
        String next = dictionaryReader.next();
        if(isWord(next)) {
          next = next.toLowerCase();
          int nextLength = next.length();
          if (!fullDictionary.containsKey(nextLength)) {
            fullDictionary.put(nextLength, new HashSet<String>());
          }
          fullDictionary.get(nextLength).add(next);
        }
      }
    }catch(FileNotFoundException fnfe){
      System.out.println("Dicitionary file does not exist.");
      System.out.println("Usage: java Hangman dicitionary wordLength guesses");
      return;
    }finally{
      if(dictionaryReader != null)
        dictionaryReader.close();
    }
  }
  private boolean isWord(String word){
    char[] wordArray = word.toCharArray();
    boolean legal = true;
    for(char c : wordArray){
      if(c < 'A' || c > 'z' || (c > 'Z' && c < 'a')){
        legal = false;
      }
    }
    return legal;
  }

  public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException{
    if(guesses[guess - 'a']) throw new GuessAlreadyMadeException();
    guesses[guess - 'a'] = true;
    HashMap<Integer, HashSet<String>> sorter = new HashMap<Integer, HashSet<String>>();
    for(String word : workingDictionary){
      int index = 0;
      char[] wordAsArray = word.toCharArray();
      for(int i = 0; i < wordAsArray.length; i++){
        if(wordAsArray[i] == guess){
          index+= 2 * i;
        }
      }
      if(!sorter.containsKey(index)){
        sorter.put(index, new HashSet<String>());
      }
      sorter.get(index).add(word);
    }

    int maxSize = 0;
    HashSet<String> bestChoice = null;
    int bestKey = 0;
    for(int key : sorter.keySet()){
      if(sorter.get(key).size() > maxSize){
        bestChoice = sorter.get(key);
        maxSize = bestChoice.size();
        bestKey = key;
      }else if(sorter.get(key).size() == maxSize){
        char[] wordFromBest = bestChoice.iterator().next().toCharArray();
        char[] wordFromMatch = sorter.get(key).iterator().next().toCharArray();
        int bestCount = 0;
        int matchCount = 0;
        for(int i = 0; i < wordFromBest.length; i++){
          if(wordFromBest[i] == guess){
            bestCount++;
          }
          if(wordFromMatch[i] == guess){
            matchCount++;
          }
        }
        if(matchCount < bestCount){
          bestChoice = sorter.get(key);
          bestKey = key;
        }else if(matchCount == bestCount){
          if(key > bestKey){
            bestChoice = sorter.get(key);
            bestKey = key;
          }
        }
      }
    }
    return bestChoice;
  }

  private void runGame(int numGuesses, int wordLength){
    char[] myWord = new char[wordLength];
    for(int i = 0; i < wordLength; i++){
      myWord[i] = '-';
    }
    Scanner input = new Scanner(System.in);
    boolean finished = false;
    while(numGuesses > 0){
      if(numGuesses == 1) {
        System.out.println("You have 1 guess left");
      }else{
        System.out.println("You have " + numGuesses + " guesses left");
      }
      System.out.print("Used letters:");
      for(int i = 0; i < 26; i++){
        if(guesses[i]){
          char c = (char)('a' + i);
          System.out.print(" " + c);
        }
      }
      System.out.print("\nWord: ");
      for(char c : myWord){
        System.out.print(c);
      }
      System.out.print("\nEnter guess: ");
      String nextGuess = input.nextLine();
      boolean isValid = true;
      if(nextGuess.length() != 1){
        isValid = false;
      }
      char[] theirGuess = nextGuess.toCharArray();
      for(char c : theirGuess){
        if(c < 'a' || c > 'z'){
          isValid = false;
        }
      }
      if(!isValid){
        System.out.println("Invalid guess");
        continue;
      }
      try {
        workingDictionary = (HashSet<String>) makeGuess(theirGuess[0]);
      }catch(GuessAlreadyMadeException game){
        System.out.println("You already guessed that!");
        continue;
      }
      int matchCount = 0;
      char[] match = workingDictionary.iterator().next().toCharArray();
      for(int i = 0; i < match.length; i++){
        if(match[i] == theirGuess[0]){
          matchCount++;
          myWord[i] = theirGuess[0];
        }
      }
      if(matchCount == 0){
        System.out.println("Sorry, there are no " + theirGuess[0] + "'s");
      }else if(matchCount == 1){
        System.out.println("Yes, there is 1 " + theirGuess[0]);
      }else{
        System.out.println("Yes, there are "+ matchCount + " " + theirGuess[0] + "'s");
      }
      finished = true;
      for(char c : myWord){
        if(c == '-'){
          finished = false;
        }
      }
      if(finished)break;
    }
    if(finished){
      System.out.println("Congratulations, you won!");
    }else{
      System.out.println("You lose!");
      System.out.print("The word was: " + new String(myWord));
    }
  }
}
