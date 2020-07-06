package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class DataJpaMealRepository implements MealRepository {

    private final CrudMealRepository crudRepository;

    private final CrudUserRepository crudUserRepository;

    public DataJpaMealRepository(CrudMealRepository crudRepository, CrudUserRepository crudUserRepository) {
        this.crudRepository = crudRepository;
        this.crudUserRepository = crudUserRepository;
    }

    @Override
    public Meal save(Meal meal, int userId) {
        meal.setUser(crudUserRepository.getOne(userId));
        return (!meal.isNew() && get(meal.getId(), userId) == null) ? null : crudRepository.save(meal);
    }

    @Override
    public boolean delete(int id, int userId) {
        return crudRepository.delete(id, userId) != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        return crudRepository.findOne(selectByIdAndUserId(id, userId)).get();
    }

    @Override
    public List<Meal> getAll(int userId) {
        return crudRepository.findAll(userId);
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return crudRepository.findAll(startDateTime, endDateTime, userId);
    }

    public Specification<Meal> selectByIdAndUserId(final Integer id, final Integer userId) {
        return new Specification<Meal>() {
            @Override
            public Predicate toPredicate(Root<Meal> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                Predicate crb1 = criteriaBuilder.equal(root.get("id"), id);
                Predicate crb2 = criteriaBuilder.equal(root.get("user"), userId);
                return criteriaBuilder.and(crb1, crb2);
            }
        };
    }
}
