package common.db.dao.impl;

import common.db.dao.UserDao;
import common.db.dao.exceptions.DuplicateUserException;
import common.db.model.Role;
import common.db.model.User;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UserDaoImplTest {
    private static String dbUrl = "jdbc:mysql://localhost:3306/test_actimind";
    private static String dbUser = "testActimind";
    private static String dbPassword = "testActimind";
    private static String dbDriver = "com.mysql.jdbc.Driver";
    private static BasicDataSource db = new BasicDataSource();
    private Connection connection = null;
    private UserDao userDao = null;

    static {
        db.setDriverClassName(dbDriver);
        db.setUrl(dbUrl);
        db.setUsername(dbUser);
        db.setPassword(dbPassword);
        db.setDefaultAutoCommit(false);
    }

    @BeforeClass
    public static void initDb() throws SQLException {
        String dropUserTableSql = "DROP TABLE IF EXISTS users;";
        String dropRoleTableSql = "DROP TABLE IF EXISTS role;";
        String usersTableSql = "CREATE TABLE IF NOT EXISTS role (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,name VARCHAR(100) NOT NULL);";
        String roleTableSql = "CREATE TABLE IF NOT EXISTS users (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,\n" +
                "username VARCHAR(100) NOT NULL UNIQUE,\n" +
                "password VARCHAR(100) NOT NULL,\n" +
                "firstName VARCHAR(100) NOT NULL,\n" +
                "lastName VARCHAR(100) NOT NULL,\n" +
                "role INT NOT NULL,\n" +
                "FOREIGN KEY (role) REFERENCES role(id) ON DELETE CASCADE ON UPDATE CASCADE);";
        String addRoleDefault = "INSERT INTO role (name) values ('default');";
        String addRoleManager = "INSERT INTO role (name) values ('manager');";


        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();) {
            stmt.execute(dropUserTableSql);
            stmt.execute(dropRoleTableSql);
            stmt.execute(usersTableSql);
            stmt.execute(roleTableSql);
            stmt.execute(addRoleDefault);
            stmt.execute(addRoleManager);
            conn.commit();
        }
    }

    @Before
    public void setUp() throws Exception {
        connection = db.getConnection();
        userDao = new UserDaoImpl(connection);
    }

    @After
    public void tearDown() throws Exception {
        connection.rollback();
        connection.close();
    }

    // TODO: Нет теста, в котором getUserById() не находит пользователя
    // TODO: Нет теста, в котором getUserById() находит нужного среди нескольких
    @Test
    public void testGetUserById() throws SQLException, DuplicateUserException {
        User defaultUser = buildDefaultUser();
        int id = userDao.addUser(defaultUser, "password");
        defaultUser.setId(id);
        User resultUser = userDao.getUserById(id);
        assertEquals(defaultUser, resultUser);
    }

    // TODO: То же самое
    @Test
    public void testGetUserByUsername() throws SQLException, DuplicateUserException {
        User defaultUser = buildDefaultUser();
        int id = userDao.addUser(defaultUser, "password");
        defaultUser.setId(id);
        User resultUser = userDao.getUserByUsername(defaultUser.getUsername());
        assertEquals(resultUser, defaultUser);
    }

    // TODO: Сортировка?
    // TODO: Параметры самих пользователей?
    @Test
    public void testGetAllUsers() throws SQLException, DuplicateUserException {
        User defaultUser = buildDefaultUser();
        List<User> userList = userDao.getAllUsers();
        userDao.addUser(defaultUser, "testUser");
        List<User> resultUserList = userDao.getAllUsers();
        assertTrue(userList.size() + 1 == resultUserList.size());
    }

    @Test
    public void testAddUser() throws SQLException, DuplicateUserException {
        User defaultUser = buildDefaultUser();
        int id = userDao.addUser(defaultUser, "password");
        User user = userDao.getUserById(id);
        assertEquals(defaultUser.getUsername(), user.getUsername());
        assertEquals(defaultUser.getUsername(), user.getUsername());
        assertEquals(defaultUser.getFirstName(), user.getFirstName());
        assertEquals(defaultUser.getLastName(), user.getLastName());
        assertEquals(defaultUser.getRole(), user.getRole());
        assertTrue(id == user.getId());
    }

    @Test
    public void testDeleteUserById() throws SQLException, DuplicateUserException {
        User defaultUser = buildDefaultUser();
        int id = userDao.addUser(defaultUser, "testUser");
        User user = userDao.getUserById(id);
        assertTrue(user != null);
        userDao.deleteUserById(id);
        User dropUser = userDao.getUserById(id);
        assertTrue(dropUser == null);
    }

    @Test
    public void testUpdateUserWithPassword() throws SQLException, DuplicateUserException {
        User defaultUser = buildDefaultUser();
        int id = userDao.addUser(defaultUser, "password");
        defaultUser.setId(id);
        User updateUser = new User(0, "updateUserName", "updateFirstName", "updateLastName", Role.MANAGER);
        updateUser.setId(id);
        userDao.updateUser(updateUser, "UpdatePassword");
        updateUser = userDao.getUserById(id);
        assertFalse(defaultUser.getUsername().equals(updateUser.getUsername()));
        assertNotNull(userDao.getUser(updateUser.getUsername(), "UpdatePassword"));
        assertFalse(defaultUser.getFirstName().equals(updateUser.getFirstName()));
        assertFalse(defaultUser.getLastName().equals(updateUser.getLastName()));
        assertEquals(Role.MANAGER, updateUser.getRole());
        assertTrue(defaultUser.getId() == updateUser.getId());
    }

    @Test
    public void testUpdateWithoutPassword() throws SQLException, DuplicateUserException {
        User defaultUser = buildDefaultUser();
        int id = userDao.addUser(defaultUser, "password");
        defaultUser.setId(id);
        User updateUser = new User(0, "updateUserName", "updateFirstName", "updateLastName", Role.MANAGER);
        updateUser.setId(id);
        userDao.updateUser(updateUser);
        updateUser = userDao.getUserById(id);
        assertFalse(defaultUser.getUsername().equals(updateUser.getUsername()));
        assertNotNull(userDao.getUser(updateUser.getUsername(), "password"));
        assertFalse(defaultUser.getFirstName().equals(updateUser.getFirstName()));
        assertFalse(defaultUser.getLastName().equals(updateUser.getLastName()));
        assertEquals(Role.MANAGER, updateUser.getRole());
        assertTrue(defaultUser.getId() == updateUser.getId());
    }

    private static User buildDefaultUser() {
        return new User(0, "username", "firstName", "lastName", Role.DEFAULT);
    }

}