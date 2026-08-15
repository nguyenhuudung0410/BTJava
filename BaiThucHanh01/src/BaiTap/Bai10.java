package BaiTap;

import java.util.Scanner;

public class Bai10 {
	public static void Tinh(int n) {
		double S = 0;
		for(int i = 1; i <= n; i++) {
			int k =2*i -1;
			long giaithua = 1;
			for(int j = 1; j <= k; j++) giaithua *= j;
			S += 1.0/giaithua;
		}
		
		System.out.println("S = 1 + 1/3! + ... + 1/(2n-1)! = " + S);
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
