package com.taoge.aggregatesearch.controller;/*
 *Author:Litao
 *Created in:
 */

import com.taoge.aggregatesearch.common.BaseResponse;
import com.taoge.aggregatesearch.common.ResultUtils;
import com.taoge.aggregatesearch.manager.SearchFacade;
import com.taoge.aggregatesearch.model.dto.search.SearchQueryRequest;
import com.taoge.aggregatesearch.model.vo.SearchVO;
import com.taoge.aggregatesearch.service.PictureService;
import com.taoge.aggregatesearch.service.PostService;
import com.taoge.aggregatesearch.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/search")
@Slf4j
public class SearchController {

    @Resource
    PictureService pictureService;

    @Resource
    PostService postService;

    @Resource
    UserService userService;

    @Resource
    SearchFacade searchFacade;

    @PostMapping("/all")
    public BaseResponse<SearchVO> searchAll(@RequestBody SearchQueryRequest searchQueryRequest,HttpServletRequest request) {
        return ResultUtils.success(searchFacade.searchAll(searchQueryRequest,request));
    }
}
