package org.openrepose.poc.farrightservice;

import org.springframework.stereotype.Service;

/**
 * Created by adrian on 2/13/17.
 */
@Service
public class FarRightServiceImpl implements FarRightService {
    @Override
    public void doAFarRightThing() {
        System.out.println("Do a far right thing");
    }
}
