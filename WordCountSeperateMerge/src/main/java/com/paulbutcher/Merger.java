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

class Merger implements Runnable {

  private BlockingQueue<HashMap<String, Integer>> queue;
  private HashMap<String, Integer> counts;
  private int total_counter;
  private int count_empty = 0;

  public Merger(BlockingQueue<HashMap<String, Integer>> queue,
                 HashMap<String, Integer> counts,
                 int counter) {
    this.queue = queue;
    this.counts = counts;
    this.total_counter = counter;
    count_empty = 0;
  }

  public void run() {
    try {
      while(true) {
        //Take a hashmap
        HashMap<String, Integer> map = queue.take();

        //Check size, if it's empty, we will exit.
        if (map.size() == 0) {
          count_empty ++;
          if(count_empty >= total_counter) {
            break;
          }
        }

        //Merge this table.
        map.forEach((key, value) -> counts.merge(key, value, (oldValue, newValue) -> oldValue + newValue ));
      }
    } catch (Exception e) { e.printStackTrace(); }
  }
}