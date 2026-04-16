package com.xr.ruistyle.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 文章核心实体类
 */
@Data
@TableName("article")
public class Article {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String title;

    private String summary;

    private String content;

    private Integer viewCount;

    // 关联的分类ID
    private Long categoryId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}