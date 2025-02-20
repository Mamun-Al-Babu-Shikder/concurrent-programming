package com.sdlc.pro.forkjoinpool;

import java.util.concurrent.*;

public class BlockingOperation extends RecursiveAction implements ForkJoinPool.ManagedBlocker {
    private final String name;
    private volatile boolean flag = false;

    public BlockingOperation(String name) {
        this.name = name;
    }

    @Override
    protected void compute() {
        try {
            ForkJoinPool.managedBlock(this);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean block() throws InterruptedException {
        System.out.println(name + " started :: " + Thread.currentThread().getName());
        Thread.sleep(5000);
        System.out.println(name + " end! :: " + Thread.currentThread().getName());
        this.flag = true;
        return true;
    }

    @Override
    public boolean isReleasable() {
        return flag;
    }

    public static void main(String[] args) throws InterruptedException {
        try(var pool = ForkJoinPool.commonPool()) {
            var task1 = new BlockingOperation("blocking-task-1");
            var task2 = new BlockingOperation("blocking-task-2");
            var task3 = new BlockingOperation("blocking-task-3");
            pool.submit(task1);
            pool.submit(task2);
            pool.submit(task3);
            pool.awaitTermination(1, TimeUnit.MINUTES);
        }
    }
}
