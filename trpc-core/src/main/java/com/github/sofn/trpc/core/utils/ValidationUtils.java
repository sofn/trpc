package com.github.sofn.trpc.core.utils;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Authors: sofn
 * Version: 1.0  Created at 2016-09-25 16:43.
 */
@Slf4j
public class ValidationUtils {
    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * 调用JSR303的validate方法, 验证失败时抛出ConstraintViolationException, 而不是返回constraintViolations.
     */
    public static void validateWithException(Object object, Class<?>... groups)
            throws ConstraintViolationException {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object, groups);
        if (!constraintViolations.isEmpty()) {
            log.error(extractMessageAsString(constraintViolations));
            throw new ConstraintViolationException(constraintViolations);
        }
    }

    /**
     * 辅助方法, 转换ConstraintViolationException中的Set<ConstraintViolations>中为List<message>.
     */
    public static List<String> extractMessage(ConstraintViolationException e) {
        return extractMessage(e.getConstraintViolations());
    }

    /**
     * 辅助方法, 转换Set<ConstraintViolation>为List<message>
     */
    public static List<String> extractMessage(Set<? extends ConstraintViolation> constraintViolations) {
        return constraintViolations.stream()
                .map(c -> c.getRootBeanClass().getSimpleName() + "." + c.getPropertyPath() + " " + c.getMessage() + " invalidValue: " + c.getInvalidValue())
                .collect(Collectors.toList());
    }

    /**
     * 辅助方法, 转换ConstraintViolationException中的Set<ConstraintViolations>中为String.
     */
    public static String extractMessageAsString(ConstraintViolationException e) {
        return extractMessageAsString(e.getConstraintViolations());
    }

    /**
     * 辅助方法, 转换Set<ConstraintViolation>为List<message>
     */
    public static String extractMessageAsString(Set<? extends ConstraintViolation> constraintViolations) {
        return constraintViolations.stream()
                .map(c -> c.getRootBeanClass().getSimpleName() + "." + c.getPropertyPath() + " " + c.getMessage() + " invalidValue: " + c.getInvalidValue())
                .collect(Collectors.joining(","));
    }

}
