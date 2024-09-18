package entity;


import lombok.*;

import java.util.Objects;


@Builder
@AllArgsConstructor
@Getter
public class Currency {
    private Long id;
    private String code;
    private String fullName;
    private String sign;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Currency currency = (Currency) o;
        return Objects.equals(id, currency.id) && Objects.equals(code, currency.code) && Objects.equals(fullName, currency.fullName) && Objects.equals(sign, currency.sign);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, fullName, sign);
    }
}
