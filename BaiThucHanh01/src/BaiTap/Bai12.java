package BaiTap;

import java.util.Scanner;

public class Bai12 {
	public static void Tinh(int m) {
		int S = 0;
		long P = 1;
		int temp = m;
		while(temp > 0) {
			int duoi = temp % 10;
			S += duoi;
			P *= duoi;
			temp /= 10;
		}
		System.out.println("Tong cac chu so cua " + m + " la: S = " + S);
        System.out.println("Tich cac chu so cua " + m + " la: P = " + P);
	}
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int m;
		do {
            System.out.print("Nhap so nguyen duong m: ");
            m = sc.nextInt();
            if (m <= 0) System.out.println("m phai > 0. Nhap lai!");
        } while (m <= 0);
		Tinh(m);
        sc.close();
	}
}
