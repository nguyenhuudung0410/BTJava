
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaiKhoanDao {

    private static final TaiKhoanDao instance = new TaiKhoanDao();
    private TaiKhoanDao() {}
    public static TaiKhoanDao getInstance() { return instance; }

    private TaiKhoanData mapResultSet(ResultSet rs) throws SQLException {
        Timestamp ngayTaoTS = rs.getTimestamp("NgayTao");
        
        return new TaiKhoanData.ThoXayTaiKhoan()
                .ganTenDangNhap(rs.getString("TenDangNhap"))
                .ganMatKhau(rs.getString("MatKhau"))
                .ganMaNV(rs.getString("MaNV"))
                .ganQuyen(TaiKhoanData.Quyen.valueOf(rs.getString("Quyen")))
                .ganNgayTao(ngayTaoTS != null ? ngayTaoTS.toLocalDateTime() : null)
                .taoMoi();
    }

    public List<TaiKhoanData> layDanhSach() {
        List<TaiKhoanData> list = new ArrayList<>();
        String sql = "SELECT * FROM TaiKhoan";
        Connection con = ConnectDB.getInstance().getConnection();
        if (con == null) return list;

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[TaiKhoanDao - layDanhSach] " + e.getMessage());
        }
        return list;
    }

    public TaiKhoanData layTheoTen(String ten) {
        String sql = "SELECT * FROM TaiKhoan WHERE TenDangNhap = ?";
        Connection con = ConnectDB.getInstance().getConnection();
        if (con == null) return null;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ten);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("[TaiKhoanDao - layTheoTen] " + e.getMessage());
        }
        return null;
    }

    public boolean them(TaiKhoanData tk) {
        String sql = "INSERT INTO TaiKhoan (TenDangNhap, MatKhau, MaNV, Quyen, NgayTao) VALUES (?, ?, ?, ?, ?)";
        Connection con = ConnectDB.getInstance().getConnection();
        if (con == null) return false;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tk.getTenDangNhap());
            ps.setString(2, tk.getMatKhau());
            ps.setString(3, tk.getMaNV());
            ps.setString(4, tk.getQuyen().name());
            ps.setTimestamp(5, Timestamp.valueOf(java.time.LocalDateTime.now()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[TaiKhoanDao - them] " + e.getMessage());
        }
        return false;
    }

    public boolean doiMatKhau(String ten, String matKhauMoi) {
        String sql = "UPDATE TaiKhoan SET MatKhau = ? WHERE TenDangNhap = ?";
        Connection con = ConnectDB.getInstance().getConnection();
        if (con == null) return false;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, matKhauMoi);
            ps.setString(2, ten);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[TaiKhoanDao - doiMatKhau] " + e.getMessage());
        }
        return false;
    }

    public boolean xoa(String ten) {
        String sql = "DELETE FROM TaiKhoan WHERE TenDangNhap = ?";
        Connection con = ConnectDB.getInstance().getConnection();
        if (con == null) return false;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ten);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[TaiKhoanDao - xoa] " + e.getMessage());
        }
        return false;
    }
}