import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class DangNhapUI extends JFrame {

    private final Color BG_COLOR     = new Color(30, 41, 59);
    private final Color INPUT_BG     = new Color(40, 53, 76);
    private final Color INPUT_BORDER = new Color(71, 85, 105);
    private final Color INPUT_FOCUS  = new Color(59, 130, 246);
    private final Color BTN_COLOR    = new Color(37, 99, 235);
    private final Color BTN_HOVER    = new Color(29, 78, 216);
    private final Color TEXT_WHITE   = new Color(248, 250, 252);

    private CustomTextField txtUser;
    private CustomPasswordField txtPass;
    private JLabel lblError;

    public DangNhapUI() {

        setTitle("Đăng nhập - Quản lý nhân viên");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(500, 650);

        setLocationRelativeTo(null);

        setResizable(false);

        setUndecorated(true);

        initUI();
    }

    private void initUI() {

        JPanel root = new JPanel(new GridBagLayout()) {

            @Override
            protected void paintComponent(Graphics g) {

                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(BG_COLOR);

                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(new Color(255, 255, 255, 20));

                g2.drawRect(
                        0,
                        0,
                        getWidth() - 1,
                        getHeight() - 1
                );

                g2.dispose();
            }
        };

        root.setOpaque(false);

        setContentPane(root);

        JPanel pnlContent = new JPanel();

        pnlContent.setLayout(
                new BoxLayout(pnlContent, BoxLayout.Y_AXIS)
        );

        pnlContent.setOpaque(false);

        pnlContent.setPreferredSize(new Dimension(380, 480));

        JLabel lblTitle = new JLabel("ĐĂNG NHẬP");

        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 46));

        lblTitle.setForeground(TEXT_WHITE);

        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel pnlUser =
                createInputGroup("Tên đăng nhập", false);

        JPanel pnlPass =
                createInputGroup("Mật khẩu", true);

        lblError = new JLabel(" ");

        lblError.setForeground(new Color(252, 165, 165));

        lblError.setFont(new Font("Segoe UI", Font.ITALIC, 13));

        lblError.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnLogin = new JButton("Đăng nhập");

        styleButton(btnLogin);

        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblExit = new JLabel("Thoát ứng dụng");

        lblExit.setFont(new Font("Segoe UI", Font.ITALIC, 15));

        lblExit.setForeground(new Color(148, 163, 184));

        lblExit.setCursor(new Cursor(Cursor.HAND_CURSOR));

        lblExit.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblExit.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {

                lblExit.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {

                lblExit.setForeground(
                        new Color(148, 163, 184)
                );
            }

            @Override
            public void mouseClicked(MouseEvent e) {

                System.exit(0);
            }
        });

        pnlContent.add(lblTitle);

        pnlContent.add(
                Box.createRigidArea(new Dimension(0, 50))
        );

        pnlContent.add(pnlUser);

        pnlContent.add(
                Box.createRigidArea(new Dimension(0, 20))
        );

        pnlContent.add(pnlPass);

        pnlContent.add(
                Box.createRigidArea(new Dimension(0, 10))
        );

        pnlContent.add(lblError);

        pnlContent.add(
                Box.createRigidArea(new Dimension(0, 15))
        );

        pnlContent.add(btnLogin);

        pnlContent.add(
                Box.createRigidArea(new Dimension(0, 25))
        );

        pnlContent.add(lblExit);

        root.add(pnlContent);

        txtUser.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_ENTER) {

                    txtPass.requestFocus();
                }
            }
        });

        txtPass.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_ENTER) {

                    handleLogin();
                }
            }
        });

        btnLogin.addActionListener(e -> handleLogin());
    }

    private JPanel createInputGroup(
            String labelText,
            boolean isPassword
    ) {

        JPanel pnl = new JPanel(
                new BorderLayout(0, 8)
        );

        pnl.setOpaque(false);

        pnl.setMaximumSize(
                new Dimension(380, 80)
        );

        JLabel lbl = new JLabel(labelText);

        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));

        lbl.setForeground(TEXT_WHITE);

        pnl.add(lbl, BorderLayout.NORTH);

        if (isPassword) {

            txtPass = new CustomPasswordField();

            pnl.add(txtPass, BorderLayout.CENTER);

        } else {

            txtUser = new CustomTextField();

            pnl.add(txtUser, BorderLayout.CENTER);
        }

        return pnl;
    }

    private void handleLogin() {

        String user =
                txtUser.getText().trim();

        String pass =
                new String(txtPass.getPassword());

        lblError.setText(" ");

        try {

            TaiKhoanData tk =
                    TaiKhoanLogic.getInstance()
                            .dangNhap(user, pass);

            if (tk.getQuyen()
                    != TaiKhoanData.Quyen.ADMIN) {

                throw new Exception(
                        "Tài khoản không có quyền truy cập!"
                );
            }

            TrangChuUI trangChu =
                    new TrangChuUI(tk);

            trangChu.setExtendedState(
                    JFrame.MAXIMIZED_BOTH
            );

            trangChu.setVisible(true);

            this.dispose();

        } catch (Exception e) {

            e.printStackTrace();

            lblError.setText(e.getMessage());
        }
    }

    private void styleButton(JButton btn) {

        btn.setPreferredSize(
                new Dimension(380, 52)
        );

        btn.setMaximumSize(
                new Dimension(380, 52)
        );

        btn.setContentAreaFilled(false);

        btn.setBorderPainted(false);

        btn.setFocusPainted(false);

        btn.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        btn.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {

                btn.putClientProperty("hover", true);

                btn.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {

                btn.putClientProperty("hover", false);

                btn.repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {

                btn.putClientProperty("pressed", true);

                btn.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {

                btn.putClientProperty("pressed", false);

                btn.repaint();
            }
        });

        btn.setUI(
                new javax.swing.plaf.basic.BasicButtonUI() {

                    @Override
                    public void paint(Graphics g, JComponent c) {

                        Graphics2D g2 =
                                (Graphics2D) g.create();

                        g2.setRenderingHint(
                                RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON
                        );

                        boolean hover =
                                Boolean.TRUE.equals(
                                        c.getClientProperty("hover")
                                );

                        boolean pressed =
                                Boolean.TRUE.equals(
                                        c.getClientProperty("pressed")
                                );

                        if (pressed) {

                            g2.setColor(BTN_HOVER.darker());

                        } else if (hover) {

                            g2.setColor(BTN_HOVER);

                        } else {

                            g2.setColor(BTN_COLOR);
                        }

                        g2.fillRoundRect(
                                0,
                                0,
                                c.getWidth(),
                                c.getHeight(),
                                12,
                                12
                        );

                        g2.setColor(Color.WHITE);

                        g2.setFont(
                                new Font(
                                        "Segoe UI",
                                        Font.BOLD,
                                        18
                                )
                        );

                        FontMetrics fm =
                                g2.getFontMetrics();

                        String text =
                                ((JButton) c).getText();

                        g2.drawString(
                                text,
                                (c.getWidth()
                                        - fm.stringWidth(text)) / 2,
                                (c.getHeight()
                                        + fm.getAscent()
                                        - fm.getDescent()) / 2
                        );

                        g2.dispose();
                    }
                }
        );
    }

    private class CustomTextField extends JTextField {

        public CustomTextField() {

            setOpaque(false);

            setForeground(TEXT_WHITE);

            setCaretColor(TEXT_WHITE);

            setFont(
                    new Font(
                            "Segoe UI",
                            Font.PLAIN,
                            16
                    )
            );

            setBorder(
                    new EmptyBorder(0, 15, 0, 15)
            );

            addFocusListener(new FocusAdapter() {

                public void focusGained(FocusEvent e) {

                    repaint();
                }

                public void focusLost(FocusEvent e) {

                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(INPUT_BG);

            g2.fillRoundRect(
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    12,
                    12
            );

            g2.dispose();

            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(
                    isFocusOwner()
                            ? INPUT_FOCUS
                            : INPUT_BORDER
            );

            g2.setStroke(
                    new BasicStroke(
                            isFocusOwner() ? 2f : 1.5f
                    )
            );

            g2.drawRoundRect(
                    1,
                    1,
                    getWidth() - 3,
                    getHeight() - 3,
                    12,
                    12
            );

            g2.dispose();
        }
    }

    private class CustomPasswordField extends JPasswordField {

        public CustomPasswordField() {

            setOpaque(false);

            setForeground(TEXT_WHITE);

            setCaretColor(TEXT_WHITE);

            setFont(
                    new Font(
                            "Segoe UI",
                            Font.PLAIN,
                            16
                    )
            );

            setBorder(
                    new EmptyBorder(0, 15, 0, 15)
            );

            addFocusListener(new FocusAdapter() {

                public void focusGained(FocusEvent e) {

                    repaint();
                }

                public void focusLost(FocusEvent e) {

                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(INPUT_BG);

            g2.fillRoundRect(
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    12,
                    12
            );

            g2.dispose();

            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(
                    isFocusOwner()
                            ? INPUT_FOCUS
                            : INPUT_BORDER
            );

            g2.setStroke(
                    new BasicStroke(
                            isFocusOwner() ? 2f : 1.5f
                    )
            );

            g2.drawRoundRect(
                    1,
                    1,
                    getWidth() - 3,
                    getHeight() - 3,
                    12,
                    12
            );

            g2.dispose();
        }
    }
}