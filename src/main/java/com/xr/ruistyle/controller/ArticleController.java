package com.xr.ruistyle.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xr.ruistyle.common.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.xr.ruistyle.common.Result;
import com.xr.ruistyle.entity.Article;
import com.xr.ruistyle.service.ArticleService;

@Tag(name = "文章管理", description = "文章的发布与查询接口")
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

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
}