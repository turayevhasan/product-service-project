package uz.pdp.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Attachment {
    private Integer id;
    private String originalName;
    private String generatedName;
    private String contentType;
    private long size;
}
