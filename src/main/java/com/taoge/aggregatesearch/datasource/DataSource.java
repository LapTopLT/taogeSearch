package com.taoge.aggregatesearch.datasource;/*
 *Author:Litao
 *Created in:
 */

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;


/**
 * 数据源接口（新接入的数据源必须实现）
 */

public interface DataSource<T> {
    Page<T> doSearch(String searchText, long pageNum, long pageSize);
}
