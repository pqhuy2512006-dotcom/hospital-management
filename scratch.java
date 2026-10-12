
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nhom12.hospital.entity.LichHen;
import java.time.LocalDate;
import java.time.LocalTime;
public class scratch {
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        LichHen lh = new LichHen();
        lh.setNgayKham(LocalDate.now());
        lh.setGioKham(LocalTime.now());
        System.out.println(mapper.writeValueAsString(lh));
    }
}
