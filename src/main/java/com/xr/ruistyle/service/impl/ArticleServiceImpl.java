package com.xr.ruistyle.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xr.ruistyle.dto.ArticleDTO;
import com.xr.ruistyle.entity.Article;
import com.xr.ruistyle.entity.ArticleTag;
import com.xr.ruistyle.mapper.ArticleMapper;
import com.xr.ruistyle.service.ArticleService;
import com.xr.ruistyle.service.ArticleTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    @Autowired
    private ArticleTagService articleTagService;

    @Override
    @Transactional(rollbackFor = Exception.class) // 开启事务，保证两张表要么都成功，要么都失败
    public void saveArticleWithTags(ArticleDTO articleDTO) {
        // 1. 保存文章基本信息
        this.save(articleDTO);

        // 2. 获取生成的文章 ID
        Long articleId = articleDTO.getId();

        // 3. 批量保存关联关系
        List<Long> tagIds = articleDTO.getTagIds();
        if (tagIds != null && !tagIds.isEmpty()) {
            List<ArticleTag> articleTags = tagIds.stream().map(tagId -> {
                ArticleTag at = new ArticleTag();
                at.setArticleId(articleId);
                at.setTagId(tagId);
                return at;
            }).collect(Collectors.toList());

            articleTagService.saveBatch(articleTags); // 批量插入，效率更高
        }
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateArticleWithTags(ArticleDTO articleDTO) {
        // 1. 更新文章基本信息
        // (因为你的 DTO 里包含了更新所需的 id、title 等字段，需要转成实体类再更新)
        Article article = new Article();
        org.springframework.beans.BeanUtils.copyProperties(articleDTO, article);
        this.updateById(article);

        // 2. 核心：清理旧账 (DELETE FROM article_tag WHERE article_id = ?)
        // 使用你注入的 articleTagService 来执行批量删除
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ArticleTag> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleTag::getArticleId, article.getId());
        articleTagService.remove(queryWrapper);

        // 3. 建立新关联 (完美复用你之前的 Stream + saveBatch 优雅写法)
        List<Long> tagIds = articleDTO.getTagIds();
        if (tagIds != null && !tagIds.isEmpty()) {
            List<ArticleTag> articleTags = tagIds.stream().map(tagId -> {
                ArticleTag at = new ArticleTag();
                at.setArticleId(article.getId());
                at.setTagId(tagId);
                return at;
            }).collect(Collectors.toList());

            articleTagService.saveBatch(articleTags); // 批量插入，效率拉满
        }
    }
}