package com.taoge.aggregatesearch.job.once;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.taoge.aggregatesearch.model.entity.Post;
import com.taoge.aggregatesearch.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 获取初始化帖子内容
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
// todo 取消注释开启任务
@Component
@Slf4j
public class GetInitPosts implements CommandLineRunner {

    @Resource
    private PostService postService;

    @Override
    public void run(String... args) {
        //1.获取数据
        String json = "{\"current\":1,\"pageSize\":8,\"sortField\":\"createTime\",\"sortOrder\":\"descend\",\"category\":\"文章\",\"reviewStatus\":1}";
        String url = "https://www.code-nav.cn/api/post/search/page/vo";
        String result = HttpRequest.post(url)
            .body(json)
            .execute().body();
        //2. json 转对象
        Map<String, Object> map = JSONUtil.toBean(result, Map.class);
        JSONObject data = (JSONObject) map.get("data");
        JSONArray records = (JSONArray) data.get("records");
        List<Post> postList = new ArrayList<>();
        for (Object record : records) {
            JSONObject object = (JSONObject) record;
            Post post = new Post();
            post.setTitle(object.getStr("title"));
            post.setContent(object.getStr("content"));
            JSONArray tags = (JSONArray) object.get("tags");
            List<String> tagList = tags.toList(String.class);
            post.setTags(JSONUtil.toJsonStr(tagList));
            post.setThumbNum(object.getInt("thumbNum"));
            post.setFavourNum(object.getInt("favourNum"));
            post.setUserId(1L);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss.SSSXXX");
            try {
                post.setCreateTime(sdf.parse(object.getStr("createTime")));
                post.setUpdateTime(sdf.parse(object.getStr("updateTime")));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            postList.add(post);
        }
        boolean b = postService.saveBatch(postList);
        if(b){
            log.info("获取帖子初始化列表成功，条数{}",postList.size());
        }else{
            log.error("获取帖子初始化列表失败");
        }
    }
}
