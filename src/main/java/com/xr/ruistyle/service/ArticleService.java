package com.xr.ruistyle.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xr.ruistyle.dto.ArticleDTO;
import com.xr.ruistyle.entity.Article;

public interface ArticleService extends IService<Article> {

    void saveArticleWithTags(ArticleDTO articleDTO);
}