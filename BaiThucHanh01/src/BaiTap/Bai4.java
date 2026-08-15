package BaiTap;

import java.util.Scanner;

public class Bai4 {
	public static void Tinh(double a, double b, double c, double d, double e, double f) {
		double D, Dx, Dy, x, y;
		D = a*e - b*d;
		Dx = c*e - b*f;
		Dy = a*f - c*d;
		if(D == 0) {
			if(Dx != 0 || Dy != 0) System.out.println("He phuong trinh vo nghiem");
			else System.out.println("He phuong trinh vo so nghiem");
		}
		else {
			x = Dx/D;
			y = Dy/D;
			System.out.printf("He phuong trinh co nghiem duy nhat: x = %.3f; y = %.3f", x, y);
		}
	}
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		double a, b, c, d, e, f;
		System.out.println("Nhap lan luot he so a, b, c, d, e, f, ");
		a = sc.nextDouble();
		b = sc.nextDouble();
		c = sc.nextDouble();
		d = sc.nextDouble();
		e = sc.nextDouble();
		f = sc.nextDouble();
		Tinh(a,b,c,d,e,f);
		sc.close();
	}
}
