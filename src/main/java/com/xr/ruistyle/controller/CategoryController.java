package com.xr.ruistyle.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.xr.ruistyle.common.Result;
import com.xr.ruistyle.entity.Category;
import com.xr.ruistyle.service.CategoryService;

@Tag(name = "分类管理", description = "文章分类的增删改查接口")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "新增分类")
    @PostMapping
    public Result<String> addCategory(@Valid @RequestBody Category category) {
        // save 方法是 MyBatis Plus 内置的
        boolean success = categoryService.save(category);
        if (success) {
            return Result.success("分类保存成功,热部署生效啦！");
        }
        return Result.fail(500, "分类保存失败");
    }
}