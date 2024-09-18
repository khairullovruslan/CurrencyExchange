package dto;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class CurrencyDto{
    private String code;
    private String fullName;
    private String sign;


}
