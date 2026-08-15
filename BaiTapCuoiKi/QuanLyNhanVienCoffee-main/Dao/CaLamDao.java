import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CaLamDao {
    private static final CaLamDao instance = new CaLamDao();
    private CaLamDao() {}
    public static CaLamDao getInstance() { return instance; }

    private CaLamData mapResultSet(ResultSet rs) throws SQLException {
        CaLamData ca = new CaLamData();
        ca.setMaCa(rs.getString("MaCa"));
        ca.setMaLoaiCa(rs.getString("MaLoaiCa"));
        ca.setNgayLam(rs.getDate("NgayLam") != null ? rs.getDate("NgayLam").toLocalDate() : null);
        ca.setGhiChu(rs.getString("GhiChu"));
        return ca;
    }

    public List<CaLamData> layDanhSach() {
        List<CaLamData> list = new ArrayList<>();
        String sql = "SELECT * FROM CaLam ORDER BY NgayLam DESC";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public CaLamData layTheoMa(String maCa) {
        String sql = "SELECT * FROM CaLam WHERE MaCa = ?";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maCa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSet(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean them(CaLamData ca) {
        String sql = "INSERT INTO CaLam (MaCa, MaLoaiCa, NgayLam, GhiChu) VALUES (?, ?, ?, ?)";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ca.getMaCa());
            ps.setString(2, ca.getMaLoaiCa());
            ps.setDate(3, Date.valueOf(ca.getNgayLam()));
            ps.setString(4, ca.getGhiChu());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean sua(CaLamData ca) {
        String sql = "UPDATE CaLam SET MaLoaiCa = ?, NgayLam = ?, GhiChu = ? WHERE MaCa = ?";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ca.getMaLoaiCa());
            ps.setDate(2, Date.valueOf(ca.getNgayLam()));
            ps.setString(3, ca.getGhiChu());
            ps.setString(4, ca.getMaCa());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean xoa(String maCa) {
        String sql = "DELETE FROM CaLam WHERE MaCa = ?";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maCa);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // --- LẤY LOẠI CA ---
    public LoaiCaData layLoaiCaTheoMa(String maLoaiCa) {
        String sql = "SELECT * FROM LoaiCa WHERE MaLoaiCa = ?";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maLoaiCa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LoaiCaData lc = new LoaiCaData();
                    lc.setMaLoaiCa(rs.getString("MaLoaiCa"));
                    lc.setTenLoaiCa(rs.getString("TenLoaiCa"));
                    lc.setGioBatDau(rs.getTime("GioBatDau").toLocalTime());
                    lc.setGioKetThuc(rs.getTime("GioKetThuc").toLocalTime());
                    lc.setSoGio(rs.getDouble("SoGio"));
                    lc.setHeSoLuong(rs.getBigDecimal("HeSoLuong"));
                    return lc;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
}