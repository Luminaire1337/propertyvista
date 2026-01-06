package io.github.luminaire1337.propertyvista.backend.dto.request;

import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record UpdatePropertyRequest(
        @Size(min = 5, max = 100, message = "Tytuł ogłoszenia musi mieć od 5 do 100 znaków")
        String title,

        @Size(max = 5000, message = "Opis ogłoszenia może mieć maksymalnie 5000 znaków")
        String description,

        Double price,

        @Size(min = 2, max = 100, message = "Nazwa miasta musi mieć od 2 do 100 znaków")
        String city,

        Double area,

        Integer rooms,

        Boolean parking,

        @Size(max = 15, message = "Można dodać maksymalnie 15 zdjęć nieruchomości")
        List<MultipartFile> images,

        String primaryImagePath,

        @Range(min = 1, max = 90, message = "Ogłoszenie może być ważne od 1 do 90 dni")
        Integer daysValid
) {
}

