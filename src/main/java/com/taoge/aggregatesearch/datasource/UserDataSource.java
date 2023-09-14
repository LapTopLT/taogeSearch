package com.taoge.aggregatesearch.datasource;/*
 *Author:Litao
 *Created in:
 */

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.taoge.aggregatesearch.model.dto.user.UserQueryRequest;
import com.taoge.aggregatesearch.model.vo.UserVO;
import com.taoge.aggregatesearch.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserDataSource implements DataSource<UserVO>{
    @Resource
    private UserService userService;


    @Override
    public Page<UserVO> doSearch(String searchText, long pageNum, long pageSize) {
        UserQueryRequest userQueryRequest = new UserQueryRequest();
        userQueryRequest.setUserName(searchText);
        userQueryRequest.setCurrent(pageNum);
        userQueryRequest.setPageSize(pageSize);
        return userService.listUserVOByPage(userQueryRequest);
    }
}
