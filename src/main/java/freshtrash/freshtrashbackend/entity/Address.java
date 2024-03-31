package freshtrash.freshtrashbackend.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
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
}
