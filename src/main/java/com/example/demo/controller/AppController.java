package com.example.demo.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.security.RolesAllowed;

@RestController
public class AppController {

    @GetMapping("/")
    public ModelAndView firstPage(ModelAndView modelAndView) {
        modelAndView.addObject("user1", "login: admin password: admin ROLE={READ, WRITE, DELETE} ");
        modelAndView.addObject("user2", "login: read password: read ROLE={READ} ");
        modelAndView.addObject("user3", "login: write password: write  ROLE={WRITE} ");
        modelAndView.addObject("user4", "login: delete password: delete ROLE={DELETE} ");
        modelAndView.setViewName("index");
        return modelAndView;
    }

    @RolesAllowed({"ROLE_WRITE", "ROLE_READ"})
    @GetMapping("/hello")
    public ModelAndView hello(ModelAndView modelAndView) {
        modelAndView.addObject("text", "Привет!");
        modelAndView.addObject("text1", getCurrentUserName());
        modelAndView.setViewName("index1");
        return modelAndView;
    }

    @Secured("ROLE_READ")
    @GetMapping("/read")
    public ModelAndView read(ModelAndView modelAndView) {
        return getAuthorizedModel(modelAndView);
    }

    @PreAuthorize("hasRole('ROLE_WRITE')")
    @GetMapping("/write")
    public ModelAndView write(ModelAndView modelAndView) {
        return getAuthorizedModel(modelAndView);
    }

    @PreAuthorize("hasAnyRole('ROLE_DELETE','ROLE_WRITE')")
    @GetMapping("/write-delete")
    public ModelAndView writeDelete(ModelAndView modelAndView) {
        return getAuthorizedModel(modelAndView);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/what-is-your-name")
    public ModelAndView whatIsYourName(@RequestParam(defaultValue = "") String name, ModelAndView modelAndView) {
        modelAndView.setViewName("forms");
        if (name.isEmpty())
            return modelAndView;
        else if (!name.equalsIgnoreCase(getCurrentUserName()))
            return modelAndView.addObject("text", "ВЫ ВВЕЛИ НЕВЕРНОЕ ИМЯ(ЛОГИН)! ПОПРОБУЙТЕ ЕЩЕ РАЗ!");
        else
            return getAuthorizedModel(modelAndView);
    }

    private String getCurrentUserName() {
        return ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUsername();
    }

    private ModelAndView getAuthorizedModel(ModelAndView modelAndView) {
        modelAndView.setViewName("index1");
        return modelAndView.addObject("text", "ПОЛЬЗОВАТЕЛЬ: " + getCurrentUserName());
    }
}
