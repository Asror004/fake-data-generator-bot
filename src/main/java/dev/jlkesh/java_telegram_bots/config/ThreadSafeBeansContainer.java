package dev.jlkesh.java_telegram_bots.config;

import dev.jlkesh.java_telegram_bots.daos.UserDao;
import dev.jlkesh.java_telegram_bots.domains.History;
import dev.jlkesh.java_telegram_bots.dto.Dictionary;
import dev.jlkesh.java_telegram_bots.faker.FakerApplicationGenerateRequest;
import dev.jlkesh.java_telegram_bots.faker.Field;
import dev.jlkesh.java_telegram_bots.handlers.CallbackHandler;
import dev.jlkesh.java_telegram_bots.handlers.Handler;
import dev.jlkesh.java_telegram_bots.handlers.MessageHandler;
import dev.jlkesh.java_telegram_bots.processors.callback.DefaultCallbackProcessor;
import dev.jlkesh.java_telegram_bots.processors.callback.GenerateDataCallbackProcessor;
import dev.jlkesh.java_telegram_bots.processors.callback.RegisterUserCallbackProcessor;
import dev.jlkesh.java_telegram_bots.processors.message.DefaultMessageProcessor;
import dev.jlkesh.java_telegram_bots.processors.message.GenerateDataMessageProcessor;
import dev.jlkesh.java_telegram_bots.processors.message.RegistrationMessageProcessor;
import dev.jlkesh.java_telegram_bots.services.UserService;
import dev.jlkesh.java_telegram_bots.state.State;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadSafeBeansContainer {
    public static final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    public static final ThreadLocal<Handler> messageHandler = ThreadLocal.withInitial(MessageHandler :: new);
    public static final ThreadLocal<Handler> callbackHandler = ThreadLocal.withInitial(CallbackHandler :: new);
    public static final ConcurrentHashMap<Long, State> userState = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Object, Dictionary<String, Object>> collected = new ConcurrentHashMap<>();
    public static final ThreadLocal<UserDao> userDao = ThreadLocal.withInitial(UserDao :: new);
    public static final ThreadLocal<UserService> userService = ThreadLocal.withInitial(() -> new UserService(userDao.get()));
    public static final ThreadLocal<RegisterUserCallbackProcessor> registerUserCallbackProcessor = ThreadLocal.withInitial(RegisterUserCallbackProcessor :: new);
    public static final ThreadLocal<GenerateDataCallbackProcessor> generateDataCallbackProcessor = ThreadLocal.withInitial(GenerateDataCallbackProcessor :: new);
    public static final ThreadLocal<DefaultCallbackProcessor> defaultCallbackProcessor = ThreadLocal.withInitial(DefaultCallbackProcessor :: new);
    public static final ThreadLocal<DefaultMessageProcessor> defaultMessageProcessor = ThreadLocal.withInitial(DefaultMessageProcessor :: new);
    public static final ThreadLocal<RegistrationMessageProcessor> registrationMessageProcessor = ThreadLocal.withInitial(RegistrationMessageProcessor :: new);
    public static final ThreadLocal<GenerateDataMessageProcessor> generateDataMessageProcessor = ThreadLocal.withInitial(GenerateDataMessageProcessor :: new);
    public static final ConcurrentHashMap<Long, FakerApplicationGenerateRequest> fakerApplicationGenerateRequest = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Long, Field> fieldMap = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Long, Integer> offset = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Long, HashMap<Object, History>> userFiles = new ConcurrentHashMap<>();
}
