package ua.knu.ynortman.lab1.parallel;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        int[] size = {10, 100, 1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000};
        int standartSize = 5000;
        long t1 = 87366;
        System.out.println(t1);
        float t = (float)t1/(standartSize*(2*standartSize-1));
        System.out.println("One opeartion takes " + t + " microseconds");
        for (int s : size) {
            System.out.println("Size: " + s);
            System.out.println("Theoretical time = " + (t*s*(2*s-1)));
            ParallelMatrixVectorMult parallelMatrixVectorMult = new ParallelMatrixVectorMult(args, s);
            //ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
            //long start = System.nanoTime();//threadMXBean.getThreadCpuTime(Thread.currentThread().getId());
            parallelMatrixVectorMult.run();
            //long finish = System.nanoTime();//threadMXBean.getThreadCpuTime(Thread.currentThread().getId());
            //System.out.println("Time of execution parallel: "
            //        + TimeUnit.NANOSECONDS.toMicros(finish-start));
            parallelMatrixVectorMult.finish();
            System.out.println("\n\n");
        }
    }
}
