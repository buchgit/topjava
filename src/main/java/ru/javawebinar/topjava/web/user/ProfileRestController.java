package ru.javawebinar.topjava.web.user;

import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.web.SecurityUtil;

@Controller
public class ProfileRestController extends AbstractUserController {

    public User get() {
        return super.get(new SecurityUtil().getAuthUserId());
    }

    public void delete() {
        super.delete(new SecurityUtil().getAuthUserId());
    }

    public void update(User user) {
        super.update(user, new SecurityUtil().getAuthUserId());
    }
}