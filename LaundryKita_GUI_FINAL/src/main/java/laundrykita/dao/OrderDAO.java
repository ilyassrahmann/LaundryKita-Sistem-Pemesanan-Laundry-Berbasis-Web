package laundrykita.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import laundrykita.config.DatabaseConfig;
import laundrykita.model.DashboardStats;
import laundrykita.model.Order;
import laundrykita.model.OrderDetail;
import laundrykita.model.OrderStatus;
import laundrykita.model.PaymentMethod;
import laundrykita.model.PaymentStatus;

public class OrderDAO {
    public List<Order> search(String keyword, OrderStatus status) {
        String sql = "SELECT o.id_order, o.id_customer, c.name_customer, o.id_admin, o.order_date, o.weight, o.total_price, o.status, o.destination, o.method_payment, o.status_payment, o.notes, o.estimated_date FROM orders o JOIN customer c ON c.id_customer = o.id_customer WHERE (? = '' OR o.status = ?) AND (c.name_customer LIKE CONCAT('%', ?, '%') OR CONCAT('LK', LPAD(o.id_order, 3, '0')) LIKE CONCAT('%', ?, '%')) ORDER BY o.id_order DESC";
        String statusLabel = status == null ? "" : status.getLabel();
        String kw = keyword == null ? "" : keyword.trim();
        return this.query(sql, ps -> {
            ps.setString(1, statusLabel);
            ps.setString(2, statusLabel);
            ps.setString(3, kw);
            ps.setString(4, kw);
        });
    }

    public List<Order> findLatest(int limit) {
        return this.query("SELECT o.id_order, o.id_customer, c.name_customer, o.id_admin, o.order_date, o.weight, o.total_price, o.status, o.destination, o.method_payment, o.status_payment, o.notes, o.estimated_date FROM orders o JOIN customer c ON c.id_customer = o.id_customer ORDER BY o.id_order DESC LIMIT ?", ps -> ps.setInt(1, limit));
    }

    public List<Order> findByCustomerId(int customerId) {
        return this.query("SELECT o.id_order, o.id_customer, c.name_customer, o.id_admin, o.order_date, o.weight, o.total_price, o.status, o.destination, o.method_payment, o.status_payment, o.notes, o.estimated_date FROM orders o JOIN customer c ON c.id_customer = o.id_customer WHERE o.id_customer = ? ORDER BY o.id_order DESC", ps -> ps.setInt(1, customerId));
    }

    public Order findById(int orderId) {
        List<Order> list = this.query("SELECT o.id_order, o.id_customer, c.name_customer, o.id_admin, o.order_date, o.weight, o.total_price, o.status, o.destination, o.method_payment, o.status_payment, o.notes, o.estimated_date FROM orders o JOIN customer c ON c.id_customer = o.id_customer WHERE o.id_order = ?", ps -> ps.setInt(1, orderId));
        return list.isEmpty() ? null : list.get(0);
    }

