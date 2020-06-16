package ru.javawebinar.topjava.web.user;

import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.web.SecurityUtil;

@Controller
public class ProfileRestController extends AbstractUserController {

    public User get() {
        return super.get(SecurityUtil.authUserId());
    }

    public void delete() {
        super.delete(SecurityUtil.authUserId());
    }

    public void update(User user) {
        super.update(user, SecurityUtil.authUserId());
    }
}