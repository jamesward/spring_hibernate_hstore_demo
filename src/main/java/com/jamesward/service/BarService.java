package com.jamesward.service;


import java.util.List;

import com.jamesward.model.Bar;

public interface BarService {
    
    public void addBar(Bar bar);
    public List<Bar> getAllBars();
    
}
