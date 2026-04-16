package com.xr.ruistyle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xr.ruistyle.entity.ArticleTag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文章-标签关联表 Mapper 接口
 */
@Mapper
public interface ArticleTagMapper extends BaseMapper<ArticleTag> {
}