package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.List;

public interface CrudMealRepository extends JpaRepository<Meal, Integer>, JpaSpecificationExecutor<Meal> {
    @Override
    @Transactional
    Meal save(Meal meal);

    @Modifying
    @Transactional
    @Query("delete from Meal m where m.id=:id and m.user.id=:userId")
    int delete(@Param("id") int id, @Param("userId") int userId);

    @Query("select m from Meal m where m.user.id=:userId order by m.dateTime desc ")
    List<Meal> findAll(@Param("userId") int userId);

    @Query("select m from Meal m where m.user.id=:userId and m.dateTime>=:startDate and m.dateTime<:endDate order by m.dateTime desc ")
    List<Meal> findAll(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("userId") int userId);
}
