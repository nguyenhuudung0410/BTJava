import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoaiCaDao {
    private static final LoaiCaDao instance = new LoaiCaDao();
    private LoaiCaDao() {}
    public static LoaiCaDao getInstance() { return instance; }

    private LoaiCaData mapResultSet(ResultSet rs) throws SQLException {
        LoaiCaData lc = new LoaiCaData();
        lc.setMaLoaiCa(rs.getString("MaLoaiCa"));
        lc.setTenLoaiCa(rs.getString("TenLoaiCa"));
        lc.setGioBatDau(rs.getTime("GioBatDau").toLocalTime());
        lc.setGioKetThuc(rs.getTime("GioKetThuc").toLocalTime());
        lc.setSoGio(rs.getDouble("SoGio"));
        lc.setHeSoLuong(rs.getBigDecimal("HeSoLuong"));
        return lc;
    }

    public List<LoaiCaData> layDanhSach() {
        List<LoaiCaData> list = new ArrayList<>();
        String sql = "SELECT * FROM LoaiCa ORDER BY MaLoaiCa ASC";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public LoaiCaData layTheoMa(String maLoaiCa) {
        String sql = "SELECT * FROM LoaiCa WHERE MaLoaiCa = ?";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maLoaiCa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSet(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean them(LoaiCaData lc) {
        String sql = "INSERT INTO LoaiCa (MaLoaiCa, TenLoaiCa, GioBatDau, GioKetThuc, SoGio, HeSoLuong) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, lc.getMaLoaiCa());
            ps.setString(2, lc.getTenLoaiCa());
            ps.setTime(3, Time.valueOf(lc.getGioBatDau()));
            ps.setTime(4, Time.valueOf(lc.getGioKetThuc()));
            ps.setDouble(5, lc.getSoGio());
            ps.setBigDecimal(6, lc.getHeSoLuong());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { 
            e.printStackTrace(); 
            return false; 
        }
    }

    public boolean sua(LoaiCaData lc) {
        String sql = "UPDATE LoaiCa SET TenLoaiCa = ?, GioBatDau = ?, GioKetThuc = ?, SoGio = ?, HeSoLuong = ? WHERE MaLoaiCa = ?";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, lc.getTenLoaiCa());
            ps.setTime(2, Time.valueOf(lc.getGioBatDau()));
            ps.setTime(3, Time.valueOf(lc.getGioKetThuc()));
            ps.setDouble(4, lc.getSoGio());
            ps.setBigDecimal(5, lc.getHeSoLuong());
            ps.setString(6, lc.getMaLoaiCa());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { 
            e.printStackTrace(); 
            return false; 
        }
    }

    public boolean xoa(String maLoaiCa) {
        String sql = "DELETE FROM LoaiCa WHERE MaLoaiCa = ?";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maLoaiCa);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { 
            e.printStackTrace(); 
            return false; 
        }
    }

    // Hàm phụ trợ: Kiểm tra khóa ngoại trước khi xóa
    public boolean kiemTraDangSuDung(String maLoaiCa) {
        String sql = "SELECT COUNT(*) FROM CaLam WHERE MaLoaiCa = ?";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maLoaiCa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Trả về true nếu đang có Ca Làm dùng Loại Ca này
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}