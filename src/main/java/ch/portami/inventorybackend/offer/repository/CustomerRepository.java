package ch.portami.inventorybackend.offer.repository;

import ch.portami.inventorybackend.offer.entity.Customer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByNameIgnoreCase(String name);

}

