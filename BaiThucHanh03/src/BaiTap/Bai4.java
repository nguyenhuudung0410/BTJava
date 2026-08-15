//jdbc:sqlserver://localhost:1433;databaseName=DATA;encrypt=true;trustServerCertificate=true;
package BaiTap;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Bai4 extends JFrame implements ActionListener {
	JLabel lb1, lb2;
	JTextField txt1, txt2;
	JComboBox<String> cbo;
	JButton btn1, btn2, btn3;
	JPanel pn, pnTop, pnGrid, pnCenter, pnBottom;
	JTable table;
	DefaultTableModel tableModel;

	public void GUI() {
		lb1 = new JLabel("Input Information:", SwingConstants.RIGHT);
		lb2 = new JLabel("SQL Query:", SwingConstants.RIGHT);

		txt1 = new JTextField("", 35);
		txt2 = new JTextField("", 35);

		cbo = new JComboBox<>(new String[] { "Select", "Insert", "Update", "Delete" });
		cbo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String item = (String) cbo.getSelectedItem();
				if (item.equals("Select"))
					txt2.setText("Select * from Table1");
				else if (item.equals("Insert"))
					txt2.setText("Insert Into Table1(Id,Name,Address,Total) Values('','','','')");
				else if (item.equals("Update"))
					txt2.setText("Update Table1 Set Name='' Where Id=''");
				else if (item.equals("Delete"))
					txt2.setText("Delete From Table1 Where Id=''");
			}
		});

		btn1 = new JButton("Submit");
		btn2 = new JButton("Reset");
		btn3 = new JButton("Cancel");

		btn1.addActionListener(this);
		btn2.addActionListener(this);
		btn3.addActionListener(this);

		String[] columns = { "Id", "Name", "Address", "Total" };
		tableModel = new DefaultTableModel(columns, 0);
		table = new JTable(tableModel);
		JScrollPane scrollPane = new JScrollPane(table);

		pnGrid = new JPanel(new GridLayout(2, 2, 5, 5));
		pnGrid.add(lb1);
		pnGrid.add(txt1);
		pnGrid.add(lb2);

		JPanel pnSQL = new JPanel(new BorderLayout(5, 0));
		pnSQL.add(txt2, BorderLayout.CENTER);
		pnSQL.add(cbo, BorderLayout.EAST);
		pnGrid.add(pnSQL);

		pnTop = new JPanel(new FlowLayout(FlowLayout.CENTER));
		pnTop.add(pnGrid);
		pnTop.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

		pnCenter = new JPanel(new BorderLayout());
		pnCenter.add(scrollPane, BorderLayout.CENTER);
		pnCenter.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

		pnBottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
		pnBottom.add(btn1);
		pnBottom.add(btn2);
		pnBottom.add(btn3);

		pn = new JPanel(new BorderLayout());
		pn.add(pnTop, BorderLayout.NORTH);
		pn.add(pnCenter, BorderLayout.CENTER);
		pn.add(pnBottom, BorderLayout.SOUTH);

		add(pn);
		setSize(850, 450);
		setTitle("Database Programming");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn3) {
			System.exit(0);
		} else if (e.getSource() == btn2) {
			txt1.setText("");
			txt2.setText("");
			tableModel.setRowCount(0);
		} else if (e.getSource() == btn1) {
			String url = txt1.getText().trim();
			String sql = txt2.getText().trim();
			try {
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				Connection conn = DriverManager.getConnection(url, "sa", "123456");
				Statement stmt = conn.createStatement();

				if (sql.toUpperCase().startsWith("SELECT")) {
					ResultSet rs = stmt.executeQuery(sql);
					tableModel.setRowCount(0);
					while (rs.next()) {
						Vector<Object> row = new Vector<>();
						row.add(rs.getObject(1));
						row.add(rs.getObject(2));
						row.add(rs.getObject(3));
						row.add(rs.getObject(4));
						tableModel.addRow(row);
					}
					rs.close();
				} else {
					int rows = stmt.executeUpdate(sql);
					JOptionPane.showMessageDialog(this, "Thành công! Số dòng ảnh hưởng: " + rows);
				}
				stmt.close();
				conn.close();
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
			}
		}
	}

	public static void main(String[] args) {
		new Bai4().GUI();
	}
}