/***
 * Excerpted from "Seven Concurrency Models in Seven Weeks",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/pb7con for more book information.
***/
package com.paulbutcher;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

class Counter implements Runnable {

  private BlockingQueue<Page> queue;
  private BlockingQueue<HashMap<String, Integer>> merge_queue;
  HashMap<String, Integer> localCounts;

  public Counter(BlockingQueue<Page> queue,
                 BlockingQueue<HashMap<String, Integer>> merge_queue) {
    this.queue = queue;
    this.merge_queue = merge_queue;
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