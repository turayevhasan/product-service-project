package uz.pdp.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import uz.pdp.dto.AttachmentDTO;
import uz.pdp.model.Attachment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class AttachmentRepository {
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    @Autowired
    public AttachmentRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedJdbcTemplate = namedParameterJdbcTemplate;
    }

    public long save(Attachment attachment) {
        try {
            String sql = """
                    insert into attachment(original_name, generated_name, content_type, size)
                    values(:original_name, :generated_name, :content_type, :size);
                    """;

            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource()
                    .addValue("original_name", attachment.getOriginalName())
                    .addValue("generated_name", attachment.getGeneratedName())
                    .addValue("content_type", attachment.getContentType())
                    .addValue("size", attachment.getSize());

            KeyHolder keyHolder = new GeneratedKeyHolder();
            namedJdbcTemplate.update(sql, mapSqlParameterSource, keyHolder, new String[]{"id"});

            Number generatedId = keyHolder.getKey();
            if (generatedId != null) {
                return generatedId.longValue();
            } else {
                throw new RuntimeException("Failed to retrieve the generated ID");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save attachment", e);
        }
    }

    public List<Attachment> getAllAttachmentByProductId(int productId) {
        String sql = """
                select * from attachment where id in (
                select p.attachment_id from product_image p where p.product_id = :productId)
                """;
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource()
                .addValue("productId", productId);
        try {
            return namedJdbcTemplate.query(sql, mapSqlParameterSource, BeanPropertyRowMapper.newInstance(Attachment.class));
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void delete(int attachmentId) {
        String sql = "delete from attachment where id = :attachmentId";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource()
                .addValue("attachmentId", attachmentId);

        namedJdbcTemplate.update(sql, mapSqlParameterSource);
    }
}
