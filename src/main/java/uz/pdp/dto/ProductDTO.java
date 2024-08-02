package uz.pdp.dto;

import uz.pdp.model.Attachment;

import java.util.List;

public record ProductDTO(Integer id, String name, String description, String producedCountry, String price, String createdAt, String createdBy, List<AttachmentDTO> images) {
}
