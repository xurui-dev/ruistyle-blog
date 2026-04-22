package com.xr.ruistyle.dto;

import lombok.Data;

/**
 * AI 聊天参数接收类
 */
@Data
public class AiChatDTO {
    // 明确告诉前端和 Swagger，我们需要一个叫 message 的参数
    private String message;
}