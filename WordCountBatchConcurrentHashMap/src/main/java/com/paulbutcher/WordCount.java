/***
 * Excerpted from "Seven Concurrency Models in Seven Weeks",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/pb7con for more book information.
***/
package com.paulbutcher;

import java.util.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Map.Entry;

public class WordCount {

  private static final int NUM_COUNTERS = 4;

  public static void main(String[] args) throws Exception {
    ArrayBlockingQueue<Page> queue = new ArrayBlockingQueue<Page>(100);
    ConcurrentHashMap<String, Integer> counts = new ConcurrentHashMap<String, Integer>();
    ExecutorService executor = Executors.newCachedThreadPool();

    for (int i = 0; i < NUM_COUNTERS; ++i)
      executor.execute(new Counter(queue, counts));
    Thread parser = new Thread(new Parser(queue));
    long start = System.currentTimeMillis();
    parser.start();
    parser.join();
    for (int i = 0; i < NUM_COUNTERS; ++i)
      queue.put(new PoisonPill());
    executor.shutdown();
    executor.awaitTermination(10L, TimeUnit.MINUTES);
    long end = System.currentTimeMillis();
    System.out.println("Elapsed time: " + (end - start) + "ms");

    Set<Entry<String, Integer>> entrySet = counts.entrySet();
    List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(entrySet);

    Comparator<Map.Entry<String, Integer>> comparator = new Comparator<Map.Entry<String, Integer>>() {
      //降序排序
      @Override
      public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
        //return o1.getValue().compareTo(o2.getValue());
        return o2.getValue().compareTo(o1.getValue());
      }
    };

    Collections.sort(list, comparator);

    for (Map.Entry<String, Integer> mapping : list) {
      System.out.println(mapping.getKey() + ":" + mapping.getValue());
    }
  }
}
