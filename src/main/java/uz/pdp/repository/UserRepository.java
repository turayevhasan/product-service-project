package uz.pdp.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import uz.pdp.model.AuthRole;
import uz.pdp.model.AuthUser;

import java.security.Key;
import java.util.List;
import java.util.Optional;

@Component
public class UserRepository {
    private final NamedParameterJdbcTemplate namedJdbcTemplate;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserStatusRepository userStatusRepository;

    @Autowired
    public UserRepository(NamedParameterJdbcTemplate namedJdbcTemplate, RoleRepository roleRepository, PermissionRepository permissionRepository, UserStatusRepository userStatusRepository) {
        this.namedJdbcTemplate = namedJdbcTemplate;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userStatusRepository = userStatusRepository;
    }

    public Integer save(AuthUser authUser) {
        try {
            String sql = "INSERT INTO auth_user (name, age, gender, email, password) VALUES (:name, :age, :gender, :email, :password)";
            SqlParameterSource params = new MapSqlParameterSource()
                    .addValue("name", authUser.getName())
                    .addValue("age", authUser.getAge())
                    .addValue("gender", authUser.getGender())
                    .addValue("email", authUser.getEmail())
                    .addValue("password", authUser.getPassword());

            KeyHolder keyHolder = new GeneratedKeyHolder();
            namedJdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});

            if (keyHolder.getKey() != null) {
                int userId = keyHolder.getKey().intValue();

                roleRepository.addUserRole(userId, "USER"); //every registered user saved with USER role
                return userId;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Optional<AuthUser> findByEmail(String email) {
        String sql = "SELECT * FROM auth_user WHERE email = :email";
        SqlParameterSource params = new MapSqlParameterSource().addValue("email", email);
        try {
            AuthUser authUser = namedJdbcTemplate.queryForObject(
                    sql,
                    params,
                    BeanPropertyRowMapper.newInstance(AuthUser.class)
            );

            if (authUser != null) {
                Integer userId = authUser.getId();
                boolean isNonActiveUser = userStatusRepository.findByUserId(userId);

                if (isNonActiveUser) {
                    return Optional.empty();
                }

                List<AuthRole> roles = roleRepository.findByUserId(authUser.getId());
                roles.forEach(r -> r.setPermissions(permissionRepository.findByRoleId(r.getId())));
                authUser.setRoles(roles);
            }
            return Optional.ofNullable(authUser);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<AuthUser> findById(Integer id) {
        String sql = "SELECT * FROM auth_user WHERE id = :id";
        SqlParameterSource params = new MapSqlParameterSource().addValue("id", id);
        try {
            AuthUser authUser = namedJdbcTemplate.queryForObject(
                    sql,
                    params,
                    BeanPropertyRowMapper.newInstance(AuthUser.class)
            );
            if (authUser != null) {
                List<AuthRole> roles = roleRepository.findByUserId(authUser.getId());
                roles.forEach(r -> r.setPermissions(permissionRepository.findByRoleId(r.getId())));
                authUser.setRoles(roles);
            }
            return Optional.ofNullable(authUser);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<AuthUser> getAllByRole(String role) {
        String sql = """
                SELECT u.*
                FROM spring_jdbc.auth_user u
                         JOIN spring_jdbc.user_role ur ON u.id = ur.user_id
                         JOIN spring_jdbc.auth_role r ON ur.role_id = r.id
                WHERE r.code = :role;
                """;
        SqlParameterSource params = new MapSqlParameterSource().addValue("role", role);
        List<AuthUser> users = namedJdbcTemplate.query(sql, params, BeanPropertyRowMapper.newInstance(AuthUser.class));
        users.forEach(u -> {
            List<AuthRole> roles = roleRepository.findByUserId(u.getId());
            roles.forEach(r -> r.setPermissions(permissionRepository.findByRoleId(r.getId())));
            u.setRoles(roles);
        }); //set roles for users
        return users;
    }

    public String delete(int id) {
        List<AuthRole> deletedUserRole = roleRepository.findByUserId(id);
        String sql = "delete from auth_user where id = :id";
        SqlParameterSource params = new MapSqlParameterSource().addValue("id", id);
        namedJdbcTemplate.update(sql, params);
        return deletedUserRole.getFirst().getCode();
    }

    public void update(AuthUser user) {
        String sql = "update auth_user set name = :name, age = :age, gender = :gender, password = :password where id = :id";
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", user.getId())
                .addValue("name", user.getName())
                .addValue("age", user.getAge())
                .addValue("gender", user.getGender())
                .addValue("password", user.getPassword());

        namedJdbcTemplate.update(sql, params);
    }
}