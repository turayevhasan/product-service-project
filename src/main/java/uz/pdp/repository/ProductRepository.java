package uz.pdp.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import uz.pdp.dto.ProductAddDTO;
import uz.pdp.model.Attachment;
import uz.pdp.model.AuthUser;
import uz.pdp.model.Product;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductRepository {
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    public List<Product> getAll() {
        return namedJdbcTemplate.query("select * from product", new BeanPropertyRowMapper<>(Product.class));
    }

    public Integer getSizeOfAll() {
        String sql = "select COUNT(*) from product";
        return namedJdbcTemplate.queryForObject(sql, new HashMap<>(), Integer.class);
    }


    public long save(ProductAddDTO productAddDTO) {
        try {
            String sql = """
                    insert into product(name, description, produced_country, price, creator_id)
                    values(:name, :description, :produced_country, :price, :creator_id);
                    """;

            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource()
                    .addValue("name", productAddDTO.name())
                    .addValue("description", productAddDTO.description())
                    .addValue("produced_country", productAddDTO.country())
                    .addValue("price", productAddDTO.price())
                    .addValue("creator_id", productAddDTO.creatorId());

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
            throw new RuntimeException("Failed to save product", e);
        }
    }

    public void addAttachmentIdToProductId(long productId, long attachmentId) {
        String sql = "insert into product_image(product_id, attachment_id) values(:productId, :attachmentId)";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource()
                .addValue("productId", productId)
                .addValue("attachmentId", attachmentId);

        namedJdbcTemplate.update(sql, mapSqlParameterSource);
    }

    public void delete(long id) {
        String sql = "delete from product where id = :id";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource()
                .addValue("id", id);

        namedJdbcTemplate.update(sql, mapSqlParameterSource);
    }

    public Integer getProductSizeByUserId(long userId) {
        String sql = "select count(*) from product where creator_id = :userId";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource()
                .addValue("userId", userId);

        return namedJdbcTemplate.queryForObject(sql, mapSqlParameterSource, Integer.class);
    }

    public Optional<Product> findById(int productId) {
        try {
            String sql = "select * from product where id = :productId";
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource()
                    .addValue("productId", productId);

            Product product = namedJdbcTemplate.queryForObject(sql, mapSqlParameterSource, BeanPropertyRowMapper.newInstance(Product.class));
            return Optional.ofNullable(product);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public void update(Product product) {
        String sql = "update product set name = :name, description = :description, produced_country = :produced_country, price = :price where id = :id";
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource()
                .addValue("id", product.getId())
                .addValue("name", product.getName())
                .addValue("description", product.getDescription())
                .addValue("produced_country", product.getProducedCountry())
                .addValue("price", product.getPrice());

        namedJdbcTemplate.update(sql, mapSqlParameterSource);
    }
}
