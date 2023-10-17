package com.sicko.leetcodeuserservice.controller.inner;

import com.sicko.leetcodeuserservice.service.UserService;
import com.sicko.model.entity.User;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * @author zwb
 */
@RestController
@RequestMapping("/inner")
public class UserInnerController {
    @Resource
    private UserService userService;

    @RequestMapping(path = "/get/id",method = RequestMethod.GET)
    public User getById(@RequestParam("userId") Long userId){
        return userService.getById(userId);
    }

    @RequestMapping(path = "/get/ids",method = RequestMethod.GET)
    public List<User> listByIds(@RequestParam("userIdSet") Set<Long> userIdSet){
        return userService.listByIds(userIdSet);
    }
}
