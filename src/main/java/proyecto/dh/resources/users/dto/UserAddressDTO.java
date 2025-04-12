package proyecto.dh.resources.users.dto;

import lombok.Data;

@Data
public class UserAddressDTO {
    private String street;
    private String city;
    private String state;
    private String zip;
    private String country;
}