    public DashboardStats getStatistics() {
        String sql = "SELECT COUNT(*) AS total, "
                + "COALESCE(SUM(CASE WHEN status = 'Menunggu' THEN 1 ELSE 0 END), 0) AS waiting, "
                + "COALESCE(SUM(CASE WHEN status IN ('Diproses','Dicuci','Disetrika') THEN 1 ELSE 0 END), 0) AS in_progress "
                + "FROM orders";
        String customerSql = "SELECT COUNT(*) FROM customer";
        try (Connection con = DatabaseConfig.getConnection()) {
            int total;
            int waiting;
            int inProgress;
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                rs.next();
                total = rs.getInt("total");
                waiting = rs.getInt("waiting");
                inProgress = rs.getInt("in_progress");
            }
            int customers;
            try (PreparedStatement ps = con.prepareStatement(customerSql);
                 ResultSet rs = ps.executeQuery()) {
                rs.next();
                customers = rs.getInt(1);
            }
            return new DashboardStats(total, waiting, inProgress, customers);
        } catch (SQLException e) {
            throw new DataAccessException("Gagal membaca statistik dashboard", e);
        }
    }

    public int insert(Order order, List<OrderDetail> details) {
        String orderSql = "INSERT INTO orders (id_customer, id_admin, order_date, weight, total_price, status, destination, method_payment, status_payment, notes, estimated_date) VALUES (?,?,CURDATE(),?,?,?,?,?,?,?,?)";
        String detailSql = "INSERT INTO order_detail (id_order, id_service, weight_kg, price_per_kg, subtotal) VALUES (?,?,?,?,?)";
        try (Connection con = DatabaseConfig.getConnection();){
            con.setAutoCommit(false);
            try {
                int orderId;
                try (PreparedStatement ps = con.prepareStatement(orderSql, 1);){
                    ps.setInt(1, order.getCustomerId());
                    if (order.getAdminId() == null) {
                        ps.setNull(2, 4);
                    } else {
                        ps.setInt(2, order.getAdminId());
                    }
                    ps.setBigDecimal(3, order.getWeight());
                    ps.setLong(4, order.getTotalPrice().longValueExact());
                    ps.setString(5, order.getStatus().getLabel());
                    ps.setString(6, order.getDestination());
                    ps.setString(7, order.getPaymentMethod().getLabel());
                    ps.setString(8, order.getPaymentStatus().getLabel());
                    OrderDAO.setNullableString(ps, 9, order.getNotes());
                    if (order.getEstimatedDate() == null) {
                        ps.setNull(10, 91);
                    } else {
                        ps.setObject(10, order.getEstimatedDate());
                    }
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys();){
                        keys.next();
                        orderId = keys.getInt(1);
                    }
                }
                PreparedStatement ps = con.prepareStatement(detailSql);
                try {
                    for (OrderDetail d : details) {
                        ps.setInt(1, orderId);
                        ps.setInt(2, d.getServiceId());
                        ps.setBigDecimal(3, d.getWeightKg());
                        ps.setLong(4, d.getPricePerKg().longValueExact());
                        ps.setLong(5, d.getSubtotal().longValueExact());
                        ps.executeUpdate();
                    }
                }
                finally {
                    if (ps != null) {
                        ps.close();
                    }
                }
                con.commit();
                int n = orderId;
                return n;
            }
            catch (RuntimeException | SQLException e) {
                con.rollback();
                throw e;
            }
        }
        catch (SQLException e) {
            throw new DataAccessException("Gagal menyimpan pesanan", e);
        }
    }

    public void updateStatus(int orderId, OrderStatus status, PaymentStatus paymentStatus) {
        String sql = "UPDATE orders SET status=?, status_payment=? WHERE id_order=?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);){
            ps.setString(1, status.getLabel());
            ps.setString(2, paymentStatus.getLabel());
            ps.setInt(3, orderId);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new DataAccessException("Gagal mengubah status pesanan", e);
        }
    }

    public void delete(int orderId) {
        String sql = "DELETE FROM orders WHERE id_order=?";
        try (Connection con = DatabaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);){
            ps.setInt(1, orderId);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new DataAccessException("Gagal menghapus pesanan", e);
        }
    }

    private List<Order> query(String sql, ParamSetter setter) {
        try (Connection con = DatabaseConfig.getConnection();){
            ArrayList<Order> arrayList;
            block22: {
                PreparedStatement ps = con.prepareStatement(sql);
                try {
                    setter.set(ps);
                    ArrayList<Order> orders = new ArrayList<Order>();
                    try (ResultSet rs = ps.executeQuery();){
                        while (rs.next()) {
                            orders.add(OrderDAO.mapOrder(rs));
                        }
                    }
                    OrderDAO.loadDetails(con, orders);
                    arrayList = orders;
                    if (ps == null) break block22;
                }
                catch (Throwable throwable) {
                    if (ps != null) {
                        try {
                            ps.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                ps.close();
            }
            return arrayList;
        }
        catch (SQLException e) {
            throw new DataAccessException("Gagal membaca data pesanan", e);
        }
    }

    private static Order mapOrder(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getInt("id_order"));
        o.setCustomerId(rs.getInt("id_customer"));
        o.setCustomerName(rs.getString("name_customer"));
        int adminId = rs.getInt("id_admin");
        o.setAdminId(rs.wasNull() ? null : Integer.valueOf(adminId));
        o.setOrderDate(rs.getObject("order_date", LocalDate.class));
        o.setWeight(rs.getBigDecimal("weight"));
        o.setTotalPrice(BigDecimal.valueOf(rs.getLong("total_price")));
        o.setStatus(OrderStatus.fromLabel(rs.getString("status")));
        o.setDestination(rs.getString("destination"));
        o.setPaymentMethod(PaymentMethod.fromLabel(rs.getString("method_payment")));
        o.setPaymentStatus(PaymentStatus.fromLabel(rs.getString("status_payment")));
        o.setNotes(rs.getString("notes"));
        o.setEstimatedDate(rs.getObject("estimated_date", LocalDate.class));
        return o;
    }

    private static void loadDetails(Connection con, List<Order> orders) throws SQLException {
        if (orders.isEmpty()) {
            return;
        }
        HashMap<Integer, Order> byId = new HashMap<Integer, Order>();
        StringBuilder placeholders = new StringBuilder();
        for (Order o : orders) {
            byId.put(o.getId(), o);
            placeholders.append(placeholders.length() == 0 ? "?" : ",?");
        }
        String sql = "SELECT d.id_order_detail, d.id_order, d.id_service, s.service_type, d.weight_kg, d.price_per_kg, d.subtotal FROM order_detail d JOIN service s ON s.id_service = d.id_service WHERE d.id_order IN (" + String.valueOf(placeholders) + ") ORDER BY d.id_order_detail";
        try (PreparedStatement ps = con.prepareStatement(sql);){
            int i = 1;
            for (Order o : orders) {
                ps.setInt(i++, o.getId());
            }
            try (ResultSet rs = ps.executeQuery();){
                while (rs.next()) {
                    OrderDetail d = new OrderDetail();
                    d.setId(rs.getInt("id_order_detail"));
                    d.setOrderId(rs.getInt("id_order"));
                    d.setServiceId(rs.getInt("id_service"));
                    d.setServiceType(rs.getString("service_type"));
                    d.setWeightKg(rs.getBigDecimal("weight_kg"));
                    d.setPricePerKg(BigDecimal.valueOf(rs.getLong("price_per_kg")));
                    long subtotal = rs.getLong("subtotal");
                    d.setSubtotal(rs.wasNull() ? OrderDetail.calculateSubtotal(d.getWeightKg(), d.getPricePerKg()) : BigDecimal.valueOf(subtotal));
                    ((Order)byId.get(d.getOrderId())).getDetails().add(d);
                }
            }
        }
    }

    private static void setNullableString(PreparedStatement ps, int index, String value) throws SQLException {
        if (value == null) {
            ps.setNull(index, 12);
        } else {
            ps.setString(index, value);
        }
    }

    @FunctionalInterface
    private static interface ParamSetter {
        public void set(PreparedStatement var1) throws SQLException;
    }
}


