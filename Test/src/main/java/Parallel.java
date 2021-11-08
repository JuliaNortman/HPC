import mpi.MPI;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Parallel {
    public static void main(String[] args) {
        int[] size = {1000, 10000, 100000, 200000, 300000, 400000,
                500000, 600000, 700000, 800000, 900000, 1000000};
        for (int s : size) {
            long start = System.nanoTime();
            MPI.Init(args);
            Parallel parallel = new Parallel(0.5, s);
            parallel.parallel();
            MPI.Finalize();
            long finish = System.nanoTime();
            System.out.println("Time of execution: "
                    + TimeUnit.NANOSECONDS.toMicros(finish - start));
        }
    }

    private double x;
    private int iter;

    public Parallel(double x, int iter) {
        this.x = x;
        this.iter = iter;
    }

    public double ln(int start, int finish, double x) {
        double sum = 0;
        for(int i = start; i < finish; ++i) {
            sum += Math.pow(x, i) / i;
        }
        return sum * -1;
    }


    public void parallel() {
        int procRank = MPI.COMM_WORLD.Rank();
        int procNum = MPI.COMM_WORLD.Size();
        double procResult = 0;
        double[] result = new double[procNum];
        MPI.COMM_WORLD.Bcast(new double[]{x}, 0,1, MPI.DOUBLE, 0);
        MPI.COMM_WORLD.Bcast(new int[]{iter}, 0,1, MPI.INT, 0);
        int start = 1+(iter/procNum)*procRank;
        int end = (iter/procNum)*(procRank+1);
        procResult = ln(start, end, x);
        MPI.COMM_WORLD.Allgather(new double[]{procResult},0,1, MPI.DOUBLE,
                result, 0, 1, MPI.DOUBLE);
        if(procRank == 0) {
            double res = Arrays.stream(result).sum();
            System.out.println(res);
        }
    }


}
