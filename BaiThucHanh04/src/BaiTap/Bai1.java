package BaiTap;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class Bai1 {
	public String nhapChuoi() throws IOException {
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		return br.readLine();
	}

	public void bienDoiChuoi(String m) {
		System.out.println("a. Chuoi dao nguoc: ");
		for (int i = m.length() - 1; i >= 0; i--) {
			System.out.print(m.charAt(i));
		}
		System.out.println();

		System.out.println("b. Chuoi da cho sang chu hoa: ");
		for (char c : m.toCharArray()) {
			if (c >= 'a' && c <= 'z') {
				System.out.print((char) (c - 32));
			} else {
				System.out.print(c);
			}
		}
		System.out.println();

		System.out.println("c. Chuoi da cho sang chu thuong: ");
		for (char c : m.toCharArray()) {
			if (c >= 'A' && c <= 'Z') {
				System.out.print((char) (c + 32));
			} else {
				System.out.print(c);
			}
		}
		System.out.println();

		System.out.println("d. Chuoi da cho sang vua chu hoa vua chu thuong: ");
		for (char c : m.toCharArray()) {
			if (c >= 'a' && c <= 'z') {
				System.out.print((char) (c - 32));
			} else if (c >= 'A' && c <= 'Z') {
				System.out.print((char) (c + 32));
			} else {
				System.out.print(c);
			}
		}
		System.out.println();

		System.out.println("e. Dem so tu co trong chuoi da cho: ");
		if (m.trim().isEmpty()) {
			System.out.println("So tu co trong chuoi da cho: 0");
		} else {
			String[] words = m.trim().split("\\s+");
			System.out.println("So tu co trong chuoi da cho: " + words.length);
		}

		System.out.println("f. Dem so lan xuat hien cua moi tu trong chuoi da cho: ");
		if (!m.trim().isEmpty()) {
			String[] words = m.trim().split("\\s+");
			String[] moiTu = new String[words.length];
			int[] counts = new int[words.length];
			int count = 0;

			for (int i = 0; i < words.length; i++) {
				String tu = words[i];
				int foundIndex = -1;

				for (int j = 0; j < count; j++) {
					if (moiTu[j].equals(tu)) {
						foundIndex = j;
						break;
					}
				}

				if (foundIndex == -1) {
					moiTu[count] = tu;
					counts[count] = 1;
					count++;
				} else {
					counts[foundIndex]++;
				}
			}

			for (int i = 0; i < count; i++) {
				System.out.println("Tu: '" + moiTu[i] + "' xuat hien " + counts[i] + " lan");
			}
		}
	}

	public static void main(String[] args) {
		Bai1 dt = new Bai1();
		String st = "";
		try {
			System.out.print("Hay nhap chuoi:  ");
			st = dt.nhapChuoi();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (st != null) {
			dt.bienDoiChuoi(st);
		}
	}
}