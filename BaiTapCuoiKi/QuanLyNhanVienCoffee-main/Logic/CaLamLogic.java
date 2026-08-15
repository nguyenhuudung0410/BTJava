import java.time.LocalDateTime;
import java.util.List;

public class CaLamLogic {
    private final CaLamDao caDao = CaLamDao.getInstance();
    private final ChamCongDao ccDao = ChamCongDao.getInstance();
    private final PhanCongCaDao pcDao = PhanCongCaDao.getInstance(); 
    
    private static final CaLamLogic instance = new CaLamLogic();
    private CaLamLogic() {}
    public static CaLamLogic getInstance() { return instance; }

    // ================= QUẢN LÝ CA LÀM =================
    public void themCa(CaLamData ca) throws Exception {
        if (ca.getMaCa() == null || ca.getMaCa().trim().isEmpty()) throw new Exception("Mã ca không được để trống!");
        if (ca.getNgayLam() == null) throw new Exception("Ngày làm không được để trống!");
        if (caDao.layTheoMa(ca.getMaCa()) != null) throw new Exception("Mã ca đã tồn tại!");
        
        LoaiCaData loaiCa = caDao.layLoaiCaTheoMa(ca.getMaLoaiCa());
        if (loaiCa == null) throw new Exception("Loại ca không tồn tại trong hệ thống!");

        if (!caDao.them(ca)) throw new Exception("Lỗi hệ thống: Thêm ca làm thất bại!");
    }

    public void suaCa(CaLamData ca) throws Exception {
        // 1. Kiểm tra ca tồn tại
        CaLamData caHienTai = caDao.layTheoMa(ca.getMaCa());
        if (caHienTai == null) throw new Exception("Không tìm thấy ca làm để sửa!");
        
        // 2. Kiểm tra ràng buộc thời gian: Không cho sửa nếu ca đang làm hoặc đã xong
        kiemTraCaChuaDienRa(caHienTai, "cập nhật");

        if (ca.getNgayLam() == null) throw new Exception("Ngày làm không được để trống!");
        
        if (!caDao.sua(ca)) throw new Exception("Lỗi hệ thống: Cập nhật ca làm thất bại!");
    }

    public void xoaCa(String maCa) throws Exception {
        CaLamData caHienTai = caDao.layTheoMa(maCa);
        if (caHienTai == null) throw new Exception("Ca làm không tồn tại!");

        // 1. Kiểm tra ràng buộc thời gian: Không cho xóa nếu ca đang làm hoặc đã xong
        kiemTraCaChuaDienRa(caHienTai, "xóa");

        // 2. Kiểm tra ràng buộc nhân sự
        List<PhanCongCaData> listPhanCong = pcDao.layDanhSachTheoCa(maCa);
        if (!listPhanCong.isEmpty()) {
            for (PhanCongCaData pc : listPhanCong) {
                if (ccDao.layTheoPhanCong(pc.getMaPhanCong()) != null) {
                    throw new Exception("Không thể xóa! Ca làm này đã có dữ liệu chấm công thực tế.");
                }
            }
            throw new Exception("Vui lòng gỡ tất cả nhân viên khỏi ca trước khi xóa!");
        }

        if (!caDao.xoa(maCa)) throw new Exception("Lỗi hệ thống: Xóa ca làm thất bại!");
    }

    /**
     * Helper: Kiểm tra xem ca làm đã bắt đầu hay chưa.
     * Nếu đã đến giờ bắt đầu hoặc đã qua, sẽ quăng Exception chặn thao tác.
     */
    private void kiemTraCaChuaDienRa(CaLamData ca, String thaoTac) throws Exception {
        LoaiCaData lc = caDao.layLoaiCaTheoMa(ca.getMaLoaiCa());
        if (lc != null && ca.getNgayLam() != null) {
            // Kết hợp Ngày làm + Giờ bắt đầu để so sánh với thời gian thực
            LocalDateTime thoiDiemBatDau = LocalDateTime.of(ca.getNgayLam(), lc.getGioBatDau());
            LocalDateTime bayGio = LocalDateTime.now();

            if (bayGio.isAfter(thoiDiemBatDau) || bayGio.isEqual(thoiDiemBatDau)) {
                throw new Exception("Từ chối " + thaoTac + ": Ca làm này đang diễn ra hoặc đã hoàn thành!");
            }
        }
    }

    // ================= PHÂN CÔNG NHÂN VIÊN =================
    public void phanCongNhanVien(String maCa, String maNV) throws Exception {
        CaLamData caLam = caDao.layTheoMa(maCa);
        if (caLam == null) throw new Exception("Ca làm không tồn tại!");
        
        // Chặn phân công thêm người khi ca đã bắt đầu
        kiemTraCaChuaDienRa(caLam, "phân công nhân viên");

        if (NhanVienDao.getInstance().layTheoMa(maNV) == null) throw new Exception("Nhân viên không tồn tại!");

        List<PhanCongCaData> dspc = pcDao.layDanhSachTheoCa(maCa);
        for (PhanCongCaData pc : dspc) {
            if (pc.getMaNV().equals(maNV)) throw new Exception("Nhân viên này đã có trong ca!");
        }

        LoaiCaData lc = caDao.layLoaiCaTheoMa(caLam.getMaLoaiCa()); 
        if (pcDao.kiemTraTrungGioLam(maNV, caLam.getNgayLam(), lc.getGioBatDau(), lc.getGioKetThuc())) { 
            throw new Exception("Nhân viên bị trùng giờ làm khác trong ngày " + caLam.getNgayLam());
        }

        String maPhanCong = "PC_" + System.currentTimeMillis(); 
        PhanCongCaData pc = new PhanCongCaData(maPhanCong, maCa, maNV);
        if (!pcDao.them(pc)) throw new Exception("Lỗi hệ thống: Phân công thất bại!");
    }
    
    public void xoaPhanCong(String maPhanCong) throws Exception {
        PhanCongCaData pc = pcDao.layTheoMa(maPhanCong);
        if (pc == null) return;

        CaLamData caLam = caDao.layTheoMa(pc.getMaCa());
        // Chặn gỡ người khi ca đã bắt đầu
        kiemTraCaChuaDienRa(caLam, "gỡ nhân viên");

        if (ccDao.layTheoPhanCong(maPhanCong) != null) {
            throw new Exception("Nhân viên đã chấm công, không thể gỡ!");
        }
        if (!pcDao.xoa(maPhanCong)) throw new Exception("Lỗi hệ thống: Gỡ thất bại!");
    }
}