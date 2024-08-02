package uz.pdp.repository;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

@Component
public class UserStatusRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public UserStatusRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public void save(int userId, String confirmationCode) {
        String sql = "insert into auth_user_status(user_id, confirmation_code) values (:userId, :confirmationCode)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("confirmationCode", confirmationCode);

        namedParameterJdbcTemplate.update(sql, params);
    }

    public boolean findByCode(String confirmationCode) {
        String sql = "select count(*) from auth_user_status where confirmation_code = :confirmationCode";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("confirmationCode", confirmationCode);

        Integer count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);

        return count != null && count > 0;
    }


    public boolean findByUserId(Integer id) {
        String sql = "select count(*) from auth_user_status where user_id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);

        Integer count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }

    public void delete(String confirmationCode) {
        String sql = "delete from auth_user_status where confirmation_code = :confirmationCode";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("confirmationCode", confirmationCode);

        namedParameterJdbcTemplate.update(sql, params);
    }
}
