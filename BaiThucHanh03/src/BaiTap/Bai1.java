package BaiTap;

import java.sql.*;

public class Bai1 {
	public static void main(String[] args) {
		System.out.println("Dang ket noi CSDL SQL Server de truy xuat du lieu...");
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String url = "jdbc:sqlserver://localhost:1433;databaseName=DATA;encrypt=true;trustServerCertificate=true;";
			String user = "sa";
			String password = "123456";
			Connection conn = DriverManager.getConnection(url, user, password);
			Statement stmt = conn.createStatement();

			String sql = "SELECT Id, Name, Address, Total FROM Table1";
			ResultSet rs1 = stmt.executeQuery(sql);
			System.out.println("=== Cau a ===");
			while (rs1.next()) {
				System.out.println("ID=" + rs1.getInt("Id") + " Name=" + rs1.getString("Name") + " Address="
						+ rs1.getString("Address") + " Total=" + rs1.getFloat("Total"));
			}
			rs1.close();
			ResultSet rs2 = stmt.executeQuery(sql);
			System.out.println("\n=== Cau b ===");
			System.out.printf("%-5s %-20s %-10s %s\n", "Id", "Name", "Address", "Total");
			while (rs2.next()) {
				System.out.printf("%-5d %-20s %-10s %.1f\n", rs2.getInt("Id"), rs2.getString("Name"),
						rs2.getString("Address"), rs2.getFloat("Total"));
			}
			rs2.close();

			stmt.close();
			conn.close();
		} catch (Exception e) {
			System.out.println("Loi: " + e.getMessage());
		}
	}
}