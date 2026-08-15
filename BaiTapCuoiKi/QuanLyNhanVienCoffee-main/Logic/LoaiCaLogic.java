import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class LoaiCaLogic {
    private final LoaiCaDao dao = LoaiCaDao.getInstance();
    
    private static final LoaiCaLogic instance = new LoaiCaLogic();
    private LoaiCaLogic() {}
    public static LoaiCaLogic getInstance() { return instance; }

    public List<LoaiCaData> layDanhSach() {
        return dao.layDanhSach();
    }

    public LoaiCaData layTheoMa(String maLoaiCa) {
        if (maLoaiCa == null || maLoaiCa.trim().isEmpty()) return null;
        return dao.layTheoMa(maLoaiCa);
    }

    public void them(LoaiCaData lc) throws Exception {
        kiemTraHopLe(lc);

        if (dao.layTheoMa(lc.getMaLoaiCa()) != null) {
            throw new Exception("Mã loại ca '" + lc.getMaLoaiCa() + "' đã tồn tại trong hệ thống!");
        }

        if (!dao.them(lc)) {
            throw new Exception("Lỗi hệ thống: Thêm loại ca thất bại!");
        }
    }

    public void sua(LoaiCaData lc) throws Exception {
        if (dao.layTheoMa(lc.getMaLoaiCa()) == null) {
            throw new Exception("Không tìm thấy loại ca để cập nhật!");
        }
        
        kiemTraHopLe(lc);

        if (!dao.sua(lc)) {
            throw new Exception("Lỗi hệ thống: Cập nhật loại ca thất bại!");
        }
    }

    public void xoa(String maLoaiCa) throws Exception {
        if (maLoaiCa == null || maLoaiCa.trim().isEmpty()) {
            throw new Exception("Mã loại ca không được để trống!");
        }
        if (dao.layTheoMa(maLoaiCa) == null) {
            throw new Exception("Không tìm thấy loại ca có mã '" + maLoaiCa + "'!");
        }

        
        if (dao.kiemTraDangSuDung(maLoaiCa)) {
            throw new Exception("Không thể xóa! Đang có lịch Ca Làm thực tế sử dụng Loại Ca này.");
        }

        if (!dao.xoa(maLoaiCa)) {
            throw new Exception("Lỗi hệ thống: Xóa loại ca thất bại!");
        }
    }

    
    
    
    private void kiemTraHopLe(LoaiCaData lc) throws Exception {
        if (lc.getMaLoaiCa() == null || lc.getMaLoaiCa().trim().isEmpty()) {
            throw new Exception("Mã loại ca không được để trống!");
        }
        if (lc.getTenLoaiCa() == null || lc.getTenLoaiCa().trim().isEmpty()) {
            throw new Exception("Tên loại ca không được để trống!");
        }
        if (lc.getGioBatDau() == null) {
            throw new Exception("Vui lòng chọn giờ bắt đầu ca!");
        }
        if (lc.getGioKetThuc() == null) {
            throw new Exception("Vui lòng chọn giờ kết thúc ca!");
        }
        if (lc.getHeSoLuong() == null || lc.getHeSoLuong().compareTo(BigDecimal.ZERO) < 0) {
            throw new Exception("Hệ số lương không hợp lệ (phải >= 0)!");
        }

        
        
        long minutes = ChronoUnit.MINUTES.between(lc.getGioBatDau(), lc.getGioKetThuc());
        
        
        if (minutes < 0) {
            minutes += 24 * 60; 
        }
        
        double soGioThucTe = minutes / 60.0;
        
        
        soGioThucTe = Math.round(soGioThucTe * 100.0) / 100.0;
        
        if (soGioThucTe <= 0) {
            throw new Exception("Thời gian ca làm không hợp lệ!");
        }
        
        
        lc.setSoGio(soGioThucTe);
    }
}
