package edu.redwoows.cis18.WebadvisorDesign.security;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

import java.lang.reflect.Method;

@Aspect
@Component
public class PermissionAspect {

    @Autowired
    private PermissionService permissionService;

    @Around("@annotation(RequiresPermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        RequiresPermission annotation = method.getAnnotation(RequiresPermission.class);
        String requiredPermission = annotation.value();
        boolean allowAll = annotation.allowAll();

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String operationRoute = getBestMatchingPattern();

        System.out.printf("!!!! Checking permission for route: %s, user: %s, required: %s%n",
                operationRoute, username, requiredPermission);

        if (permissionService.hasPermission(username, operationRoute, requiredPermission)) {
            return joinPoint.proceed();
        } else {
            throw new SecurityException("Access denied. Required permission: " + requiredPermission);
        }
    }

    private String getBestMatchingPattern() {
        try {
            RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

            // Get the pattern that Spring actually matched
            String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

            if (pattern != null) {
                System.out.printf("!!!! Best matching pattern: %s%n", pattern);
                return pattern;
            }

            // Fallback to request URI
            String requestURI = request.getRequestURI();
            String contextPath = request.getContextPath();
            String route = requestURI.replaceFirst(contextPath, "");
            System.out.printf("!!!! Fallback to request URI: %s%n", route);

            return route;

        } catch (IllegalStateException e) {
            System.out.println("!!!! No request context available");
            return "unknown-route";
        }
    }
}