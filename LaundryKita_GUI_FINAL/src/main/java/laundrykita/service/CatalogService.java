package laundrykita.service;

import java.math.BigDecimal;
import java.util.List;
import laundrykita.dao.DataAccessException;
import laundrykita.dao.ServiceDAO;
import laundrykita.model.Service;

public class CatalogService {
    private static final BigDecimal MAX_PRICE = BigDecimal.valueOf(0xFFFFFFFFL);
    static final String NOT_FOUND = "Layanan tidak ditemukan.";
    private final ServiceDAO serviceDAO;

    public CatalogService() {
        this(new ServiceDAO());
    }

    public CatalogService(ServiceDAO serviceDAO) {
        this.serviceDAO = serviceDAO;
    }

    public List<Service> getAllServices() {
        return this.serviceDAO.findAll();
    }

    public void createService(Service service) {
        this.serviceDAO.insert(CatalogService.validated(service));
    }

    public void updateService(Service service) {
        Service clean = CatalogService.validated(service);
        if (this.serviceDAO.findById(clean.getId()) == null) {
            throw new ServiceException(NOT_FOUND);
        }
        this.serviceDAO.update(clean);
    }

    public void deleteService(int serviceId) {
        Service existing = this.serviceDAO.findById(serviceId);
        if (existing == null) {
            throw new ServiceException(NOT_FOUND);
        }
        if (existing.getUsageCount() > 0) {
            throw new ServiceException("Layanan tidak bisa dihapus karena dipakai di " + existing.getUsageCount() + " item pesanan.");
        }
        try {
            this.serviceDAO.delete(serviceId);
        }
        catch (DataAccessException e) {
            if (e.isForeignKeyViolation()) {
                throw new ServiceException("Layanan tidak bisa dihapus karena dipakai di item pesanan.", e);
            }
            throw e;
        }
    }

    private static Service validated(Service input) {
        String description;
        if (input == null) {
            throw new ServiceException("Data layanan tidak boleh kosong.");
        }
        String name = CatalogService.trimToNull(input.getServiceType());
        if (name == null) {
            throw new ServiceException("Nama layanan wajib diisi.");
        }
        CatalogService.requireMaxLength(name, 100, "Nama layanan");
        BigDecimal price = input.getPricePerKg();
        if (price == null || price.signum() <= 0 || price.stripTrailingZeros().scale() > 0) {
            throw new ServiceException("Harga harus berupa bilangan bulat minimal 1.");
        }
        if (price.compareTo(MAX_PRICE) > 0) {
            throw new ServiceException("Harga terlalu besar.");
        }
        String category = CatalogService.trimToNull(input.getCategory());
        if (category != null) {
            CatalogService.requireMaxLength(category, 50, "Kategori");
        }
        if ((description = CatalogService.trimToNull(input.getDescription())) != null) {
            CatalogService.requireMaxLength(description, 255, "Deskripsi");
        }
        return new Service(input.getId(), name, price.setScale(0), category, description);
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static void requireMaxLength(String value, int max, String label) {
        if (value.codePointCount(0, value.length()) > max) {
            throw new ServiceException(label + " maksimal " + max + " karakter.");
        }
    }
}

