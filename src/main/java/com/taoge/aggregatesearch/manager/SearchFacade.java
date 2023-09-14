package com.taoge.aggregatesearch.manager;/*
 *Author:Litao
 *Created in:
 */

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.taoge.aggregatesearch.common.ErrorCode;
import com.taoge.aggregatesearch.datasource.*;
import com.taoge.aggregatesearch.exception.ThrowUtils;
import com.taoge.aggregatesearch.model.dto.search.SearchQueryRequest;
import com.taoge.aggregatesearch.model.entity.Picture;
import com.taoge.aggregatesearch.model.enums.SearchTypeEnum;
import com.taoge.aggregatesearch.model.vo.PostVO;
import com.taoge.aggregatesearch.model.vo.SearchVO;
import com.taoge.aggregatesearch.model.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Component
@Slf4j
public class SearchFacade {
    @Resource
    PostDataSource postDataSource;

    @Resource
    PictureDataSource pictureDataSource;

    @Resource
    UserDataSource userDataSource;

    @Resource
    DataSourceRegistry dataSourceRegistry;


    public SearchVO searchAll(@RequestBody SearchQueryRequest searchQueryRequest,
        HttpServletRequest request) {
        String type = searchQueryRequest.getType();
        String searchText = searchQueryRequest.getSearchText();
        long current = searchQueryRequest.getCurrent();
        long pageSize = searchQueryRequest.getPageSize();

        SearchTypeEnum searchTypeEnum = SearchTypeEnum.getEnumByValue(type);
        ThrowUtils.throwIf(StringUtils.isBlank(type), ErrorCode.PARAMS_ERROR);
        if(searchTypeEnum == null){

            Page<Picture> picturePage = pictureDataSource.doSearch(searchText,current,pageSize);

            Page<UserVO> userVOPage = userDataSource.doSearch(searchText,current,pageSize);

            Page<PostVO> postVOPage = postDataSource.doSearch(searchText,current,pageSize);

            SearchVO searchVO = new SearchVO();
            searchVO.setUserList(userVOPage.getRecords());
            searchVO.setPostList(postVOPage.getRecords());
            searchVO.setPictureList(picturePage.getRecords());
            return searchVO;
        }
        else{
            DataSource<?> dataSource = dataSourceRegistry.getDataSourceByType(type);
            Page page = dataSource.doSearch(searchText, current, pageSize);
            SearchVO searchVO = new SearchVO();
            searchVO.setDataList(page.getRecords());
            return searchVO;
        }
    }
}
