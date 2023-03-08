package dev.jlkesh.java_telegram_bots.daos;

import com.pengrad.telegrambot.model.Document;
import com.pengrad.telegrambot.model.Message;
import dev.jlkesh.java_telegram_bots.domains.History;
import dev.jlkesh.java_telegram_bots.domains.UserDomain;
import dev.jlkesh.java_telegram_bots.faker.FakerApplicationGenerateRequest;
import lombok.NonNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class UserDao extends Dao {

    public void save(@NonNull UserDomain domain) throws SQLException {

        Connection connection = getConnection();

        String query = "insert into users(chatid,username, password, firstname,language) values(?,?,?,?,?);";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setLong(1, domain.getChatID());
            pst.setString(2, domain.getUsername());
            pst.setString(3, domain.getPassword());
            pst.setString(4, domain.getFirstName());
            pst.setString(5, domain.getLanguage());
            pst.execute();
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    public List<UserDomain> findAll() throws SQLException {

        Connection connection = getConnection();

        String query = "select chatid, username, firstname, created_at, language from users";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            ResultSet rs = pst.executeQuery();
            ArrayList<UserDomain> userDomains = new ArrayList<>();
            while (rs.next()) {
                userDomains.add(UserDomain.builder()
                        .chatID(rs.getLong("chatid"))
                        .username(rs.getString("username"))
                        .firstName(rs.getString("firstname"))
                        .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                        .language(rs.getString("language"))
                        .build());
            }
            return userDomains;
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    public void addHistory(Message eMessage, FakerApplicationGenerateRequest faker) throws SQLException {

        Connection connection = getConnection();

        String query = "insert into history(user_chat_id, file_id, size, file_name, row_count, field_count) values(?,?,?,?,?,?);";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            Document document = eMessage.document();
            pst.setLong(1, eMessage.chat().id());
            pst.setString(2, document.fileId());
            pst.setLong(3, document.fileSize());
            pst.setString(4, document.fileName());
            pst.setInt(5, faker.getCount());
            pst.setInt(6, faker.getFields().size());
            pst.execute();
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    public boolean hasUsername(String userName) throws SQLException {
        Connection connection = getConnection();

        String query = "select * from users where username ilike ?";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, userName);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        return false;
    }

    public HashMap<Object, History> findUserFiles(Integer offset, Long chatID) throws SQLException {

        String query = "select * from history where user_chat_id = ? order by created_at desc offset ? limit 10;";

        Connection connection = getConnection();

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, chatID.toString());
            pst.setInt(2, offset);
            ResultSet rs = pst.executeQuery();

            HashMap<Object, History> files = new HashMap<>();

            while (rs.next()) {
                files.put(
                        rs.getInt("id"),
                        History.builder()
                                .userChatId(rs.getString("user_chat_id"))
                                .fileId(rs.getString("file_id"))
                                .size(rs.getLong("size"))
                                .fileName(rs.getString("file_name"))
                                .rowCount(rs.getInt("row_count"))
                                .fieldCount(rs.getInt("field_count"))
                                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                                .build()
                );
            }

            return files.isEmpty() ? null : files;
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }
}
