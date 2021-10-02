package ua.knu.ynortman.lab1;

import ua.knu.ynortman.lab1.parallel.ParallelMatrixVectorMult;

public class Main {
    public static void main(String[] args) {
        SerialMV serialMV = new SerialMV();


        int[] size = {10, 100, 1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000};
        int standartSize = 5000;
        long t1 = 34417;//serialMV.run(standartSize);
        System.out.println(t1);
        float t = (float)t1/(standartSize*(2*standartSize-1));
        System.out.println("One opeartion takes " + t + " microseconds");
        for(int s : size) {
            System.out.println("Time: " + s);
            serialMV.run(s, null, null);
            System.out.println("Theoretical time = " + (t*s*(2*s-1)));
            System.out.println("==============================\n");
        }

        //ParallelMatrixVectorMult parallelMatrixVectorMult = new ParallelMatrixVectorMult(args);


    }
}
