package BaiTap;

import java.util.Scanner;

public class Bai7 {
	public static void Tinh(int thang, int nam) {
		switch (thang) {
		case 1: 
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			System.out.println("Co 31 ngay");
			break;
		case 4:
		case 6:
		case 9:
		case 11:
			System.out.println("Co 30 ngay");
			break;
		case 2:
			if((nam % 4 == 0 && nam %100 !=0) ||nam % 400 == 0) {
				System.out.println("Co 29 ngay");
			}
			else System.out.println("Co 28 ngay");
			break;
		default:
			System.out.println("Nhap du lieu sai");
			break;
		}
	}
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int thang, nam;
		System.out.println("Nhap thang: ");
		thang = sc.nextInt();
		System.out.println("Nhap nam");
		nam = sc.nextInt();
		Tinh(thang, nam);
		sc.close();
	}
}
