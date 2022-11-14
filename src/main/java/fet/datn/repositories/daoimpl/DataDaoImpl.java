package fet.datn.repositories.daoimpl;

import fet.datn.repositories.DataDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
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
    public Map<String, Integer> reportCustomer() {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        Map<String, Integer> data = new HashMap<>();

        try {
            String sql = "SELECT DATE(created_time) date,COUNT(ordinal_number) count FROM `ORDINAL_NUMBERS` GROUP BY DATE(created_time)";
            con = dataSource.getConnection();
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                data.put(rs.getString("date"), rs.getInt("count"));
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
