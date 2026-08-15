package BaiTap;

import java.util.Scanner;

public class Bai20 {
	public static void Tinh(int k) {
		long f1 = 1, f2 = 1, fn = 0;
        int position = 3;
        boolean found = false;
        if (k == 1) {
            System.out.println(k + " thuoc day Fibonacci (vi tri thu nhat va hai)");
            found = true;
        } else {
            while (fn < k) {
                fn = f1 + f2;
                if (fn == k) {
                    System.out.println(k + " thuoc day Fibonacci o vi tri thu " + position);
                    found = true;
                    break;
                }
                f1 = f2;
                f2 = fn;
                position++;
            }
        }
        if (found == false) {
            System.out.println(k + " khong thuoc day Fibonacci.");
        }
	}
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Nhap K: ");
        int k = sc.nextInt();
        Tinh(k);
        sc.close();
    }
}