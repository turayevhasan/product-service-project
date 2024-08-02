package uz.pdp.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import uz.pdp.model.AuthRole;

import java.util.Collections;
import java.util.List;

@Component
public class RoleRepository {
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    @Autowired
    public RoleRepository(NamedParameterJdbcTemplate namedJdbcTemplate) {
        this.namedJdbcTemplate = namedJdbcTemplate;
    }

    public List<AuthRole> findByUserId(Integer userId) {
        try {
            String sql = "select ar.* from user_role ur join auth_role ar on ar.id = ur.role_id where ur.user_id = :userId";
            SqlParameterSource params = new MapSqlParameterSource().addValue("userId", userId);

            return namedJdbcTemplate.query(sql, params, BeanPropertyRowMapper.newInstance(AuthRole.class));
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void addUserRole(Integer userId, String roleCode) {
        String sql = "insert into user_role(user_id, role_id) values(:userId, (select ar.id from auth_role ar where ar.code = :roleCode))";
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("roleCode", roleCode);
        namedJdbcTemplate.update(sql, params);
    }

    public void updateUserRole(int userId, String roleCode) {
        try {
            String sql = "update user_role ur set role_id = (select ar.id from auth_role ar where ar.code = :roleCode) where ur.user_id = :userId";
            SqlParameterSource params = new MapSqlParameterSource()
                    .addValue("roleCode", roleCode)
                    .addValue("userId", userId);
            namedJdbcTemplate.update(sql, params);
        } catch (Exception e) {
            throw new RuntimeException("update user_role error !!!");
        }
    }
}