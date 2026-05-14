package ch.portami.inventorybackend.offer.repository;

import ch.portami.inventorybackend.offer.entity.OfferItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfferItemRepository extends JpaRepository<OfferItem, Long> {

}

