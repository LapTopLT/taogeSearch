package com.taoge.aggregatesearch.crawler;/*
 *Author:Litao
 *Created in:
 */
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.taoge.aggregatesearch.model.entity.Picture;
import com.taoge.aggregatesearch.model.entity.Post;
import com.taoge.aggregatesearch.service.PostService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static com.sun.activation.registries.LogSupport.log;

@SpringBootTest
public class CrawlerTest {
    @Resource
    private PostService postService;

    @Test
    void pictureFetch() throws IOException {
        int curr = 1;
        String url = String.format("https://cn.bing.com/images/search?q=八方旅人2&form=HDRSC2&first=%d",curr);
        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.select(".iuscp.isv");
        List<Picture> pictures = new ArrayList<>();
        for (Element element : elements) {
            String m = element.select(".iusc").get(0).attr("m");
            String title = element.select(".inflnk").get(0).attr("aria-label");
            Map<String,Object>  map = JSONUtil.toBean(m,Map.class);
            String murl = (String) map.get("murl");
            System.out.println(murl);
            System.out.println(title);
            Picture pic = new Picture();
            pic.setTitle(title);
            pic.setUrl(murl);
            pictures.add(pic);
        }
//        Elements newsHeadlines = doc.select("#mp-itn b a");
//        for (Element headline : newsHeadlines) {
//
//        }
    }

    @Test
    void passageFetch(){
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
        Assertions.assertTrue(b);
    }
}
