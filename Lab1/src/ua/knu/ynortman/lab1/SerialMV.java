package ua.knu.ynortman.lab1;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SerialMV {
    private int size; //dimension

    private double[] pMatrix; //square matrix size*size
    private double[] pVector; //vector
    private double[] pResult; //result vector

    public SerialMV() {
        System.out.println("Serial matrix-vector multiplication program");
    }

    public double[] getpResult() {
        return pResult;
    }

    public long run(int size, double[] pMatrix, double[] pVector) {
        this.size = size;
        // Memory allocation and definition of objects’ elements
        processInitialization(pMatrix, pVector);

        // Matrix and vector output
        //System.out.println("Initial Matrix: ");
        //printMatrix (size, size);
        //System.out.println("Initial Vector: \n");
        //printVector(pVector, size);

        // Matrix-vector multiplication
        //ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        long start = System.nanoTime();//threadMXBean.getThreadCpuTime(Thread.currentThread().getId());
        resultCalculation();
        long finish = System.nanoTime();//threadMXBean.getThreadCpuTime(Thread.currentThread().getId());
        System.out.println("Time of execution: "
                + TimeUnit.NANOSECONDS.toMicros(finish-start));
        return TimeUnit.NANOSECONDS.toMicros(finish-start);
        // Printing the result vector
        //System.out.println ("\n Result Vector: ");
        //printVector(pResult, size);
    }

    private void processInitialization(double[] pMatrix, double[] pVector) {
        // Memory allocation
        this.pMatrix = new double [size*size];
        this.pVector = new double [size];
        pResult = new double [size];

        if(pMatrix == null || pVector == null) {
            // Random definition of objects’ elements
            randomDataInitialization(size);
        } else {
            initialization(pMatrix, pVector);
        }
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

    // Function for formatted matrix output
    private void printMatrix (int rowCount, int colCount) {
        int i, j; // Loop variables
        for (i=0; i<rowCount; i++) {
            for (j=0; j<colCount; j++) {
                System.out.printf("%7.4f ", pMatrix[i * colCount + j]);
            }
            System.out.printf("\n");
        }
    }
    // Function for formatted vector output
    private void printVector (double[] pVector, int size) {
        int i;
        for (i=0; i<size; i++)
            System.out.printf("%7.4f ", pVector[i]);
        System.out.printf("\n");
    }

    // Function for matrix-vector multiplication
    private void resultCalculation() {
        int i, j; // Loop variables
        for (i=0; i<size; i++) {
            pResult[i] = 0;
            for (j=0; j<size; j++)
                pResult[i] += pMatrix[i*size+j]*pVector[j];
        }
    }

    // Function for random initialization of objects’ elements
    private void randomDataInitialization (int size) {
        Random random = new Random();
        pVector = random.doubles(size, 0, 500).toArray();
        pMatrix = random.doubles(size*size, 0, 500).toArray();
    }

}
