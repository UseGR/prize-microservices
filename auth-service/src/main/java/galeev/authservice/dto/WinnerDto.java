package galeev.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WinnerDto {
    private Long id;
    private String fullname;
    private String username;
    private String phoneNumber;
    private String prizeId;
}
