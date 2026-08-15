package BaiTap;

import java.util.Scanner;

public class Bai8 {
	public static void Tinh(int n) {
		double S = 0;
		for(int i = 1; i <= n; i++) {
			S += 1.0/i;
		}
		System.out.println("S = 1 + 1/2 + 1/3 +....+ 1/n = " + S);
	}
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int n;
		do {
			System.out.println("Nhap n (n > 0): ");
			n = sc.nextInt();
			if(n <= 0) System.out.println("n phai lon hon 0, vui long nhap lai!");
		}while (n <= 0);
		Tinh(n);
		sc.close();
	}
} 
