package pl.asku.askumagazineservice.model.search;

import org.springframework.data.domain.Sort;

public enum SortOptions {
    PRICE_ASC, PRICE_DESC;

    public Sort getSort() {
        return switch (this) {
            case PRICE_ASC -> Sort.by("pricePerMeter").ascending();
            case PRICE_DESC -> Sort.by("pricePerMeter").descending();
        };
    }
}
