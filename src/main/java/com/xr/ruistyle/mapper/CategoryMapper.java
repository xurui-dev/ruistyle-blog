package com.xr.ruistyle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xr.ruistyle.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}