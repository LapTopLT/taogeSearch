# 淘歌聚合搜索平台

提供一个可复用的搜索平台框架，例如，在淘宝里搜索商品，结果可以有淘宝的，也可以有天猫的，用户无需知道商品具体在哪个平台。不需要为每个项目都写一个单独的搜索。

对于用户：用户体验更好。

对于企业：提高了企业开发效率。

# 技术栈

前端：

* Vue
* Ant Design
* Lodash

后端：

* SpringBoot
* MySQL
* Elasticsearch(Elastic Stack)
* 数据抓取
* 数据同步
    * 4种
    * logstash
    * Canal（阿里）
* Guava Retrying
* 怎么保证API的稳定性
* 设计模式：使用门面模式的思想，发送请求的一方不用关心后端从哪里，怎么去取不同来源，怎么去聚合不同来源的数据，更方便地获取内容。

# 业务流程

1. 先得到各种不同分类的数据。
2. 提供一个搜索页面（单一搜索 + 聚合搜索），支持搜索
3. 可以做一些优化，比如关键词高亮，防抖节流

# 项目计划

## 前端搭建

* 后端返回用户内容给前端时，不要返回用户敏感信息，如：密码。
* 将用户的搜索状态记录在URL中，刷新页面后仍保留当前的搜索状态。
    * 核心小技巧：把同步状态改成单向，即只允许url来改变页面状态，不允许反向
        1. 在用户在操作的时候，改变url地址(点击搜索框，搜索内容填充到url上，切换tab时，也要填充)
        2. 当url改变的时候，去改变页面状态（监听url的改变）

联调后端：

1. 前端整合Axios，并自定义Axios实例

## 后端搭建

1. 获取多种不同类型的数据源
    1. 文章（内部）
    2. 用户（内部）
    3. 图片（外部，不是我们自己的项目、自己的用户生产的数据）
2. 前后端搜索接口联调，跑通整个页面。
3. 听鱼皮分析现有项目的问题 =》优化，聚合接口的开发。

### 获取多种不同类型的数据源

内部没有，只能从互联网上获取基础数据 =》爬虫技术

抓取： https://www.code-nav.cn/learn/passage

#### 数据抓取的几种方式

1. 直接请求数据接口（最直接，最方便）HttpClient, OKHttp, RestTemplate, Hutool
2. 直接在渲染好的页面抓取前端内容
3. 有一些网站可能是动态请求的，他不会一次性加载所有的数据，而是要点某个按钮，输入某个验证码才会显示出数据。 =》 无头浏览器： selenium、node.js puppeteer

#### 数据抓取流程

1. 分析数据源（怎么获取）
2. 拿到数据后，怎么处理？
3. 写入数据库等存储。

#### 图片获取

实时抓取：我们自己的网站不存这些数据，用户要搜的时候，直接从别人的网站上爬取。

**jsoup库**：获取到HTML文档，然后从中解析出需要的字段。

#### 前后端联调，跑通整个页面

基本完成后，发现的几个问题以及目标：

点击搜索的时候，一次性查询出了用户、图片、文章三种结果。

不同的业务场景：

1）、在哪个标签页就调用当前标签页的接口。

2）、如果是针对聚合内容的网页，其实可以一个请求搞定。

3）、有可能要查询其他标签页的数据，比如其他数据的总数，同时给用户反馈

问题：

1. 请求数量比较多，浏览器对HTTP一般限制同时发送6个 => 用一个接口接收所有数据
2. 请求不同接口的参数可能不一致，增加前后端沟通成本。=> 用一个接口把请求参数统一，前端每次传固定的参数，后端去对参数进行转换。
3. 重复代码=>用一个接口，通过不同的参数去区分查询的数据源

#### **聚合接口优化：**

怎样让前端又能一次搜出全部数据，又能分别获取某一类数据（比如分页场景）

新增：前端传type调用后端同一个接口，后端根据type调用不同的service查询，

比如：tyoe = user, userService.query

#### 门面模式

帮助用户（客户端）更轻松的实现功能，不需要关心门面背后的业务细节。

聚合搜索类的业务基本都是门面模式：即前端不用关心后端从哪里，怎么去取不同来源，怎么去聚合不同来源的数据，更方便地获取内容。

当调用你系统的客户端觉得麻烦的时候，你就应该思考，是不是可以抽象一个门面了。

门面就像是酒店的前台。

#### 适配器模式

1. 定制统一的数据源接入规范：什么数据源允许接入？数据源要做什么事情？
   任何接入我们系统的数据，它必须要能够根据关键词搜索、并且支持分页搜索。
   声明接口来定义规范。
2. 加入我们的数据源已经支持了搜索，但是原有的方法参数和我们的规范不一致，怎么办？适配器模式的作用：通过转换，让两个系统能够完成对接。

