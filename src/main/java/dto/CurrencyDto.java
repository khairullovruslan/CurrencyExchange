package dto;

import lombok.Builder;

@Builder
public record CurrencyDto( String code, String fullName, String sign) {
}
