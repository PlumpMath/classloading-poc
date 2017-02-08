package org.openrepose.poc.leftservice;

import org.openrepose.poc.coreservice.CoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by adrian on 2/8/17.
 */
@Service
public class LeftServiceImpl implements LeftService {
    private CoreService coreService;

    @Autowired
    public LeftServiceImpl(CoreService coreService) {
        this.coreService = coreService;
    }

    @Override
    public void doALeftThing() {
        System.out.println("Do a left thing");
    }
}