## Elasticsearch

### Elasticsearch相关概念

把文章的内容分成词，再以词作为索引指向文章。

### ES的几种调用方式

1. restful api 调用 （HTTP请求

GET请求： http://localhost:9200/ (ES的启动端口)

http://localhost:9300（ES集群内部通信的）

curl可模拟发送请求： curl -X GET "localhost:9200/?pretty

**Kibana：**

kibana地址： localhost:5601

devtools

2. kibana devtools
3. Java客户端，Golang客户端等代码中调用

### ES的语法

#### DSL

性能最高的语法。JSON格式，和HTTP请求兼容，好理解。

建表/插入数据（创建一张名为post的表，里面有title和desc字段。）

```json
POST post/_doc
{
  "title": "李涛",
  "desc": "laptop"
}
```

查询语句：

```json
GET post/_search
{
  "query": {
    "match_all": { }
  },
  "sort": [
    {
    }
  ]
}
```

根据ID查询：

```
GET post/_doc/nopHf4oBgTuZNA2twVbx
```

删除语句：

```
DELETE _data_stream/post


```

更新语句：

```json
POST post/_doc/nopHf4oBgTuZNA2twVbx
{
  "title": "涛哥",
  "desc": "LapTopLT"
}
```

#### EQL

专门查询ECS文档的数据的语法（标准指标字段）

官方：To run an EQL search, the searched data stream or index must contain a *timestamp* and *event category* field.

意思是：运行EQL搜索语句，需要你搜索的数据流或者数据索引中含有timestamp（时间戳）和event category字段。

#### SQL

```json
GET /_sql?format=txt
{
  "query": "select * from post where age is not null"
}
```

SQL查询一般性能较低，企业不用。

#### Painless Scripting Language PSL

自带的脚本语言，注意性能问题。

#### Mapping

ES的表结构可以动态改变，非常灵敏，不像mysql一样，表中没有的字段类型就不能新增数据。

查看表结构：

```
GET post/_mapping
```

**Explicit Mapping显式字段**

建表时可以显示指定字段的类型：

```json
PUT /user
{
  "mappings": {
    "properties": {
      "age":    { "type": "integer" },  
      "email":  { "type": "keyword"  }, 
      "name":   { "type": "text"  }   
    }
  }
}
```

age是整数类型，email是关键词类型（不可分割，es不会拆分），text文本类型（es会进行拆分）

### ElasticStack概念

ES 索引（index） =》 表

ES field（字段） =》列

倒排索引 =》 根据内容的一部分（字段）找内容

调用语法（DSL、EQL、SQL等）

Mapping 表结构

* 自动生成mapping
* **手动指定mapping** （限定表结构的类型）

### 分词器

分词的一种规则

```json
POST _analyze
{
  "analyzer": "whitespace",
  "text":     "The quick brown fox."
}
```

es的标准分词：

结果：is, this, deja, vu

```json
POST _analyze
{
  "tokenizer": "standard",
  "filter":  [ "lowercase", "asciifolding" ],
  "text":      "Is this déja vu?"
}
```

关键词分词器：就是不分词，整个句子当做专业术语。

```
POST _analyze
{
  "tokenizer": "keyword",
  "text":      "Is this déja vu?"
}
```

国内用的比较多的：

#### IK分词器（ES插件）：

ik_smart: 尽可能选择最像的一个词来拆分

```json
POST _analyze
{
  "analyzer": "ik_smart",
  "text":     "你好，我是李涛"
}
```

ik_max_word:尽可能的分词

```json
POST _analyze
{
  "analyzer": "ik_max_word",
  "text":     "你好，我是李涛"
}
```

##### 打分机制

鱼皮是狗

鱼皮是小黑子

我是小黑子

搜索鱼皮，会优先匹配第一条，因为4中2，匹配度最高。

### ES的调用方式

1. HTTP Restful调用
2. kibana （dev tools）
3. 客户端操作（Java）

#### Java操作ES

1. ES官方的Java api （不建议用）
2. Spring Data Elasticsearch
    * Spring-data系列：spring提供的操作数据的框架
    * spring-data-redis
    * spring-data-mongoDB
    * spring-data-elasticsearch

推荐使用spring-data-elasticsearch


##### 用ES实现搜索接口

**1、建表（建立索引）**

**SQL语句**
```sql
-- 帖子表
create table if not exists post
(
    id         bigint auto_increment comment 'id' primary key,
    title      varchar(512)                       null comment '标题',
    content    text                               null comment '内容',
    tags       varchar(1024)                      null comment '标签列表（json 数组）',
    thumbNum   int      default 0                 not null comment '点赞数',
    favourNum  int      default 0                 not null comment '收藏数',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment '帖子' collate = utf8mb4_unicode_ci;
```
***
**对应的ES mapping**:
> id(可以不放到字段设置里)

