
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDao {

    private static final NhanVienDao instance = new NhanVienDao();
    private NhanVienDao() {}
    public static NhanVienDao getInstance() { return instance; }

    // ==============================
    // HELPER: MAP RESULTSET → OBJECT
    // ==============================
    private NhanVienData mapResultSet(ResultSet rs) throws SQLException {
        Date vaoLamSQL = rs.getDate("NgayVaoLam");
        Date nghiSQL   = rs.getDate("NgayNghiViec");

        NhanVienData.ChucVuNV chucVu = null;
        try {
            chucVu = NhanVienData.ChucVuNV.valueOf(rs.getString("ChucVu"));
        } catch (IllegalArgumentException | NullPointerException e) {
            System.err.println("[mapResultSet] ChucVu không hợp lệ: " + rs.getString("ChucVu"));
        }

        NhanVienData.TrangThaiNV trangThai = null;
        try {
            trangThai = NhanVienData.TrangThaiNV.valueOf(rs.getString("TrangThai"));
        } catch (IllegalArgumentException | NullPointerException e) {
            System.err.println("[mapResultSet] TrangThai không hợp lệ: " + rs.getString("TrangThai"));
        }

        return new NhanVienData.ThoXayNhanVien()
                .ganMaNV        (rs.getString("MaNV"))
                .ganHoTen       (rs.getString("HoTen"))
                .ganSDT         (rs.getString("SDT"))
                .ganEmail       (rs.getString("Email"))
                .ganCccd        (rs.getString("CCCD"))
                .ganGioiTinh    (rs.getString("GioiTinh"))
                .ganDiaChi      (rs.getString("DiaChi"))
                .ganNgaySinh    (rs.getDate("NgaySinh") != null ? rs.getDate("NgaySinh").toLocalDate() : null)
                .ganChucVu      (chucVu)
                .ganTrangThai   (trangThai)
                .ganLuongGio    (rs.getBigDecimal("LuongGio"))
                .ganLuongThuong (rs.getBigDecimal("LuongThuong"))
                .ganNgayVaoLam  (vaoLamSQL != null ? vaoLamSQL.toLocalDate() : null)
                .ganNgayNghiViec(nghiSQL   != null ? nghiSQL.toLocalDate()   : null)
                .taoMoi();
    }

    // ==============================
    // HELPER: enum → String lưu DB
    // ==============================
    private String chucVuToString(NhanVienData.ChucVuNV chucVu) {
        return chucVu != null ? chucVu.name() : null;
    }

    private String trangThaiToString(NhanVienData.TrangThaiNV trangThai) {
        return trangThai != null ? trangThai.name() : null;
    }

    // ==============================
    // 1. LẤY DANH SÁCH
    // ==============================
    public List<NhanVienData> layDanhSach() {
        List<NhanVienData> list = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien";

        Connection con = ConnectDB.getInstance().getConnection();
        if (con == null) return list;

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            logError("layDanhSach", e);
        }

        return list;
    }

    // ==============================
    // 2. LẤY THEO MÃ
    // ==============================
    public NhanVienData layTheoMa(String maNV) {
        String sql = "SELECT * FROM NhanVien WHERE MaNV = ?";

        Connection con = ConnectDB.getInstance().getConnection();
        if (con == null) return null;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maNV);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSet(rs);
            }
        } catch (SQLException e) {
            logError("layTheoMa", e);
        }

        return null;
    }

    // ==============================
    // 3. THÊM NHÂN VIÊN
    // ==============================
    public boolean them(NhanVienData nv) {
        String sql = "INSERT INTO NhanVien "
                   + "(MaNV, HoTen, SDT, Email, CCCD, GioiTinh, DiaChi, NgaySinh, "
                   + "ChucVu, TrangThai, LuongGio, LuongThuong, NgayVaoLam, NgayNghiViec) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection con = ConnectDB.getInstance().getConnection();
        if (con == null) return false;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString    (1,  nv.getMaNV());
            ps.setString    (2,  nv.getHoTen());
            ps.setString    (3,  nv.getSDT());
            ps.setString    (4,  nv.getEmail());
            ps.setString    (5,  nv.getCccd());
            ps.setString    (6,  nv.getGioiTinh());
            ps.setString    (7,  nv.getDiaChi());
            ps.setDate      (8,  nv.getNgaySinh() != null
                    ? Date.valueOf(nv.getNgaySinh()) : null);
            ps.setString    (9,  chucVuToString(nv.getChucVu()));
            ps.setString    (10, trangThaiToString(nv.getTrangThai()));
            ps.setBigDecimal(11, nv.getLuongGio());
            if (nv.getLuongThuong() != null)
                ps.setBigDecimal(12, nv.getLuongThuong());
            else
                ps.setNull(12, Types.DECIMAL);
            ps.setDate(13, nv.getNgayVaoLam() != null
                    ? Date.valueOf(nv.getNgayVaoLam())
                    : Date.valueOf(LocalDate.now()));
            ps.setNull(14, Types.DATE);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logError("them", e);
        }
        return false;
    }

    // ==============================
    // 4. SỬA NHÂN VIÊN
    // ==============================
    public boolean sua(NhanVienData nv) {
        String sql = "UPDATE NhanVien SET "
                   + "HoTen=?, SDT=?, Email=?, CCCD=?, GioiTinh=?, DiaChi=?, NgaySinh=?, "
                   + "ChucVu=?, TrangThai=?, LuongGio=?, LuongThuong=?, NgayVaoLam=?, NgayNghiViec=? "
                   + "WHERE MaNV=?";

        Connection con = ConnectDB.getInstance().getConnection();
        if (con == null) return false;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString    (1,  nv.getHoTen());
            ps.setString    (2,  nv.getSDT());
            ps.setString    (3,  nv.getEmail());
            ps.setString    (4,  nv.getCccd());
            ps.setString    (5,  nv.getGioiTinh());
            ps.setString    (6,  nv.getDiaChi());
            ps.setDate      (7,  nv.getNgaySinh() != null
                    ? Date.valueOf(nv.getNgaySinh()) : null);
            ps.setString    (8,  chucVuToString(nv.getChucVu()));
            ps.setString    (9,  trangThaiToString(nv.getTrangThai()));
            ps.setBigDecimal(10, nv.getLuongGio());
            if (nv.getLuongThuong() != null)
                ps.setBigDecimal(11, nv.getLuongThuong());
            else
                ps.setNull(11, Types.DECIMAL);
            ps.setDate(12, nv.getNgayVaoLam() != null
                    ? Date.valueOf(nv.getNgayVaoLam())
                    : Date.valueOf(LocalDate.now()));
            if (nv.getTrangThai() == NhanVienData.TrangThaiNV.NGHI_VIEC)
                ps.setDate(13, Date.valueOf(LocalDate.now()));
            else
                ps.setNull(13, Types.DATE);
            ps.setString(14, nv.getMaNV());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logError("sua", e);
        }
        return false;
    }

 // ==============================
    // 5. XÓA NHÂN VIÊN (Xóa mềm / Soft Delete)
    // ==============================
    public boolean xoa(String maNV) {
        // Thay vì DELETE, ta cập nhật trạng thái thành NGHI_VIEC và chốt ngày nghỉ là hôm nay
        String sql = "UPDATE NhanVien SET TrangThai = ?, NgayNghiViec = ? WHERE MaNV = ?";

        Connection con = ConnectDB.getInstance().getConnection();
        if (con == null) return false;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, NhanVienData.TrangThaiNV.NGHI_VIEC.name());
            ps.setDate(2, Date.valueOf(LocalDate.now())); 
            ps.setString(3, maNV);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logError("xoa (soft delete)", e);
        }
        return false;
    }
    // ==============================
    // 7. TÍNH THÂM NIÊN (SQL Server)
    // ==============================
    public int tinhThamNien(String maNV) {
        String sql = "SELECT "
                   + "DATEDIFF(YEAR, NgayVaoLam, GETDATE()) "
                   + "- CASE "
                   + "    WHEN MONTH(GETDATE()) < MONTH(NgayVaoLam) "
                   + "      OR (MONTH(GETDATE()) = MONTH(NgayVaoLam) AND DAY(GETDATE()) < DAY(NgayVaoLam)) "
                   + "    THEN 1 ELSE 0 "
                   + "  END "
                   + "FROM NhanVien WHERE MaNV = ?";

        Connection con = ConnectDB.getInstance().getConnection();
        if (con == null) return 0;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maNV);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            logError("tinhThamNien", e);
        }

        return 0;
    }

    // ==============================
    // LOG LỖI
    // ==============================
    private void logError(String method, Exception e) {
        System.err.println("[NhanVienDao - " + method + "] ERROR: " + e.getMessage());
    }
}