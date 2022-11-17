package fet.datn.repositories;

import fet.datn.response.ReportModel;

import java.util.List;
import java.util.Map;

public interface DataDao {
    List<ReportModel> reportCustomer(String from, String to);
}
