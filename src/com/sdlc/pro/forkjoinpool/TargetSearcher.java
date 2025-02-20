package com.sdlc.pro.forkjoinpool;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicBoolean;

class TargetSearcher extends RecursiveTask<Integer> {
    private final int[] array;
    private final int target;
    private final int start;
    private final int end;

    private final AtomicBoolean found;
    private static final int THRESHOLD = 10;

    public TargetSearcher(int[] array, int target) {
        this(array, target, 0, array.length - 1, new AtomicBoolean(false));
    }

    private TargetSearcher(int[] array, int target, int start, int end, AtomicBoolean found) {
        this.array = array;
        this.target = target;
        this.start = start;
        this.end = end;
        this.found = found;
    }

    @Override
    protected Integer compute() {
        if (found.get()) {
            System.out.println("task canceled for: " + Thread.currentThread().getName());
            return -1;
        }
        if (end - start <= THRESHOLD) {
            // search
            for (var i = start; i <= end; i++) {
                if (array[i] == target) {
                    found.set(true);
                    return i;
                }
            }

            return -1;
        }

        // create subtasks
        var mid = (start + end) / 2;
        var subtask1 = new TargetSearcher(array, target, start, mid, found);
        var subtask2 = new TargetSearcher(array, target, mid + 1, end, found);

        subtask1.fork();
        subtask2.fork();

        int result = subtask1.join();

        return result == -1 ? subtask2.join() : result;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        var n = 1000;
        var array = new int[n];
        for (var i = 0; i < n; i++) {
            array[i] = i;
        }

        try (var pool = ForkJoinPool.commonPool()) {
            var targetSearcher = new TargetSearcher(array, 67);
            var result = pool.submit(targetSearcher).get();
            System.out.println("Found at index: " + result);
        }
    }
}
