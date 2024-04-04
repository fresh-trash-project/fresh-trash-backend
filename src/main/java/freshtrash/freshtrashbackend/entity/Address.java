package freshtrash.freshtrashbackend.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class Address {
    private String zipcode;
    private String state;
    private String city;
    private String district;
    private String detail;

    private Address(String zipcode, String state, String city, String district, String detail) {
        this.zipcode = zipcode;
        this.state = state;
        this.city = city;
        this.district = district;
        this.detail = detail;
    }

    public static Address of(String zipcode, String state, String city, String district, String detail) {
        return new Address(zipcode, state, city, district, detail);
    }

    public boolean allBlank() {
        return StringUtils.isAllBlank(this.zipcode, this.state, this.city, this.district, this.detail);
    }
}