ES中，尽量存放需要用户筛选（搜索）的数据
字段类型是text，这个字段是可被分词的、可模糊查询的；而如果是keyword，只能完全匹配。
analyzer（存储时生效的分词器）：用ik_max_word,拆的更碎，索引更多，更有可能被搜出来
search_analyzer（查询时生效的分词器）：用ik_smart，更偏向于用户想搜的分词。

如果想让text类型的分词字段也支持精确查询，可以创建keyword 类型的子字段。

```json
{
  "aliases": {
    "post": {}
  },
  "mappings": {
    "properties": {
      "title": {
        "type": "text",
        "analyzer": "ik_max_word",
        "search_analyzer": "ik_smart",
        "fields": {
          "keyword": {
            "type": "keyword",
            "ignore_above": 256
          }
        }
      },
      "content": {
        "type": "text",
        "analyzer": "ik_max_word",
        "search_analyzer": "ik_smart",
        "fields": {
          "keyword": {
            "type": "keyword",
            "ignore_above": 256
          }
        }
      },
      "tags": {
        "type": "keyword"
      },
      "thumbNum": {
        "type": "long"
      },
      "favourNum": {
        "type": "long"
      },
      "userId": {
        "type": "keyword"
      },
      "createTime": {
        "type": "date"
      },
      "updateTime": {
        "type": "date"
      },
      "isDelete": {
        "type": "keyword"
      }
    }
  }
}
```
#####增删改查

1. 继承ElasticsearchRepository<T,ID> 默认提供简单的增删改查，多用于自定义搜索方法，返回结果相对精简。
```java
@NoRepositoryBean
public interface CrudRepository<T, ID> extends Repository<T, ID> {
    <S extends T> S save(S entity);

    <S extends T> Iterable<S> saveAll(Iterable<S> entities);

    Optional<T> findById(ID id);

    boolean existsById(ID id);

    Iterable<T> findAll();

    Iterable<T> findAllById(Iterable<ID> ids);

    long count();

    void deleteById(ID id);

    void delete(T entity);

    void deleteAllById(Iterable<? extends ID> ids);

    void deleteAll(Iterable<? extends T> entities);

    void deleteAll();
}
```

ES中，_开头的字段表示系统默认字段，比如_id，如果系统不指定，会自动生成。但是不会在_source字段中补充id的值，所以尽量手动指定。

支持根据方法名自动生成方法，比如：
```java
List<PostEsDTO> findByTitle(String title);
```

2. Spring默认给我们提供的操作ES的客户端对象 ElasticsearchRestTemplate，返回结果更加复杂，需要自己解析

**对于复杂的查询，建议第二种方式**
三个步骤：
1. 取参数
2. 把参数组合为 ES 支持的搜搜条件
3. 从返回值中取结果

查询DSL：
```json
GET /_search
{
  "query": { 
    "bool": { //组合条件
      "must": [ //必须都满足
        { "match": { "title":   "Search"        }}, //match 模糊查询
        { "match": { "content": "Elasticsearch" }}
      ],
      "filter": [ 
        { "term":  { "status": "published" }}, //term 精确查询
        { "range": { "publish_date": { "gte": "2015-01-01" }}} //范围查询
      ]
    }
  }
}
```
wildcard 模糊查询
regexp 正则表达式

查询结果中，score是分数，代表匹配分数。

**minimum_should_match 最小匹配**
```json
POST _search
{
  "query": {
    "bool" : {
      "must" : {
        "term" : { "user.id" : "kimchy" }
      },
      "filter": {
        "term" : { "tags" : "production" }
      },
      "must_not" : {
        "range" : {
          "age" : { "gte" : 10, "lte" : 20 }
        }
      },
      "should" : [
        { "term" : { "tags" : "env1" } },
        { "term" : { "tags" : "deployed" } }
      ],
      "minimum_should_match" : 1,
      "boost" : 1.0
    }
  }
}
```
**转换成Java代码**

