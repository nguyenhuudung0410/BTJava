package BaiTap;

import java.util.Scanner;

public class Bai13 {
	public static int gcd(int a, int b) {
		if(b == 0) return a;
		return gcd(b, a%b);
	}
	public static int lcm(int a, int b) {
		if (a == 0 || b == 0) return 0;
		return (a * b) / gcd(a, b);
	}
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Nhap a va b: ");
		int a = sc.nextInt();
        int b = sc.nextInt();
        System.out.println("UCLN: " + gcd(a, b));
        System.out.println("BCNN: " + lcm(a, b));
        sc.close();
	}
}
