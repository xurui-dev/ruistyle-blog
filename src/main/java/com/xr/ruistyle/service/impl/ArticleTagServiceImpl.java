package com.xr.ruistyle.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xr.ruistyle.entity.ArticleTag;
import com.xr.ruistyle.mapper.ArticleTagMapper;
import com.xr.ruistyle.service.ArticleTagService;
import org.springframework.stereotype.Service;

/**
 * 文章-标签关联表 Service 实现类
 */
@Service
public class ArticleTagServiceImpl extends ServiceImpl<ArticleTagMapper, ArticleTag> implements ArticleTagService {
}