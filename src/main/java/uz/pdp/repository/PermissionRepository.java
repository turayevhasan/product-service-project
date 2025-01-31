package uz.pdp.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import uz.pdp.model.AuthPermission;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PermissionRepository {
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    public List<AuthPermission> findByRoleId(Integer roleId) {
        String sql = "select ap.* from role_permission rp join auth_permission ap on ap.id = rp.permission_id where rp.role_id = :roleId";
        SqlParameterSource params = new MapSqlParameterSource().addValue("roleId", roleId);
        try {
            return namedJdbcTemplate.query(sql, params, BeanPropertyRowMapper.newInstance(AuthPermission.class));
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

}