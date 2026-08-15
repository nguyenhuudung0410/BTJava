package BaiTap;

import java.util.Scanner;

public class Bai1 {
	public static void max(double a, double b, double c) {
		System.out.println("Gia tri lon nhat la: " + Math.max(a, Math.max(b, c)));
	}
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		double a, b, c;
		System.out.println("Nhap lan luot gia tri cua a, b, c: ");
		a = sc.nextDouble();
		b = sc.nextDouble();
		c = sc.nextDouble();
		max(a, b, c);
		sc.close();
	}
}
