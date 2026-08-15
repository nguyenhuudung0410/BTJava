package BaiTap;

import java.util.Scanner;

public class Bai5 {
	public static void Tinh(double a, double b, double c) {
		double max, min, stg;
		max = Math.max(a, Math.max(b, c));
		min = Math.min(a, Math.min(b, c));
		stg = a + b + c - max - min;
		System.out.printf("So trung gian la: %.3f", stg);
	}
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		double a, b, c;
		System.out.println("Nhap lan luot gia tri cua a, b, c: ");
		a = sc.nextDouble();
		b = sc.nextDouble();
		c = sc.nextDouble();
		Tinh(a,b,c);
		sc.close();
	}
}
