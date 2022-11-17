package fet.datn.repositories.daoimpl;

import fet.datn.repositories.DataDao;
import fet.datn.response.ReportModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DataDaoImpl implements DataDao {
    private static final Logger logger = LoggerFactory.getLogger(DataDaoImpl.class);

    @Autowired
    private DataSource dataSource;

    public void closeCon(Connection con) {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeSt(Statement st) {
        try {
            if (st != null && !st.isClosed()) {
                st.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closePst(PreparedStatement pst) {
        try {
            if (pst != null && !pst.isClosed()) {
                pst.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeRs(ResultSet rs) {
        try {
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public List<ReportModel> reportCustomer(String from, String to) {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<ReportModel> data = new ArrayList<>();

        try {
            String sql = "SELECT DATE(created_time) date,COUNT(ordinal_number) count FROM `ORDINAL_NUMBERS` WHERE DATE(created_time) BETWEEN ? AND ? GROUP BY DATE(created_time)";
            con = dataSource.getConnection();
            pst = con.prepareStatement(sql);
            pst.setString(1, from);
            pst.setString(2, to);
            rs = pst.executeQuery();
            while (rs.next()) {
                ReportModel entity = new ReportModel();
                entity.setDate(rs.getString("date"));
                entity.setTotal(rs.getInt("count"));

                data.add(entity);
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            closeCon(con);
            closePst(pst);
            closeRs(rs);
        }
        return data;
    }
}
