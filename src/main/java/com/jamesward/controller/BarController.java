package com.jamesward.controller;

import com.jamesward.model.Bar;
import com.jamesward.service.BarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value="/api/bar")
public class BarController {

    @Autowired
    private BarService barService;

    @RequestMapping(method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Bar> getAllBars() {
        return barService.getAllBars();
    }

    @RequestMapping(method=RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public final void create(@RequestBody final Bar bar) {
        barService.addBar(bar);
    }

}
