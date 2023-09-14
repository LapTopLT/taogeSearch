package com.taoge.aggregatesearch.datasource;/*
 *Author:Litao
 *Created in:
 */

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.taoge.aggregatesearch.model.entity.Picture;
import com.taoge.aggregatesearch.service.PictureService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class PictureDataSource implements DataSource<Picture>{
    @Resource
    PictureService pictureService;

    @Override
    public Page<Picture> doSearch(String searchText, long pageNum, long pageSize) {
        return pictureService.searchPicture(searchText, pageNum, pageSize);
    }
}
