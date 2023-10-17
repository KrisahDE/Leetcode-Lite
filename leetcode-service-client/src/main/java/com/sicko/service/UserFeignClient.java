package com.sicko.service;

import com.sicko.commons.common.ErrorCode;
import com.sicko.commons.exception.BusinessException;
import com.sicko.model.entity.User;
import com.sicko.model.enums.UserRoleEnum;
import com.sicko.model.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

import static com.sicko.commons.constant.UserConstant.USER_LOGIN_STATE;


/**
 * 用户服务
 *
 * @author zwb
 */
@FeignClient(name = "leetcode-user-service", path = "/api/user/inner")
public interface UserFeignClient {

    default UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    default User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 可以考虑在这里做全局权限校验
        return currentUser;
    }

    /**
     * 是否为管理员
     *
     * @param user
     * @return
     */
    default boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }



    @GetMapping(path = "/get/id")
    User getById(@RequestParam("userId") Long userId);

    @GetMapping(path = "/get/ids")
    List<User> listByIds(@RequestParam("userIdSet") Set<Long> userIdSet);

}
