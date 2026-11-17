/*
 * Decompiled with CFR 0.152.
 */
package laundrykita.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import laundrykita.dao.CustomerDAO;
import laundrykita.dao.OrderDAO;
import laundrykita.dao.ServiceDAO;
import laundrykita.model.DashboardStats;
import laundrykita.model.Order;
import laundrykita.model.OrderDetail;
import laundrykita.model.OrderStatus;
import laundrykita.model.PaymentStatus;
import laundrykita.model.Service;
import laundrykita.model.UserSession;
import laundrykita.service.ServiceException;

public class OrderService {
    static final String NOT_FOUND = "Pesanan tidak ditemukan.";
    static final String INCOMPLETE = "Data belum lengkap: pilih pelanggan, isi alamat, dan isi berat minimal satu layanan.";
    static final String ADMIN_ONLY = "Pesanan hanya dapat dicatat oleh admin yang sedang login.";
    private static final int MAX_NOTES = 255;
    private static final int MAX_DESTINATION = 255;
    private static final int WEIGHT_SCALE = 2;
    private static final BigDecimal MAX_WEIGHT = new BigDecimal("9999.99");
    private static final BigDecimal MAX_MONEY = BigDecimal.valueOf(0xFFFFFFFFL);
    private final OrderDAO orderDAO;
    private final ServiceDAO serviceDAO;
    private final CustomerDAO customerDAO;

    public OrderService() {
        this(new OrderDAO(), new ServiceDAO(), new CustomerDAO());
    }

    public OrderService(OrderDAO orderDAO, ServiceDAO serviceDAO, CustomerDAO customerDAO) {
        this.orderDAO = orderDAO;
        this.serviceDAO = serviceDAO;
        this.customerDAO = customerDAO;
    }

    public List<Order> getAllOrders() {
        return this.orderDAO.findAll();
    }

    public List<Order> searchOrders(String keyword, OrderStatus status) {
        return this.orderDAO.search(keyword, status);
    }

    public List<Order> getLatestOrders(int limit) {
        if (limit < 1) {
            throw new ServiceException("Jumlah pesanan yang ditampilkan minimal 1.");
        }
        return this.orderDAO.findLatest(limit);
    }

    public DashboardStats getDashboardStats() {
        return this.orderDAO.getStatistics();
    }

    public Order getOrderById(int orderId) {
        Order order = this.orderDAO.findById(orderId);
        if (order == null) {
            throw new ServiceException(NOT_FOUND);
        }
        return order;
    }

    public Order getOrderById(int authenticatedCustomerId, int orderId) {
        Order order = this.orderDAO.findById(orderId);
        if (order == null || order.getCustomerId() != authenticatedCustomerId) {
            throw new ServiceException(NOT_FOUND);
        }
        return order;
    }

    public List<Order> getOrdersByCustomerId(int authenticatedCustomerId) {
        return this.orderDAO.findByCustomerId(authenticatedCustomerId);
    }

    public List<Order> getActiveOrdersByCustomerId(int authenticatedCustomerId) {
        return this.orderDAO.findActiveByCustomerId(authenticatedCustomerId);
    }

    public int createOrder(UserSession session, Order order, List<OrderDetail> details) {
        String notes;
        String destination;
        if (session == null || !session.isAdmin() || session.getUserId() <= 0) {
            throw new ServiceException(ADMIN_ONLY);
        }
        if (order == null || details == null) {
            throw new ServiceException(INCOMPLETE);
        }
        ArrayList<OrderDetail> requested = new ArrayList<OrderDetail>();
        for (OrderDetail d : details) {
            if (d == null || d.getWeightKg() == null || d.getWeightKg().signum() <= 0) continue;
            requested.add(d);
        }
        String string = destination = order.getDestination() == null ? "" : order.getDestination().trim();
        if (order.getCustomerId() <= 0 || destination.isEmpty() || requested.isEmpty()) {
            throw new ServiceException(INCOMPLETE);
        }
        if (OrderService.codePoints(destination) > 255) {
            throw new ServiceException("Alamat tujuan maksimal 255 karakter.");
        }
        String string2 = notes = order.getNotes() == null ? "" : order.getNotes().trim();
        if (OrderService.codePoints(notes) > 255) {
            throw new ServiceException("Catatan maksimal 255 karakter.");
        }
        if (order.getStatus() == null || order.getPaymentMethod() == null || order.getPaymentStatus() == null) {
            throw new ServiceException("Status pesanan, metode bayar, dan status pembayaran wajib dipilih.");
        }
        for (OrderDetail d : requested) {
            BigDecimal w = d.getWeightKg();
            if (w.stripTrailingZeros().scale() > 2) {
                throw new ServiceException("Berat maksimal 2 angka di belakang koma.");
            }
            if (w.compareTo(MAX_WEIGHT) <= 0) continue;
            throw new ServiceException("Berat per layanan maksimal 9999,99 kg.");
        }
        if (this.customerDAO.findById(order.getCustomerId()) == null) {
            throw new ServiceException("Pelanggan tidak ditemukan.");
        }
        HashMap<Integer, Service> services = new HashMap<Integer, Service>();
        ArrayList<OrderDetail> priced = new ArrayList<OrderDetail>();
        BigDecimal totalWeight = BigDecimal.ZERO.setScale(2);
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderDetail d : requested) {
            Service service = (Service)services.get(d.getServiceId());
            if (service == null) {
                service = this.serviceDAO.findById(d.getServiceId());
                if (service == null) {
                    throw new ServiceException("Layanan dengan id " + d.getServiceId() + " tidak ditemukan.");
                }
                services.put(d.getServiceId(), service);
            }
            OrderDetail line = new OrderDetail(service.getId(), d.getWeightKg().setScale(2), service.getPricePerKg());
            priced.add(line);
            totalWeight = totalWeight.add(line.getWeightKg());
            totalPrice = totalPrice.add(line.getSubtotal());
        }
        if (totalWeight.compareTo(MAX_WEIGHT) > 0) {
            throw new ServiceException("Total berat maksimal 9999,99 kg.");
        }
        if (totalPrice.compareTo(MAX_MONEY) > 0) {
            throw new ServiceException("Total harga terlalu besar.");
        }
        Order toSave = new Order();
        toSave.setCustomerId(order.getCustomerId());
        toSave.setAdminId(session.getUserId());
        toSave.setDestination(destination);
        toSave.setNotes(notes.isEmpty() ? null : notes);
        toSave.setStatus(order.getStatus());
        toSave.setPaymentMethod(order.getPaymentMethod());
        toSave.setPaymentStatus(order.getPaymentStatus());
        toSave.setEstimatedDate(order.getEstimatedDate());
        toSave.setWeight(totalWeight);
        toSave.setTotalPrice(totalPrice);
        return this.orderDAO.insert(toSave, priced);
    }

    public void updateOrderStatus(int orderId, OrderStatus status, PaymentStatus paymentStatus) {
        if (status == null || paymentStatus == null) {
            throw new ServiceException("Status pesanan dan status pembayaran wajib dipilih.");
        }
        if (this.orderDAO.findById(orderId) == null) {
            throw new ServiceException(NOT_FOUND);
        }
        this.orderDAO.updateStatus(orderId, status, paymentStatus);
    }

    public void deleteOrder(int orderId) {
        if (this.orderDAO.findById(orderId) == null) {
            throw new ServiceException(NOT_FOUND);
        }
        this.orderDAO.delete(orderId);
    }

    private static int codePoints(String value) {
        return value.codePointCount(0, value.length());
    }
}

