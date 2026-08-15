package BaiTap;

import java.util.Scanner;

public class Bai2 {
	public static void Tinh(double a, double b) {
		double x;
		if(a==0) {
			if(b == 0) System.out.println("Phuong trinh vo so nghiem");
			else System.out.println("Phuong trinh vo nghiem");
		}
		else {
			x = -b/a;
			System.out.printf("Nghiem cua phuong trinh la: x = %.3f", x);
		}
	}
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		double a, b;
		System.out.println("Nhap lan luot he so a, b: ");
		a = sc.nextDouble();
		b = sc.nextDouble();
		Tinh(a,b);
		sc.close();
	}
}
