package com.example.controller;

import com.example.entities.User;
import com.example.hadoop.MovieClassify;
import com.example.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import java.io.*;

@Slf4j
@Controller
public class UserController {

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    UserService userService;

    private String sex="";
    private String username="";

    //推测用户性别并推荐电影给用户
    @RequestMapping("/forecast")
    public String recommend(@RequestParam("name") String name,@RequestParam("password") String password) throws Exception {

        //验证用户密码是否正确
        User user = userService.selectUserByName(name);
        if (!user.getPassword().equals(password)){
            return "main";
        }
        String UserID = user.getId();
        username = user.getName();

        //根据用户浏览记录对用户进行性别预测
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        MovieClassify movieClassify = new MovieClassify();
        movieClassify.main(conf,UserID);

        String path = "/movie/knnout/part-r-00000";

        Path resultPath = new Path(path);
        FSDataInputStream inputStream = fs.open(resultPath);
        String out = IOUtils.toString(inputStream,"UTF-8");
        log.info("knnout结果为："+out);
        String[] result = out.split(",");
        sex = result[0];
        log.info("根据用户浏览历史推测该用户性别为："+ sex);
        inputStream.close(); //关闭输入流
        fs.close();  //关闭文件系统
        return "main";
    }

    //返回推荐页面
    @RequestMapping("/recommend")
    public String recommend(Model model){

        //定义男生跟女生喜欢的6部电影
        String[] female = {"Hideous Kinky","Jeanne and the Perfect Guy","Heaven","Mummy, The","Mascara","Frogs for Snakes"};
        String[] man = {"This Is My Father","Xiu Xiu: The Sent-Down Girl","Midsummer Night's Dream, A","Trippin","Love Letter, The","Mummy's Curse, The"};

        if (sex.equals("0")){
            model.addAttribute("movies",man);
            model.addAttribute("username",username+"先生");
        }else {
            model.addAttribute("movies",female);
            model.addAttribute("username",username+"女士");
        }

        return "recommend";
    }


}
