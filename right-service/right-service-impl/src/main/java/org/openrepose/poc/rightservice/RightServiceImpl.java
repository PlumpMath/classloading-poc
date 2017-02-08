package org.openrepose.poc.rightservice;

import org.openrepose.poc.coreservice.CoreService;
import org.openrepose.poc.leftservice.LeftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by adrian on 2/8/17.
 */
@Service
public class RightServiceImpl implements RightService {
    private CoreService coreService;
    private LeftService leftService;

    @Autowired
    public RightServiceImpl(CoreService coreService, LeftService leftService) {
        this.coreService = coreService;
        this.leftService = leftService;
    }

    @Override
    public void doARightThing() {
        System.out.println("Do a right service thing");
    }
}
