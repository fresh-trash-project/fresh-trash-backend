package freshtrash.freshtrashbackend.domain.product.entity.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductCategory {
    ELECTRONICS(0),
    CLOTHING(1),
    HOME_KITCHEN(2),
    BEAUTY(3),
    HEALTH(4),
    SPORTS(5),
    BOOKS(6),
    TOYS_GAMES(7),
    FURNITURE_DECOR(8),
    PET_SUPPLIES(9),
    PLANT_SUPPLIES(10);

    private final int profileIndex;
}
