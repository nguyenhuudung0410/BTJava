package BaiTap;

import java.util.Scanner;

public class Bai16 {
	public static void Tinh(int m) {
		int temp = m;
		int nguoc = 0;
		while(temp > 0) {
			int cuoi = temp%10;
			nguoc = nguoc *10 + cuoi;
			temp /= 10;
		}
		if (nguoc == m) {
            System.out.println(m + " la so doi xung.");
        } else {
            System.out.println(m + " khong phai so doi xung.");
        }
	}
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Nhap M: ");
		int m = sc.nextInt();
		Tinh(m);
		sc.close();
	}
}
