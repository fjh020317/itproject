package com.fjh.commity.controller;

import com.fjh.commity.entity.*;
import com.fjh.commity.event.EventProducer;
import com.fjh.commity.service.*;
import com.fjh.commity.util.CommunityConst;
import com.fjh.commity.util.CommunityUtil;
import com.fjh.commity.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConst {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private HostHolder hostHolder;
    private static final Logger logger = LoggerFactory.getLogger(DiscussPostController.class);
    @PostMapping("/add")
    @ResponseBody
    public String postDiscussPost(String title,String content){
        if(StringUtils.isBlank(title)){
            return CommunityUtil.getJsonString(-1,"标题不能为空");
        }
        if (StringUtils.isBlank(content)) {
            return CommunityUtil.getJsonString(-1,"内容不能为空");
        }
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJsonString(-1,"您还没有登录");
        }
        DiscussPost discussPost = new DiscussPost();
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setUserId(user.getId().toString());
        discussPost.setCreateTime(new Date());
        discussPost.setType(0);
        discussPost.setStatus(0);
        discussPost.setCommentCount(0);
        discussPostService.addDiscussPost(discussPost);

        //添加事件
        Event event = new Event()
                .setTopic(TOPIC_PUSH_POST)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_COMMENT)
                .setEntityId(discussPost.getId());

        eventProducer.producer(event);
        return CommunityUtil.getJsonString(0,"发布成功");
    }
    @GetMapping("/detail/{discussPostId}")
    public String getDiscussPost(@PathVariable("discussPostId") int id, Model model, Page page){
        //首先查询帖子
        DiscussPost discussPost = discussPostService.selectDiscussPostById(id);
        model.addAttribute("post",discussPost);
        //根据userId查询用户的信息
        User user = userService.findUserById(discussPost.getUserId().toString());
        model.addAttribute("user",user);
        long likeCount = likeService.findLikeCount(CommunityConst.ENTITY_TYPE_COMMENT,id);
        int likeStatus = hostHolder.getUser() == null ? 0 :
                likeService.findLikeStatus(hostHolder.getUser().getId(),CommunityConst.ENTITY_TYPE_COMMENT,id);
        model.addAttribute("likeCount",likeCount);
        model.addAttribute("likeStatus",likeStatus);
        //对于帖子的评论进行处理
        //1.对评论的分页信息进行处理
        // 评论分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + id);
        Integer commentCount = discussPost.getCommentCount();
        page.setRows(commentCount);

        // 评论: 给帖子的评论
        // 回复: 给评论的评论
        // 评论列表
        List<Comment> commentList = commentService.selectCommentsByEntity(
                ENTITY_TYPE_COMMENT, discussPost.getId(), page.getOffset(), page.getLimit());
        // 评论VO列表
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                // 评论VO
                Map<String, Object> commentVo = new HashMap<>();
                // 评论
                commentVo.put("comment", comment);
                // 作者
                commentVo.put("user", userService.findUserById(comment.getUserId().toString()));
                likeCount = likeService.findLikeCount(CommunityConst.ENTITY_TYPE_REPLAY,comment.getId());
               likeStatus = hostHolder.getUser() == null ? 0 :
                        likeService.findLikeStatus(hostHolder.getUser().getId(),CommunityConst.ENTITY_TYPE_REPLAY,comment.getId());
                // 回复列表
                commentVo.put("likeCount",likeCount);
                commentVo.put("likeStatus",likeStatus);
                List<Comment> replyList = commentService.selectCommentsByEntity(
                        ENTITY_TYPE_REPLAY, comment.getId(), 0, Integer.MAX_VALUE);
                // 回复VO列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>();
                        // 回复
                        replyVo.put("reply", reply);
                        // 作者
                        replyVo.put("user", userService.findUserById(reply.getUserId().toString()));
                        // 回复目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId().toString());
                        replyVo.put("target", target);
                        likeCount = likeService.findLikeCount(CommunityConst.ENTITY_TYPE_REPLAY,reply.getId());
                        likeStatus = hostHolder.getUser() == null ? 0 :
                                likeService.findLikeStatus(hostHolder.getUser().getId(),CommunityConst.ENTITY_TYPE_REPLAY,reply.getId());
                        replyVo.put("likeCount",likeCount);
                        replyVo.put("likeStatus",likeStatus);
                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys", replyVoList);

                // 回复数量
                int replyCount = commentService.selectCount(ENTITY_TYPE_REPLAY, comment.getId());
                commentVo.put("replyCount", replyCount);

                commentVoList.add(commentVo);
            }
        }

        model.addAttribute("comments", commentVoList);
        return "/site/discuss-detail";
    }
}
