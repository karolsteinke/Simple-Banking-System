import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int l = scanner.nextInt();
        int[] numbers = new int[l];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = scanner.nextInt();
        }
        int n = scanner.nextInt();
        int m = scanner.nextInt();
        boolean result = true;
        for (int i = 0; i < numbers.length - 1; i++) {
            //check if m stands before n or otherwise
            if ((numbers[i] == m) && (numbers[i+1] == n)) result = false;
            if ((numbers[i] == n) && (numbers[i+1] == m)) result = false;
        }
        /*
        //debug
        System.out.println("l: " + l + " n: " + n + " m: " + m);
        for (int i=0;i<numbers.length;i++) {
            System.out.println("numbers[i] " + numbers[i]);
        }
        //end of debug
         */
        System.out.println(result);
    }
}
