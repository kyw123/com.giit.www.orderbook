package com.itaem.crazy.shirodemo.modules.shiro.controller;
import com.itaem.crazy.shirodemo.modules.shiro.dto.LoginDTO;
import com.itaem.crazy.shirodemo.modules.shiro.entity.User;
import com.itaem.crazy.shirodemo.modules.shiro.service.ShiroService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
/**
 * 编写了一个登陆入口
 * 为了方便这里不做密码加盐加密
 * @Author 大誌
 * @Date 2019/3/30 22:04
 * @Version 1.0
 */
@Api(tags = "Shiro权限管理")
@RestController
public class ShiroController {
    private final ShiroService shiroService;
    public ShiroController(ShiroService shiroService) {
        this.shiroService = shiroService;
    }

    /**
     *登录
     * @param loginDTO  登录传输类 loginDTO
     * @param bindingResult   为绑定的结果。
     *        在使用@Valid 进行参数校验的时候。可以使用BindingResult对象。这个对象的作用是将所有的异常信息存起来。内置的验证约束注解如下表所示
     * @return
     */
    @ApiOperation(value = "登陆", notes = "参数:用户名 密码")  /*swagger 的标记接口*/
    @PostMapping("/sys/login")
    public Map<String, Object> login(@RequestBody @Validated LoginDTO loginDTO, BindingResult bindingResult) {
        Map<String, Object> result = new HashMap<>();   //创建了一个Map 集合。
        if (bindingResult.hasErrors()) {
            result.put("status", 400);
            result.put("msg", bindingResult.getFieldError().getDefaultMessage());
            return result;
        }
        String username = loginDTO.getUsername();
        System.out.println("账户是\t"+username);
        String password = loginDTO.getPassword();
        System.out.println("密码是\t"+password);
        //用户信息
        User user = shiroService.findByUsername(username);
        //账号不存在、密码错误
        if (user == null || !user.getPassword().equals(password)) {
            result.put("status", 400);
            result.put("msg", "账号或密码有误");
        } else {
            //生成token，并保存到数据库
            result = shiroService.createToken(user.getUserId());      //生成token ，并保存到数据库
            result.put("status", 200);
            result.put("msg", "登陆成功");
        }
        return result;
    }

    /**
     * 退出
     */
    @ApiOperation(value = "登出", notes = "参数:token")
    @PostMapping("/sys/logout")
    public Map<String, Object> logout(@RequestHeader("token")String token) {
        Map<String, Object> result = new HashMap<>();
        shiroService.logout(token);
        result.put("status", 200);
        result.put("msg", "您已安全退出系统");
        return result;
    }
}


