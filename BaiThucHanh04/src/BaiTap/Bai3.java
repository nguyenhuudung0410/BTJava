package BaiTap;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class Bai3 {

    public int[] nhapso(Scanner sc) {
        String s = sc.nextLine();
        return Arrays.stream(s.split("\\s+")).mapToInt(Integer::parseInt).toArray();
    }

    public void tongChuSo(int[] a) {
        int S = 0;
        for (int i = 0; i < a.length; i++) {
            if (a[i] > 0 && a[i] % 2 != 0) {
                S += a[i];
            }
        }
        System.out.println("Tong cac so duong le: " + S);
    }

    public void timK(int[] a, int k) {
        int index = -1;
        for (int i = 0; i < a.length; i++) {
            if (a[i] == k) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            System.out.println("Phan tu " + k + " xuat hien tai vi tri (index) dau tien la " + index);
        } else {
            System.out.println("Phan tu " + k + " khong xuat hien trong mang");
        }
    }

    public void sapXep(int[] a) {
        Arrays.sort(a);
    }

    public void chenPhanTu(int[] a, int p) {
        int[] newArray = new int[a.length + 1];
        int i = 0;
        
        while (i < a.length && a[i] < p) {
            newArray[i] = a[i];
            i++;
        }
        newArray[i] = p;
        for (int j = i; j < a.length; j++) {
            newArray[j + 1] = a[j];
        }
        
        System.out.println("Mang sau khi chen p: " + Arrays.toString(newArray));
    }

    public static void main(String[] args) throws IOException {
        Bai3 dt = new Bai3();
        Scanner sc = new Scanner(System.in);
        
        System.out.println("Nhap mang a (cac so cach nhau boi dau cach): ");
        int[] a = dt.nhapso(sc);
        
        dt.tongChuSo(a);
        
        System.out.print("Nhap phan tu k de kiem tra: ");
        int k = sc.nextInt();
        dt.timK(a, k);
        
        dt.sapXep(a);
        System.out.println("Mang sau khi sap xep: " + Arrays.toString(a));
        
        System.out.print("Nhap phan tu p de chen sao cho mang van tang dan: ");
        int p = sc.nextInt();
        dt.chenPhanTu(a, p);

        sc.close();
    }
}