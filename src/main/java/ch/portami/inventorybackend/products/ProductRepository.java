package ch.portami.inventorybackend.products;

import ch.portami.inventorybackend.products.model.Color;
import ch.portami.inventorybackend.products.model.Product;
import ch.portami.inventorybackend.products.model.ProductType;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class ProductRepository {

    private final Map<Integer, Product> store = new ConcurrentHashMap<>();
    private final AtomicInteger idSequence = new AtomicInteger(1);

    public List<Product> findAll() {
        return new ArrayList<>(store.values());
    }

    /**
     * Returns all products, optionally filtered by type and/or color.
     * {@code null} parameters are treated as "no filter".
     */
    public List<Product> findAll(ProductType type, Color color) {
        return store.values().stream()
                .filter(p -> type  == null || p.getType()  == type)
                .filter(p -> color == null || p.getColor() == color)
                .toList();
    }

    public Optional<Product> findById(int id) {
        return Optional.ofNullable(store.get(id));
    }

    public Product save(Product product) {
        if (product.getId() == 0) {
            product.setId(idSequence.getAndIncrement());
        }
        store.put(product.getId(), product);
        return product;
    }

    public void deleteById(int id) {
        store.remove(id);
    }

    public boolean existsById(int id) {
        return store.containsKey(id);
    }
}
