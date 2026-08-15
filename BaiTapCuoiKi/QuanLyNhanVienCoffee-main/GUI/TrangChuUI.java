import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class TrangChuUI extends JFrame {
    private JPanel pnlContent;
    private CardLayout cardLayout;
    private TaiKhoanData user;
    
    // Đưa các nút Tab ra ngoài để các Panel con có thể yêu cầu chuyển Tab
    private JButton btnTabNV, btnTabLuong, btnTabCaLam, btnTabTK;

    public TrangChuUI(TaiKhoanData tk) {
        this.user = tk;
        setTitle("Hệ Thống Quản Trị Cửa Hàng");
        setSize(1300, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        // 1. HEADER CHỨA TAB
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setPreferredSize(new Dimension(0, 85)); 
        pnlHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(232, 234, 237)));

        // --- Cụm các nút Tab bên trái ---
        JPanel pnlTabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 25)); 
        pnlTabs.setOpaque(false);
        pnlTabs.setBorder(new EmptyBorder(0, 30, 0, 0));

        btnTabNV = createTabButton("Danh sách nhân viên", true);
        btnTabLuong = createTabButton("Quản lý Lương", false);
        btnTabCaLam = createTabButton("Ca Làm", false); 
        btnTabTK = createTabButton("Tài khoản", false);

        pnlTabs.add(btnTabNV);
        pnlTabs.add(btnTabLuong);
        pnlTabs.add(btnTabCaLam); 
        pnlTabs.add(btnTabTK);

        // --- Thông tin người dùng & Đăng xuất bên phải ---
        JPanel pnlUserRight = new JPanel();
        pnlUserRight.setLayout(new BoxLayout(pnlUserRight, BoxLayout.Y_AXIS));
        pnlUserRight.setOpaque(false);
        pnlUserRight.setBorder(new EmptyBorder(15, 0, 0, 35)); 

        NhanVienData nv = NhanVienLogic.getInstance().timTheoMa(user.getMaNV());
        String displayTitle = (nv != null) ? nv.getHoTen() : user.getTenDangNhap();

        JLabel lblName = new JLabel(displayTitle);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 18)); 
        lblName.setForeground(new Color(32, 33, 36));
        lblName.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel lblLogout = new JLabel("Đăng xuất");
        lblLogout.setPreferredSize(new Dimension(100, 25)); 
        lblLogout.setHorizontalAlignment(SwingConstants.RIGHT); 
        lblLogout.setFont(new Font("Segoe UI", Font.BOLD, 15)); 
        lblLogout.setForeground(Color.RED); 
        lblLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblLogout.setAlignmentX(Component.RIGHT_ALIGNMENT);

        lblLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int choice = JOptionPane.showConfirmDialog(null, 
                        "Bạn có chắc chắn muốn đăng xuất không?", 
                        "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (choice == JOptionPane.YES_OPTION) {
                    dispose(); 
                    new DangNhapUI().setVisible(true); 
                }
            }
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { lblLogout.setText("<html><u>Đăng xuất</u></html>"); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { lblLogout.setText("<html>Đăng xuất</html>"); }
        });

        pnlUserRight.add(lblName);
        pnlUserRight.add(Box.createRigidArea(new Dimension(0, 2))); 
        pnlUserRight.add(lblLogout);

        pnlHeader.add(pnlTabs, BorderLayout.WEST);
        pnlHeader.add(pnlUserRight, BorderLayout.EAST);

        // 2. VÙNG NỘI DUNG
        cardLayout = new CardLayout();
        pnlContent = new JPanel(cardLayout);
        
        pnlContent.add(new DanhSachNhanVienUI(), "NV");
        pnlContent.add(new QuanLyLuongUI(), "LUONG");
        pnlContent.add(new QuanLyCaLamUI(), "CALAM"); 
        pnlContent.add(new TaiKhoanUI(user), "TK");

        // Sự kiện chuyển Tab
        btnTabNV.addActionListener(e -> {
            switchTab(btnTabNV, btnTabLuong, btnTabCaLam, btnTabTK);
            cardLayout.show(pnlContent, "NV");
        });
        btnTabLuong.addActionListener(e -> {
            switchTab(btnTabLuong, btnTabNV, btnTabCaLam, btnTabTK);
            cardLayout.show(pnlContent, "LUONG");
        });
        btnTabCaLam.addActionListener(e -> {
            switchTab(btnTabCaLam, btnTabNV, btnTabLuong, btnTabTK);
            cardLayout.show(pnlContent, "CALAM");
        });
        btnTabTK.addActionListener(e -> {
            switchTab(btnTabTK, btnTabNV, btnTabLuong, btnTabCaLam);
            cardLayout.show(pnlContent, "TK");
        });

        root.add(pnlHeader, BorderLayout.NORTH);
        root.add(pnlContent, BorderLayout.CENTER);
        add(root);
    }

    // === HÀM CÔNG KHAI ĐỂ CHUYỂN TAB TỪ CÁC GIAO DIỆN CON ===
    public void chuyenSangTabCaLam() {
        if (btnTabCaLam != null) {
            btnTabCaLam.doClick(); // Giả lập hành động click vào Tab Ca Làm của người dùng
        }
    }

    private JButton createTabButton(String text, boolean isInitialActive) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (Boolean.TRUE.equals(getClientProperty("activeTab"))) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(26, 115, 232));
                    g2.fillRoundRect(0, getHeight() - 4, getWidth(), 4, 4, 4);
                    g2.dispose();
                }
            }
        };
        
        btn.putClientProperty("activeTab", isInitialActive);
        btn.setFont(new Font("Segoe UI", isInitialActive ? Font.BOLD : Font.PLAIN, 15));
        btn.setForeground(isInitialActive ? new Color(26, 115, 232) : new Color(95, 99, 104));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(0, 5, 8, 5));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!Boolean.TRUE.equals(btn.getClientProperty("activeTab"))) {
                    btn.setBackground(new Color(248, 249, 250));
                    btn.setOpaque(true);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setOpaque(false);
                btn.repaint();
            }
        });
        return btn;
    }

    private void switchTab(JButton activeBtn, JButton... others) {
        activeBtn.putClientProperty("activeTab", true);
        activeBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        activeBtn.setForeground(new Color(26, 115, 232));
        activeBtn.setOpaque(false);
        activeBtn.repaint(); 

        for (JButton b : others) {
            b.putClientProperty("activeTab", false);
            b.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            b.setForeground(new Color(95, 99, 104));
            b.setOpaque(false);
            b.repaint(); 
        }
    }
}