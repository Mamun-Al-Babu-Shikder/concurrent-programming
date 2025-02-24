package com.sdlc.pro.forkjoinpool;

import java.util.concurrent.*;

public class StringUppercase extends RecursiveAction {
    private final char[] chars;
    private final int start;
    private final int end;

    public StringUppercase(char[] chars) {
        this(chars, 0, chars.length - 1);
    }

    private StringUppercase(char[] chars, int start, int end) {
        this.chars = chars;
        this.start = start;
        this.end = end;
    }

    @Override
    protected void compute() {
        if (end - start == 0) {
            var ch = chars[start];
            if (ch >= 'a' && ch <= 'z') {
                chars[start] -= 32;
            }
            return;
        }

        var mid = (start + end) / 2;
        var subtask1 = new StringUppercase(chars, start, mid);
        var subtask2 = new StringUppercase(chars, mid + 1, end);
        invokeAll(subtask1, subtask2);
    }

    public static void main(String[] args) throws Exception {
        try (var pool = ForkJoinPool.commonPool()) {
            var chars = "Simple Program to Convert the Characters from Lowercase to Uppercase!".toCharArray();
            var stringUppercase = new StringUppercase(chars);
            pool.invoke(stringUppercase);
            System.out.println(new String(chars));
        }
    }
}
