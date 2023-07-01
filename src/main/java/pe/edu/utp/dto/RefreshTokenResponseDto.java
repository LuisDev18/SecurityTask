package pe.edu.utp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class RefreshTokenResponseDto {
  private String token;
  private String refreshToken;
  private String name;
}
