package freshtrash.freshtrashbackend.entity;

import lombok.*;
import org.apache.commons.lang3.StringUtils;

@Getter
@Builder
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class Address {
    private String zipcode;
    private String state;
    private String city;
    private String district;
    private String detail;

    public boolean allBlank() {
        return StringUtils.isAllBlank(this.zipcode, this.state, this.city, this.district, this.detail);
    }
}
