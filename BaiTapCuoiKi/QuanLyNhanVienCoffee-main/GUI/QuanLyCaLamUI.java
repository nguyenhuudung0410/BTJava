import javax.swing.*;

import javax.swing.border.EmptyBorder;

import java.awt.*;

import java.awt.event.*;

import java.awt.geom.Ellipse2D;

import java.math.BigDecimal;

import java.math.RoundingMode;

import java.sql.Connection;

import java.sql.PreparedStatement;

import java.sql.ResultSet;

import java.time.Duration;

import java.time.LocalDate;

import java.time.LocalDateTime;

import java.time.YearMonth;

import java.time.format.DateTimeFormatter;

import java.util.ArrayList;

import java.util.HashMap;

import java.util.List;

import java.util.Map;



public class QuanLyCaLamUI extends JPanel {



    
    private final Color BG_MAIN = new Color(248, 250, 252);

    private final Color PANEL_BG = Color.WHITE;

    private final Color TEXT_MAIN = new Color(30, 41, 59);

    private final Color TEXT_MUTED = new Color(100, 116, 139);

    

    
    private JTextField txtMaCa, txtNgayLam, txtGhiChu;

    private JComboBox<String> cbLoaiCa;

    private JComboBox<String> cbNhanVien;

    private JPanel pnlNhanVienList; 

    private CaLamData currentCaLam = null;

    private List<NhanVienData> dsNhanVienTam = new ArrayList<>();

    
    private YearMonth thangHienTai;

    private JLabel lblThangNam;

    private JPanel pnlLich;



    
    private List<LoaiCaData> listLoaiCa;

    private List<NhanVienData> listNhanVien;



    public QuanLyCaLamUI() {

        setLayout(new BorderLayout()); 

        setBackground(BG_MAIN);

        setBorder(new EmptyBorder(20, 20, 20, 20));



        thangHienTai = YearMonth.now(); 

        initDataCache();

        

        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        splitPane.setLeftComponent(createLeftPanel());

        splitPane.setRightComponent(createRightPanel());

        

        splitPane.setDividerSize(8); 

        splitPane.setBorder(null);

        splitPane.setOpaque(false);

        splitPane.setBackground(BG_MAIN);

        splitPane.setResizeWeight(0.34); 



        splitPane.setUI(new javax.swing.plaf.basic.BasicSplitPaneUI() {

            @Override

            public javax.swing.plaf.basic.BasicSplitPaneDivider createDefaultDivider() {

                return new javax.swing.plaf.basic.BasicSplitPaneDivider(this) {

                    @Override

                    public void paint(Graphics g) {

                        g.setColor(BG_MAIN); 
                        g.fillRect(0, 0, getWidth(), getHeight());

                    }

                };

            }

        });



        SwingUtilities.invokeLater(() -> splitPane.setDividerLocation(0.34));



        add(splitPane, BorderLayout.CENTER);



        loadDanhSachCa();

        clearForm(); 

    }



    
    private void initDataCache() {

        listLoaiCa = fetchDanhSachLoaiCa(); 

        listNhanVien = NhanVienDao.getInstance().layDanhSach(); 

    }



    
    private List<LoaiCaData> fetchDanhSachLoaiCa() {

        List<LoaiCaData> list = new ArrayList<>();

        try (java.sql.Connection con = ConnectDB.getInstance().getConnection();

             java.sql.PreparedStatement ps = con.prepareStatement("SELECT * FROM LoaiCa");

             java.sql.ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                LoaiCaData lc = new LoaiCaData();

                lc.setMaLoaiCa(rs.getString("MaLoaiCa"));

                lc.setTenLoaiCa(rs.getString("TenLoaiCa"));

                lc.setGioBatDau(rs.getTime("GioBatDau").toLocalTime());

                lc.setGioKetThuc(rs.getTime("GioKetThuc").toLocalTime());

                list.add(lc);

            }

        } catch (Exception e) {}

