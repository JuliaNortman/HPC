package ua.knu.ynortman.lab1.parallel;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        ParallelMatrixVectorMult parallelMatrixVectorMult = new ParallelMatrixVectorMult(args, 10000);
        //ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        //long start = System.nanoTime();//threadMXBean.getThreadCpuTime(Thread.currentThread().getId());
        parallelMatrixVectorMult.run();
        //long finish = System.nanoTime();//threadMXBean.getThreadCpuTime(Thread.currentThread().getId());
        //System.out.println("Time of execution parallel: "
        //        + TimeUnit.NANOSECONDS.toMicros(finish-start));
        parallelMatrixVectorMult.finish();
    }
}
