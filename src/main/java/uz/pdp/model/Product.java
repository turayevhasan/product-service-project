package uz.pdp.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {
    private Integer id;
    private String name;
    private String description;
    private String producedCountry;
    private long price;
    private LocalDateTime createdAt;

    private Integer creatorId;

    List<Attachment> images;
}
