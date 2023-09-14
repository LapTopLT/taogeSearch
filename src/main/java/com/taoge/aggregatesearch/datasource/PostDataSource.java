package com.taoge.aggregatesearch.datasource;/*
 *Author:Litao
 *Created in:
 */

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.taoge.aggregatesearch.model.dto.post.PostQueryRequest;
import com.taoge.aggregatesearch.model.entity.Post;
import com.taoge.aggregatesearch.model.vo.PostVO;
import com.taoge.aggregatesearch.service.PostService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Service
public class PostDataSource implements DataSource<PostVO>{
    @Resource
    private PostService postService;


    @Override
    public Page<PostVO> doSearch(String searchText, long pageNum, long pageSize) {
        PostQueryRequest postQueryRequest = new PostQueryRequest();
        postQueryRequest.setSearchText(searchText);
        postQueryRequest.setCurrent(pageNum);
        postQueryRequest.setPageSize(pageSize);

        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        Page<Post> postPage = postService.searchFromEs(postQueryRequest);

        return postService.getPostVOPage(postPage,request);
    }
}
