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

import java.util.concurrent.*;
import java.util.Map.Entry;

public class WordCount {

  private static final int NUM_COUNTERS = 6;

  public static void main(String[] args) throws Exception {
    ArrayBlockingQueue<Page> queue = new ArrayBlockingQueue<Page>(1000);
    ArrayBlockingQueue<HashMap<String, Integer>> merge_queue = new ArrayBlockingQueue<HashMap<String, Integer>>(10000);

    HashMap<String, Integer> counts = new HashMap<String, Integer>(0x10000);

    System.out.println(Runtime.getRuntime().availableProcessors());

    ExecutorService executor = Executors.newCachedThreadPool();

    for (int i = 0; i < NUM_COUNTERS; ++i)
      executor.execute(new Counter(queue, merge_queue));

    Thread parser = new Thread(new Parser(queue));

    Thread merger = new Thread(new Merger(merge_queue, counts, NUM_COUNTERS));

    long start = System.currentTimeMillis();

    parser.start();
    merger.start();

    parser.join();

    for (int i = 0; i < NUM_COUNTERS; ++i)
      queue.put(new PoisonPill());

    executor.shutdown();
    executor.awaitTermination(10L, TimeUnit.MINUTES);

    merger.join();

    //Get time
    long end = System.currentTimeMillis();
    System.out.println("Elapsed time: " + (end - start) + "ms");

    //Print Result
    printResult(counts);

  }

  private static void printPageInfo(Iterable<Page> pages) {
    int count_page = 0;
    for(Page page: pages) {
      count_page ++;
      if(count_page < 100) {
        System.out.println("NO: " + count_page + " " + "Page Title: " + page.getTitle() + " " + "Text Length: " + page.getText().length());
      }
    }

    System.out.println("Total Page: " + count_page);
  }

  private static void printResult(HashMap<String, Integer> result) {
    Set<Entry<String, Integer>> entrySet = result.entrySet();
    List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(entrySet);

    Comparator<Map.Entry<String, Integer>> comparator = new Comparator<Map.Entry<String, Integer>>() {
      @Override
      public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
        //return o1.getValue().compareTo(o2.getValue());
        return o2.getValue().compareTo(o1.getValue());
      }
    };

    Collections.sort(list, comparator);

    for (Map.Entry<String, Integer> mapping : list.subList(0, 10)) {
      System.out.println(mapping.getKey() + ":" + mapping.getValue());
    }
  }
}
