package com.sdlc.pro.forkjoinpool;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class PrimerSearcher extends RecursiveAction {
    private static final int THRESHOLD = 50;

    private final int start;
    private final int end;

    public PrimerSearcher(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    protected void compute() {
        if (end - start <= THRESHOLD) {
            System.out.println("from: " + start + " to: " + end + ", subtask execute by: " + Thread.currentThread().getName());
            // find prime and print
            for (int i = start; i <= end; i++) {
                if (PrimerSearcher.isPrime(i)) {
                    System.out.println(i);
                }
            }
        } else {
            // make subtasks to find prime
            int mid = (start + end) / 2;

            var subtask1 = new PrimerSearcher(start, mid);
            var subtask2 = new PrimerSearcher(mid + 1, end);

            System.out.println("from: " + subtask1.start + " to: " + subtask1.end + ", subtask created by: " + Thread.currentThread().getName());
            System.out.println("from: " + subtask2.start + " to: " + subtask2.end + ", subtask created by: " + Thread.currentThread().getName());

            invokeAll(subtask1, subtask2);
        }
    }

    static boolean isPrime(int num) {
        if (num <= 1) return false;
        for (int i = 2; i <= Math.sqrt(num); i++) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try (var pool = ForkJoinPool.commonPool()) {
            var primeSearcher = new PrimerSearcher(1, 1000);
            pool.submit(primeSearcher).get();
        }
    }
}
