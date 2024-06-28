package francescocristiano.U5_W2_D5_Progetto.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record NewEmployeeDTO(
        @NotBlank(message = "Username cannot be blank")
        String username,
        @NotBlank(message = "Name cannot be blank")
        String name,
        @NotBlank(message = "Last name cannot be blank")
        String surname,
        @NotBlank(message = "Email cannot be blank")
        @Email
        String email
) {
}