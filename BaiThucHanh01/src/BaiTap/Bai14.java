package BaiTap;

import java.util.Scanner;

public class Bai14 {
	public static boolean kiemtra(int n){
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
		System.out.println("Nhap n: ");
		int n = sc.nextInt();
		System.out.println(n + (kiemtra(n)?" la":" khong la") + " so nguyen to");
		sc.close();
	}
}
