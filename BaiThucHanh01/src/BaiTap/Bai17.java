package BaiTap;

import java.util.Scanner;

public class Bai17 {
	public static boolean isPrime(int n) {
		if (n < 2) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;
        for (int i = 3; i * i <= n; i += 2) {
            if (n % i == 0) return false;
        }
        return true;
	}
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
        System.out.print("Nhap n: ");
        int n = sc.nextInt();
        System.out.println("Cac so nguyen to <= " + n + " la: ");
        for (int i = 2; i <= n; i++) {
            if (isPrime(i)) {
                System.out.print(i + " ");
            }
        }
        sc.close();
	}
}
