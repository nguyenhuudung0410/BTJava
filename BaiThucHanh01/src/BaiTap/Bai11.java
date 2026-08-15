package BaiTap;

import java.util.Scanner;

public class Bai11 {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int n;
        long kq = 1;
        do {
			System.out.println("Nhap n (n > 0): ");
			n = sc.nextInt();
			if(n <= 0) System.out.println("n phai lon hon 0, vui long nhap lai!");
		}while (n <= 0);
        int m;
        if(n %2 ==0) m = 2;
        else m = 1;
        for(int i = m; i <=n ; i= i+2) {
        	kq *= i;
        }
        System.out.println("Dap an la: " + kq);
        sc.close();
	}
}