```Java
public Page<Post> searchFromEs(PostQueryRequest postQueryRequest) {
        Long id = postQueryRequest.getId();
        Long notId = postQueryRequest.getNotId();
        String searchText = postQueryRequest.getSearchText();
        String title = postQueryRequest.getTitle();
        String content = postQueryRequest.getContent();
        List<String> tagList = postQueryRequest.getTags();
        List<String> orTagList = postQueryRequest.getOrTags();
        Long userId = postQueryRequest.getUserId();
        // es 起始页为 0
        long current = postQueryRequest.getCurrent() - 1;
        long pageSize = postQueryRequest.getPageSize();
        String sortField = postQueryRequest.getSortField();
        String sortOrder = postQueryRequest.getSortOrder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 过滤
        boolQueryBuilder.filter(QueryBuilders.termQuery("isDelete", 0));
        if (id != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("id", id));
        }
        if (notId != null) {
            boolQueryBuilder.mustNot(QueryBuilders.termQuery("id", notId));
        }
        if (userId != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("userId", userId));
        }
        // 必须包含所有标签
        if (CollectionUtils.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                boolQueryBuilder.filter(QueryBuilders.termQuery("tags", tag));
            }
        }
        // 包含任何一个标签即可
        if (CollectionUtils.isNotEmpty(orTagList)) {
            BoolQueryBuilder orTagBoolQueryBuilder = QueryBuilders.boolQuery();
            for (String tag : orTagList) {
                orTagBoolQueryBuilder.should(QueryBuilders.termQuery("tags", tag));
            }
            orTagBoolQueryBuilder.minimumShouldMatch(1);
            boolQueryBuilder.filter(orTagBoolQueryBuilder);
        }
        // 按关键词检索
        if (StringUtils.isNotBlank(searchText)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("title", searchText));
            boolQueryBuilder.should(QueryBuilders.matchQuery("description", searchText));
            boolQueryBuilder.should(QueryBuilders.matchQuery("content", searchText));
            boolQueryBuilder.minimumShouldMatch(1);
        }
        // 按标题检索
        if (StringUtils.isNotBlank(title)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("title", title));
            boolQueryBuilder.minimumShouldMatch(1);
        }
        // 按内容检索
        if (StringUtils.isNotBlank(content)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("content", content));
            boolQueryBuilder.minimumShouldMatch(1);
        }
        // 排序
        SortBuilder<?> sortBuilder = SortBuilders.scoreSort();
        if (StringUtils.isNotBlank(sortField)) {
            sortBuilder = SortBuilders.fieldSort(sortField);
            sortBuilder.order(CommonConstant.SORT_ORDER_ASC.equals(sortOrder) ? SortOrder.ASC : SortOrder.DESC);
        }
        // 分页
        PageRequest pageRequest = PageRequest.of((int) current, (int) pageSize);
        // 构造查询
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
                .withPageable(pageRequest).withSorts(sortBuilder).build();
        SearchHits<PostEsDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, PostEsDTO.class);
        Page<Post> page = new Page<>();
        page.setTotal(searchHits.getTotalHits());
        List<Post> resourceList = new ArrayList<>();
        // 查出结果后，从 db 获取最新动态数据（比如点赞数）
        if (searchHits.hasSearchHits()) {
            List<SearchHit<PostEsDTO>> searchHitList = searchHits.getSearchHits();
            List<Long> postIdList = searchHitList.stream().map(searchHit -> searchHit.getContent().getId())
                    .collect(Collectors.toList());
            List<Post> postList = baseMapper.selectBatchIds(postIdList);
            if (postList != null) {
                Map<Long, List<Post>> idPostMap = postList.stream().collect(Collectors.groupingBy(Post::getId));
                postIdList.forEach(postId -> {
                    if (idPostMap.containsKey(postId)) {
                        resourceList.add(idPostMap.get(postId).get(0));
                    } else {
                        // 从 es 清空 db 已物理删除的数据
                        String delete = elasticsearchRestTemplate.delete(String.valueOf(postId), PostEsDTO.class);
                        log.info("delete post {}", delete);
                    }
                });
            }
        }
        page.setRecords(resourceList);
        return page;
    }
```

先筛选静态数据，查出数据后，再根据查到的内容id取数据库中搜索动态数据

###数据同步
一般情况，做查询搜索功能，使用 ES 来模糊搜索，但数据存放在 MySQL，所以说我们需要把 MySQL 的数据和 ES 进行同步，保证数据一致。

MySQL => ES（单向）

首次安装完成 ES，把 MySQL数据全量同步到 ES 里，写一个单次脚本。
4种方式，全量同步（首次）+ 增量同步（新数据）：
1. 定时任务，比如1分钟一次，找到MySQL过去几分钟内，至少是定时周期的2倍发生改变的数据然后更新到 ES中。
   优点：简单易懂，占用资源少，不用引入第三方中间件
   缺点：有时间差
   应用场景： 数据短时间内不同步影响不大
2. 双写：写数据的时候，必须也去写 ES；更新删除同理。（事务：先保证MySQL写成功，
   如果数据库写 ES失败了，可以通过定时任务 + 日志 + 告警进行检测和修复（补偿））
3. 用Logstash 数据同步管道（一般配合kafka消息队列　+ beats采集器）：
4. 订阅数据库流水的同步方式 Canal：优点：实时监控
   原理：数据库每次修改时，会修改binlog文件，只要监听该文件的修改，就能第一时间得到消息并处理
   canal： 帮你监听 binlog，并解析binlog 为你可以理解的内容
   它伪装成了mysql的从节点。



# 业务注意点
