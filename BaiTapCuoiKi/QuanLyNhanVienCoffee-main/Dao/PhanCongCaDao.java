import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class PhanCongCaDao {
    private static final PhanCongCaDao instance = new PhanCongCaDao();
    private PhanCongCaDao() {}
    public static PhanCongCaDao getInstance() { return instance; }

    public boolean them(PhanCongCaData pc) {
        String sql = "INSERT INTO PhanCongCa (MaPhanCong, MaCa, MaNV) VALUES (?, ?, ?)";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, pc.getMaPhanCong());
            ps.setString(2, pc.getMaCa());
            ps.setString(3, pc.getMaNV());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { 
            e.printStackTrace(); 
            return false; 
        }
    }

    public boolean xoa(String maPhanCong) {
        String sql = "DELETE FROM PhanCongCa WHERE MaPhanCong = ?";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maPhanCong);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { 
            e.printStackTrace(); 
            return false; 
        }
    }

    public PhanCongCaData layTheoMa(String maPhanCong) {
        String sql = "SELECT * FROM PhanCongCa WHERE MaPhanCong = ?";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maPhanCong);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new PhanCongCaData(rs.getString("MaPhanCong"), rs.getString("MaCa"), rs.getString("MaNV"));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<PhanCongCaData> layDanhSachTheoCa(String maCa) {
        List<PhanCongCaData> list = new ArrayList<>();
        String sql = "SELECT * FROM PhanCongCa WHERE MaCa = ?";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maCa);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new PhanCongCaData(rs.getString("MaPhanCong"), rs.getString("MaCa"), rs.getString("MaNV")));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // TỐI ƯU: Truy vấn kiểm tra nhân viên có bị xếp ca trùng giờ trong cùng 1 ngày không
    public boolean kiemTraTrungGioLam(String maNV, LocalDate ngayLam, LocalTime gioBatDauMoi, LocalTime gioKetThucMoi) {
        String sql = "SELECT lc.GioBatDau, lc.GioKetThuc FROM PhanCongCa pc " +
                     "JOIN CaLam c ON pc.MaCa = c.MaCa " +
                     "JOIN LoaiCa lc ON c.MaLoaiCa = lc.MaLoaiCa " +
                     "WHERE pc.MaNV = ? AND c.NgayLam = ?";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maNV);
            ps.setDate(2, Date.valueOf(ngayLam));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalTime startExist = rs.getTime("GioBatDau").toLocalTime();
                    LocalTime endExist = rs.getTime("GioKetThuc").toLocalTime();
                    
                    // Logic check Overlap thời gian: (Start A < End B) AND (Start B < End A)
                    if (gioBatDauMoi.isBefore(endExist) && startExist.isBefore(gioKetThucMoi)) {
                        return true; // Bị lặp / trùng giờ
                    }
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}