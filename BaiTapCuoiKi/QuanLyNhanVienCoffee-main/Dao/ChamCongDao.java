import java.sql.*;

public class ChamCongDao {
    private static final ChamCongDao instance = new ChamCongDao();
    private ChamCongDao() {}
    public static ChamCongDao getInstance() { return instance; }

    public boolean them(ChamCongData cc) {
        String sql = "INSERT INTO ChamCong (MaChamCong, MaPhanCong, CheckIn, CheckOut, SoGioLam, LuongCa) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cc.getMaChamCong());
            ps.setString(2, cc.getMaPhanCong());
            ps.setTimestamp(3, Timestamp.valueOf(cc.getCheckIn()));
            ps.setNull(4, Types.TIMESTAMP); // Lúc check-in chưa có checkout
            ps.setDouble(5, 0);
            ps.setBigDecimal(6, java.math.BigDecimal.ZERO);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { 
            e.printStackTrace(); 
            return false; 
        }
    }

    public boolean capNhatCheckOut(ChamCongData cc) {
        String sql = "UPDATE ChamCong SET CheckOut = ?, SoGioLam = ?, LuongCa = ? WHERE MaChamCong = ?";
        try (Connection con = ConnectDB.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(cc.getCheckOut()));
            ps.setDouble(2, cc.getSoGioLam()); // Số giờ vừa được cộng vào
            ps.setBigDecimal(3, cc.getLuongCa());
            ps.setString(4, cc.getMaChamCong());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { 
            e.printStackTrace(); 
            return false; 
        }
    }

    public ChamCongData layTheoPhanCong(String maPhanCong) {
        String sql = "SELECT * FROM ChamCong WHERE MaPhanCong = ?";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maPhanCong);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ChamCongData cc = new ChamCongData();
                    cc.setMaChamCong(rs.getString("MaChamCong"));
                    cc.setMaPhanCong(rs.getString("MaPhanCong"));
                    cc.setCheckIn(rs.getTimestamp("CheckIn").toLocalDateTime());
                    if (rs.getTimestamp("CheckOut") != null) {
                        cc.setCheckOut(rs.getTimestamp("CheckOut").toLocalDateTime());
                    }
                    cc.setSoGioLam(rs.getDouble("SoGioLam"));
                    cc.setLuongCa(rs.getBigDecimal("LuongCa"));
                    return cc;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
    // =======================================================
    // KIỂM TRA NHÂN VIÊN CÓ CA NÀO CHƯA CHECK-OUT KHÔNG
    // =======================================================
    public boolean kiemTraNhanVienChuaCheckOut(String maNV) {
        // Tìm xem có bản ghi Chấm Công nào mà CheckOut bị NULL của nhân viên này không
        String sql = "SELECT cc.MaChamCong FROM ChamCong cc " +
                     "JOIN PhanCongCa pc ON cc.MaPhanCong = pc.MaPhanCong " +
                     "WHERE pc.MaNV = ? AND cc.CheckOut IS NULL";
        
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            ps.setString(1, maNV);
            try (ResultSet rs = ps.executeQuery()) {
                // Nếu rs.next() là true -> Tồn tại ít nhất 1 ca chưa Check-out
                return rs.next(); 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}