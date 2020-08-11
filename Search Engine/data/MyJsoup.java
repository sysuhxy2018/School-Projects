package com.xinyuan.my_jsoup;

public class MyJsoup 
{
    public static void main(String[] args)
    {
        EdgeList el = new EdgeList();
        // all absolute path
        el.genGraph("C:\\Users\\Xinyuan\\Desktop\\hw4\\nytimes\\nytimes", 
        		"C:\\Users\\Xinyuan\\Desktop\\hw4\\URLtoHTML_nytimes_news.csv",
        		"C:\\Users\\Xinyuan\\Desktop\\hw4\\edgeList.txt");
    }
}
