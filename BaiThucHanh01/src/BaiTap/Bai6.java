package BaiTap;

import java.util.Scanner;

public class Bai6 {
	public static void Tinh(int a, int b) {
		long t = 0;
		if (b <= a) {
            System.out.println("Gio ket thuc phai lon hon gio bat dau!");
        }
		else {
        	if(b < 18) t = (b - a) * 45000;
        	else if(a >= 18) t = (b - a) * 60000;
        	else t = (18 - a) * 45000 + (b - 18) * 60000;
        	System.out.println("Tong tien la: " + t);
        }
	}
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int a, b;
		System.out.println("Nhap gio bat dau: ");
		a = sc.nextInt();
		System.out.println("Nhap gio ket thuc: ");
		b = sc.nextInt();
		Tinh(a,b);
		sc.close();
	}
}
