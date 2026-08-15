package BaiTap;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.Arrays;

public class Bai4 {

    public int[][] nhapMaTran() throws IOException {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        
        String[] line = br.readLine().trim().split("\\s+");
        int m = Integer.parseInt(line[0]);
        int n = Integer.parseInt(line[1]);
        int[][] a = new int[m][n];
        
        for (int i = 0; i < m; i++) {
            line = br.readLine().trim().split("\\s+");
            for (int j = 0; j < n; j++) {
                a[i][j] = Integer.parseInt(line[j]);
            }
        }
        return a;
    }

    public void tichBoi3(int[][] a) {
        if (a.length == 0 || a[0].length == 0) return;
        
        int P = 1;
        boolean found = false;
        for (int j = 0; j < a[0].length; j++) {
            if (a[0][j] % 3 == 0) {
                P *= a[0][j];
                found = true;
            }
        }
        
        if (found) {
            System.out.println("Tich cac so boi 3 tren dong dau tien: " + P);
        } else {
            System.out.println("Khong co so boi 3 tren dong dau tien");
        }
    }

    public int[] timMaxTrenDong(int[][] a) {
        int[] X = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            int max = a[i][0];
            for (int j = 1; j < a[i].length; j++) {
                if (a[i][j] > max) {
                    max = a[i][j];
                }
            }
            X[i] = max;
        }
        return X;
    }

    public void xoaPhanTuDau(int[] X) {
        if (X == null || X.length == 0) {
            System.out.println("Mang rong, khong the xoa!");
            return;
        }
        
        int[] newArray = new int[X.length - 1];
        for (int i = 1; i < X.length; i++) {
            newArray[i - 1] = X[i];
        }
        System.out.println("Mang X sau khi xoa phan tu dau: " + Arrays.toString(newArray));
    }

    public static void main(String[] args) {
        Bai4 dt = new Bai4();
        try {
            System.out.println("Nhap vao so hang (m) va so cot (n) cua ma tran a (cach nhau bang khoang trang): ");
            int[][] a = dt.nhapMaTran();
            
            System.out.println("--- KET QUA ---");
            dt.tichBoi3(a);
            
            int[] X = dt.timMaxTrenDong(a);
            System.out.println("Mang X gom cac phan tu max tren tung dong: " + Arrays.toString(X));
            
            dt.xoaPhanTuDau(X);
            
        } catch (IOException | NumberFormatException e) {
            System.out.println("Loi nhap du lieu! Vui long kiem tra lai.");
        }
    }
}