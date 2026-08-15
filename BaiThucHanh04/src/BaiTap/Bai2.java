package BaiTap;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class Bai2 {

    public int nhapso() throws IOException {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        String s = br.readLine();
        return Integer.parseInt(s);
    }

    public void tongChuSo(int n) {
        int S = 0;
        int temp = n;
        while (temp != 0) {
            S += temp % 10;
            temp /= 10;
        }
        System.out.println("a. Tong cac chu so la: " + S);
    }

    public int timSoDaoNguoc(int n) {
        int m = 0;
        while (n != 0) {
            m = m * 10 + n % 10;
            n /= 10;
        }
        return m;
    }

    public void inDaoNguoc(int n) {
        System.out.println("b. So dao nguoc cua " + n + " la: " + timSoDaoNguoc(n));
    }

    public void Fibo(int n) {
        if (n == 0 || n == 1) {
            System.out.println("c. So " + n + " CO thuoc day Fibonacci.");
            return;
        }
        
        int f1 = 1, f2 = 1, fn = 2;
        while (fn < n) {
            f1 = f2;
            f2 = fn;
            fn = f1 + f2;
        }
        
        if (fn == n) {
            System.out.println("c. So " + n + " CO thuoc day Fibonacci.");
        } else {
            System.out.println("c. So " + n + " KHONG thuoc day Fibonacci.");
        }
    }

    public void SDX(int m) {
        int daoNguocCuaM = timSoDaoNguoc(m);
        int daoNguocCuaDaoNguoc = timSoDaoNguoc(daoNguocCuaM);
        
        if (daoNguocCuaM == daoNguocCuaDaoNguoc) {
            System.out.println("d. Dao nguoc cua " + m + " (la " + daoNguocCuaM + ") LA so doi xung.");
        } else {
            System.out.println("d. Dao nguoc cua " + m + " (la " + daoNguocCuaM + ") KHONG la so doi xung.");
        }
    }

    public static void main(String[] args) throws IOException {
        Bai2 dt = new Bai2();
        int m = 0;
        
        do {
            System.out.print("Hay nhap so nguyen duong m (m > 0):  ");
            try {
                m = dt.nhapso();
            } catch (NumberFormatException e) {
                System.out.println("Vui long nhap mot so nguyen hop le!");
                m = 0;
            }
        } while (m <= 0);
        
        System.out.println("--- KET QUA ---");
        dt.tongChuSo(m);
        dt.inDaoNguoc(m);
        dt.Fibo(m);
        dt.SDX(m);
    }
}