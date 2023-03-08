package dev.jlkesh.java_telegram_bots.config;

import dev.jlkesh.java_telegram_bots.domains.UserDomain;
import dev.jlkesh.java_telegram_bots.dto.Response;
import dev.jlkesh.java_telegram_bots.state.DefaultState;
import dev.jlkesh.java_telegram_bots.utils.factory.SendMessageFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static dev.jlkesh.java_telegram_bots.config.ThreadSafeBeansContainer.*;


public class InitializerConfiguration {

    public static void init() {
        Response<List<UserDomain>> response = userService.get().getAllUsers();
        if ( !response.isSuccess() ) {
            // TODO: 05/02/23 log
            System.err.println(response.getDeveloperErrorMessage());
            System.exit(-1);
        } else {
            List<UserDomain> users = response.getBody();
            users.forEach((user) -> {
                userState.put(user.getChatID(), DefaultState.MAIN_STATE);
                CompletableFuture.runAsync(() -> TelegramBotConfiguration.get().execute(
                        // TODO: 07/02/23 localized
                        SendMessageFactory.sendMessageWithMainMenu(user.getChatID(), "The bot restarted", user.getLanguage()))
                );
            });
        }
    }
}
