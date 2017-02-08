package org.openrepose.poc.coreservice;

import org.springframework.stereotype.Service;

/**
 * Created by adrian on 2/8/17.
 */
@Service
public class CoreServiceImpl implements CoreService {
    @Override
    public void doAThing() {
        System.out.println("Doing a thing");
    }
}
