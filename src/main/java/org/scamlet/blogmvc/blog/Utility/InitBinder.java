package org.scamlet.blogmvc.blog.Utility;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class InitBinder {

    @org.springframework.web.bind.annotation.InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.setDisallowedFields("thumbnail");
    }

}
