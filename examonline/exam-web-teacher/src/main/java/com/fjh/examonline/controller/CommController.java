package com.fjh.examonline.controller;

import cn.hutool.crypto.digest.DigestUtil;
import com.fjh.examonline.domain.Teacher;
import com.fjh.examonline.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/comm")
public class CommController {
    @Autowired
    private TeacherService teacherService;
    @RequestMapping("/login.html")
    public String toLogin(){
        return "comm/login";
    }

    @RequestMapping("/login")
    @ResponseBody
    public boolean login(String tname, String pass, HttpSession session){
        Teacher teacher = teacherService.findTeacherByName(tname);
        if(teacher == null){
            System.out.println("用户不存在");
            return false;
        }
        String password = DigestUtil.md5Hex(pass);
        if(!teacher.getPass().equals(password)){
            System.out.println("密码错误");
            return false;
        }
        session.setAttribute("loginTeacher",teacher);
        return true;
    }
    @RequestMapping("/main.html")
    public  String toMain(){
        return "comm/main";
    }

    @RequestMapping("/exit")
    public String exit(HttpSession session){
        session.removeAttribute("loginTeacher");
        return "comm/login";
    }
    @RequestMapping("/timeout.html")
    public String toTimeOut(){
        return "comm/timeout";
    }
}
