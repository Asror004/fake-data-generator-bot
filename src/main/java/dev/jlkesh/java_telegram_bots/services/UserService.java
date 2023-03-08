package dev.jlkesh.java_telegram_bots.services;

import com.pengrad.telegrambot.model.Message;
import dev.jlkesh.java_telegram_bots.daos.UserDao;
import dev.jlkesh.java_telegram_bots.domains.History;
import dev.jlkesh.java_telegram_bots.domains.UserDomain;
import dev.jlkesh.java_telegram_bots.dto.Response;
import dev.jlkesh.java_telegram_bots.faker.FakerApplicationGenerateRequest;
import dev.jlkesh.java_telegram_bots.utils.BaseUtils;
import dev.jlkesh.java_telegram_bots.utils.PasswordEncoderUtils;
import lombok.NonNull;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public Response<Void> create(@NonNull UserDomain domain) {
        try {
            domain.setPassword(PasswordEncoderUtils.encode(domain.getPassword()));
            userDao.save(domain);
            return new Response<>(null);
        } catch (SQLException e) {
            // TODO: 05/02/23 log
            e.printStackTrace();
            return new Response<>("Something is wrong try later again", BaseUtils.getStackStraceAsString(e));
        }
    }

    public Response<List<UserDomain>> getAllUsers() {
        try {
            return new Response<>(userDao.findAll());
        } catch (SQLException e) {
            // TODO: 05/02/23 log
            e.printStackTrace();
            return new Response<>(e.getMessage(), BaseUtils.getStackStraceAsString(e));
        }
    }

    public Response<Void> addHistory(Message eMessage, FakerApplicationGenerateRequest faker) {
        try {
            userDao.addHistory(eMessage, faker);
            return new Response<>(null);
        }catch (SQLException e){
            e.printStackTrace();
            return new Response<>("Exception", BaseUtils.getStackStraceAsString(e));
        }
    }

    public Response<Boolean> hasUsername(String username) {
        try {
            return new Response<>(userDao.hasUsername(username));
        }catch (SQLException e){
            e.printStackTrace();
            return new Response<>("Exception", BaseUtils.getStackStraceAsString(e));
        }
    }

    public Response<HashMap<Object, History>> getUserFiles(Integer offset, Long chatID) {
        try {
            return new Response<>(userDao.findUserFiles(offset, chatID));
        }catch (SQLException e){
            e.printStackTrace();
            return new Response<>("Exception", BaseUtils.getStackStraceAsString(e));
        }
    }
}
