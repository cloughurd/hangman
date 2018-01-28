package hangman;

import java.util.Scanner;
import java.util.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Set;

public class Hangman implements IEvilHangmanGame{
  private HashMap<int,HashSet<String>> fullDictionary = new HashMap<int,HashSet<String>>();
  private HashSet<String> workingDictionary = new HashSet<String>();
  private boolean[] guesses = new boolean[26];

  public void startGame(File dictionary, int wordLength){
    if(fullDictionary.size() == 0){
      readInDictionary(dictionary);
      if(fullDictionary.size() == 0)return;
    }
    workingDictionary = fullDictionary.get(wordLength);
  }
  private void readInDictionary(File dictionary){
    Scanner dictionaryReader;
    try{
      dictionaryReader = new Scanner(dictionary);
      while(dictionaryReader.hasNext()){
        String next = dictionaryReader.next();
        int nextLength = next.length();
        if(!fullDictionary.containsKey(nextLength)){
          fullDictionary.put(nextLength, new ArrayList<String>());
        }
        fullDictionary.get(nextLength).add(next);
      }
    }catch(FileNotFoundException fnfe){
      System.out.println("Dicitionary file does not exist.");
      return;
    }finally{
      dictionaryReader.close();
    }
  }
  public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException{
    
  }
}