        return list;

    }



    
    
    
    private JPanel createLeftPanel() {

        JPanel pnlLeft = new JPanel(new BorderLayout(0, 15));

        pnlLeft.setMinimumSize(new Dimension(340, 0)); 

        pnlLeft.setOpaque(false);



        JPanel pnlContent = new JPanel() {

            @Override protected void paintComponent(Graphics g) {

                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(PANEL_BG);

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                g2.setColor(new Color(0, 0, 0, 10));

                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);

                g2.dispose();

                super.paintComponent(g);

            }

        };

        pnlContent.setOpaque(false);

        pnlContent.setLayout(new BoxLayout(pnlContent, BoxLayout.Y_AXIS));

        pnlContent.setBorder(new EmptyBorder(20, 20, 20, 20));



        JLabel lblTitle = new JLabel("Thông Tin Ca Làm");

        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));

        lblTitle.setForeground(TEXT_MAIN);

        pnlContent.add(lblTitle);

        pnlContent.add(Box.createVerticalStrut(20));



        txtMaCa = createModernTextField("Mã ca tự động sinh");

        txtMaCa.setEditable(false);

        txtMaCa.setBackground(new Color(241, 245, 249)); 

        txtMaCa.setForeground(new Color(37, 99, 235)); 

        txtMaCa.setFont(new Font("Segoe UI", Font.BOLD, 15));



        txtGhiChu = createModernTextField("Nhập ghi chú...");

        

        cbLoaiCa = new JComboBox<>();

        if (listLoaiCa != null) {

            for (LoaiCaData lc : listLoaiCa) {

                cbLoaiCa.addItem(lc.getMaLoaiCa() + " - " + lc.getTenLoaiCa());

            }

        }

        
        JPanel pnlNgayLam = new JPanel(new BorderLayout(0, 0));

        pnlNgayLam.setOpaque(false);

        pnlNgayLam.setBorder(BorderFactory.createEmptyBorder());

        txtNgayLam = createModernTextField("yyyy-mm-dd");

        


        JButton btnCalendar = new JButton() {

            @Override protected void paintComponent(Graphics g) {

                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(100, 116, 139));

                g2.setStroke(new BasicStroke(1.5f));

                int x = getWidth()/2 - 7;

                int y = getHeight()/2 - 6;

                g2.drawRoundRect(x, y+2, 14, 12, 3, 3);

                g2.drawLine(x, y+6, x+14, y+6);

                g2.drawLine(x+3, y, x+3, y+3);

                g2.drawLine(x+11, y, x+11, y+3);

                g2.dispose();

            }

        };

        btnCalendar.setPreferredSize(new Dimension(45, 0));

        btnCalendar.setBackground(new Color(241, 245, 249)); 
        btnCalendar.setFocusPainted(false);

        btnCalendar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnCalendar.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10)); 


        MiniCalendarPopup calendarPopup = new MiniCalendarPopup(txtNgayLam);

        btnCalendar.addActionListener(e -> calendarPopup.show(btnCalendar, 0, btnCalendar.getHeight() + 2));



        pnlNgayLam.add(txtNgayLam, BorderLayout.CENTER);

        pnlNgayLam.add(btnCalendar, BorderLayout.EAST);



        pnlContent.add(createInputRow("Mã Ca:", txtMaCa));

        pnlContent.add(createInputRow("Loại Ca:", cbLoaiCa));

        pnlContent.add(createInputRow("Ngày Làm:", pnlNgayLam));

        pnlContent.add(createInputRow("Ghi Chú:", txtGhiChu));



        pnlContent.add(Box.createVerticalStrut(10));

        JSeparator sep = new JSeparator();

        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        pnlContent.add(sep);

        sep.setForeground(new Color(226, 232, 240));  
        sep.setBackground(new Color(226, 232, 240));

        pnlContent.add(Box.createVerticalStrut(10));



        JLabel lblPhanCong = new JLabel("Nhân sự trong ca");

        lblPhanCong.setFont(new Font("Segoe UI", Font.BOLD, 16));

        lblPhanCong.setForeground(TEXT_MAIN);

        pnlContent.add(lblPhanCong);

        pnlContent.add(Box.createVerticalStrut(10));



        JPanel pnlAddNV = new JPanel(new BorderLayout(10, 0));

        pnlAddNV.setOpaque(false);

        cbNhanVien = new JComboBox<>();

        if(listNhanVien != null) {

            for (NhanVienData nv : listNhanVien) cbNhanVien.addItem(nv.getMaNV() + " - " + nv.getHoTen());

        }

        

        ModernButton btnThemNV = new ModernButton("+ Thêm", new Color(16, 185, 129));

        btnThemNV.setPreferredSize(new Dimension(95, 40)); 

        btnThemNV.addActionListener(e -> handlePhanCongNhanVien());

        

        pnlAddNV.add(cbNhanVien, BorderLayout.CENTER);

        pnlAddNV.add(btnThemNV, BorderLayout.EAST);

        pnlAddNV.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); 

        pnlContent.add(pnlAddNV);

        pnlContent.add(Box.createVerticalStrut(10));



        pnlNhanVienList = new JPanel();

        pnlNhanVienList.setLayout(new BoxLayout(pnlNhanVienList, BoxLayout.Y_AXIS));

        pnlNhanVienList.setOpaque(false);

        JScrollPane scrollNV = new JScrollPane(pnlNhanVienList);

        scrollNV.setBorder(BorderFactory.createEmptyBorder());

        scrollNV.setOpaque(false);

        scrollNV.getViewport().setOpaque(false);

        scrollNV.setPreferredSize(new Dimension(300, 150));

        pnlContent.add(scrollNV);



        pnlLeft.add(pnlContent, BorderLayout.CENTER);



        JPanel pnlActions = new JPanel(new GridLayout(2, 2, 10, 10));

        pnlActions.setOpaque(false);

        pnlActions.setPreferredSize(new Dimension(360, 100));



        ModernButton btnThemCa = new ModernButton("Tạo Ca Mới", new Color(59, 130, 246));

        ModernButton btnSuaCa = new ModernButton("Cập Nhật", new Color(245, 158, 11));

        ModernButton btnXoaCa = new ModernButton("Xóa Ca", new Color(239, 68, 68));

        ModernButton btnLamMoi = new ModernButton("Làm Mới", TEXT_MUTED);



        btnThemCa.addActionListener(e -> handleThemCa());

        btnSuaCa.addActionListener(e -> handleSuaCa());

        btnXoaCa.addActionListener(e -> handleXoaCa());

        btnLamMoi.addActionListener(e -> clearForm());



        pnlActions.add(btnThemCa);

        pnlActions.add(btnSuaCa);

        pnlActions.add(btnXoaCa);

        pnlActions.add(btnLamMoi);

        

        pnlLeft.add(pnlActions, BorderLayout.SOUTH);



        return pnlLeft;

    }



    private JPanel createInputRow(String label, JComponent comp) {

        JPanel pnl = new JPanel(new BorderLayout(0, 5));

        pnl.setOpaque(false);

        JLabel lbl = new JLabel(label);

        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));

        lbl.setForeground(TEXT_MUTED);

        pnl.add(lbl, BorderLayout.NORTH);

        pnl.add(comp, BorderLayout.CENTER);

        pnl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

        pnl.setBorder(new EmptyBorder(0, 0, 15, 0));

        return pnl;

    }



    private JTextField createModernTextField(String placeholder) {

        JTextField txt = new JTextField();

        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        txt.setBackground(new Color(241, 245, 249)); 
        txt.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12)); 
        return txt;

    }



    
    
    
    private JPanel createRightPanel() {

        JPanel pnlRight = new JPanel(new BorderLayout(10, 10));

        pnlRight.setOpaque(false);



        JPanel pnlHeader = new JPanel(new BorderLayout());

        pnlHeader.setOpaque(false);

        

        JPanel pnlNav = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));

        pnlNav.setOpaque(false);



        JPanel pnlArrows = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));

        pnlArrows.setOpaque(false);

        JButton btnTruoc = taoNutDieuHuong("←");

        JButton btnSau = taoNutDieuHuong("→");

        

        btnTruoc.addActionListener(e -> { thangHienTai = thangHienTai.minusMonths(1); loadDanhSachCa(); });

        btnSau.addActionListener(e -> { thangHienTai = thangHienTai.plusMonths(1); loadDanhSachCa(); });

        pnlArrows.add(btnTruoc);

        pnlArrows.add(btnSau);



        lblThangNam = new JLabel();

        lblThangNam.setOpaque(true);

        lblThangNam.setBackground(Color.WHITE);

        lblThangNam.setFont(new Font("Calibri", Font.BOLD, 18));

        lblThangNam.setForeground(new Color(73, 80, 87));

        lblThangNam.setBorder(new EmptyBorder(6, 12, 6, 12));



        pnlNav.add(pnlArrows);

        pnlNav.add(lblThangNam);

        pnlHeader.add(pnlNav, BorderLayout.WEST);



        ModernButton btnChamCongTong = new ModernButton("Chấm Công Ca Chọn", new Color(139, 92, 246));

        btnChamCongTong.setPreferredSize(new Dimension(200, 40));

        btnChamCongTong.addActionListener(e -> showChamCongDialog());

        pnlHeader.add(btnChamCongTong, BorderLayout.EAST);



        pnlRight.add(pnlHeader, BorderLayout.NORTH);



        JPanel pnlCenter = new JPanel(new BorderLayout(0, 0));

        pnlCenter.setOpaque(false);



        JPanel pnlThu = new JPanel(new GridLayout(1, 7, 1, 1)); 

        pnlThu.setBackground(Color.WHITE);

        String[] cacThu = {"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ Nhật"};

        for (int i = 0; i < cacThu.length; i++) {

            JLabel lblThu = new JLabel(cacThu[i], SwingConstants.CENTER);

            lblThu.setFont(new Font("Calibri", Font.BOLD, 18));

            lblThu.setOpaque(true);

            lblThu.setBackground(Color.WHITE);

            if (i >= 5) lblThu.setForeground(new Color(220, 53, 69)); 

            else lblThu.setForeground(new Color(73, 80, 87));

            lblThu.setBorder(new EmptyBorder(12, 0, 12, 0));

            pnlThu.add(lblThu);

        }



        pnlLich = new JPanel(new GridLayout(0, 7, 1, 1));

        pnlLich.setBackground(Color.WHITE); 



        pnlCenter.add(pnlThu, BorderLayout.NORTH);

        

        JScrollPane scroll = new JScrollPane(pnlLich);

        scroll.setBorder(BorderFactory.createEmptyBorder());

        scroll.getVerticalScrollBar().setUnitIncrement(16);

        pnlCenter.add(scroll, BorderLayout.CENTER);



        pnlRight.add(pnlCenter, BorderLayout.CENTER);



        return pnlRight;

    }



    private JButton taoNutDieuHuong(String text) {

        JButton btn = new JButton(text);

        btn.setFont(new Font("Arial", Font.BOLD, 14)); 

        btn.setPreferredSize(new Dimension(36, 36)); 

        btn.setBackground(new Color(241, 245, 249)); 
        btn.setForeground(new Color(100, 100, 100));

        btn.setFocusPainted(false);

        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); 


        btn.addMouseListener(new MouseAdapter() {

            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(226, 232, 240)); }

            public void mouseExited(MouseEvent e) { btn.setBackground(new Color(241, 245, 249)); }

        });

        return btn;

    }



    private JPanel taoOCongViec(String soNgay, boolean laCuoiTuan) {

        JPanel pnl = new JPanel();

        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));

        pnl.setBackground(soNgay.isEmpty() ? BG_MAIN : (laCuoiTuan ? new Color(241, 245, 249) : Color.WHITE));

        pnl.setBorder(new EmptyBorder(5, 5, 5, 5));



        if (!soNgay.isEmpty()) {

            JLabel lblSo = new JLabel(soNgay);

            lblSo.setFont(new Font("Calibri", Font.PLAIN, 16));

            lblSo.setForeground(new Color(73, 80, 87));

            lblSo.setAlignmentX(Component.LEFT_ALIGNMENT);

            

            if (thangHienTai.equals(YearMonth.now()) && soNgay.equals(String.valueOf(LocalDate.now().getDayOfMonth()))) {

                lblSo.setFont(new Font("Calibri", Font.BOLD, 18));

                lblSo.setForeground(new Color(0, 123, 255)); 

            }

            pnl.add(lblSo);

            pnl.add(Box.createVerticalStrut(5)); 

        }

        return pnl;

    }



    
    
    
    private String taoMaCaTuDong() {

        List<CaLamData> danhSachCa = CaLamDao.getInstance().layDanhSach();

        int maxId = 0;

        for (CaLamData ca : danhSachCa) {

            String ma = ca.getMaCa();

            if (ma != null && ma.startsWith("CA")) {

                try {

                    int id = Integer.parseInt(ma.substring(2));

                    if (id > maxId) maxId = id;

                } catch (Exception ignored) {}

            }

        }

        return String.format("CA%03d", maxId + 1); 

    }



    private void loadDanhSachCa() {

        pnlLich.removeAll();

        lblThangNam.setText("Tháng " + thangHienTai.getMonthValue() + ", " + thangHienTai.getYear());



        List<CaLamData> danhSachCa = CaLamDao.getInstance().layDanhSach();

        

        Map<Integer, List<CaLamData>> mapCaTheoNgay = new HashMap<>();

        for (CaLamData ca : danhSachCa) {

            if (ca.getNgayLam() != null 

                && ca.getNgayLam().getYear() == thangHienTai.getYear() 

                && ca.getNgayLam().getMonthValue() == thangHienTai.getMonthValue()) {

                

                int ngay = ca.getNgayLam().getDayOfMonth();

                mapCaTheoNgay.computeIfAbsent(ngay, k -> new ArrayList<>()).add(ca);

            }

        }



        LocalDate ngayDauThang = thangHienTai.atDay(1);

        int soNgayCuaThang = thangHienTai.lengthOfMonth();

        int thuCuaNgayDauThang = ngayDauThang.getDayOfWeek().getValue(); 



        for (int i = 1; i < thuCuaNgayDauThang; i++) pnlLich.add(taoOCongViec("", false));



        for (int ngay = 1; ngay <= soNgayCuaThang; ngay++) {

            int thuHienTai = thangHienTai.atDay(ngay).getDayOfWeek().getValue();

            boolean laCuoiTuan = (thuHienTai == 6 || thuHienTai == 7);

            

            JPanel pnlNgay = taoOCongViec(String.valueOf(ngay), laCuoiTuan);

            

            if (mapCaTheoNgay.containsKey(ngay)) {

                for (CaLamData ca : mapCaTheoNgay.get(ngay)) {

                    LoaiCaData lc = CaLamDao.getInstance().layLoaiCaTheoMa(ca.getMaLoaiCa()); 
                    List<PhanCongCaData> dspc = PhanCongCaDao.getInstance().layDanhSachTheoCa(ca.getMaCa()); 
                    pnlNgay.add(new TagCaLam(ca, lc, dspc.size()));

                    pnlNgay.add(Box.createVerticalStrut(3));

                }

            }

            pnlLich.add(pnlNgay);

        }



        int tongSoO = (thuCuaNgayDauThang - 1) + soNgayCuaThang;

        int soODuocThem = (tongSoO % 7 != 0) ? 7 - (tongSoO % 7) : 0;

        for(int i = 0; i < soODuocThem; i++) pnlLich.add(taoOCongViec("", false));



        pnlLich.revalidate();

        pnlLich.repaint();

    }



   private void handleThemCa() {

        try {

            if (dsNhanVienTam.isEmpty()) {

                int confirm = JOptionPane.showConfirmDialog(this, 

                    "Bạn chưa thêm nhân viên nào vào ca. Vẫn muốn tạo ca rỗng?", "Xác nhận", JOptionPane.YES_NO_OPTION);

                if (confirm != JOptionPane.YES_OPTION) return;

            }



            
            CaLamData ca = new CaLamData();

            String maCaMoi = txtMaCa.getText().trim();

            ca.setMaCa(maCaMoi);

            ca.setMaLoaiCa(cbLoaiCa.getSelectedItem().toString().split(" - ")[0]);

            ca.setNgayLam(LocalDate.parse(txtNgayLam.getText().trim()));

            ca.setGhiChu(txtGhiChu.getText().trim());



            CaLamLogic.getInstance().themCa(ca); 


            
            int countSuccess = 0;

            StringBuilder errorLog = new StringBuilder();

            

            for (NhanVienData nv : dsNhanVienTam) {

                try {

                    PhanCongCaLogic.getInstance().phanCongNhanVien(maCaMoi, nv.getMaNV()); 

                    countSuccess++;

                } catch (Exception ex) {

                    
                    errorLog.append("- ").append(nv.getHoTen()).append(": ").append(ex.getMessage()).append("\n");

                }

            }



            
            if (countSuccess == dsNhanVienTam.size() && !dsNhanVienTam.isEmpty()) {

                JOptionPane.showMessageDialog(this, "Đã tạo ca " + maCaMoi + " kèm " + countSuccess + " nhân viên thành công!");

            } else if (!dsNhanVienTam.isEmpty()) {

                
                JOptionPane.showMessageDialog(this, 

                    "Tạo ca " + maCaMoi + " thành công NHƯNG nhân viên không được lưu do:\n\n" + errorLog.toString(), 

                    "Lỗi Phân Công", JOptionPane.ERROR_MESSAGE);

            } else {

                JOptionPane.showMessageDialog(this, "Đã tạo ca rỗng " + maCaMoi + " thành công!");

            }

            

            clearForm();

            loadDanhSachCa();

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(this, "Lỗi tạo ca: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);

        }

    }



    private void handleSuaCa() {

        if (currentCaLam == null) {

            JOptionPane.showMessageDialog(this, "Vui lòng chọn một ca làm trên Lịch để sửa!");

            return;

        }

        try {

            currentCaLam.setMaLoaiCa(cbLoaiCa.getSelectedItem().toString().split(" - ")[0]);

            currentCaLam.setNgayLam(LocalDate.parse(txtNgayLam.getText().trim()));

            currentCaLam.setGhiChu(txtGhiChu.getText().trim());



            CaLamLogic.getInstance().suaCa(currentCaLam);

            JOptionPane.showMessageDialog(this, "Cập nhật ca làm thành công!");

            loadDanhSachCa();

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);

        }

    }



    private void handleXoaCa() {

        if (currentCaLam == null) {

            JOptionPane.showMessageDialog(this, "Vui lòng chọn ca làm trên Lịch cần xóa!");

            return;

        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa ca " + currentCaLam.getMaCa() + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {

            try {

                CaLamLogic.getInstance().xoaCa(currentCaLam.getMaCa());

                JOptionPane.showMessageDialog(this, "Đã xóa ca làm!");

                clearForm();

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(this, ex.getMessage(), "Không thể xóa", JOptionPane.WARNING_MESSAGE);

            }

        }

    }



    private void handlePhanCongNhanVien() {

        String selectedItem = cbNhanVien.getSelectedItem().toString();

        String maNV = selectedItem.split(" - ")[0];



        
        if (currentCaLam != null) {

            try {

                PhanCongCaLogic.getInstance().phanCongNhanVien(currentCaLam.getMaCa(), maNV);

                loadDanhSachPhanCong(currentCaLam.getMaCa()); 
            } catch (Exception ex) {

                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);

            }

            return; 
        }



        
        for (NhanVienData nv : dsNhanVienTam) {

            if (nv.getMaNV().equals(maNV)) {

                JOptionPane.showMessageDialog(this, "Nhân viên này đã có trong danh sách chờ!");

                return;

            }

        }



        NhanVienData nvChon = null;

        for (NhanVienData nv : listNhanVien) {

            if (nv.getMaNV().equals(maNV)) {

                nvChon = nv;

                break;

            }

        }



        if (nvChon != null) {

            dsNhanVienTam.add(nvChon);

            renderDanhSachNhanVienCho(); 

        }

    }

    private void renderDanhSachNhanVienCho() {

        pnlNhanVienList.removeAll();

        for (int i = 0; i < dsNhanVienTam.size(); i++) {

            NhanVienData nv = dsNhanVienTam.get(i);

            final int index = i;

            

            
            EmployeeItem item = new EmployeeItem("TEMP", nv); 

            
            for (Component c : item.getComponents()) {

                if (c instanceof JButton) {

                    ((JButton) c).addActionListener(e -> {

                        dsNhanVienTam.remove(index);

                        renderDanhSachNhanVienCho();

                    });

                }

            }

            pnlNhanVienList.add(item);

        }

        pnlNhanVienList.revalidate();

        pnlNhanVienList.repaint();

    }



    private void handleGoPhanCong(String maPhanCong) {

        try {

            PhanCongCaLogic.getInstance().xoaPhanCong(maPhanCong);

            loadDanhSachNhanVienCuaCa();

            loadDanhSachCa();

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);

        }

    }



    private void clearForm() {

        currentCaLam = null;

        dsNhanVienTam.clear(); 
        txtMaCa.setText(taoMaCaTuDong()); 

        txtNgayLam.setText(LocalDate.now().toString());

        txtGhiChu.setText("");

        pnlNhanVienList.removeAll();

        pnlNhanVienList.revalidate();

        pnlNhanVienList.repaint();

        loadDanhSachCa(); 

    }



    private void fillToForm(CaLamData ca) {

        currentCaLam = ca;

        txtMaCa.setText(ca.getMaCa());

        txtMaCa.setEditable(false); 

        txtNgayLam.setText(ca.getNgayLam().toString());

        txtGhiChu.setText(ca.getGhiChu() != null ? ca.getGhiChu() : "");

        

        for (int i = 0; i < cbLoaiCa.getItemCount(); i++) {

            if (cbLoaiCa.getItemAt(i).startsWith(ca.getMaLoaiCa())) {

                cbLoaiCa.setSelectedIndex(i); break;

            }

        }

        loadDanhSachNhanVienCuaCa();

        loadDanhSachCa(); 

    }



    private void loadDanhSachNhanVienCuaCa() {

        pnlNhanVienList.removeAll();

        if (currentCaLam != null) {

            List<PhanCongCaData> dspc = PhanCongCaDao.getInstance().layDanhSachTheoCa(currentCaLam.getMaCa());

            for (PhanCongCaData pc : dspc) {

                NhanVienData nv = NhanVienDao.getInstance().layTheoMa(pc.getMaNV());

                if(nv != null) pnlNhanVienList.add(new EmployeeItem(pc.getMaPhanCong(), nv));

            }

        }

        pnlNhanVienList.revalidate();

        pnlNhanVienList.repaint();

    }



    
    
    
    private void showChamCongDialog() {

        if (currentCaLam == null) {

            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 thẻ Ca Làm trên Lịch trước!");

            return;

        }



        
        Window parentWindow = SwingUtilities.getWindowAncestor(this);

        ChamCongPopupUI popup = new ChamCongPopupUI(parentWindow, currentCaLam);

        popup.setVisible(true);

        

        loadDanhSachCa(); 

    }



    
    
    
    private class TagCaLam extends JPanel {

        private Color bgColor, fgColor;

        private CaLamData ca; 

        private int soNhanVien; 



        public TagCaLam(CaLamData ca, LoaiCaData lc, int soNhanVien) {

            this.ca = ca;

            this.soNhanVien = soNhanVien; 

            

            String tenCa = lc != null ? lc.getTenLoaiCa() : "Unknown";

            setOpaque(false);

            setAlignmentX(Component.LEFT_ALIGNMENT);

            setMaximumSize(new Dimension(Integer.MAX_VALUE, 28)); 



            if (tenCa.toLowerCase().contains("sáng")) {

                bgColor = new Color(204, 235, 255); fgColor = new Color(0, 86, 179);

            } else if (tenCa.toLowerCase().contains("chiều")) {

                bgColor = new Color(128, 191, 255); fgColor = new Color(0, 64, 133);

            } else {

                bgColor = new Color(0, 123, 255); fgColor = Color.WHITE;

            }



            if (currentCaLam != null && currentCaLam.getMaCa().equals(ca.getMaCa())) {

                bgColor = new Color(245, 158, 11); 

                fgColor = Color.WHITE;

            }

            

            addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) { setCursor(new Cursor(Cursor.HAND_CURSOR)); }

                public void mousePressed(MouseEvent e) { fillToForm(ca); } 

            });

        }



        @Override

        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(bgColor);

            g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 6, 6);

            g2.setColor(fgColor);

            g2.setFont(new Font("Calibri", Font.BOLD, 14)); 

            

            String txt = "● " + ca.getMaCa() + " (" + soNhanVien + ")";

            FontMetrics fm = g2.getFontMetrics();

            g2.drawString(txt, 6, (getHeight() - fm.getHeight()) / 2 + fm.getAscent());

            g2.dispose();

        }



        @Override public Dimension getPreferredSize() { return new Dimension(100, 26); }

    }
    
    class EmployeeItem extends JPanel {

        public EmployeeItem(String maPhanCong, NhanVienData nv) {

            setLayout(new BorderLayout(10, 0));

            setOpaque(false);

            setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

            setBorder(new EmptyBorder(8, 5, 8, 5));



            JPanel pnlAvatar = new JPanel() {

                @Override protected void paintComponent(Graphics g) {

                    Graphics2D g2 = (Graphics2D) g.create();

                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    g2.setColor(new Color(224, 231, 255));

                    g2.fill(new Ellipse2D.Double(0, 5, 30, 30));

                    g2.setColor(new Color(67, 56, 202));

                    g2.setFont(new Font("Segoe UI", Font.BOLD, 14));

                    String initial = nv.getHoTen().substring(nv.getHoTen().lastIndexOf(" ")+1).substring(0,1).toUpperCase();

                    FontMetrics fm = g2.getFontMetrics();

                    g2.drawString(initial, (30-fm.stringWidth(initial))/2, 5 + (30-fm.getHeight())/2 + fm.getAscent());

                    g2.dispose();

                }

            };

            pnlAvatar.setPreferredSize(new Dimension(35, 40));

            pnlAvatar.setOpaque(false);



            JLabel lblName = new JLabel(nv.getHoTen());

            lblName.setFont(new Font("Segoe UI", Font.BOLD, 14));

            lblName.setForeground(TEXT_MAIN);



            JButton btnRemove = new JButton("×");

            btnRemove.setFont(new Font("Arial", Font.BOLD, 18));

            btnRemove.setForeground(new Color(239, 68, 68));

            btnRemove.setContentAreaFilled(false);

            btnRemove.setBorderPainted(false);

            btnRemove.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btnRemove.addActionListener(e -> handleGoPhanCong(maPhanCong));



            add(pnlAvatar, BorderLayout.WEST);

            add(lblName, BorderLayout.CENTER);

            add(btnRemove, BorderLayout.EAST);

        }

    }

    
    private void loadDanhSachPhanCong(String maCa) {

        pnlNhanVienList.removeAll();

        

        
        List<PhanCongCaData> dsPhanCong = PhanCongCaDao.getInstance().layDanhSachTheoCa(maCa);

        

        for (PhanCongCaData pc : dsPhanCong) {

            NhanVienData nv = NhanVienDao.getInstance().layTheoMa(pc.getMaNV());

            if (nv != null) {

                
                EmployeeItem item = new EmployeeItem(pc.getMaPhanCong(), nv);

                pnlNhanVienList.add(item);

            }

        }

        

        
        pnlNhanVienList.revalidate();

        pnlNhanVienList.repaint();

    }

    class ModernButton extends JButton {

        private Color bgColor;

        public ModernButton(String text, Color bg) {

            super(text);

            this.bgColor = bg;

            setForeground(Color.WHITE);

            setFont(new Font("Segoe UI", Font.BOLD, 14));

            setContentAreaFilled(false);

            setBorderPainted(false);

            setFocusPainted(false);

            setCursor(new Cursor(Cursor.HAND_CURSOR));

        }

        @Override protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(getModel().isRollover() ? bgColor.darker() : bgColor);

            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

            g2.dispose();

            super.paintComponent(g);

        }

    }



    
    
    
    class MiniCalendarPopup extends JPopupMenu {

        private YearMonth currentMonth;

        private JTextField targetField;

        private JPanel pnlDays;

        private JLabel lblMonthYear;



        public MiniCalendarPopup(JTextField targetField) {

            this.targetField = targetField;

            this.currentMonth = YearMonth.now();



            setLayout(new BorderLayout());

            setBackground(Color.WHITE);

            setBorder(BorderFactory.createCompoundBorder(

                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),

                new EmptyBorder(10, 10, 10, 10)

            ));



            JPanel pnlHeader = new JPanel(new BorderLayout());

            pnlHeader.setBackground(Color.WHITE);

            JButton btnPrev = new JButton("◄");

            JButton btnNext = new JButton("►");

            lblMonthYear = new JLabel("", SwingConstants.CENTER);

            lblMonthYear.setFont(new Font("Segoe UI", Font.BOLD, 14));

            lblMonthYear.setForeground(new Color(30, 41, 59));



            setupNavButton(btnPrev);

            setupNavButton(btnNext);



            btnPrev.addActionListener(e -> { currentMonth = currentMonth.minusMonths(1); renderCalendar(); });

            btnNext.addActionListener(e -> { currentMonth = currentMonth.plusMonths(1); renderCalendar(); });



            pnlHeader.add(btnPrev, BorderLayout.WEST);

            pnlHeader.add(lblMonthYear, BorderLayout.CENTER);

            pnlHeader.add(btnNext, BorderLayout.EAST);

            add(pnlHeader, BorderLayout.NORTH);



            pnlDays = new JPanel(new GridLayout(0, 7, 4, 4));

            pnlDays.setBackground(Color.WHITE);

            pnlDays.setBorder(new EmptyBorder(10, 0, 0, 0));

            add(pnlDays, BorderLayout.CENTER);



            renderCalendar();

        }



        private void setupNavButton(JButton btn) {

            btn.setContentAreaFilled(false);

            btn.setBorderPainted(false);

            btn.setFocusPainted(false);

            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btn.setForeground(new Color(100, 116, 139));

        }



        private void renderCalendar() {

            pnlDays.removeAll();

            lblMonthYear.setText("Tháng " + currentMonth.getMonthValue() + ", " + currentMonth.getYear());



            String[] days = {"T2", "T3", "T4", "T5", "T6", "T7", "CN"};

            for (String d : days) {

                JLabel lbl = new JLabel(d, SwingConstants.CENTER);

                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));

                lbl.setForeground(new Color(148, 163, 184));

                pnlDays.add(lbl);

            }



            LocalDate firstDay = currentMonth.atDay(1);

            int startDayOfWeek = firstDay.getDayOfWeek().getValue();

            int daysInMonth = currentMonth.lengthOfMonth();



            for (int i = 1; i < startDayOfWeek; i++) {

                pnlDays.add(new JLabel(""));

            }



            for (int i = 1; i <= daysInMonth; i++) {

                int day = i;

                JButton btnDay = new JButton(String.valueOf(day));

                btnDay.setFont(new Font("Segoe UI", Font.PLAIN, 13));

                btnDay.setBackground(Color.WHITE);

                btnDay.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

                btnDay.setFocusPainted(false);

                btnDay.setCursor(new Cursor(Cursor.HAND_CURSOR));



                if (currentMonth.equals(YearMonth.now()) && day == LocalDate.now().getDayOfMonth()) {

                    btnDay.setForeground(new Color(37, 99, 235));

                    btnDay.setFont(new Font("Segoe UI", Font.BOLD, 14));

                    btnDay.setBorder(BorderFactory.createLineBorder(new Color(191, 219, 254), 2, true));

                }



                btnDay.addMouseListener(new MouseAdapter() {

                    public void mouseEntered(MouseEvent e) { btnDay.setBackground(new Color(241, 245, 249)); }

                    public void mouseExited(MouseEvent e) { btnDay.setBackground(Color.WHITE); }

                });



                btnDay.addActionListener(e -> {

                    LocalDate selected = currentMonth.atDay(day);

                    targetField.setText(selected.toString());

                    setVisible(false); 

                });



                pnlDays.add(btnDay);

            }

            revalidate(); repaint(); pack();

        }

    }

    

    
    
    
    class ChamCongPopupUI extends JDialog {



        private CaLamData caLam;

        private LoaiCaData loaiCa;

        private JPanel pnlCardContainer;

        private CardLayout cardLayout;

        private StartShiftPanel startPanel;

        private EndShiftPanel endPanel;

        private JPanel completedPanel;



        public ChamCongPopupUI(Window parent, CaLamData caLam) {

            super(parent, "Quản Lý Chấm Công - " + caLam.getMaCa(), ModalityType.APPLICATION_MODAL);

            this.caLam = caLam;

            this.loaiCa = CaLamDao.getInstance().layLoaiCaTheoMa(caLam.getMaLoaiCa()); 


            setSize(950, 650);

            setLocationRelativeTo(parent);

            getContentPane().setBackground(BG_MAIN);

            setLayout(new BorderLayout());



            initUI();

            kiemTraTrangThaiVaHienThi();

        }



        private void initUI() {

            JPanel pnlHeader = new JPanel(new BorderLayout());

            pnlHeader.setBackground(Color.WHITE);

            pnlHeader.setBorder(BorderFactory.createCompoundBorder(

                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),

                new EmptyBorder(20, 30, 20, 30)

            ));



            JLabel lblTitle = new JLabel("⏱ Điểm Danh / Chấm Công - Ca: " + caLam.getMaCa());

            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));

            lblTitle.setForeground(TEXT_MAIN);

            

            JLabel lblSub = new JLabel(loaiCa.getTenLoaiCa() + " | Ngày: " + caLam.getNgayLam().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

            lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 15));

            lblSub.setForeground(TEXT_MUTED);



            JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 0, 5));

            pnlTitle.setOpaque(false);

            pnlTitle.add(lblTitle); pnlTitle.add(lblSub);

            pnlHeader.add(pnlTitle, BorderLayout.WEST);

            add(pnlHeader, BorderLayout.NORTH);



            cardLayout = new CardLayout();

            pnlCardContainer = new JPanel(cardLayout);

            pnlCardContainer.setOpaque(false);

            pnlCardContainer.setBorder(new EmptyBorder(20, 30, 20, 30));



            startPanel = new StartShiftPanel();

            endPanel = new EndShiftPanel();

            initCompletedPanel();



            pnlCardContainer.add(startPanel, "START");

            pnlCardContainer.add(endPanel, "END");

            pnlCardContainer.add(completedPanel, "COMPLETED");



            add(pnlCardContainer, BorderLayout.CENTER);

        }



        private void kiemTraTrangThaiVaHienThi() {

            List<PhanCongCaData> dsPhanCong = PhanCongCaDao.getInstance().layDanhSachTheoCa(caLam.getMaCa());

            

            if (dsPhanCong.isEmpty()) {

                JOptionPane.showMessageDialog(this, "Ca này chưa có nhân sự nào được phân công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);

                dispose(); return;

            }



            int checkedInCount = 0;

            int checkedOutCount = 0;



            for (PhanCongCaData pc : dsPhanCong) {

                ChamCongData cc = ChamCongDao.getInstance().layTheoPhanCong(pc.getMaPhanCong()); 
                if (cc != null) {

                    checkedInCount++;

                    if (cc.getCheckOut() != null) checkedOutCount++;

                }

            }



            if (checkedInCount == 0) {

                startPanel.loadData(dsPhanCong);

                cardLayout.show(pnlCardContainer, "START");

            } else if (checkedInCount > 0 && checkedOutCount < checkedInCount) {

                endPanel.loadData(dsPhanCong);

                cardLayout.show(pnlCardContainer, "END");

            } else {

                cardLayout.show(pnlCardContainer, "COMPLETED");

            }

        }



        
        class StartShiftPanel extends JPanel {

            private JPanel pnlList;

            private List<EmployeeStartCard> listCards = new ArrayList<>();



            public StartShiftPanel() {

                setLayout(new BorderLayout(0, 20));

                setOpaque(false);



                pnlList = new JPanel(new WrapLayout(FlowLayout.LEFT, 15, 15));

                pnlList.setOpaque(false);

                JScrollPane scroll = new JScrollPane(pnlList);

                scroll.setBorder(null); scroll.setOpaque(false); scroll.getViewport().setOpaque(false);

                scroll.getVerticalScrollBar().setUnitIncrement(16);

                add(scroll, BorderLayout.CENTER);



                JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));

                pnlBottom.setOpaque(false);

                ModernButton btnSelectAll = new ModernButton("Chọn tất cả", new Color(148, 163, 184));

                ModernButton btnDeselect = new ModernButton("Bỏ chọn", new Color(148, 163, 184));

                ModernButton btnStart = new ModernButton("▶ BẮT ĐẦU CA MỚI", new Color(59, 130, 246));

                btnStart.setPreferredSize(new Dimension(220, 45));



                btnSelectAll.addActionListener(e -> listCards.forEach(c -> c.setSelected(true)));

                btnDeselect.addActionListener(e -> listCards.forEach(c -> c.setSelected(false)));

                btnStart.addActionListener(e -> handleBulkCheckIn());



                pnlBottom.add(btnSelectAll); pnlBottom.add(btnDeselect); pnlBottom.add(btnStart);

                add(pnlBottom, BorderLayout.SOUTH);

            }



            public void loadData(List<PhanCongCaData> dsPhanCong) {

                pnlList.removeAll(); listCards.clear();

                for (PhanCongCaData pc : dsPhanCong) {

                    NhanVienData nv = NhanVienDao.getInstance().layTheoMa(pc.getMaNV());

                    if (nv != null) {

                        EmployeeStartCard card = new EmployeeStartCard(pc, nv);

                        listCards.add(card); pnlList.add(card);

                    }

                }

                pnlList.revalidate(); pnlList.repaint();

            }



            private void handleBulkCheckIn() {

                int count = 0;

                for (EmployeeStartCard card : listCards) {

                    if (card.isSelected()) {

                        try {

                            ChamCongLogic.getInstance().checkIn(card.pc.getMaPhanCong()); 
                            count++;

                        } catch (Exception ex) {}

                    }

                }

                if (count > 0) {

                    JOptionPane.showMessageDialog(ChamCongPopupUI.this, "Đã check-in thành công cho " + count + " nhân sự!");

                    kiemTraTrangThaiVaHienThi(); 

                } else {

                    JOptionPane.showMessageDialog(ChamCongPopupUI.this, "Vui lòng tick chọn ít nhất 1 nhân viên!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);

                }

            }

        }



        class EmployeeStartCard extends JPanel {

            public PhanCongCaData pc;

            private JCheckBox chkSelect;



            public EmployeeStartCard(PhanCongCaData pc, NhanVienData nv) {

                this.pc = pc;

                setPreferredSize(new Dimension(270, 90));

                setOpaque(false);

                setLayout(new BorderLayout(15, 0));

                setBorder(new EmptyBorder(15, 15, 15, 15));



                JPanel pnlLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

                pnlLeft.setOpaque(false);

                chkSelect = new JCheckBox(); chkSelect.setOpaque(false); chkSelect.setCursor(new Cursor(Cursor.HAND_CURSOR)); chkSelect.setSelected(true); 



                JPanel pnlAvatar = new JPanel() {

                    @Override protected void paintComponent(Graphics g) {

                        Graphics2D g2 = (Graphics2D) g.create();

                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        g2.setColor(new Color(226, 232, 240));

                        g2.fill(new Ellipse2D.Double(0, 0, 45, 45));

                        g2.setColor(TEXT_MUTED);

                        g2.setFont(new Font("Segoe UI", Font.BOLD, 16));

                        String initial = nv.getHoTen().substring(nv.getHoTen().lastIndexOf(" ")+1).substring(0,1).toUpperCase();

                        FontMetrics fm = g2.getFontMetrics();

                        g2.drawString(initial, (45-fm.stringWidth(initial))/2, (45-fm.getHeight())/2 + fm.getAscent());

                        g2.dispose();

                    }

                };

                pnlAvatar.setPreferredSize(new Dimension(45, 45)); pnlAvatar.setOpaque(false);

                pnlLeft.add(chkSelect); pnlLeft.add(pnlAvatar);



                JPanel pnlInfo = new JPanel(new GridLayout(3, 1));

                pnlInfo.setOpaque(false);

                JLabel lblName = new JLabel(nv.getHoTen()); lblName.setFont(new Font("Segoe UI", Font.BOLD, 15)); lblName.setForeground(TEXT_MAIN);

                JLabel lblRole = new JLabel("Chức vụ: " + (nv.getChucVu() != null ? nv.getChucVu().name() : "N/A")); lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 12)); lblRole.setForeground(TEXT_MUTED);

                JLabel lblWage = new JLabel("Lương: " + String.format("%,.0f đ/h", nv.getLuongGio())); lblWage.setFont(new Font("Segoe UI", Font.BOLD, 12)); lblWage.setForeground(new Color(59, 130, 246));



                pnlInfo.add(lblName); pnlInfo.add(lblRole); pnlInfo.add(lblWage);

                add(pnlLeft, BorderLayout.WEST); add(pnlInfo, BorderLayout.CENTER);



                addMouseListener(new MouseAdapter() {

                    public void mouseEntered(MouseEvent e) { setCursor(new Cursor(Cursor.HAND_CURSOR)); }

                    public void mousePressed(MouseEvent e) { chkSelect.setSelected(!chkSelect.isSelected()); }

                });

            }

            public boolean isSelected() { return chkSelect.isSelected(); }

            public void setSelected(boolean b) { chkSelect.setSelected(b); }

            @Override protected void paintComponent(Graphics g) {

                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(0, 0, 0, 8)); 

                g2.fillRoundRect(2, 4, getWidth()-4, getHeight()-6, 15, 15);

                g2.setColor(PANEL_BG); 

                g2.fillRoundRect(0, 0, getWidth()-2, getHeight()-4, 15, 15);

                if (chkSelect.isSelected()) {

                    g2.setColor(new Color(59, 130, 246));

                    g2.setStroke(new BasicStroke(2f));

                    g2.drawRoundRect(0, 0, getWidth()-2, getHeight()-4, 15, 15);

                }

                g2.dispose(); super.paintComponent(g);

            }

        }



        
        class EndShiftPanel extends JPanel {

            private JPanel pnlList;

            private List<EmployeeEndCard> activeCards = new ArrayList<>();

            private Timer realtimeTimer;



            public EndShiftPanel() {

                setLayout(new BorderLayout(0, 20));

                setOpaque(false);



                pnlList = new JPanel(new WrapLayout(FlowLayout.LEFT, 15, 15));

                pnlList.setOpaque(false);

                JScrollPane scroll = new JScrollPane(pnlList);

                scroll.setBorder(null); scroll.setOpaque(false); scroll.getViewport().setOpaque(false);

                scroll.getVerticalScrollBar().setUnitIncrement(16);

                add(scroll, BorderLayout.CENTER);



                JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));

                pnlBottom.setOpaque(false);

                ModernButton btnRefresh = new ModernButton("Làm Mới", new Color(148, 163, 184));

                ModernButton btnEnd = new ModernButton("■ KẾT THÚC CA LÀM", new Color(239, 68, 68)); 

                btnEnd.setPreferredSize(new Dimension(220, 45));



                btnRefresh.addActionListener(e -> loadData(PhanCongCaDao.getInstance().layDanhSachTheoCa(caLam.getMaCa())));

                btnEnd.addActionListener(e -> handleBulkCheckOut());



                pnlBottom.add(btnRefresh); pnlBottom.add(btnEnd);

                add(pnlBottom, BorderLayout.SOUTH);



                realtimeTimer = new Timer(1000, e -> activeCards.forEach(EmployeeEndCard::updateRealtimeData));

                realtimeTimer.start();

            }



            public void loadData(List<PhanCongCaData> dsPhanCong) {

                pnlList.removeAll(); activeCards.clear();

                for (PhanCongCaData pc : dsPhanCong) {

                    ChamCongData cc = ChamCongDao.getInstance().layTheoPhanCong(pc.getMaPhanCong());

                    if (cc != null && cc.getCheckOut() == null) {

                        NhanVienData nv = NhanVienDao.getInstance().layTheoMa(pc.getMaNV());

                        if (nv != null) {

                            EmployeeEndCard card = new EmployeeEndCard(pc, nv, cc);

                            activeCards.add(card); pnlList.add(card);

                        }

                    }

                }

                pnlList.revalidate(); pnlList.repaint();

            }



            private void handleBulkCheckOut() {

                int confirm = JOptionPane.showConfirmDialog(ChamCongPopupUI.this, 

                    "Chốt ca và Check-out toàn bộ nhân sự?", "Xác nhận", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {

                    int count = 0;

                    for (EmployeeEndCard card : activeCards) {

                        try {

                            ChamCongLogic.getInstance().checkOut(card.pc.getMaPhanCong()); 
                            count++;

                        } catch (Exception ex) {}

                    }

                    if (count > 0) {

                        JOptionPane.showMessageDialog(ChamCongPopupUI.this, "Đã chốt lương cho " + count + " nhân sự thành công!");

                        realtimeTimer.stop();

                        kiemTraTrangThaiVaHienThi();

                    }

                }

            }

        }



        class EmployeeEndCard extends JPanel {

            public PhanCongCaData pc;

            private NhanVienData nv;

            private ChamCongData cc;

            private JLabel lblCurrentTime, lblHours, lblTempSalary;



            public EmployeeEndCard(PhanCongCaData pc, NhanVienData nv, ChamCongData cc) {

                this.pc = pc; this.nv = nv; this.cc = cc;

                setPreferredSize(new Dimension(280, 120));

                setOpaque(false);

                setLayout(new BorderLayout());

                setBorder(new EmptyBorder(12, 15, 12, 15));



                JPanel pnlTop = new JPanel(new BorderLayout());

                pnlTop.setOpaque(false);

                JLabel lblName = new JLabel("👨‍🍳 " + nv.getHoTen()); lblName.setFont(new Font("Segoe UI", Font.BOLD, 15)); lblName.setForeground(TEXT_MAIN);

                JLabel lblBadge = new JLabel("● Đang làm"); lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 12)); lblBadge.setForeground(new Color(16, 185, 129));

                pnlTop.add(lblName, BorderLayout.WEST); pnlTop.add(lblBadge, BorderLayout.EAST);



                JPanel pnlMid = new JPanel(new GridLayout(2, 1));

                pnlMid.setOpaque(false); pnlMid.setBorder(new EmptyBorder(10, 0, 10, 0));

                JLabel lblStartTime = new JLabel("Vào ca: " + cc.getCheckIn().format(DateTimeFormatter.ofPattern("HH:mm:ss"))); lblStartTime.setFont(new Font("Segoe UI", Font.PLAIN, 13)); lblStartTime.setForeground(TEXT_MUTED);

                lblCurrentTime = new JLabel("Hiện tại: --:--:--"); lblCurrentTime.setFont(new Font("Segoe UI", Font.PLAIN, 13)); lblCurrentTime.setForeground(TEXT_MUTED);

                pnlMid.add(lblStartTime); pnlMid.add(lblCurrentTime);



                JPanel pnlBot = new JPanel(new BorderLayout());

                pnlBot.setOpaque(false);

                lblHours = new JLabel("0.00 giờ"); lblHours.setFont(new Font("Segoe UI", Font.BOLD, 14)); lblHours.setForeground(TEXT_MAIN);

                lblTempSalary = new JLabel("0 đ"); lblTempSalary.setFont(new Font("Segoe UI", Font.BOLD, 16)); lblTempSalary.setForeground(new Color(59, 130, 246));

                pnlBot.add(lblHours, BorderLayout.WEST); pnlBot.add(lblTempSalary, BorderLayout.EAST);



                add(pnlTop, BorderLayout.NORTH); add(pnlMid, BorderLayout.CENTER); add(pnlBot, BorderLayout.SOUTH);

                updateRealtimeData(); 

            }



            public void updateRealtimeData() {

                LocalDateTime now = LocalDateTime.now();

                lblCurrentTime.setText("Hiện tại: " + now.format(DateTimeFormatter.ofPattern("HH:mm:ss")));

                Duration duration = Duration.between(cc.getCheckIn(), now);

                double hours = Math.max(0, duration.toMinutes() / 60.0);

                lblHours.setText(String.format("Đã làm: %.2f giờ", hours));



                BigDecimal luongGio = nv.getLuongGio() != null ? nv.getLuongGio() : BigDecimal.ZERO;

                BigDecimal heSo = loaiCa.getHeSoLuong() != null ? loaiCa.getHeSoLuong() : BigDecimal.ONE;

                BigDecimal tempSalary = luongGio.multiply(new BigDecimal(String.valueOf(hours))).multiply(heSo).setScale(2, RoundingMode.HALF_UP);

                lblTempSalary.setText(String.format("Tạm tính: %,.0f đ", tempSalary));

            }



            @Override protected void paintComponent(Graphics g) {

                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(0, 0, 0, 8)); 

                g2.fillRoundRect(2, 4, getWidth()-4, getHeight()-6, 15, 15);

                g2.setColor(PANEL_BG); 

                g2.fillRoundRect(0, 0, getWidth()-2, getHeight()-4, 15, 15);

                g2.setColor(new Color(16, 185, 129));

                g2.fillRoundRect(0, 0, 6, getHeight()-4, 15, 15);

                g2.dispose(); super.paintComponent(g);

            }

        }



        private void initCompletedPanel() {

            completedPanel = new JPanel(new GridBagLayout());

            completedPanel.setOpaque(false);

            JLabel lblMsg = new JLabel("🎉 Ca làm này đã được chốt sổ hoàn tất!");

            lblMsg.setFont(new Font("Segoe UI", Font.BOLD, 22));

            lblMsg.setForeground(new Color(16, 185, 129));

            completedPanel.add(lblMsg);

        }

    }



    class WrapLayout extends FlowLayout {

        public WrapLayout(int align, int hgap, int vgap) { super(align, hgap, vgap); }

        @Override public Dimension preferredLayoutSize(Container target) { return layoutSize(target, true); }

        @Override public Dimension minimumLayoutSize(Container target) { return layoutSize(target, false); }

        private Dimension layoutSize(Container target, boolean preferred) {

            synchronized (target.getTreeLock()) {

                int targetWidth = target.getSize().width;

                if (targetWidth == 0) targetWidth = Integer.MAX_VALUE;

                int hgap = getHgap(), vgap = getVgap(); Insets insets = target.getInsets();

                int maxWidth = targetWidth - (insets.left + insets.right + hgap * 2);

                Dimension dim = new Dimension(0, 0); int rowWidth = 0, rowHeight = 0;

                for (int i = 0; i < target.getComponentCount(); i++) {

                    Component m = target.getComponent(i);

                    if (m.isVisible()) {

                        Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();

                        if (rowWidth + d.width > maxWidth) {

                            dim.width = Math.max(dim.width, rowWidth);

                            dim.height += rowHeight + vgap;

                            rowWidth = 0; rowHeight = 0;

                        }

                        rowWidth += d.width + hgap; rowHeight = Math.max(rowHeight, d.height);

                    }

                }

                dim.width = Math.max(dim.width, rowWidth); dim.height += rowHeight;

                dim.width += insets.left + insets.right + hgap * 2; dim.height += insets.top + insets.bottom + vgap * 2;

                return dim;

            }

        }

    }

    

    
    
    
    public static void main(String[] args) {

        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } 

        catch (Exception e) {}

        JFrame frame = new JFrame("Hệ thống Quản Lý Ca Làm Mới - Clean Architecture");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setSize(1200, 800);

        frame.setLocationRelativeTo(null);

        frame.add(new QuanLyCaLamUI());

        frame.setVisible(true);

    }

}
