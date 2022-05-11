/***
 * Excerpted from "Seven Concurrency Models in Seven Weeks",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/pb7con for more book information.
***/
package com.paulbutcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import org.yaml.snakeyaml.Yaml;

class Counter implements Runnable {

  private BlockingQueue<Page> queue;
  private BlockingQueue<HashMap<String, Integer>> merge_queue;
  HashMap<String, Integer> localCounts;
  private static HashSet<String> prohibitWord;

  public Counter(BlockingQueue<Page> queue,
                 BlockingQueue<HashMap<String, Integer>> merge_queue) {
    this.queue = queue;
    this.merge_queue = merge_queue;
  }

  public static void generateProhibitTable(String file){
    Yaml yaml = new Yaml();
    File f = new File(file);
    try (InputStream in = new FileInputStream(f))
    {
      Map<String, Object> obj = yaml.load(in);

      List<String> prohibitWordList = (List<String>) obj.get("list");

      prohibitWord = new HashSet<String>(prohibitWordList);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void run() {
    try {
      while(true) {

        //Create a new hashmap
        localCounts = new HashMap<String, Integer>(0x1000);

        //Take a page
        Page page = queue.take();

        //Check if Poison Pill
        if (page.isPoisonPill()) {
          merge_queue.put(localCounts);
          break;
        }

        //Count word
        Iterable<String> words = new Words(page.getText());
        for (String word: words)
          if(!prohibitWord.contains(word))
            countWord(word, localCounts);

        merge_queue.put(localCounts);
      }

    } catch (Exception e) { e.printStackTrace(); }
  }

  private void countWord(String word, HashMap<String, Integer> map) {
    Integer currentCount = map.get(word);
    if (currentCount == null)
      map.put(word, 1);
    else
      map.put(word, currentCount + 1);
  }

}