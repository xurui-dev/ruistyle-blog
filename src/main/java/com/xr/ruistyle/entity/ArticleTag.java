package com.xr.ruistyle.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 文章与标签的关联实体类（多对多关系）
 */
@Data
@TableName("article_tag")
public class ArticleTag {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long articleId;

    private Long tagId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}