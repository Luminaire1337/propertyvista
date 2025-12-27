package io.github.luminaire1337.propertyvista.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

public record CreatePropertyRequest(
        @NotBlank(message = "Tytuł ogłoszenia jest wymagany")
        @Size(min = 5, max = 100, message = "Tytuł ogłoszenia musi mieć od 5 do 100 znaków")
        String title,

        @Size(max = 5000, message = "Opis ogłoszenia może mieć maksymalnie 5000 znaków")
        String description,

        @NotNull(message = "Cena ogłoszenia jest wymagana")
        Double price,

        @NotBlank(message = "Miasto jest wymagane")
        @Size(min = 2, max = 100, message = "Nazwa miasta musi mieć od 2 do 100 znaków")
        String city,

        @NotNull(message = "Powierzchnia nieruchomości jest wymagana")
        Double area,

        @NotNull(message = "Liczba pokoi jest wymagana")
        Integer rooms,

        @NotNull(message = "Informacja o miejscu parkingowym jest wymagana")
        Boolean parking,

        @NotNull(message = "Zdjęcia nieruchomości są wymagane")
        @Length(min = 1, max = 15, message = "Można dodać od 1 do 15 zdjęć nieruchomości")
        MultipartFile[] images,

        @NotBlank(message = "Ścieżka do głównego zdjęcia jest wymagana")
        String primaryImagePath
) {
}
