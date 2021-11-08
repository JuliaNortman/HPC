import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

public class Serial {
    public static double serial(int iter, double x) {
        double sum = 0;
        for(int i = 1; i < iter; ++i) {
            sum += Math.pow(x, i) / i;
        }
        return sum * -1;
    }

    public static void main(String[] args) {
        int[] size = {1000, 10000, 100000, 200000, 300000, 400000,
                500000, 600000, 700000, 800000, 900000, 1000000};
        for(int s : size) {
            long start = System.nanoTime();
            Serial.serial(s, 0.5);
            long finish = System.nanoTime();
            System.out.println("Time of execution: "
                    + TimeUnit.NANOSECONDS.toMicros(finish - start));
        }

    }
}
