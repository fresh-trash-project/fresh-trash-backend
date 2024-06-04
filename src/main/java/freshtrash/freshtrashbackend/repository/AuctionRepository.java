package freshtrash.freshtrashbackend.repository;

import freshtrash.freshtrashbackend.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionRepository extends JpaRepository<Auction, Long> {}
