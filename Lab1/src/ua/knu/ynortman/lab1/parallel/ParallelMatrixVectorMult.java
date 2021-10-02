package ua.knu.ynortman.lab1.parallel;

import mpi.MPI;
import ua.knu.ynortman.lab1.SerialMV;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ParallelMatrixVectorMult {
    private final int procNum; // Number of available processes
    private final int procRank; // Rank of current process

    private double[] pMatrix; //square matrix size*size
    private double[] pVector;
    private double[] pResult;
    private int size;

    double[] pProcRows; // Stripe of the matrix on current process
    double[] pProcResult; // Block of result vector on current process
    private int rowNum; //the number of matrix rows for process

    private int[] pSendNum; // the number of elements sent to the process
    private int[] pSendInd; // the index of the first data element sent to the process

    int[] pReceiveNum; // Number of elements, that current process sends
    int[] pReceiveInd; /* Index of the first element from current process
                          in result vector */

    private final int MASTER = 0;
    private int lock = -1;

    public ParallelMatrixVectorMult(String[] args, int size) {
        MPI.Init(args);

        procNum = MPI.COMM_WORLD.Size();
        procRank = MPI.COMM_WORLD.Rank();
        this.size = size;
        if (procRank == MASTER) {
            System.out.println("Parallel matrix-vector multiplication program");
        }
        processInitialization();
    }

    public void run() {
        long start = System.nanoTime();
        // Distributing the initial objects between the processes
        dataDistribution();
        // Distribution test
        //testDistribution();
        // Process rows and vector multiplication
        parallelResultCalculation();
        //testPartialResults();
        // Result replication
        resultReplication();
        long finish = System.nanoTime();
        //printVector(pResult, pResult.length);
        System.out.println("Time of execution parallel: "
                + TimeUnit.NANOSECONDS.toMicros(finish-start));
        if(procRank == MASTER) {
            System.out.println("Result: " + testResult());
        }
    }

    public void finish() {
        MPI.Finalize();
    }

    private void processInitialization() {
        int restRows; // Number of rows, that haven’t been distributed yet
        int i; // Loop variable
        MPI.COMM_WORLD.Bcast(new int[] {size}, 0,1, MPI.INT, MASTER);
        restRows = size;
        for (i=0; i<procRank; i++) {
            restRows = restRows - restRows / (procNum - i);
        }

        // Determine the number of matrix rows stored on each process
        rowNum = restRows/(procNum-procRank);
        //rowNum = size/procNum;
        // Memory allocation
        this.pMatrix = new double[0];
        this.pVector = new double [size];
        pResult = new double [size];
        pProcRows = new double [rowNum*size];
        pProcResult = new double [rowNum];
        // Obtain the values of initial objects’ elements
        if (procRank == MASTER) {
            // Initial matrix exists only on the pivot process
            pMatrix = new double [size*size];

            // Values of elements are defined only on the pivot process
            randomDataInitialization();
            //dummyDataInitialization();
        }
        MPI.COMM_WORLD.Barrier();

    }

    private void initPSend() {
        pSendNum = new int[procNum]; // the number of elements sent to the process
        pSendInd = new int[procNum]; // the index of the first data element sent to the process
        int restRows = size; // Number of rows, that haven’t been distributed yet

        // Determine the disposition of the matrix rows for current process
        rowNum = (size / procNum);
        pSendNum[0] = rowNum * size;
        pSendInd[0] = 0;
        for (int i = 1; i < procNum; i++) {
            restRows -= rowNum;
            rowNum = restRows / (procNum - i);
            pSendNum[i] = rowNum * size;
            pSendInd[i] = pSendInd[i - 1] + pSendNum[i - 1];
        }
    }

    // Function for distribution of the initial objects between the processes
    private void dataDistribution() {
        initPSend();
        MPI.COMM_WORLD.Bcast(pVector, 0,size, MPI.DOUBLE, MASTER);
        // Scatter the rows
        MPI.COMM_WORLD.Scatterv(pMatrix, 0, pSendNum, pSendInd, MPI.DOUBLE,
                pProcRows, 0, pSendNum[procRank], MPI.DOUBLE, MASTER);
    }

    private void randomDataInitialization () {
        Random random = new Random();
        pVector = random.doubles(size, 0, 500).toArray();
        pMatrix = random.doubles(size*size, 0, 500).toArray();
    }

    private void dummyDataInitialization () {
        int i, j; // Loop variables
        for (i=0; i<size; i++) {
            pVector[i] = 1;
            for (j=0; j<size; j++)
                pMatrix[i*size+j] = i;
        }
    }

    private void initialization(double[] pMatrix, double[] pVector) {
        this.pMatrix = pMatrix;
        this.pVector = pVector;
    }

    private void testDistribution() {
        if (procRank == 0) {
            System.out.print("Initial Matrix: ");
            printMatrix(pMatrix, size, size);
            System.out.print("\nInitial Vector: ");
            printVector(pVector, size);
        }
        MPI.COMM_WORLD.Barrier();
        for (int i=0; i<procNum; i++) {
            if (procRank == i) {
                System.out.printf("\n\nProcRank = %d \n", procRank);
                System.out.print(" Matrix Stripe:\n");
                printMatrix(pProcRows, rowNum, size);
                System.out.print(" Vector: \n");
                printVector(pVector, size);
            }
            MPI.COMM_WORLD.Barrier();
        }
    }

    // Fuction for testing the results of multiplication of matrix stripe
    // by a vector
    void testPartialResults() {
        int i; // Loop variable
        for (i=0; i<procNum; i++) {
            if (procRank == i) {
                System.out.printf("ProcRank = %d \n", procRank);
                System.out.print("Part of result vector: \n");
                printVector(pProcResult, rowNum);
            }
            MPI.COMM_WORLD.Barrier();
        }
    }

    // Function for formatted matrix output
    private void printMatrix (double[] pMatrix, int rowCount, int colCount) {
        int i, j; // Loop variables
        for (i=0; i<rowCount; i++) {
            for (j=0; j<colCount; j++) {
                System.out.printf("%7.4f ", pMatrix[i * colCount + j]);
            }
            System.out.print("\n");
        }
    }
    // Function for formatted vector output
    private void printVector (double[] pVector, int size) {
        int i;
        for (i=0; i<size; i++)
            System.out.printf("%7.4f ", pVector[i]);
        System.out.print("\n");
    }

    // Process rows and vector multiplication
    private void parallelResultCalculation() {
        int i, j;
        for (i=0; i<pProcResult.length; i++) {
            pProcResult[i] = 0;
            for (j=0; j<size; j++) {
                pProcResult[i] += pProcRows[i*size+j]*pVector[j];
            }
        }
    }

    private void initPReceive() {
        int i; // Loop variable
        pReceiveNum = new int[procNum]; // Number of elements, that current process sends
        pReceiveInd = new int[procNum]; /* Index of the first element from current process
        in result vector */
        int restRows=size; // Number of rows, that haven’t been distributed yet

        // Detrmine the disposition of the result vector block of current processor
        pReceiveInd[0] = 0;
        pReceiveNum[0] = size/procNum;
        for (i=1; i<procNum; i++) {
            restRows -= pReceiveNum[i-1];
            pReceiveNum[i] = restRows/(procNum-i);
            pReceiveInd[i] = pReceiveInd[i-1]+pReceiveNum[i-1];
        }
    }

    // Result vector replication
    private void resultReplication() {
        initPReceive();
        // Gather the whole result vector on every processor
        MPI.COMM_WORLD.Allgatherv(pProcResult, 0, pReceiveNum[procRank], MPI.DOUBLE,
                pResult, 0, pReceiveNum, pReceiveInd, MPI.DOUBLE);
    }

    private boolean testResult() {
        SerialMV serialMV = new SerialMV();
        serialMV.run(size, pMatrix, pVector);
        double[] serialRes = serialMV.getpResult();
        return Arrays.equals(serialRes, pResult);
    }
}
