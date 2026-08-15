package BaiTap;

public class Bai18 {
    public static void main(String[] args) {
        System.out.println("Cac so hoan hao nho hon 1000 la: ");
        for (int n = 1; n < 1000; n++) {
        	int tonguoc = 0;
            for (int i = 1; i <= n / 2; i++) {
                if (n % i == 0) {
                    tonguoc = tonguoc + i;
                }
            }
            if (tonguoc == n) {
                System.out.print(n + " ");
            }
        }
    }
}