import java.util.*;

public class Main {
    public static void main(String[] args) {
        // write your code here
        Scanner scan = new Scanner(System.in);
        int[] boxA = {
                scan.nextInt(),
                scan.nextInt(),
                scan.nextInt()
        };
        int[] boxB = {
                scan.nextInt(),
                scan.nextInt(),
                scan.nextInt()
        };
        orderArray(boxA);
        orderArray(boxB);
        //System.out.println(boxA[0] + " " + boxA[1] + " " + boxA[2]);
        //System.out.println(boxB[0] + " " + boxB[1] + " " + boxB[2]);

        int a = boxA[0] - boxB[0];
        int b = boxA[1] - boxB[1];
        int c = boxA[2] - boxB[2];
        //System.out.println("a:" + a + "b:" + b + "c:" + c); //DEBUG

        if (a > 0 && b > 0 && c > 0) {
            System.out.println("Box 1 > Box 2");
        }
        else if (a < 0 && b < 0 && c < 0) {
            System.out.println("Box 1 < Box 2");
        }
        else {
            System.out.println("Incompatible");
        }

    }
    private static void orderArray(int[] arr) {
        if (arr[0] <= arr[1]) {
            if (arr[1] <= arr[2]) {
                return;
            }
            else if (arr[0] <= arr[2]){
                int temp = arr[1];
                arr[1] = arr[2];
                arr[2] = temp;
            }
            else {
                int temp = arr[0];
                arr[0] = arr[2];
                arr[2] = arr[1];
                arr[1] = temp;
            }
        }
        else if (arr[0] <= arr[2]) {
            int temp = arr[0];
            arr[0] = arr[1];
            arr[1] = temp;
        }
        else if (arr[1] <= arr[2]) {
            int temp = arr[0];
            arr[0] = arr[1];
            arr[1] = arr[2];
            arr[2] = temp;
        }
        else {
            int temp = arr[0];
            arr[0] = arr[2];
            arr[2] = temp;
        }
    }

}