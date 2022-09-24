import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        // put your code here
        Scanner scan = new Scanner(System.in);
        int size = scan.nextInt();
        int min = 100;
        for (int i=1; i<=size; i++) {
            int a = scan.nextInt();
            if (a < min) min = a;
        }
        System.out.println(min);
    }
}