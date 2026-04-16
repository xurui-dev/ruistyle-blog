package com.xr.ruistyle.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xr.ruistyle.entity.Category;
import com.xr.ruistyle.mapper.CategoryMapper;
import com.xr.ruistyle.service.CategoryService;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
}