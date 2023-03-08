package dev.jlkesh.java_telegram_bots.domains;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@Builder
public class History {
    private String userChatId;
    private String fileId;
    private long size;
    private String fileName;
    private int rowCount;
    private int fieldCount;
    private LocalDateTime createdAt;
}
