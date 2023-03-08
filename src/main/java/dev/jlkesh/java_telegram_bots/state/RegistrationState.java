package dev.jlkesh.java_telegram_bots.state;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RegistrationState implements State {
    USERNAME,
    PASSWORD
}
