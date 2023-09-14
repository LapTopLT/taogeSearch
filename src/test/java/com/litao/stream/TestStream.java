package com.litao.stream;/*
 *Author:Litao
 *Created in:
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestStream {

    Book book = new Book("1", "111");
    Book book2 = new Book("2", "211");
    Book book3 = new Book("3", "311");
    Book book4 = new Book("4", "411");
    Book book5 = new Book("5", "511");

    Author litao = new Author("李涛", 21, Arrays.asList(book,book2));
    Author hikari = new Author("hikari", 16, Arrays.asList(book3,book4));
    Author samurai = new Author("samurai", 15, Arrays.asList(book4,book5));
    Author samurai2 = new Author("samurai", 15, Arrays.asList(book4,book5));

    List<Author> authors = Arrays.asList(litao,hikari,samurai,samurai2);
    @Test
    void test01(){
        //用Stream流筛选出18岁以下的，不重复的作家，打印出来。
        authors.stream()//转换成stream
            .distinct()//去重
            .filter(author -> author.getAge() < 18)
            .forEach(System.out::println);
    }

    @Test
    void testMap(){
        //map的用法：（例子：遍历所有作者的姓名）
        authors.stream()
            .map(Author::getName)//这里原本stream保存的是Author，用map改成保存的是Author里的成员变量name。
            .forEach(System.out::println);
    }

    @Test
    void testSorted(){
        //sorted： 如果调用空参sorted，必须实现comparable接口；
        //要求实现：按照年龄降序输出作者
        authors.stream()
            .sorted((o1,o2) -> o2.getAge() - o1.getAge())
            .forEach(System.out::println);
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
class Author{
    private String name;
    private Integer age;
    private List<Book> books;


}

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
class Book{
    private String name;
    private String content;
}