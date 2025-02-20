package com.sdlc.pro.forkjoinpool;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

class FrequencyCounter extends RecursiveTask<Integer> {
    private final int[] array;
    private final int target;
    private final int start;
    private final int end;

    private static final int THRESHOLD = 50;

    public FrequencyCounter(int[] array, int target) {
        this(array, target, 0, array.length - 1);
    }

    private FrequencyCounter(int[] array, int target, int start, int end) {
        this.array = array;
        this.target = target;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        if (end - start <= THRESHOLD) {
            System.out.println("from: " + start + " to: " + end + ", count by: " + Thread.currentThread().getName());
            // count the frequency of target value
            int count = 0;
            for (var i = start; i <= end; i++) {
                if (array[i] == target) {
                    count++;
                }
            }
            return count;
        }

        // create subtasks
        int mid = (start + end) / 2;

        var subtask1 = new FrequencyCounter(array, target, start, mid);
        var subtask2 = new FrequencyCounter(array, target, mid + 1, end);

        subtask1.fork();
        subtask2.fork();

        return subtask1.join() + subtask2.join();
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        var n = 1000;
        var rand = new Random();
        var array = new int[n];
        for (var i = 0; i < n; i++) {
            array[i] = rand.nextInt(50) + 1;
        }

        try (var pool = ForkJoinPool.commonPool()) {
            var frequencyCounter = new FrequencyCounter(array, 13);
            var count = pool.submit(frequencyCounter).get();
            System.out.println(count);
        }
    }
}
