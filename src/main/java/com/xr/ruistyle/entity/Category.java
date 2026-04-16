package com.xr.ruistyle.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 文章分类实体类
 */
@Data
@TableName("category")
public class Category {

    // 使用 ASSIGN_ID 配合 MySQL 的 BIGINT，MyBatis Plus 会自动生成雪花算法 ID
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    @NotBlank(message = "分类名称不能为空")
    private String name;

    private Integer sort;

    // 插入时自动填充
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // 插入和更新时自动填充
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 逻辑删除标志
    @TableLogic
    private Integer deleted;
}