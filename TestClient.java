import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TestClient {
    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        
        String jsonBody = "{\"patientName\":\"Nguyen Van Test\",\"cccd\":\"123456789012\",\"maKhoa\":\"KNT\",\"maBacSi\":\"NV-DOC01\",\"ngayKham\":\"2026-10-10\",\"gioKham\":\"08:30\",\"lyDoKham\":\"Test\",\"loaiKham\":\"KhamThuong\",\"hinhThucDat\":\"Online\"}";
        
        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8083/api/v1/lichhen"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build();
            
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        System.out.println("Status: " + res.statusCode());
        System.out.println("Body: " + res.body());
    }
}
