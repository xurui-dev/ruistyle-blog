package com.xr.ruistyle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xr.ruistyle.entity.Article;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
}