package com.xr.ruistyle.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xr.ruistyle.common.BusinessException;
import com.xr.ruistyle.common.ResultCodeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import com.xr.ruistyle.common.Result;
import com.xr.ruistyle.entity.Article;
import com.xr.ruistyle.service.ArticleService;

import java.util.concurrent.TimeUnit;

@Tag(name = "文章管理", description = "文章的发布与查询接口")
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    // 🌟 注入 Redis 操作模板
    private final StringRedisTemplate stringRedisTemplate;

    @Operation(summary = "发布新文章(含标签)")
    @PostMapping
    public Result<String> publishArticle(@RequestBody com.xr.ruistyle.dto.ArticleDTO articleDTO) {
        // 调用带事务的方法，同时保存文章和标签关联
        articleService.saveArticleWithTags(articleDTO);
        return Result.success("文章发布成功");
    }

    @Operation(summary = "分页查询文章列表")
    @GetMapping
    public Result<Page<Article>> pageQuery(
            @RequestParam(defaultValue = "1") Integer current, // 当前页码，默认第1页
            @RequestParam(defaultValue = "10") Integer size,   // 每页条数，默认10条
            @RequestParam(required = false) Long categoryId) { // 分类ID，非必传参数

        // 1. 构造分页对象
        Page<Article> pageParam = new Page<>(current, size);

        // 2. 构造查询条件构造器
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();

        // 如果前端传了 categoryId，就加上 where category_id = ? 的条件
        if (categoryId != null) {
            wrapper.eq(Article::getCategoryId, categoryId);
        }

        // 按照创建时间倒序排列 (最新的文章排在最前面)
        wrapper.orderByDesc(Article::getCreateTime);

        // 3. 执行分页查询 (MyBatis Plus 会自动帮你执行 count 语句和 limit 语句)
        Page<Article> resultPage = articleService.page(pageParam, wrapper);

        return Result.success(resultPage);
    }


    @Operation(summary = "获取文章详情")
    @GetMapping("/{id}")
    public Result<Article> getArticleDetail(@PathVariable Long id) {
        Article article = articleService.getById(id);
        if (article == null) {
            throw new BusinessException(404, "文章不存在");
        }

        // 亮点：异步或原子自增浏览量
        // 这里我们先用最简单且安全的方式：直接更新数据库
        articleService.update().setSql("view_count = view_count + 1").eq("id", id).update();

        // 更新后重新设置对象里的值，保证返回给前端的是最新数据
        article.setViewCount(article.getViewCount() + 1);

        return Result.success(article);
    }

    @Operation(summary = "删除文章")
    @DeleteMapping("/{id}")
    public Result<String> deleteArticle(@PathVariable Long id) {
        // MyBatis Plus 自带的根据 ID 删除方法
        boolean success = articleService.removeById(id);
        if (success) {
            return Result.success("删除成功");
        } else {
            throw new BusinessException(500, "删除失败，文章可能不存在");
        }
    }

    @PutMapping
    public Result<String> updateArticle(@RequestBody com.xr.ruistyle.dto.ArticleDTO articleDTO) {
        if (articleDTO.getId() == null) {
            return Result.fail(ResultCodeEnum.PARAM_ERROR);
        }
        articleService.updateArticleWithTags(articleDTO);
        return Result.success("文章更新成功");
    }

    @Operation(summary = "点赞文章(带Redis IP防刷)")
    @PostMapping("/{id}/like")
    public Result<String> likeArticle(@PathVariable Long id, HttpServletRequest request) {

        // 1. 获取用户的真实 IP（生产环境如果用了 Nginx，需要拿 X-Forwarded-For，这里用最简单的演示）
        String ip = request.getRemoteAddr();
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }

        // 2. 组装 Redis 的 Key。格式为：类目:动作:文章ID:用户IP
        String redisKey = "blog:like:article:" + id + ":ip:" + ip;

        // 3. 核心防刷逻辑：尝试向 Redis 写入这个 Key，有效期 24 小时
        // setIfAbsent 底层就是 Redis 的 SETNX。如果 Key 不存在，写入成功返回 true；如果已存在，返回 false。
        Boolean isFirstTime = stringRedisTemplate.opsForValue().setIfAbsent(redisKey, "1", 24, TimeUnit.HOURS);

        if (Boolean.TRUE.equals(isFirstTime)) {
            // 4. Redis 里没有记录，说明是24小时内第一次点赞。放行！修改 MySQL 数据库。
            // 利用 MyBatis Plus 的原子更新，防止并发超卖
            boolean success = articleService.update()
                    .setSql("like_count = like_count + 1")
                    .eq("id", id)
                    .update();

            if (success) {
                return Result.success("点赞成功！");
            } else {
                // 如果数据库更新失败，记得把 Redis 里的记录删掉，给用户重试的机会
                stringRedisTemplate.delete(redisKey);
                return Result.fail(500, "点赞异常，请稍后再试");
            }
        } else {
            // 5. Redis 里查到了这个 IP 的记录，直接无情拦截！绝对不碰 MySQL！
            return Result.fail(400, "您今天已经为这篇文章点过赞啦，明天再来吧！");
        }
    }
}