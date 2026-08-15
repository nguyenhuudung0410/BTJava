import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class NhanVienLogic {

    private final NhanVienDao dao = NhanVienDao.getInstance();

    
    private static final NhanVienLogic instance = new NhanVienLogic();
    private NhanVienLogic() {}
    public static NhanVienLogic getInstance() { return instance; }

    
    
    
    public List<NhanVienData> layDanhSach() {
        return dao.layDanhSach();
    }

    
    
    
    public NhanVienData timTheoMa(String maNV) {
        if (maNV == null || maNV.trim().isEmpty()) return null;
        return dao.layTheoMa(maNV.trim());
    }

    
    
    
    public void them(NhanVienData nv) throws Exception {
        
        
        if (nv.getTrangThai() == null) {
            nv.setTrangThai(NhanVienData.TrangThaiNV.DANG_LAM);
        }

        
        if (nv.getLuongGio() == null || nv.getLuongGio().compareTo(BigDecimal.ZERO) <= 0) {
            nv.setLuongGio(new BigDecimal("20000"));
        }

        
        if (nv.getNgayVaoLam() == null) {
            nv.setNgayVaoLam(LocalDate.now());
        }

        
        kiemTraHopLe(nv);

        
        if (timTheoMa(nv.getMaNV()) != null) {
            throw new Exception("Mã nhân viên '" + nv.getMaNV() + "' đã tồn tại trong hệ thống!");
        }

        boolean ok = dao.them(nv);
        if (!ok) throw new Exception("Lỗi hệ thống: Thêm nhân viên thất bại!");
    }

    public void sua(NhanVienData nv) throws Exception {
        
        if (nv.getTrangThai() == NhanVienData.TrangThaiNV.NGHI_VIEC
                && nv.getNgayNghiViec() == null) {
            nv.setNgayNghiViec(LocalDate.now());
        }
        if (nv.getTrangThai() != NhanVienData.TrangThaiNV.NGHI_VIEC) {
            nv.setNgayNghiViec(null);
        }

        kiemTraHopLe(nv); 

        boolean ok = dao.sua(nv);
        if (!ok) throw new Exception("Lỗi hệ thống: Cập nhật thông tin thất bại!");
    }

    
    
    
    public void xoa(String maNV) throws Exception {
        if (maNV == null || maNV.trim().isEmpty()) {
            throw new Exception("Mã nhân viên không được để trống!");
        }
        if (timTheoMa(maNV) == null) {
            throw new Exception("Không tìm thấy nhân viên có mã '" + maNV + "'!");
        }

        boolean ok = dao.xoa(maNV);
        if (!ok) throw new Exception("Lỗi hệ thống: Xóa nhân viên thất bại!");
    }

    
    
    
    public int tinhThamNien(String maNV) {
        if (maNV == null || maNV.trim().isEmpty()) return 0;
        return dao.tinhThamNien(maNV);
    }

    
    
    
    public BigDecimal tinhLuongCuoiThang(
            NhanVienData nv,
            BigDecimal tongGioLam,
            BigDecimal tienThieuKet,
            BigDecimal tienPhat) {

        
        BigDecimal luongGio     = nv.getLuongGio()     != null ? nv.getLuongGio()     : BigDecimal.ZERO;
        BigDecimal luongThuong  = nv.getLuongThuong()  != null ? nv.getLuongThuong()  : BigDecimal.ZERO;
        if (tongGioLam  == null) tongGioLam  = BigDecimal.ZERO;
        if (tienThieuKet== null) tienThieuKet= BigDecimal.ZERO;
        if (tienPhat    == null) tienPhat    = BigDecimal.ZERO;

        
        int soNamThamNien = 0;
        if (nv.getNgayVaoLam() != null) {
            soNamThamNien = java.time.Period.between(nv.getNgayVaoLam(), LocalDate.now()).getYears();
        }

        
        BigDecimal luongCoBan = luongGio.multiply(tongGioLam);

        
        BigDecimal heSoThamNien   = new BigDecimal(soNamThamNien).multiply(new BigDecimal("0.10"));
        BigDecimal thuongThamNien = luongCoBan.multiply(heSoThamNien);

        
        BigDecimal tongThuNhap = luongCoBan.add(thuongThamNien).add(luongThuong);

        
        BigDecimal tongKhauTru = tienThieuKet.add(tienPhat);

        
        BigDecimal luongThucLanh = tongThuNhap.subtract(tongKhauTru);
        return luongThucLanh.compareTo(BigDecimal.ZERO) < 0
                ? BigDecimal.ZERO
                : luongThucLanh;
    }

    
    
    
    private void kiemTraHopLe(NhanVienData nv) throws Exception {

        
        if (nv.getMaNV() == null || nv.getMaNV().trim().isEmpty()) {
            throw new Exception("Mã nhân viên không được để trống!");
        }
        if (!nv.getMaNV().toUpperCase().startsWith("NV")) {
            throw new Exception("Mã nhân viên phải bắt đầu bằng 'NV' (Ví dụ: NV001)!");
        }

        
        if (nv.getHoTen() == null || nv.getHoTen().trim().length() < 2) {
            throw new Exception("Họ tên phải có ít nhất 2 ký tự!");
        }

        
        if (nv.getSDT() == null || !nv.getSDT().matches("^0\\d{9}$")) {
            throw new Exception("Số điện thoại không hợp lệ (10 số, bắt đầu bằng 0)!");
        }

        
        if (nv.getEmail() != null && !nv.getEmail().isEmpty()
                && !nv.getEmail().matches("^[\\w.+\\-]+@[\\w\\-]+\\.[a-z]{2,}$")) {
            throw new Exception("Email không đúng định dạng!");
        }

        
        if (nv.getLuongGio() != null && nv.getLuongGio().compareTo(BigDecimal.ZERO) < 0) {
            throw new Exception("Lương/giờ không thể là số âm!");
        }
        if (nv.getLuongThuong() != null && nv.getLuongThuong().compareTo(BigDecimal.ZERO) < 0) {
            throw new Exception("Lương thưởng không thể là số âm!");
        }

        
        if (nv.getNgayVaoLam() == null) {
            throw new Exception("Vui lòng chọn ngày vào làm!");
        }
        if (nv.getNgayVaoLam().isAfter(LocalDate.now())) {
            throw new Exception("Ngày vào làm không thể là ngày trong tương lai!");
        }

        
        if (nv.getNgayNghiViec() != null
                && nv.getNgayNghiViec().isBefore(nv.getNgayVaoLam())) {
            throw new Exception("Ngày nghỉ việc không thể trước ngày vào làm!");
        }

        
        if (nv.getTrangThai() == NhanVienData.TrangThaiNV.NGHI_VIEC
                && nv.getNgayNghiViec() == null) {
            throw new Exception("Nhân viên 'Nghỉ việc' phải có ngày nghỉ việc!");
        }
        if (nv.getTrangThai() == NhanVienData.TrangThaiNV.DANG_LAM
                && nv.getNgayNghiViec() != null) {
            throw new Exception("Nhân viên 'Đang làm' không được có ngày nghỉ việc!");
        }

        
        if (nv.getChucVu() == null) {
            throw new Exception("Vui lòng chọn chức vụ cho nhân viên!");
        }
    }
}
