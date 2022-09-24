import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int max = 0;
        int n = scanner.nextInt();
        int previous = scanner.nextInt();
        for (int i = 0; i < n - 1; i++) {
            int next = scanner.nextInt();
            int result = previous * next;
            if (result > max) max = result;
            previous = next;
        }
        System.out.println(max);
    }
}