package org.openrepose.poc.topservice;

import org.springframework.stereotype.Service;

/**
 * Created by adrian on 2/13/17.
 */
@Service
public class TopServiceImpl implements TopService {
    @Override
    public void doATopThing() {
        System.out.println("Do a top thing");
    }
}
