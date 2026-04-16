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
}