package pe.edu.utp.dto;

import lombok.*;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class RefreshTokenResponseDTO {
    private String token;
    private String refreshToken;
    private String name;
}
