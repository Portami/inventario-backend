package ch.portami.inventorybackend.offer.repository;

import ch.portami.inventorybackend.offer.domain.OfferState;
import ch.portami.inventorybackend.offer.entity.Offer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfferRepository extends JpaRepository<Offer, Long> {

    List<Offer> findByState(OfferState state);

    List<Offer> findByCustomer_NameContainingIgnoreCase(String name);

}

