package com.xr.ruistyle.dto;

import com.xr.ruistyle.entity.Article;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ArticleDTO extends Article {
    // 接收前端传来的标签 ID 列表
    private List<Long> tagIds;
}