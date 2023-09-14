package com.taoge.aggregatesearch.datasource;/*
 *Author:Litao
 *Created in:
 */

import com.taoge.aggregatesearch.model.enums.SearchTypeEnum;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
public class DataSourceRegistry {
    @Resource
    private PostDataSource postDataSource;

    @Resource
    private  PictureDataSource pictureDataSource;

    @Resource
    private  UserDataSource userDataSource;

    /**
     * 初始化datasourcemap
     */
    Map<String, DataSource> typeDataSourceMap;

    @PostConstruct
    public void init(){
        typeDataSourceMap = new HashMap(){{
            put(SearchTypeEnum.POST.getValue(),postDataSource);
            put(SearchTypeEnum.PICTURE.getValue(),pictureDataSource);
            put(SearchTypeEnum.USER.getValue(),userDataSource);
        }};
    }

        public DataSource getDataSourceByType(String type){
            return typeDataSourceMap.get(type);
        }
}
