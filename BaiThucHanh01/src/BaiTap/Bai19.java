package BaiTap;

import java.util.Scanner;

public class Bai19 {
	public static void Tinh(int n) {
		long f1 = 1, f2 = 1, fn;
        if (n < 1) {
            System.out.println("n phai >= 1");
        } else if (n == 1) {
            System.out.print("1");
        } else {
            System.out.print("1 1 ");
            for (int i = 3; i <= n; i++) {
                fn = f1 + f2;
                System.out.print(fn + " ");
                f1 = f2;
                f2 = fn;
            }
        }
	}
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Nhap n: ");
        int n = sc.nextInt();
    	Tinh(n);
        sc.close();
    }
}