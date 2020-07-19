package ru.javawebinar.topjava.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.web.meal.AbstractMealController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;

@RequestMapping(value = "/meals")
@Controller
public class JspMealController extends AbstractMealController {

    public Meal get(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        return super.get(id);
    }

    @GetMapping(params = "delete")
    public String delete(HttpServletRequest request, Model model) {
        int id = Integer.parseInt(request.getParameter("id"));
        super.delete(id);
        model.addAttribute("meals", super.getAll());
        return "meals";
    }

    @GetMapping
    public String getAll(Model model, HttpServletRequest servletRequest) {
        model.addAttribute("meals", super.getAll());
        return "meals";
    }

    @GetMapping(params = "create")
    public String create(Model model, HttpServletRequest servletRequest) {
        Meal meal = new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000);
        model.addAttribute("meal", meal);
        return "mealForm";
    }

    @GetMapping(params = "update")
    public String updateGet(Model model, HttpServletRequest servletRequest) {
        int id = Integer.parseInt(servletRequest.getParameter("id"));
        model.addAttribute("meal", super.get(id));
        return "mealForm";
    }

    @PostMapping(params = "update")
    public String update(Model model, HttpServletRequest servletRequest) throws UnsupportedEncodingException {
        servletRequest.setCharacterEncoding("UTF-8");
        Meal meal = new Meal(
                LocalDateTime.parse(servletRequest.getParameter("dateTime")),
                servletRequest.getParameter("description"),
                Integer.parseInt(servletRequest.getParameter("calories")));

        if (StringUtils.isEmpty(servletRequest.getParameter("id"))) {
            super.create(meal);
        } else {
            super.update(meal, getId(servletRequest));
        }
        model.addAttribute("meals", super.getAll());
        return "meals";
    }

    @GetMapping(params = "filter")
    public String getBetween(Model model, HttpServletRequest servletRequest) {
        LocalDate startDate = parseLocalDate(servletRequest.getParameter("startDate"));
        LocalDate endDate = parseLocalDate(servletRequest.getParameter("endDate"));
        LocalTime startTime = parseLocalTime(servletRequest.getParameter("startTime"));
        LocalTime endTime = parseLocalTime(servletRequest.getParameter("endTime"));
        model.addAttribute("meals", super.getBetween(startDate, startTime, endDate, endTime));
        return "meals";
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }
}
