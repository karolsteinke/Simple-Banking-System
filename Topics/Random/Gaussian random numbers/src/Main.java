import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // write your code here
        int k = scanner.nextInt();
        int n = scanner.nextInt();
        double m = scanner.nextDouble();
        while (true) {
            Random random = new Random(k);
            boolean isComplete = true;
            for (int i = 1; i <= n; i++) {
                if (random.nextGaussian() > m) {
                    isComplete = false;
                    break;
                }
            }
            if (isComplete) break;
            k++;
        }
        System.out.println(k);
    }
}