package com.xr.ruistyle.controller;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.xr.ruistyle.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j // 🌟 优化 1：添加 Lombok 日志注解，抛弃 e.printStackTrace()
@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Value("${deepseek.api-key}")
    private String apiKey;

    @Value("${deepseek.api-url}")
    private String apiUrl;

    @PostMapping("/chat")
    public Result<String> chat(@RequestBody(required = false) com.xr.ruistyle.dto.AiChatDTO aiChatDTO) {

        // 🌟 防弹校验也跟着变一下
        if (aiChatDTO == null || aiChatDTO.getMessage() == null) {
            return Result.fail(400, "提问不能为空！");
        }

        String userMessage = aiChatDTO.getMessage();
        if (userMessage.trim().isEmpty()) {
            return Result.fail(400, "提问内容不能为空格！");
        }

        try {
            // 1. 构造请求体 (标准 OpenAI 格式)
            Map<String, Object> body = new HashMap<>();
            body.put("model", "deepseek-chat");
            body.put("messages", List.of(
                    Map.of("role", "system", "content", "你是一个嵌入在徐锐博客里的AI写作助手。"),
                    Map.of("role", "user", "content", userMessage)
            ));

            // 2. 发送请求
            String jsonResponse = HttpRequest.post(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .body(JSONUtil.toJsonStr(body))
                    .timeout(20000)
                    .execute().body();

            // 3. 安全解析结果
            if (jsonResponse != null && jsonResponse.contains("choices")) {
                String aiResult = JSONUtil.parseObj(jsonResponse)
                        .getByPath("choices[0].message.content", String.class);
                return Result.success(aiResult); // 调用你自定义的 success 方法
            } else {
                // 🌟 优化 3：外部接口调用失败时，将脏数据打印到日志里，而不是直接抛给前端用户看
                log.error("DeepSeek调用失败，原始返回：{}", jsonResponse);
                // 调用你的 fail 方法，传入 500 状态码
                return Result.fail(500, "AI 接口调用异常，请稍后再试");
            }

        } catch (Exception e) {
            // 🌟 优化 4：把异常堆栈打进系统日志，方便排查，给前端返回友好的提示
            log.error("连接 DeepSeek 服务器时发生内部异常", e);
            return Result.fail(500, "服务器网络连接异常：" + e.getMessage());
        }
    }
    /**
     * 核心通信方法封装 (避免重复写 Http 请求代码)
     * @param systemPrompt 给 AI 的身份设定
     * @param userContent 用户传过来的实际内容
     */
    private String callDeepSeek(String systemPrompt, String userContent) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("model", "deepseek-chat");
        body.put("messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userContent)
        ));

        String jsonResponse = HttpRequest.post(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(body))
                .timeout(30000) // 生成摘要可能需要多一点时间，改成 30 秒
                .execute().body();

        if (jsonResponse != null && jsonResponse.contains("choices")) {
            return JSONUtil.parseObj(jsonResponse).getByPath("choices[0].message.content", String.class);
        }
        throw new RuntimeException("调用失败：" + jsonResponse);
    }

    @PostMapping("/polish-title")
    public Result<String> polishTitle(@RequestBody com.xr.ruistyle.dto.AiChatDTO dto) {
        if (dto == null || dto.getMessage() == null || dto.getMessage().trim().isEmpty()) {
            return Result.fail(400, "原标题不能为空");
        }
        try {
            // 设定极其严苛的规则，确保 AI 只返回标题，不返回多余的废话
            String systemPrompt = "你是一个爆款技术文章的起名专家。请帮我润色以下标题，使其更具吸引力、更专业。要求：只返回润色后的标题文本，不要包含任何标点符号、解释或确认语。字数控制在 20 字以内。";
            String result = callDeepSeek(systemPrompt, dto.getMessage());
            return Result.success(result);
        } catch (Exception e) {
            log.error("AI 润色标题失败", e);
            return Result.fail(500, "AI 润色失败");
        }
    }

    @PostMapping("/generate-summary")
    public Result<String> generateSummary(@RequestBody com.xr.ruistyle.dto.AiChatDTO dto) {
        if (dto == null || dto.getMessage() == null || dto.getMessage().trim().isEmpty()) {
            return Result.fail(400, "文章正文不能为空");
        }
        try {
            String systemPrompt = "你是一个专业的技术博客编辑。请根据用户提供的 Markdown 正文，提取一段精炼的文章摘要。要求：客观陈述，突出核心技术点和解决的问题。字数严格控制在 100 字左右。不要包含 Markdown 语法，只返回纯文本摘要。";
            String result = callDeepSeek(systemPrompt, dto.getMessage());
            return Result.success(result);
        } catch (Exception e) {
            log.error("AI 生成摘要失败", e);
            return Result.fail(500, "AI 生成摘要失败");
        }
    }
}