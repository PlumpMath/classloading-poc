package org.openrepose.poc.leftservice;

import org.openrepose.poc.coreservice.CoreService;
import org.openrepose.poc.topservice.TopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by adrian on 2/8/17.
 */
@Service
public class LeftServiceImpl implements LeftService {
    private CoreService coreService;
    private TopService topService;

    @Autowired
    public LeftServiceImpl(CoreService coreService, TopService topService) {
        this.coreService = coreService;
        this.topService = topService;
    }

    @Override
    public void doALeftThing() {
        System.out.println("Do a left thing");
    }
}
