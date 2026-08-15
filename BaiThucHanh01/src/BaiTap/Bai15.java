package BaiTap;

import java.util.Scanner;

public class Bai15 {
	public static void Tinh(int p) {
		int canp = (int)Math.sqrt(p);
		if (canp * canp == p) {
            System.out.println(p + " la so chinh phuong");
        } else {
            System.out.println(p + " khong phai so chinh phuong");
        }
	}
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Nhap P: ");
		int p = sc.nextInt();
		Tinh(p);
		sc.close();
	}
}
