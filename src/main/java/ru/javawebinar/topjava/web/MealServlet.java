package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {

    private static final Logger log = getLogger(MealServlet.class);

    private MealService service;

    @Override
    public void init() throws ServletException {
        super.init();
        service = new MealService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        //Date date = new SimpleDateFormat("MM-dd-yyyy").parse(request.getParameter("dateTime"));
        LocalDateTime dateTime = LocalDateTime.parse(request.getParameter("dateTime"));
        //LocalDateTime dateTime = LocalDateTime.now();
        String mealId = request.getParameter("id");

        String description = request.getParameter("description");
        int calories = Integer.parseInt(request.getParameter("calories"));

        if (mealId == null || mealId.isEmpty()) {
            Meal meal = new Meal(dateTime, description, calories, 0);
            service.add(meal);
        } else {
            int id = Integer.parseInt(mealId);
            Meal meal = new Meal(dateTime, description, calories, id);
            try {
                service.update(id, meal);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        RequestDispatcher view = request.getRequestDispatcher("meals.jsp");
        request.setAttribute("mealsList", service.getAll());
        view.forward(request, response);

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("method doGet");

        String action = request.getParameter("action");

        if (action != null && action.equalsIgnoreCase("getAll")) {
            service.getAll();
        } else if (action != null && action.equalsIgnoreCase("delete")) {
            int id = Integer.parseInt(request.getParameter("id"));
            service.delete(id);
        } else if (action != null && action.equalsIgnoreCase("insert")) {
            request.setAttribute("dateTime", request.getParameter("dateTime"));
            request.setAttribute("description", request.getParameter("description"));
            request.setAttribute("calories", request.getParameter("calories"));
            request.setAttribute("mealId", request.getParameter("id"));
            request.getRequestDispatcher("addUpdateMeal.jsp").forward(request, response);
        }
        request.setAttribute("mealsList", service.getAll());
        request.getRequestDispatcher("meals.jsp").forward(request, response);
    }
}
