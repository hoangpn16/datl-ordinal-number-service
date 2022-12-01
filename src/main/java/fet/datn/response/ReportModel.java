package fet.datn.response;

import lombok.Data;

@Data
public class ReportModel {
    private String date;
    private Integer total;

    public ReportModel() {
    }

    public ReportModel(String date, Integer total) {
        this.date = date;
        this.total = total;
    }
}
