import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class TaiKhoanUI extends JPanel {

    private TaiKhoanData user;
    private NhanVienData nvInfo;

    // Màu sắc Google Style
    private final Color BG_MAIN       = new Color(248, 249, 250);
    private final Color CARD_BG       = Color.WHITE;
    private final Color BORDER_COLOR  = new Color(218, 220, 224);
    private final Color PRIMARY_BLUE  = new Color(26, 115, 232);
    private final Color TEXT_DARK     = new Color(32, 33, 36);
    private final Color TEXT_GRAY     = new Color(95, 99, 104);

    // TĂNG CỠ CHỮ
    private final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    private final Font FONT_BOLD  = new Font("Segoe UI", Font.BOLD, 17); // In đậm mục lục
    private final Font FONT_PLAIN = new Font("Segoe UI", Font.PLAIN, 17); // Nội dung viết thường

    private JPasswordField txtPassCu, txtPassMoi, txtXacNhanPass;

    public TaiKhoanUI(TaiKhoanData user) {
        this.user = user;
        // Lấy thông tin chi tiết từ Logic
        this.nvInfo = NhanVienLogic.getInstance().timTheoMa(user.getMaNV());

        setLayout(new BorderLayout(0, 0));
        setBackground(BG_MAIN);
        initUI();
    }

    private void initUI() {
        // --- 1. Top Banner (Đã xóa chữ Q và thu hẹp chiều cao) ---
        JPanel pnlHeader = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, new Color(26, 115, 232), getWidth(), 0, new Color(37, 99, 235)));
                g2.fillRect(0, 0, getWidth(), 100);
                g2.dispose();
            }
        };
        pnlHeader.setPreferredSize(new Dimension(0, 100)); // Giảm chiều cao xuống
        pnlHeader.setOpaque(false);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. Vùng nội dung chính ---
        JPanel pnlBody = new JPanel(new GridBagLayout());
        pnlBody.setOpaque(false);
        pnlBody.setBorder(new EmptyBorder(30, 50, 40, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 15, 0, 15);

        // Cột trái: Thông tin cá nhân
        gbc.gridx = 0; gbc.weightx = 0.5;
        pnlBody.add(createProfileCard(), gbc);

        // Cột phải: Đổi mật khẩu
        gbc.gridx = 1; gbc.weightx = 0.5;
        pnlBody.add(createSecurityCard(), gbc);

        add(pnlBody, BorderLayout.CENTER);
    }

    private JPanel createProfileCard() {
        JPanel card = createBaseCard("Thông tin cá nhân");
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // Dữ liệu hiển thị
        String hoTen = (nvInfo != null) ? nvInfo.getHoTen() : "Quản trị viên";
        String sdt = (nvInfo != null) ? nvInfo.getSDT() : "0000000000";
        String email = (nvInfo != null && nvInfo.getEmail() != null) ? nvInfo.getEmail() : "admin@gmail.com";
        String username = user.getTenDangNhap();
        String quyen = user.getQuyen().toString();

        // Gắn các dòng thông tin (Mục in đậm, nội dung bên phải viết thường)
        content.add(createInfoRow("Họ và Tên", hoTen));
        content.add(Box.createRigidArea(new Dimension(0, 15)));
        content.add(createInfoRow("Số điện thoại", sdt));
        content.add(Box.createRigidArea(new Dimension(0, 15)));
        content.add(createInfoRow("Email cá nhân", email));
        content.add(Box.createRigidArea(new Dimension(0, 15)));
        content.add(createInfoRow("Tên đăng nhập", username));
        content.add(Box.createRigidArea(new Dimension(0, 15)));
        content.add(createInfoRow("Quyền truy cập", quyen));

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    // HÀM TẠO DÒNG THÔNG TIN: Mục in đậm bên trái, Nội dung viết thường bên phải
    private JPanel createInfoRow(String label, String value) {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnl.setOpaque(false);
        
        JLabel lblL = new JLabel(label + ": ");
        lblL.setFont(FONT_BOLD); // IN ĐẬM
        lblL.setForeground(TEXT_DARK);
        
        JLabel lblV = new JLabel(value);
        lblV.setFont(FONT_PLAIN); // VIẾT THƯỜNG
        lblV.setForeground(TEXT_DARK);
        
        pnl.add(lblL);
        pnl.add(lblV);
        
        // Đường gạch chân mờ giữa các dòng
        pnl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(241, 243, 244)));
        return pnl;
    }

    private JPanel createSecurityCard() {
        JPanel card = createBaseCard("Bảo mật tài khoản");
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        txtPassCu = createStyledPasswordField();
        txtPassMoi = createStyledPasswordField();
        txtXacNhanPass = createStyledPasswordField();

        content.add(createInputLabel("Mật khẩu hiện tại"));
        content.add(txtPassCu);
        content.add(Box.createRigidArea(new Dimension(0, 15)));

        content.add(createInputLabel("Mật khẩu mới"));
        content.add(txtPassMoi);
        content.add(Box.createRigidArea(new Dimension(0, 15)));

        content.add(createInputLabel("Xác nhận lại mật khẩu mới"));
        content.add(txtXacNhanPass);
        content.add(Box.createRigidArea(new Dimension(0, 30)));

        JButton btnUpdate = new JButton("Thay đổi mật khẩu") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PRIMARY_BLUE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnUpdate.setFont(FONT_BOLD);
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setPreferredSize(new Dimension(0, 50)); // Nút to hơn
        btnUpdate.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btnUpdate.setContentAreaFilled(false);
        btnUpdate.setBorderPainted(false);
        btnUpdate.setFocusPainted(false);
        btnUpdate.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnUpdate.addActionListener(e -> handleDoiMatKhau());

        content.add(btnUpdate);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel createBaseCard(String title) {
        JPanel card = new JPanel(new BorderLayout(0, 20)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(25, 30, 30, 30));
        JLabel lblT = new JLabel(title);
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 20)); // Title card to hơn
        lblT.setForeground(PRIMARY_BLUE);
        card.add(lblT, BorderLayout.NORTH);
        return card;
    }

    private JLabel createInputLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_BOLD.deriveFont(15f));
        lbl.setForeground(TEXT_DARK);
        lbl.setBorder(new EmptyBorder(0, 0, 5, 0));
        return lbl;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField f = new JPasswordField();
        f.setFont(FONT_PLAIN);
        f.setPreferredSize(new Dimension(0, 45)); // Ô nhập cao hơn
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(0, 12, 0, 12)));
        return f;
    }

    private void handleDoiMatKhau() {
        String mkCu = new String(txtPassCu.getPassword());
        String mkMoi = new String(txtPassMoi.getPassword());
        String xacNhan = new String(txtXacNhanPass.getPassword());

        if (mkCu.isEmpty() || mkMoi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng không để trống mật khẩu!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!mkMoi.equals(xacNhan)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            TaiKhoanLogic.getInstance().doiMatKhau(user.getTenDangNhap(), mkCu, mkMoi);
            JOptionPane.showMessageDialog(this, "Cập nhật mật khẩu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            txtPassCu.setText(""); txtPassMoi.setText(""); txtXacNhanPass.setText("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}