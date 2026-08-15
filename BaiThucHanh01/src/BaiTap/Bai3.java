package BaiTap;

import java.util.Scanner;

public class Bai3 {
	public static void Tinh(double a, double b, double c) {
		double x1, x2, delta;
		if(a == 0) System.out.println("Phuong trinh khong phai la phuong trinh bac 2 !");
		else {
			delta = b*b - 4*a*c;
			if(delta < 0) System.out.println("Phuong trinh vo nghiem");
			else if(delta == 0) {
				x1 = -b/(2*a);
				System.out.printf("Phuong trinh co nghiem kep: x1 = x2 = %.3f", x1);
			}
			else {
				x1 = (-b - Math.sqrt(delta))/(2*a);
				x2 = (-b + Math.sqrt(delta))/(2*a);
				System.out.printf("Phuong trinh co hai nghiem lan luot la: x1 = %.3f; x2 = %.3f", x1, x2);
			}
		}
	}
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		double a, b, c;
		System.out.println("Nhap lan luot he so a, b, c (a khac 0): ");
		a = sc.nextDouble();
		b = sc.nextDouble();
		c = sc.nextDouble();
		Tinh(a,b,c);
		sc.close();
	}
}
