package com.xr.ruistyle.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 文章核心实体类
 */
@Data
@TableName("article")
public class Article {

    @TableId(type = IdType.ASSIGN_ID)
    // 重点就是加这一行注解：让 Jackson 把它序列化成字符串发给前端
    @JsonSerialize(using = ToStringSerializer.class)
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